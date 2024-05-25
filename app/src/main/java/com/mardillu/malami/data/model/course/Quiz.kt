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