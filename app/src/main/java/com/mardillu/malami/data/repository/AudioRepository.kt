package com.mardillu.malami.data.repository

import com.mardillu.malami.data.model.AudioConfig
import com.mardillu.malami.data.model.Input
import com.mardillu.malami.data.model.TextToSpeechRequest
import com.mardillu.malami.data.model.TextToSpeechResponse
import com.mardillu.malami.data.model.Voice
import com.mardillu.malami.data.model.course.ModuleAudio
import com.mardillu.malami.data.model.course.ModuleContent
import com.mardillu.malami.network.NetworkResult
import com.mardillu.malami.network.TTSApiService
import com.mardillu.malami.network.makeRequestToApi
import java.io.File
import javax.inject.Inject

/**
 * Created on 15/06/2024 at 11:07 pm
 * @author mardillu
 */
class AudioRepository @Inject constructor(
    private val textToSpeechService: AndroidTextToSpeechService,
    private val ttsApiService: TTSApiService
    ) {
     suspend fun convertTextToWavLocal(contentList: List<ModuleContent>, outputDir: File): Pair<List<ModuleAudio>, List<File>> {
        val files = mutableListOf<File>()
         val moduleAudio = mutableListOf<ModuleAudio>()
        for ((index, content) in contentList.withIndex()) {
            val file = File(outputDir, "audio_$index.wav")
            val audio = ModuleAudio(
                courseId = content.courseId,
                moduleId = content.moduleId,
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

    suspend fun convertTextToWavRemote(apiKey: String, text: String): NetworkResult<TextToSpeechResponse> {
        val request = TextToSpeechRequest(
            input = Input(text),
            voice = Voice(languageCode = "en-US", name = "en-US-Journey-F"),
            audioConfig = AudioConfig(audioEncoding = "LINEAR16")
        )

        val response = makeRequestToApi {
            ttsApiService.synthesizeSpeech(request, apiKey)
        }
        return response
    }
}