package com.grandfatherpikhto.frequency

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlin.math.PI
import kotlin.math.sin

class PulsePlayer {
    companion object {
        const val TAG:String = "Pulse"
        const val sampleRate = 8000  //
        const val duration    = 1    // Секунды
    }

    private var sample = Array(ToneModulationPlayer.sampleRate) { _ -> 0.0 }
    private var generatedSound:ByteArray = ByteArray(ToneModulationPlayer.sampleRate * ToneModulationPlayer.duration * 2)
    private var isPlay = true
    private var playThread:Thread? = null
    private var audioTrack: AudioTrack? = null
    var envelope = 0.0
        set(value:Double) {
            field = value
            Log.e(TAG, "displacement: $field")
            generateTone()
        }

    var frequency = 20.0
        set(value: Double) {
            field = value
            Log.e(TAG, "frequency: $field")
            generateTone()
        }


    private fun generateTone(step: Int = 0) {
        for(i in 0 until ToneModulationPlayer.sampleRate) {
            val value = sin(2 * PI * (i +  step * sampleRate) * frequency / sampleRate )
            if (value >= 0.0) {
                sample[i] = if(value + envelope >= 1.0) 1.0 else value
            } else {
                sample[i] = if(value - envelope <= -1.0) -1.0 else value
            }
        }

        var idx = 0
        sample.forEach { dVal ->
            val value = (dVal * 32767).toInt()
            generatedSound[idx++] = (value.and(0x00ff)).toByte()
            generatedSound[idx++] = (value.and(0xff00).ushr(8)).toByte()
        }
    }

    fun play() {
        Log.e(TAG, "Play()")
        isPlay = true
        if(playThread == null) {
            playThread = Thread {
                var step = 0
                try {
                    audioTrack = AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(ToneModulationPlayer.sampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                        )
                        .setBufferSizeInBytes(generatedSound.size)
                        .build()
                    while (isPlay) {
                        generateTone(step++)
                        audioTrack?.write(generatedSound, 0, generatedSound.size)
                        audioTrack?.play()
                    }

                    close()
                } catch (e: Exception) {
                    Log.e(ModulationFragment.TAG, "Error: $e")
                }
            }
            playThread?.start()
        }
    }

    private fun close() {
        playThread?.interrupt()
        playThread?.join()
        audioTrack?.stop()
        audioTrack?.release()
        playThread = null
        audioTrack = null
    }

    fun stop() {
        Log.e(TAG, "Stop()")
        isPlay = false
    }
}