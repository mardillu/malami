package com.mardillu.malami.data.model.course

/**
 * Created on 15/06/2024 at 9:59 pm
 * @author mardillu
 */
data class ModuleAudio(
    val courseId: String,
    val moduleId: String,
    val courseTitle: String,
    val sectionTitle: String,
    val moduleTitle: String,
    val moduleDescription: String,
    val audioUri: String
)

data class ModuleContent(
    val courseId: String,
    val moduleId: String,
    val courseTitle: String,
    val sectionTitle: String,
    val moduleTitle: String,
    val moduleDescription: String,
    val moduleContent: String,
    val sequence: Int
)
