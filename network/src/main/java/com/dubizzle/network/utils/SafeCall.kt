package com.dubizzle.network.utils

import com.dubizzle.network.models.ErrorResponse
import com.dubizzle.network.models.NetworkException
import com.dubizzle.network.models.NetworkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException


/**
 * Handles API calls and wraps the result in a [NetworkResult] object.
 *
 * This function takes a suspending lambda [execute] that represents the actual API call.
 * It executes the call and handles potential exceptions, returning a [NetworkResult]
 * based on the outcome.
 *
 * @param T The type of the data expected from the API response.
 * @param execute A suspending lambda that performs the API call and returns a [Response] object.
 * @return A [NetworkResult] object representing the outcome of the API call.
 *         - [NetworkResult.Success] if the call was successful and a body was received.
 *         - [NetworkResult.Error] if the call failed due to a network error, providing the HTTP status code, message, and potential error body.
 *         - [NetworkResult.Exception] if an unexpected exception occurred during the call.
 */
suspend fun <T : Any> handleApi(
    execute: suspend () -> Response<T?>
): NetworkResult<T> = withContext(Dispatchers.IO) {
    runCatching {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful) {
            NetworkResult.Success(body)
        } else {
            httpException<T>(response)
        }
    }.getOrElse { exception ->
        when (exception) {
            is SocketTimeoutException, is SocketException, is SSLException -> {
                NetworkResult.Error<T>(NetworkException.NoInternet(message = exception.message.orEmpty()))
            }

            is UnknownHostException -> {
                NetworkResult.Error<T>(NetworkException.UnknownHostException(message = exception.message.orEmpty()))
            }

            is IOException -> {
                NetworkResult.Error<T>(NetworkException.IOException(message = exception.message.orEmpty()))
            }

            is IllegalStateException -> {
                NetworkResult.Error<T>(NetworkException.IllegalStateException(message = exception.message.orEmpty()))
            }

            else -> NetworkResult.Error(
                NetworkException.DefaultException(
                    message = exception.message.orEmpty(), cause = exception.cause
                )
            )
        }
    }
}

/**
 * Handles HTTP exceptions and converts them into a [NetworkResult.Error] object.
 *
 * This function takes a [Response] object and attempts to parse the error body into an [ErrorResponse] object.
 * If parsing is successful, it creates a [NetworkResult.Error] with an [ApiException] containing the HTTP status code,
 * the raw error string, and the parsed [ErrorResponse]. If parsing fails, it still creates a [NetworkResult.Error]
 * with an [ApiException] but uses an empty [ErrorResponse].
 *
 * @param T The type of the expected response data.
 * @param response The [Response] object representing the HTTP response.
 * @return A [NetworkResult.Error] object containing information about the HTTP exception.
 */
private fun <T : Any> CoroutineScope.httpException(response: Response<T?>): NetworkResult.Error<T> {
    val rawErrorString = response.errorBody()?.string() ?: response.message()
    val errorBody: ErrorResponse? = rawErrorString.let { ch ->
        runCatching {
            Json.decodeFromString<ErrorResponse>(ch.toString())
        }.onFailure { exception ->
            exception.printStackTrace()
        }.getOrNull()
    }
    return NetworkResult.Error(
        NetworkException.ApiException(
            response.code(), rawErrorString.orEmpty(), errorBody
        )
    )
}