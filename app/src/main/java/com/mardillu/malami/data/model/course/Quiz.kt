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
    val quizId: String,
    val obtainablePoints: Int,
    val obtainedPoints: Int,
    val attemptedAt: Long,
)