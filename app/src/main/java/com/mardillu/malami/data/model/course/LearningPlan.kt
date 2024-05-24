package com.mardillu.malami.data.model.course

import kotlinx.serialization.Serializable

@Serializable
data class LearningPlan(
    val modules: List<Module>,
    val section: String
)