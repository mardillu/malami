package com.mardillu.malami.data.model.course

import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

@Serializable
data class Course(
    val courseOutline: String,
    val id: String = UUID.randomUUID().toString(),
    val learningSchedule: LearningSchedule,
    val sections: List<Section>,
    val shortDescription: String,
    val title: String,
    val bannerImage: String = "",
    val createdAt: Long = Date().time,
)

@Serializable
data class UserCourses(
    val courses: List<Course>,
)