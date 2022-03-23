package com.urbanoexpress.iridio.urbanocore


/**
 * Created by Brandon Quintanilla on February/28/2022.
 */

fun <T> T?.ifSafe(block: (t: T) -> Unit) = if (this != null) block(this) else this
fun Any?.ifNull(block: () -> Unit) = if (this == null) block().also { return Unit } else this

/**
 * Utils
 * */

/**
*Returns true if there is any item with the given condition
* */
fun <T> List<T>.hasAnyWith(condition: (item: T) -> Boolean): Boolean {

    for (item in this) {
        if (condition(item)) {
            return true
        }
    }

    return false
}

/**
* Returns the count of items with certain condition
* */
fun <T> List<T>.countWith(condition: (item: T) -> Boolean): Int {

    var count = 0

    for (item in this) {
        if (condition(item)) {
            count++
        }
    }

    return count
}