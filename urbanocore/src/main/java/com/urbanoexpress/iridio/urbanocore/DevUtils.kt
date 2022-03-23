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

fun Any?.logString(tag: String = "TAG") = devFunction {
    this.toString().longlog(tag)
}

fun String.longlog(tag: String = "#Brandon") = devFunction {
    val maxLogSize = 900
    this.chunked(maxLogSize).forEach {segment-> Log.v(tag, "******************************\n$segment") }
}

