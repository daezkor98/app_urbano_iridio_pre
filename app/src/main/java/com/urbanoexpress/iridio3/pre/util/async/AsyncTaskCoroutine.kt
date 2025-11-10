package com.urbanoexpress.iridio3.pre.util.async


/**
 * Created by Brandon Quintanilla on February/17/2022.
 */

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Custom AsyncTask implementation using GlobalScoped Coroutines
 * */
abstract class AsyncTaskCoroutine<I, O> {
    var result: O? = null
    //private var result: O

    /**
     * Executed using Dispatchers.Main
     * */
    open fun onPreExecute() {}

    /**
     * Executed using Dispatchers.IO
     * */
    abstract fun doInBackground(vararg params: I): O

    /**
     * Executed using Dispatchers.Main
     * */
    open fun onPostExecute(result: O?) {}

    fun <T> execute(vararg input: I) {
        GlobalScope.launch(Dispatchers.Main) {
            onPreExecute()
            callAsync(*input)
        }
    }

    private suspend fun callAsync(vararg input: I) {
        GlobalScope.async(Dispatchers.IO) {
            result = doInBackground(*input)
        }.await()
        GlobalScope.launch(Dispatchers.Main) {
            onPostExecute(result)
        }
    }
}