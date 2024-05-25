package com.mardillu.malami.data.model.course

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Module(
    val id : String = UUID.randomUUID().toString(),
    val content: String,
    val shortDescription: String,
    val title: String,
    val timeToRead: String = "10 min",
    val completed: Boolean = false,
)