package com.example.geofancingapplication.ui.addgeofence.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Step2ViewModel : ViewModel() {

    private val _nextButtonEnabled = MutableStateFlow(false)
    val nextButtonEnable: StateFlow<Boolean> get() = _nextButtonEnabled

    private val _internetAvailable = MutableLiveData(false)
    val internetAvailable: LiveData<Boolean> get() = _internetAvailable

    fun enabledNextButton(enable: Boolean) {
        _nextButtonEnabled.value = enable
    }

    fun setInternetAvailable(online: Boolean) {
        _internetAvailable.value = online
    }
}