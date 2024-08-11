package com.mardillu.malami.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.mardillu.malami.data.model.course.Course
import com.mardillu.malami.data.model.course.GenerateContentRequest
import com.mardillu.malami.data.model.course.LearningSchedule
import com.mardillu.malami.data.model.course.MlGenerateContentResponse
import com.mardillu.malami.data.model.course.Module
import com.mardillu.malami.data.model.course.OpenAIContentRequest
import com.mardillu.malami.data.model.course.OpenAIMessage
import com.mardillu.malami.data.model.course.Quiz
import com.mardillu.malami.data.model.course.QuizAttempt
import com.mardillu.malami.data.model.course.Section
import com.mardillu.malami.data.model.course.UserCourses
import com.mardillu.malami.network.GeminiApiService
import com.mardillu.malami.network.NetworkResult
import com.mardillu.malami.network.makeRequestToApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import javax.inject.Inject

/**
 * Created on 21/05/2024 at 10:35 pm
 * @author mardillu
 */
class CoursesRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val geminiApiService: GeminiApiService
) {
    private val _userCoursesFlow = MutableStateFlow<List<Course>>(emptyList())
    val userCoursesFlow: StateFlow<List<Course>> get() = _userCoursesFlow
    private var listenerRegistration: ListenerRegistration? = null

    suspend fun createCourse(prompt: String, geminiApiKey: String): Result<GenerateContentResponse> {
        val model = GenerativeModel(
            "gemini-1.5-pro-latest", //gemini-1.5-pro-001
            geminiApiKey,
            generationConfig = generationConfig {
                temperature = 1f
                topK = 64
                topP = 0.95f
                maxOutputTokens = 700_000
                responseMimeType = "application/json"
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
            ),
            systemInstruction = content {
                text(
                    "You are an AI instructor. You create custom, full content " +
                            "courses with quizzes for people based on their specific learning styles and a " +
                            "learning plan to complete the course. Courses are broken down into sections, " +
                            "then modules. Each section comes with a quiz"
                )
            },
            requestOptions = RequestOptions(timeout = 5 * 60_000),
        )

        val chat = model.startChat()

        val response = chat.sendMessage(prompt)
        return Result.success(response)
    }

    suspend fun createCourseCustom(prompt: String, geminiApiKey: String): NetworkResult<Response<MlGenerateContentResponse>> {
        val systemText =  "You are an AI instructor. You create custom, full content " +
                "courses with quizzes for people based on their specific learning styles and a " +
                "learning plan to complete the course. Courses are broken down into sections, " +
                "then modules. Each section comes with a quiz"
        val request = GenerateContentRequest(
            contents = listOf(content { text(prompt) }),
            generationConfig = generationConfig {
                temperature = 1f
                topK = 64
                topP = 0.95f
                maxOutputTokens = 700_000
                responseMimeType = "application/json"
            },
//            safetySettings = listOf(
//                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
//                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
//                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
//                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
//            ),
            systemInstruction = content {
                text(
                    systemText
                )
            },
        )

        val response = makeRequestToApi {
            geminiApiService.generateCompletion(geminiApiKey, request,)
        }
        return response
    }

    suspend fun saveCourse(course: List<Course>): Result<Unit>  {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

        return try {
            firestore.collection("courses")
                .document(userId)
                .set(UserCourses(course))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun startListeningForUserCourses(){
        val userId = auth.currentUser?.uid ?: return

        listenerRegistration = firestore.collection("courses")
            .document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _userCoursesFlow.value = emptyList()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists() && snapshot["courses"] != null) {
                    val coursesList = (snapshot["courses"] as List<Map<String, Any>>).map { coursesMap ->
                        val sectionsList =
                            (coursesMap["sections"] as List<Map<String, Any>>).map { sectionMap ->
                                val modulesList =
                                    (sectionMap["modules"] as List<Map<String, Any>>).map { moduleMap ->
                                        Module(
                                            id = moduleMap["id"] as String,
                                            content = moduleMap["content"] as String,
                                            shortDescription = moduleMap["shortDescription"] as String,
                                            title = moduleMap["title"] as String,
                                            timeToRead = moduleMap["timeToRead"] as String,
                                            completed = moduleMap["completed"] as Boolean,
                                            bannerImage = moduleMap["bannerImage"] as String
                                        )
                                    }

                                val quizList =
                                    (sectionMap["quiz"] as List<Map<String, Any>>).map { quizMap ->
                                        Quiz(
                                            id = quizMap["id"] as String,
                                            answer = quizMap["answer"] as String,
                                            options = (quizMap["options"] as List<String>),
                                            question = quizMap["question"] as String
                                        )
                                    }

                                Section(
                                    id = sectionMap["id"] as String,
                                    modules = modulesList,
                                    quiz = quizList,
                                    shortDescription = sectionMap["shortDescription"] as String,
                                    title = sectionMap["title"] as String,
                                    aiExplainability = sectionMap["aiExplainability"] as String
                                )
                            }
                        val course = Course(
                            id = coursesMap["id"] as String,
                            courseOutline = coursesMap["courseOutline"] as String,
                            shortDescription = coursesMap["shortDescription"] as String,
                            title = coursesMap["title"] as String,
                            learningSchedule = LearningSchedule(
                                day = (coursesMap["learningSchedule"] as Map<String, String>)["day"],
                                time = (coursesMap["learningSchedule"] as Map<String, String>)["time"],
                                frequency = (coursesMap["learningSchedule"] as Map<String, String>)["frequency"]
                            ),
                            sections = sectionsList,
                            bannerImage = coursesMap["bannerImage"] as String
                        )

                        course
                    }
                    _userCoursesFlow.update {
                        coursesList
                    }
                } else {
                    _userCoursesFlow.update {
                        emptyList()
                    }
                }
            }
    }

    fun stopListeningForUserCourses() {
        listenerRegistration?.remove()
    }

    fun updateModuleCompletedStatusById(courseId: String, sectionId: String, moduleId: String, newStatus: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val courseRef = firestore.collection("courses").document(userId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(courseRef)
            if (snapshot.exists()) {
                // Get the sections array
                val courses = snapshot.get("courses") as? List<Map<String, Any>> ?: emptyList()
                val updatedCourses = courses.map { course ->
                    if (course["id"] == courseId) {
                        val sections =
                            course["sections"] as? List<Map<String, Any>> ?: emptyList()
                        val updatedSections = sections.map { section ->
                            if (section["id"] == sectionId) {
                                val modules =
                                    section["modules"] as? List<Map<String, Any>> ?: emptyList()
                                val updatedModules = modules.map { module ->
                                    if (module["id"] == moduleId) {
                                        module.toMutableMap()
                                            .apply { this["completed"] = newStatus }
                                    } else {
                                        module
                                    }
                                }
                                section.toMutableMap().apply { this["modules"] = updatedModules }
                            } else {
                                section
                            }
                        }
                        course.toMutableMap().apply { this["sections"] = updatedSections }
                    } else {
                        course
                    }
                }
                transaction.update(courseRef, "courses", updatedCourses)
            }
        }.addOnSuccessListener {
            //println("Module updated successfully.")
        }.addOnFailureListener { e ->
            //println("Error updating module: ${e.message}")
        }
    }

    suspend fun getCourses(fromCache: Boolean): Result<List<Course>> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

        return try {
            val courses = if (fromCache) {
                firestore.collection("courses")
                    .document(userId)
                    .get(Source.CACHE)
                    .await()
            } else {
                firestore.collection("courses")
                    .document(userId)
                    .get()
                    .await()
            }


            if (courses != null && courses.exists() && courses["courses"] != null) {
                val coursesList = (courses["courses"] as List<Map<String, Any>>).map { coursesMap ->
                    val sectionsList =
                        (coursesMap["sections"] as List<Map<String, Any>>).map { sectionMap ->
                            val modulesList =
                                (sectionMap["modules"] as List<Map<String, Any>>).map { moduleMap ->
                                    Module(
                                        id = moduleMap["id"] as String,
                                        content = moduleMap["content"] as String,
                                        shortDescription = moduleMap["shortDescription"] as String,
                                        title = moduleMap["title"] as String,
                                        timeToRead = moduleMap["timeToRead"] as String,
                                        completed = moduleMap["completed"] as Boolean,
                                        bannerImage = moduleMap["bannerImage"] as String
                                    )
                                }

                            val quizList =
                                (sectionMap["quiz"] as List<Map<String, Any>>).map { quizMap ->
                                    Quiz(
                                        id = quizMap["id"] as String,
                                        answer = quizMap["answer"] as String,
                                        options = (quizMap["options"] as List<String>),
                                        question = quizMap["question"] as String
                                    )
                                }

                            Section(
                                id = sectionMap["id"] as String,
                                modules = modulesList,
                                quiz = quizList,
                                shortDescription = sectionMap["shortDescription"] as String,
                                title = sectionMap["title"] as String,
                                aiExplainability = sectionMap["aiExplainability"] as String
                            )
                        }
                    val course = Course(
                        id = coursesMap["id"] as String,
                        courseOutline = coursesMap["courseOutline"] as String,
                        shortDescription = coursesMap["shortDescription"] as String,
                        title = coursesMap["title"] as String,
                        learningSchedule = LearningSchedule(
                            day = (coursesMap["learningSchedule"] as Map<String, String>)["day"],
                            time = (coursesMap["learningSchedule"] as Map<String, String>)["time"],
                            frequency = (coursesMap["learningSchedule"] as Map<String, String>)["frequency"]
                        ),
                        sections = sectionsList,
                        bannerImage = coursesMap["bannerImage"] as String
                    )

                    course
                }
                Result.success(coursesList)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
           Result.failure(e)
        }
    }

    suspend fun getQuizAttempts(): Result<List<QuizAttempt>> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

        return try {
            val courses = firestore.collection("quizAttempts")
                .document(userId)
                .get()
                .await()

            if (courses != null && courses.exists() && courses["quizAttempts"] != null) {
                val quizAttempts = (courses["quizAttempts"] as List<Map<String, Any>>).map { attemptsMap ->
                    val attempt = QuizAttempt(
                        id = attemptsMap["id"] as String,
                        courseId = attemptsMap["courseId"] as String,
                        sectionId = attemptsMap["sectionId"] as String,
                        obtainablePoints = attemptsMap["obtainablePoints"] as Long,
                        obtainedPoints = (attemptsMap["obtainedPoints"] as Number).toDouble(),
                        passed = attemptsMap["passed"] as Boolean,
                        fraction = (attemptsMap["fraction"] as Number).toDouble(),
                        attemptedAt = attemptsMap["attemptedAt"] as Long
                    )
                    attempt
                }
                Result.success(quizAttempts)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun saveQuizAttempt(attempt: QuizAttempt): Result<Unit>  {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

        return try {
            firestore.collection("quizAttempts")
                .document(userId)
                .update("quizAttempts", FieldValue.arrayUnion(attempt))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


