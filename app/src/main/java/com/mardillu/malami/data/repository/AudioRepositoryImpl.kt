package com.mardillu.malami.data.repository

import android.speech.tts.TextToSpeechService
import com.mardillu.malami.data.model.course.ModuleAudio
import com.mardillu.malami.data.model.course.ModuleContent
import java.io.File
import javax.inject.Inject

/**
 * Created on 15/06/2024 at 11:07 pm
 * @author mardillu
 */
class AudioRepositoryImpl @Inject constructor(private val textToSpeechService: AndroidTextToSpeechService) {
     suspend fun convertTextToWavLocal(contentList: List<ModuleContent>, outputDir: File): Pair<List<ModuleAudio>, List<File>> {
        val files = mutableListOf<File>()
         val moduleAudio = mutableListOf<ModuleAudio>()
        for ((index, content) in contentList.withIndex()) {
            val file = File(outputDir, "audio_$index.wav")
            val audio = ModuleAudio(
                courseId = content.courseId,
                courseTitle = content.courseTitle,
                sectionTitle = content.sectionTitle,
                moduleTitle = content.moduleTitle,
                moduleDescription = content.moduleDescription,
                audioUri = file.absolutePath
            )
            textToSpeechService.convertTextToWav(content.moduleContent, file)
            files.add(file)
            moduleAudio.add(audio)
        }
        return Pair(moduleAudio, files)
    }

    suspend fun convertTextToWavRemote() {

    }
}