package com.urbanoexpress.iridio3.urbanocore


/**
 * Created by Brandon Quintanilla on February/28/2022.
 */
typealias OnItemClick = ((index: Int) -> Unit)?
typealias SimpleEvent = (() -> Unit)
typealias Condition = (() -> Boolean)
typealias ConditionOver<T> = ((T) -> Boolean?)
typealias DataEvent<T> = ((T) -> Unit)
typealias ThrowableEvent = ((Throwable) -> Unit)

typealias DataCompletion = ((bytes: ByteArray, filename: String) -> Unit)