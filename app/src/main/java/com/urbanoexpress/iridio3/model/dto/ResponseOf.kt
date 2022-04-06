package com.urbanoexpress.iridio3.model.dto

import android.util.Log
import com.google.gson.reflect.TypeToken
import com.urbanoexpress.iridio3.urbanocore.ST
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

fun <T> ResponseOf<T>.wasSuccessful(): ResponseOf<T> {
    if (this.success!!) {
        return this
    } else {
        throw  Exception("Solicitud fallida")
    }
}