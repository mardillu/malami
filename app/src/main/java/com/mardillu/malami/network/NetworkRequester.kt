package com.mardillu.malami.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Created on 17/06/2024 at 1:04 pm
 * @author mardillu
 */

sealed class NetworkResult<out T : Any> {

    data class Success<out T : Any>(val data: T?) : NetworkResult<T>()

    data class HttpError(val code: Int?, val message: String) : NetworkResult<Nothing>()

    data class GenericError(val error: Exception) : NetworkResult<Nothing>(){
        override fun toString(): String{
            return "Error message: ${error.message},==> ${error.stackTrace.toList()} ==> ${error.localizedMessage}"
        }
    }

    object NoInternet : NetworkResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is GenericError -> "Error message: ${error.message},==> ${error.stackTrace} ==> ${error.localizedMessage}"
            is HttpError -> "Http Error code: $code, message: $message"
            is Success<*> -> "Success => data: ${Gson().toJson(data)} \t ${data?.javaClass?.name}"
            NoInternet -> "No Internet (Socket Timeout)"
        }
    }
}

suspend fun <T : Any> makeRequestToApi(
    call: suspend () -> T,
): NetworkResult<T> {
    return try {
        val data = withContext(Dispatchers.IO) {
            call.invoke()
        }
        NetworkResult.Success(data)
    } catch (throwable: Exception) {
        return when (throwable) {
            is HttpException -> {
                NetworkResult.HttpError(throwable.code(), parseErrorMessage(throwable))
            }
            is IOException -> {
                NetworkResult.NoInternet
            }
            else -> {
                NetworkResult.GenericError(throwable)
            }
        }
    }
}

private fun parseErrorMessage(httpException: HttpException): String {
    val errorBody = httpException.response()?.errorBody()?.string()

    return try {
        val messageObject = Gson().fromJson(errorBody, JsonObject::class.java)
        messageObject?.get("message")?.asString ?: httpException.message()
    } catch (ex: Exception) {
        httpException.message()
    }
}