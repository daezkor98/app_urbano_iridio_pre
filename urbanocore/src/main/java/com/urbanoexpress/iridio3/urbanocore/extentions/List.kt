package com.urbanoexpress.iridio3.urbanocore.extentions


/**
 * Created by Brandon Quintanilla on March/29/2022.
 */

fun <T> List<T>.operateOver(toThenThat: (item: T) -> Boolean, operate: T.() -> Unit): List<T> {
    for (i in this.indices) {
        val item = this[i]
        if (toThenThat(item)) {
            item.operate()
        }
    }
    return this
}


/**
 * OBS: The bettwen items usually gets moved
 * @param from index of element to move
 * @param to resultant index
 * */
fun <T> ArrayList<T>.moveItem(from: Int, to: Int) {
    val item = this[from]
    this.removeAt(from)
    this.add(to, item)
}


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