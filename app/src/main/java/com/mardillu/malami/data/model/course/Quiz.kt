package com.mardillu.malami.data.model.course

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Quiz(
    val id : String = UUID.randomUUID().toString(),
    val answer: String,
    val options: List<String>,
    val question: String
)

@Serializable
data class QuizAttempt(
    val id: String = UUID.randomUUID().toString(),
    val courseId: String,
    val sectionId: String,
    val obtainablePoints: Long,
    val obtainedPoints: Double,
    val passed: Boolean = false,
    val fraction: Double = 0.0,
    val attemptedAt: Long = System.currentTimeMillis(),
)

@Serializable
data class QuizAttempts(
    val quizAttempts: List<QuizAttempt>
)