package com.urbanoexpress.iridio3.urbanocore

import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * Created by Brandon Quintanilla on April/28/2022.
 * There comes (old java - mvvm) compatibility code
 */

/**
 *   Replacing old callbacks with suspend functions
 **/
suspend fun <T> secureSuspendCoroutine(block: (Continuation<T>) -> Unit) =
    suspendCoroutine<T> { continuation ->
        try {
            block(continuation)
        } catch (t: Throwable) {
            continuation.resumeWithException(t)
        }
    }
