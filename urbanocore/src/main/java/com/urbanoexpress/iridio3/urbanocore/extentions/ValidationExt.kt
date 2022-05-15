package com.urbanoexpress.iridio3.urbanocore.extentions

import com.android.volley.NoConnectionError
import java.net.UnknownHostException


/**
 * Created by Brandon Quintanilla on February/28/2022.
 */

/**Executes the block after null validation
 * @return the safe object useful in concatenation syntax
 * */
fun <T> T?.ifSafe(block: (t: T) -> Unit): T? {
    if (this != null) block(this)
    return this
}

/**
 * Executes the block on null validation
 * @return the safe object useful in concatenation syntax
 * */
fun <T> T?.ifNull(block: () -> Unit): T? {
    if (this == null) block()
    return this
}

/**
 * Uses [ifSafe] extention
 * Ending clause of concatenation syntax
 * */
fun <T> T?.ifNotNull(block: (t: T) -> Unit) {
    this.ifSafe(block)
}


fun Throwable.findMessage(): String {
    print("findMessage() > " + this.javaClass.canonicalName)
    print("findMessage() > " + (this is UnknownHostException))
    return when (this) {
        is UnknownHostException, is NoConnectionError -> {
            "Verifique su conexióm a internet"
        }
        else -> {
            this.message ?: "Error, notifique a Holding"
        }
    }
}