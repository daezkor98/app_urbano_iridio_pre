package com.urbanoexpress.iridio3.urbanocore


/**
 * Created by Brandon Quintanilla on February/28/2022.
 */

/**Executes the block after null validation
 * @return the safe object useful in concatenation syntax
 * */
fun <T> T?.ifSafe(block: (t: T) -> Unit): T? {
    if (this != null) block(this)
    return this
}

/**
 * Executes the block on null validation
 * @return the safe object useful in concatenation syntax
 * */
fun <T> T?.ifNull(block: () -> Unit): T? {
    if (this == null) block()
    return this
}

/**
 * Uses [ifSafe] extention
 * Ending clause of concatenation syntax
 * */
fun <T> T?.ifNotNull(block: (t: T) -> Unit) {
    this.ifSafe(block)
}
