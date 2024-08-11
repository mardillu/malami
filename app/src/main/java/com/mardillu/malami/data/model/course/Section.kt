package com.mardillu.malami.data.model.course

import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class Section(
    val id : String = UUID.randomUUID().toString(),
    val modules: List<Module>,
    val quiz: List<Quiz>,
    val shortDescription: String,
    val title: String,
)