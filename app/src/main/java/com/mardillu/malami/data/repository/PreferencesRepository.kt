package com.mardillu.malami.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mardillu.malami.data.model.UserPreferences
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created on 19/05/2024 at 1:21 pm
 * @author mardillu
 */
class PreferencesRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun saveUserPreferences(userPreferences: UserPreferences): Result<Unit> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

        return try {
            firestore.collection("userPreferences")
                .document(userId)
                .set(userPreferences)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserPreferences(): Result<UserPreferences?> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

        return try {
            val userPreferences = firestore.collection("userPreferences")
                .document(userId)
                .get()
                .await()

            return if (userPreferences.exists()) {
                val settings = UserPreferences(
                    learningStyle = userPreferences["learningStyle"] as String,
                    paceOfLearning = userPreferences["paceOfLearning"] as String,
                    studyTime = userPreferences["studyTime"] as String,
                    //contentFormat = userPreferences["contentFormat"] as? String,
                    readingSpeed = userPreferences["readingSpeed"] as String,
                    difficultyLevel = userPreferences["difficultyLevel"] as String,
                    feedbackType = userPreferences["feedbackType"] as String,
                    //assessmentPreferences = userPreferences["assessmentPreferences"] as? String,
                    specialRequirements = userPreferences["specialRequirements"] as String
                )

                Result.success(settings)
            } else {
                Result.failure(Exception("User preferences not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}