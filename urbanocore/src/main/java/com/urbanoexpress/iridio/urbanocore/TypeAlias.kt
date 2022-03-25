package com.urbanoexpress.iridio.urbanocore


/**
 * Created by Brandon Quintanilla on February/28/2022.
 */
typealias SimpleCompletion = (() -> Unit)
typealias OnItemClick = ((index:Int) -> Unit)?
typealias SimpleEvent = (() -> Unit)

typealias DataCompletion = ((bytes:ByteArray, filename:String) -> Unit)
