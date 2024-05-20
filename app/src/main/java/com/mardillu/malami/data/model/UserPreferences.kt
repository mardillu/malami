package com.mardillu.malami.data.model

/**
 * Created on 19/05/2024 at 1:19 pm
 * @author mardillu
 */
data class UserPreferences(
    val learningStyle: String,
    val paceOfLearning: String,
    val studyTime: String,
    val studyEnvironment: String,
    val deviceUsage: String,
    val contentFormat: String,
    val readingSpeed: String,
    val difficultyLevel: String,
    val topicsOfInterest: String,
    val learningGoals: String,
    val priorKnowledge: String,
    val feedbackType: String,
    val assessmentPreferences: String,
    val specialRequirements: String
)