package com.urbanoexpress.iridio.urbanocore


/**
 * Created by Brandon Quintanilla on February/28/2022.
 */

fun <T> T?.ifSafe(block: (t: T) -> Unit) = if (this != null) block(this) else this
fun Any?.ifNull(block: () -> Unit?) = if (this == null) block().also { return Unit } else this