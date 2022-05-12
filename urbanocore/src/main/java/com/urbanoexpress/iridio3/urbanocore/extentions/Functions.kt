package com.urbanoexpress.iridio3.urbanocore.extentions

import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Brandon Quintanilla on March/21/2022.
 */

/*Dates*/
const val DATE_FORMAT = "dd/MM/yyyy"

fun getCurrentDay(): String {
    val date = Calendar.getInstance().time
    val df = getDateFormatter().format(date)
    return df.toString()
}

fun getDateFormatter(): SimpleDateFormat {
    return SimpleDateFormat(DATE_FORMAT, Locale.US)
}

fun secureFunc(exceptionLD: MutableLiveData<Throwable>? = null, action: () -> Unit) {
    try {
        action.invoke()
    } catch (e: Exception) {
        e.printStackTrace()
        exceptionLD?.postValue(e)
        Log.e("secureFunc", "secureFunc: " + e.message, e)
    }
}

//Pasar a utils
fun Throwable.logException(whereTAG: String = "TAG") {
    Log.e(whereTAG, "logException: " + this.message, this)
    this.printStackTrace()
}