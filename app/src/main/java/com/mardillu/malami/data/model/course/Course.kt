package com.mardillu.malami.data.model.course

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val courseOutline: String,
    val learningSchedule: LearningSchedule,
    val sections: List<Section>,
    val shortDescription: String,
    val title: String
)