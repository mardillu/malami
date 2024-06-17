package com.mardillu.malami.data.repository

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.io.File
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created on 15/06/2024 at 10:44 pm
 * @author mardillu
 */
class AndroidTextToSpeechService @Inject constructor (context: Context): TextToSpeech.OnInitListener {
    private val textToSpeech: TextToSpeech = TextToSpeech(context, this)

     suspend fun convertTextToWav(text: String, outputFile: File) = suspendCoroutine { cont ->
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                cont.resume(Unit)
            }

            @Deprecated(
                "Deprecated in Java",
                ReplaceWith("cont.resumeWith(Result.failure(Exception(\"Text to Speech Error\")))")
            )
            override fun onError(p0: String?) {
                cont.resumeWith(Result.failure(Exception("Text to Speech Error")))
            }

            override fun onError(utteranceId: String, errorCode: Int) {
                cont.resumeWith(Result.failure(Exception("Text to Speech Error")))
            }
        })
        val bundle = Bundle()
        bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "tts")

        textToSpeech.synthesizeToFile(text, bundle, outputFile, "tts")
    }

    override fun onInit(i: Int) {
        if (i == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.US
        }
    }
}