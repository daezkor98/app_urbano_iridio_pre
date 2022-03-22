package com.urbanoexpress.iridio.urbanocore.values

import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Brandon Quintanilla on March/21/2022.
 */


//TODO reformat annotation
private fun generateImageName(prefix: String): String {
/*    val timeStamp =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
    var photoName = "Imagen_$timeStamp.jpg"
    if (!prefix.isEmpty()) {
        photoName = prefix + "_" + timeStamp + ".jpg"
    }
    return photoName*/
    return ""
}

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