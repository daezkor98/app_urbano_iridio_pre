package com.urbanoexpress.iridio3.pe.presenter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbanoexpress.iridio3.urbanocore.extentions.logException

import kotlinx.coroutines.*

/**
 * Created by Brandon Quintanilla on March/01/2022.
 */

typealias RunOnThreadWithScope = suspend CoroutineScope.() -> Unit

open class BaseViewModel : ViewModel() {

    val isLoadingLD = MutableLiveData<Boolean>()
    val exceptionLD = MutableLiveData<Throwable>()

    fun executeIO(runOnThreadWithScope: RunOnThreadWithScope): Job {
        return viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    isLoadingLD.postValue(true)
                    runOnThreadWithScope(this)
                } catch (ex: Throwable) {
                    ex.logException("executeIO")
                    exceptionLD.postValue(ex)
                } finally {
                    isLoadingLD.postValue(false)
                }
            }
        }
    }
}