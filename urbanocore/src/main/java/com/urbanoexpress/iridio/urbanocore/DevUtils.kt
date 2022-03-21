package com.urbanoexpress.iridio.urbanocore

import android.util.Log

/**
 * Created by Brandon Quintanilla on March/17/2022.
 */
fun devFunction(action: () -> Unit) {
    //TODO ADD a DEV GRADLE COMPILATION PROPERTIE (IS_DEV_ENABLED) ya DEBUG Siempre existe
    if (!BuildConfig.DEBUG) {
        throw  Exception("Funcion valida sólo en DEBUG")
    } else {
        action.invoke()
    }
}

fun Any?.logJson(tag: String = "TAG") = devFunction {
    ST.gson.toJson(this).longlog(tag)
}

fun String.longlog(tag: String = "#Brandon") = devFunction {
    val maxLogSize = 1000
    this.chunked(maxLogSize).forEach { Log.v(tag, it) }
}

//Pasar a utils
fun Throwable.logException(TAG: String = "TAG") {
    Log.e(TAG, "logException: " + this.message, this)
    this.printStackTrace()
}