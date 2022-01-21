package com.grandfatherpikhto.frequency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PulseModel: ViewModel() {
    private val sharedEnable = MutableLiveData<Boolean>(false)
    val enable:LiveData<Boolean> get() = sharedEnable

    private val sharedFrequency = MutableLiveData<Double>(20.0)
    val frequency:LiveData<Double> get() = sharedFrequency

    private val sharedEnvelope = MutableLiveData<Double>(0.0)
    val envelope:LiveData<Double> get() = sharedEnvelope

    fun changeEnable(value: Boolean) {
        sharedEnable.postValue(value)
    }

    fun changeFrequency(value: Double) {
        sharedFrequency.postValue(value)
    }

    fun changeEnvelope(value:Double) {
        sharedEnvelope.postValue(value)
    }
}