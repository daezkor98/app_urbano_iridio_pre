package com.urbanoexpress.iridio3.pre.model.dto

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
        print("toInstance() $this \n" + e.message)
        throw Exception("Could´nt convert Json")
    }
}

//TODO remove wasSuccessful to AssertSuccess()
fun <T> ResponseOf<T>.wasSuccessful(): ResponseOf<T> {
    if (this.success!!) {
        return this
    } else {
        throw Exception("Solicitud fallida")
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

fun <T> ResponseOf<T>?.assertSuccess(): T? {

    if (this != null) {

        this.success?.let {
            return this.data
        } ?: run {
            throw Exception("Solicitud fallida")
        }

    } else {
        throw Exception("Respuesta nula")
    }
}