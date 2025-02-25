package com.dubizzle.network.models

/**
 * A sealed class representing various network-related exceptions.
 *
 * This class provides a structured way to handle different types of network errors,
 * such as API errors, connectivity issues, and unknown exceptions.
 *
 * @property errorCode An integer code representing the type of error.
 * @property message A descriptive message about the error.
 * @property errorResponse An optional [ErrorResponse] object containing detailed error information.
 */
sealed class NetworkException(
    open val errorCode: Int,
    override val message: String,
    open val errorResponse: ErrorResponse? = null
) : Throwable() {
    /**
     * Represents an API exception that occurs during network communication.
     *
     * This exception inherits from [NetworkException] and provides additional information
     * specific to API errors, such as the error code and an optional error response object.
     *
     * @param errorCode The error code returned by the API.
     * @param message A human-readable message describing the error.
     * @param errorResponse An optional [ErrorResponse] object containing detailed error information.
     */
    data class ApiException(
        override val errorCode: Int,
        override val message: String,
        override val errorResponse: ErrorResponse? = null
    ) : NetworkException(errorCode, message, errorResponse)

    /**
     * Represents an exception thrown when there is no internet connection.
     *
     * This exception is typically thrown when a network operation fails due to the
     * device being offline or having no internet access.
     *
     * @property errorCode The error code associated with this exception. Defaults to 2.
     * @property message The error message associated with this exception. Defaults to "Socket Timeout".
     */
    data class NoInternet(
        override val errorCode: Int = 2,
        override val message: String = "Socket Timeout"
    ) : NetworkException(errorCode, message)

    /**
     * Represents an exception that is thrown when a host cannot be resolved to an IP address.
     *
     * This exception is a subclass of [NetworkException] and indicates a problem with network connectivity
     * where the hostname provided could not be found or resolved.
     *
     * @property errorCode The error code associated with the exception, defaults to 8.
     * @property message The error message associated with the exception, defaults to "UnknownHostException".
     */
    data class UnknownHostException(
        override val errorCode: Int = 8,
        override val message: String = "UnknownHostException"
    ) : NetworkException(errorCode, message)

    /**
     * Represents an IOException that occurred during a network operation.
     *
     * This exception is typically thrown when there is an error related to input/output operations,
     * such as reading or writing data to a network stream.
     *
     * @property errorCode The error code associated with the exception. Defaults to 9.
     * @property message A descriptive message about the exception. Defaults to "IOException".
     */
    data class IOException(
        override val errorCode: Int = 9,
        override val message: String = "IOException"
    ) : NetworkException(errorCode, message)

    /**
     * Represents an IllegalStateException that occurs during network operations.
     *
     * This exception is typically thrown when an operation is attempted in an illegal or inappropriate state.
     *
     * @property errorCode The error code associated with this exception, defaults to 11.
     * @property message The error message describing the exception, defaults to "IllegalStateException".
     */
    data class IllegalStateException(
        override val errorCode: Int = 11,
        override val message: String = "IllegalStateException"
    ) : NetworkException(errorCode, message)

    /**
     * A default implementation of [NetworkException] representing an unknown or generic error.
     *
     * This exception is typically used when a specific exception type is not available
     * or when the underlying cause is unknown.
     *
     * @property errorCode The error code associated with the exception. Defaults to 1.
     * @property message A descriptive message about the exception. Defaults to "UnKnown Exception".
     * @property cause The underlying cause of the exception, if available.
     */
    data class DefaultException(
        override val errorCode: Int = 1,
        override val message: String = "UnKnown Exception",
        override val cause: Throwable?
    ) : NetworkException(1, message)
}

