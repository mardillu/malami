package com.mardillu.malami.data.model.course

import kotlinx.serialization.Serializable

@Serializable
data class Module(
    val content: String,
    val shortDescription: String,
    val title: String
)