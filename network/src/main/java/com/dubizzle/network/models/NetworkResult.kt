package com.dubizzle.network.models


/**
 * A sealed interface representing the result of a network operation.
 *
 * This interface has two possible implementations:
 * - [Success] indicating a successful network operation with the resulting data.
 * - [Error] indicating a failed network operation with a [NetworkException] detailing the error.
 *
 * @param T The type of data expected from the network operation.
 */
sealed interface NetworkResult<T> {
    data class Success<T>(val data: T?) : NetworkResult<T>
    data class Error<T>(val error: NetworkException) : NetworkResult<T>
}

