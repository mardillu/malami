package com.mardillu.malami.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import javax.inject.Inject

/**
 * Created on 21/05/2024 at 10:35 pm
 * @author mardillu
 */
class CoursesRepository @Inject constructor() {

    suspend fun createCourse(prompt: String, geminiApiKey: String): Result<GenerateContentResponse> {
        val model = GenerativeModel(
            "gemini-1.5-flash-latest",
            geminiApiKey,
            generationConfig = generationConfig {
                temperature = 1f
                topK = 64
                topP = 0.95f
                maxOutputTokens = 100_000
                responseMimeType = "application/json"
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
            ),
            systemInstruction = content { text("You are an AI instructor. You create custom full " +
                    "courses with quizzes for people based on their specific learning styles and a " +
                    "learning plan to complete the course. The course should be broken down into sections, then modules.") },
        )

        val chat = model.startChat()

        // Note that sendMessage() is a suspend function and should be called from
        // a coroutine scope or another suspend function
        val response = chat.sendMessage(prompt)
        return Result.success(response)
        // Get the first text part of the first candidate
        //println(response.text)
        // Alternatively
        //println(response.candidates.first().content.parts.first().asTextOrNull())
    }
}

//text("Create a complete course for a person with the following learning styles: [What do you want to learn?: product management; learning goals: I want to be able to get into product management at the end of the course; Prior Knowledge: Beginner; Preferred Learning Style: Visual; Pace of Learning: medium; Preferred Study Time: Evening; Reading Speed: slow: Difficulty Level of Reading Material: medium; Special Requirements: Having many examples and anecdotes help me learn better]. The course should be divided into sections, and each section into modules whose content can be as long as possible. Create the full content for each of the modules for the person to read, (not bullet point guides). Your response should be in json and should exactly match this json structure:\n                                {\n                                    title: title,\n                                    short_description: short description,\n                                    course_outline: *course outline in markdown*,\n                                    learning_schedule: {\n                                        time: 5:30 PM,\n                                        frequency: daily or weekly,\n                                        day: day of week, or null of daily\n                                    },\n                                    sections:[\n                                        {\n                                            title: title,\n                                            short_description: short description,\n                                            modules: [\n                                                {\n                                                    title: title,\n                                                    short_description: short description,\n                                                    content: module content in markdown,\n                                                }\n                                            ]\n                                            quiz: [\n                                                {\n                                                    question: question,\n                                                    options: [up to 5 options],\n                                                    answer: correct answer\n                                                }\n                                            ]\n                                        },\n                                    ]\n                                }")