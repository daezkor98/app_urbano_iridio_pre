package com.urbanoexpress.iridio3.model.dto

import android.util.Log
import com.google.gson.reflect.TypeToken
import com.urbanoexpress.iridio3.urbanocore.DataEvent
import com.urbanoexpress.iridio3.urbanocore.ST
import com.urbanoexpress.iridio3.urbanocore.ThrowableEvent
import org.json.JSONObject


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
class ResponseOf<DataType> {
    val success: Boolean? = null
    val data: DataType? = null
}

inline fun <reified T> JSONObject.toInstance(): T? {
    return try {
        ST.gson.fromJson<T>(this.toString(), object : TypeToken<T>() {}.type)
    } catch (e: Exception) {
        Log.e("TAG", "toInstance: $this", e)
        null
    }
}

//TODO remove wasSuccessful to AssertSuccess()
fun <T> ResponseOf<T>.wasSuccessful(): ResponseOf<T> {
    if (this.success!!) {
        return this
    } else {
        throw  Exception("Solicitud fallida")
    }
}

//TODO Remove usage and replace for AssertSuccess()
fun <T> ResponseOf<T>.validate(
    ifValid: DataEvent<T?>? = null,
    ifNotValid: ThrowableEvent? = null
): ResponseOf<T> {
    if (this.success!!) {
        ifValid?.invoke(this.data)
    } else {
        ifNotValid?.invoke(Exception("Solicitud fallida"))
    }
    return this
}

fun <T> ResponseOf<T>.assertSuccess(): T? {
    if (this.success!!) {
        return this.data
    } else {
        throw  Exception("Solicitud fallida")
    }
}