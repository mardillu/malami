package com.mardillu.malami.data.model.course

import com.google.ai.client.generativeai.type.Candidate
import com.google.ai.client.generativeai.type.CitationMetadata
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.FinishReason
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.Part
import com.google.ai.client.generativeai.type.PromptFeedback
import com.google.ai.client.generativeai.type.SafetyRating
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.ToolConfig
import com.google.ai.client.generativeai.type.UsageMetadata
import kotlinx.serialization.SerialName

/**
 * Created on 06/07/2024 at 1:59 pm
 * @author mardillu
 */
data class GeminiRequest(
    val prompt: String,
    val safety: String,
    val temperature: Double,
    val max_tokens: Int,
    val system_instruction: String
)

data class MlGenerateContentResponse(
    val candidates: List<MlCandidate>,
    val promptFeedback: PromptFeedback?,
    val usageMetadata: UsageMetadata?,
)

data class MlCandidate(
    val content: Content,
    val safetyRatings: List<SafetyRating>,
    val citationMetadata: MlCitationMetadata,
    val finishReason: FinishReason?,
)

data class MlCitationMetadata(
   val citationSources: List<CitationMetadata>
)

data class Choice(
    val text: String,
    val index: Int,
    val logprobs: Any?,
    val finish_reason: String
)

data class PartImpl(
    val text: String,
) : Part

data class GenerateContentRequest(
    val contents: List<Content>,
    @SerialName("safety_settings") val safetySettings: List<SafetySetting>? = null,
    @SerialName("generation_config") val generationConfig: GenerationConfig? = null,
    val tools: List<Tool>? = null,
    @SerialName("tool_config") var toolConfig: ToolConfig? = null,
    @SerialName("system_instruction") val systemInstruction: Content? = null,
)