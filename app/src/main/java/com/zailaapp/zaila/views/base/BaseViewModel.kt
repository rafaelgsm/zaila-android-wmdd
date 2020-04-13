package com.zailaapp.zaila.views.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.KoinComponent

//KoinComponent -> gives access to the "inject()" extension
open class BaseViewModel : KoinComponent, ViewModel() {

    //Each ViewModel in the application has access to a Coroutine Scope that allows us to
    //run jobs in the background, asynchronously.

    //(SupervisorJob allows other inner jobs to fail without stopping the whole flow)
    protected val bgScope = CoroutineScope(Dispatchers.Default + SupervisorJob())


    //region default mutable data

    /* We also use a default loading and error message variables,
    to be used by all ViewModels: */

    val isLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    //endregion default mutable data

}