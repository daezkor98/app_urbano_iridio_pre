package com.urbanoexpress.iridio3.pe.util.Exception


/**
 * Created by Brandon Quintanilla on Marzo/24/2025.
 */
class BaseException(
    val errorCode: String = "UNKNOWN",
    message: String = "Un error ha ocurrido",
    cause: Throwable? = null
) : Exception(message, cause) {

    override fun toString(): String {
        return "Error Code: $errorCode, Message: $message, Cause: ${cause?.message}"
    }
}