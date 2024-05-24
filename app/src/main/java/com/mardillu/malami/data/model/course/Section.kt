package com.mardillu.malami.data.model.course

import kotlinx.serialization.Serializable


@Serializable
data class Section(
    val modules: List<Module>,
    val quiz: List<Quiz>,
    val shortDescription: String,
    val title: String
)