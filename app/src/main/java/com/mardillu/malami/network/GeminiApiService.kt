package com.mardillu.malami.network

import com.google.ai.client.generativeai.type.Part
import com.google.ai.client.generativeai.type.TextPart
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.mardillu.malami.BuildConfig
import com.mardillu.malami.data.PreferencesManager
import com.mardillu.malami.data.model.course.GenerateContentRequest
import com.mardillu.malami.data.model.course.MlGenerateContentResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * Created on 17/06/2024 at 12:54 pm
 * @author mardillu
 */
interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-pro-latest:generateContent")
    suspend fun generateCompletion(@Query("key") apiKey: String, @Body request: GenerateContentRequest): Response<MlGenerateContentResponse>

    companion object {

        private const val BASE_URL = "https://generativelanguage.googleapis.com/"

        operator fun invoke(
            prefManager: PreferencesManager,
        ): GeminiApiService {
            val httpClient = OkHttpClient.Builder().apply {

                addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("Accept", "application/json")
                        .build()

                    chain.proceed(request)
                }

                if (BuildConfig.DEBUG) {
                    val interceptor = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }

                    addInterceptor(interceptor)
                }


                writeTimeout(5, TimeUnit.MINUTES)
                readTimeout(5, TimeUnit.MINUTES)
                connectTimeout(5, TimeUnit.MINUTES)
            }
                .build()

            val gson = GsonBuilder()
                .registerTypeAdapter(Part::class.java, PartTypeAdapter())
                .create()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build()
                .create(GeminiApiService::class.java)
        }
    }
}

class PartTypeAdapter : JsonSerializer<Part>, JsonDeserializer<Part> {
    override fun serialize(src: Part, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src, TextPart::class.java)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Part {
        return context.deserialize(json, TextPart::class.java)
    }
}