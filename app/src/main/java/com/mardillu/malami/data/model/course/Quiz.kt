package com.mardillu.malami.data.model.course

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val answer: String,
    val options: List<String>,
    val question: String
)