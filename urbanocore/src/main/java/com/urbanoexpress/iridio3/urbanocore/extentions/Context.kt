package com.urbanoexpress.iridio3.urbanocore.extentions

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns


/**
 * Created by Brandon Quintanilla on April/28/2022.
 */
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