package com.grandfatherpikhto.frequency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ModulationModel: ViewModel() {
    companion object {
        const val TAG:String = "ModulationModel"
    }

    private val sharedEnable = MutableLiveData(false)
    val enable:LiveData<Boolean>
        get() = sharedEnable

    private val sharedFrequency = MutableLiveData(20)
    val frequency:LiveData<Int>
        get() = sharedFrequency

    fun changeEnable(value:Boolean) {
        sharedEnable.postValue(value)
    }

    fun changeFrequency(value: Int) {
        sharedFrequency.postValue(value)
    }
}