package com.mardillu.malami.data.model.course

import kotlinx.serialization.Serializable


@Serializable
data class LearningSchedule(
    val day: String? = "",
    val frequency: String,
    val time: String
)