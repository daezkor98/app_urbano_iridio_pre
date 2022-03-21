package com.urbanoexpress.iridio.model.dto

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        Gson().fromJson<T>(this.toString(), object : TypeToken<T>() {}.type)//TODO use singleton
    } catch (e: Exception) {
        Log.e("TAG", "toInstance: $this", e)
        null
    }
}
