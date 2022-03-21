package com.grandfatherpikhto.frequency

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModulationModel
    @Inject internal constructor(private val savedStateHandle: SavedStateHandle): ViewModel() {
    companion object {
        const val TAG:String = "ModulationModel"
        const val SAVED_FREQUENCY_KEY = "SavedFrequency"
        const val DEFAULT_FREQUENCY = 20
        const val SAVED_ENABLE_KEY = "SavedEnable"
        const val DEFAULT_ENABLE = false
    }

    private val sharedEnable: MutableStateFlow<Boolean>
        = MutableStateFlow(savedStateHandle.get(SAVED_ENABLE_KEY) ?: DEFAULT_ENABLE)
    val enable get() = sharedEnable.asStateFlow()

    private val sharedFrequency: MutableStateFlow<Int>
        = MutableStateFlow(savedStateHandle.get(SAVED_FREQUENCY_KEY) ?: DEFAULT_FREQUENCY)
    val frequency get() = sharedFrequency.asStateFlow()

    fun changeEnable(value:Boolean) {
        Log.e(TAG, "Enable: $value")
        if(enable.value != value) {
            sharedEnable.tryEmit(value)
        }
    }

    fun resetPlay() {
        sharedEnable.value?.let { value ->
            sharedEnable.tryEmit(value.not())
        }
    }

    fun changeFrequency(value: Int) {
        Log.e(TAG, "Frequency: $value")
        if(frequency.value != value) {
            sharedFrequency.tryEmit(value)
        }
    }

    override fun onCleared() {
        savedStateHandle.set(SAVED_FREQUENCY_KEY, frequency.value)
        super.onCleared()
    }

    init {
        Log.e(TAG, "frequency: ${frequency.value}")
        viewModelScope.launch {
            sharedEnable.collect { value ->
                Log.e(TAG, "Enable: $value")
                savedStateHandle.set(SAVED_ENABLE_KEY, value)
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
                savedStateHandle.set(SAVED_FREQUENCY_KEY, frequency.value)
                val freq = savedStateHandle.get(SAVED_FREQUENCY_KEY) ?: DEFAULT_FREQUENCY
                Log.e(TAG, "Frequency: $freq")
            }
        }
    }
}