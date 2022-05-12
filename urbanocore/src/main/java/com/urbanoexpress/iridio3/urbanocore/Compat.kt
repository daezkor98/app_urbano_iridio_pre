package com.urbanoexpress.iridio3.urbanocore

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * Created by Brandon Quintanilla on April/28/2022.
 */


/************** Replacing old callbacks with suspend functions ***********************/
suspend fun <T> secureSuspendCoroutine(block: (ResultHolder<T>) -> Unit) =
    suspendCoroutine<T> { continuation ->
        try {
            val holder = ResultHolder<T>()
            block(holder)
            if (holder.hasException()) {
                continuation.resumeWithException(holder.t!!)
            } else {
                continuation.resume(holder.result!!)
            }
        } catch (t: Throwable) {
            continuation.resumeWithException(t)
        }
    }

class ResultHolder<T> {
    var result: T? = null
    var t: Throwable? = null

    fun hasException(): Boolean {
        return t != null
    }
}

fun <T> ResultHolder<T>.hold(data: T) {
    this.result = data
}

fun <T> ResultHolder<T>.throwException(throwable: Throwable) {
    this.t = throwable
}

/*************************************/