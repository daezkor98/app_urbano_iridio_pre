package com.urbanoexpress.iridio.urbanocore

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
        Log.e("secureFunc", "secureFunc: "+e.message, e)
    }
}

//Pasar a utils
fun Throwable.logException(TAG: String = "TAG") {
    Log.e(TAG, "logException: " + this.message, this)
    this.printStackTrace()
}

/*URI*/
fun Context.readFileBytes(uri: Uri): ByteArray {
    return this.applicationContext.contentResolver.openInputStream(uri).use { pdfStream ->
        pdfStream?.readBytes() ?: ByteArray(0)
    }
}

fun Context.getFileName(uri: Uri): String? {
    return uri.let { returnUri ->
        this.applicationContext.contentResolver.query(returnUri, null, null, null, null)
    }?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    }
}