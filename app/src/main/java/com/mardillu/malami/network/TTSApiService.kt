package com.mardillu.malami.network

import com.mardillu.malami.BuildConfig
import com.mardillu.malami.data.PreferencesManager
import com.mardillu.malami.data.model.TextToSpeechRequest
import com.mardillu.malami.data.model.TextToSpeechResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Created on 17/06/2024 at 12:54 pm
 * @author mardillu
 */
interface TTSApiService {
    @POST("v1beta1/text:synthesize")
    suspend fun synthesizeSpeech(
        @Body request: TextToSpeechRequest,
        @Query("key") apiKey: String
    ): TextToSpeechResponse

    companion object {

        private const val BASE_URL = "https://texttospeech.googleapis.com/"

        operator fun invoke(
            prefManager: PreferencesManager,
        ): TTSApiService {
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

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
                .create(TTSApiService::class.java)
        }
    }
}
