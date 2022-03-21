package com.grandfatherpikhto.frequency

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ModulationModel: ViewModel() {
    companion object {
        const val TAG:String = "ModulationModel"
    }

    private val sharedEnable: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val enable get() = sharedEnable.asStateFlow()

    private val sharedFrequency: MutableStateFlow<Int> = MutableStateFlow(20)
    val frequency get() = sharedFrequency.asStateFlow()

    fun changeEnable(value:Boolean) {
        sharedEnable.tryEmit(value)
    }

    fun resetPlay() {
        sharedEnable.value?.let { value ->
            sharedEnable.tryEmit(value.not())
        }
    }

    fun changeFrequency(value: Int) {
        sharedFrequency.tryEmit(value)
    }

    init {
        viewModelScope.launch {
            sharedEnable.collect { value ->
                Log.e(TAG, "Enable: $value")
                if(value) {
                    PlayService.play()
                } else {
                    PlayService.stop()
                }
            }
        }

        viewModelScope.launch {
            sharedFrequency.collect { value ->
                PlayService.frequency = value
            }
        }
    }
}