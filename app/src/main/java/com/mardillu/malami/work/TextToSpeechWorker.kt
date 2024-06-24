package com.mardillu.malami.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.mardillu.malami.data.PreferencesManager
import com.mardillu.malami.data.model.TextToSpeechResponse
import com.mardillu.malami.data.model.course.ModuleAudio
import com.mardillu.malami.data.repository.AudioRepository
import com.mardillu.malami.network.NetworkResult
import com.mardillu.malami.utils.NotificationConstants.TTS_CHANNEL_ID
import com.mardillu.malami.utils.NotificationConstants.TTS_NOTIFICATION_ID
import com.mardillu.malami.utils.add
import com.mardillu.malami.utils.createNotification
import com.mardillu.malami.utils.sanitiseForTts
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Created on 17/06/2024 at 1:25 pm
 * @author mardillu
 */
@HiltWorker
class TextToSpeechWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val audioRepositoryImpl: AudioRepository,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(context, workerParams) {


    override suspend fun doWork(): Result {
        val contentJson = workerParams.inputData.getString("content") ?: return Result.failure()
        val apiKey = workerParams.inputData.getString("key") ?: return Result.failure()

        val moduleContent: ModuleAudio = Gson().fromJson(contentJson, ModuleAudio::class.java)
        return try {
            val moduleSequence = "Module ${moduleContent.sequence}"
            val moduleTitle = moduleContent.moduleTitle
            val sanitizedModuleContent = moduleContent.moduleContent.sanitiseForTts()
            val moduleDescription = moduleContent.moduleDescription

            val contentForTts = "$moduleSequence. $moduleTitle. $moduleDescription. $sanitizedModuleContent"

            val audioContent = synthesizeText(apiKey, contentForTts)
            if (audioContent is NetworkResult.Success) {
                saveAudioContent(moduleContent, audioContent.data!!.audioContent)
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun synthesizeText(apiKey: String, text: String): NetworkResult<TextToSpeechResponse> {
        return audioRepositoryImpl.convertTextToWavRemote(apiKey, text)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun saveAudioContent(moduleContent: ModuleAudio, audioContent: String) {
        val existingAudios = preferencesManager.savedCourseAudios
        val audioFile = File(context.filesDir.absolutePath + "/${moduleContent.courseId}", "audio_audio_${moduleContent.moduleId}.wav")
        if (!audioFile.exists()){
            audioFile.parentFile?.mkdirs();
            audioFile.createNewFile()
        }

        val updatedAudio = existingAudios.add(moduleContent)
        preferencesManager.savedCourseAudios = updatedAudio
        val audioBytes = Base64.decode(audioContent,)
        audioFile.writeBytes(audioBytes)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val contentJson = workerParams.inputData.getString("content")
        var moduleTitle = "modules"
        var courseTitle = "courses"
        contentJson?.let {
            val moduleContent: ModuleAudio = Gson().fromJson(it, ModuleAudio::class.java)
            moduleTitle = moduleContent.moduleTitle
            courseTitle = moduleContent.courseTitle
        }
        return ForegroundInfo(TTS_NOTIFICATION_ID, context.createNotification(
            TTS_CHANNEL_ID,
            "Synthesizing Audios",
            "Synthesizing audios for module: $moduleTitle",
            "Synthesizing audios for module: $moduleTitle in course: $courseTitle"
        ))
    }
}
