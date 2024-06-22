package com.mardillu.malami.data.model

/**
 * Created on 17/06/2024 at 12:40 pm
 * @author mardillu
 */

data class TextToSpeechRequest(
    val input: Input,
    val voice: Voice,
    val audioConfig: AudioConfig
)

data class Input(val text: String)
data class Voice(val languageCode: String, val name: String)
data class AudioConfig(val audioEncoding: String)

data class TextToSpeechResponse(val audioContent: String)