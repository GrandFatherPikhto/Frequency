package com.grandfatherpikhto.frequency

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlin.math.PI
import kotlin.math.sin

class ToneModulationPlayer {
    companion object {
        const val TAG:String = "ToneModulationPlayer"
        const val sampleRate = 8000  //
        const val duration    = 1    // Секунды
        val modulations:Array<Pair<Int, Int>> = arrayOf(
            Pair(2, 50),
            Pair(3, 51),
            Pair(4, 56),
            Pair(5, 55),
            Pair(6, 54),
            Pair(7, 56),
            Pair(8, 56),
            Pair(9, 54),
            Pair(10, 50),
            Pair(11, 55),
            Pair(12, 60),
            Pair(13, 52),
            Pair(14, 56),
            Pair(15, 60),
            Pair(16, 64),
            Pair(17, 51),
            Pair(18, 54),
            Pair(19, 57),
            Pair(20, 60),
            Pair(21, 63),
            Pair(22, 66),
            Pair(23, 46),
            Pair(24, 48),
            Pair(25, 50),
            Pair(26, 52),
            Pair(27, 54),
            Pair(28, 56),
            Pair(29, 58),
            Pair(30, 60),
            Pair(31, 62),
            Pair(32, 64),
            Pair(33, 66),
            Pair(34, 68),
            Pair(35, 70),
            Pair(36, 72),
            Pair(37, 74),
            Pair(38, 76),
            Pair(39, 78),
            Pair(40, 80),
            Pair(41, 82),
            Pair(42, 84),
            Pair(43, 86),
            Pair(44, 88),
            Pair(45, 90),
            Pair(46, 92),
            Pair(47, 94),
            Pair(48, 96),
            Pair(49, 98),
            Pair(50, 100)
        )
    }

    /**
     * Можно также использовать шаблон observeable
     */
    var frequency:Int = 10
        get() = field
        set(value: Int) {
            Log.e(TAG, "Frequency: field = $field, value = $value")
            if(field != value) {
                field = value
                generateTone()
            }
        }

    var enable:Boolean = false
        get() = field
        set(value: Boolean) {
            Log.e(TAG, "Enable: field = $field, value = $value")
            if(field != value) {
                field = value
                if (value) {
                    play()
                } else {
                    close()
                }
            }
        }

    private var sample = Array(sampleRate) { _ -> 0.0 }
    private var generatedSound:ByteArray = ByteArray(sampleRate * duration * 2)
    private var isPlay = false
    private var playThread:Thread? = null
    private var audioTrack:AudioTrack? = null
    private val enveloped = true

    private fun calcEnvelope(count: Int, step: Int): Double {
        return sin( PI * (count + step * sampleRate) * frequency / sampleRate)
    }

    private fun generateTone(step: Int = 0) {
        // var j = 0
        // Log.e(TAG, "generateTone()")
        for(i in 0 until sampleRate) {
            if(enveloped) {
                val envelope = calcEnvelope(i, step)
                modulations.find { pair -> pair.first == frequency }?.let { pair ->
                    sample[i] = sin(2 * PI * (i + step * sampleRate) * pair.second / sampleRate ) * envelope
                }
            } else {
                sample[i] = sin(8 * PI * i * frequency / sampleRate)
            }

//            if(envelope >= 0) {
//                sample[i] = sin(2 * PI * (i +  step * sampleRate) * modulationTone / sampleRate )
//                // sample[i] = sin(2 * PI * (j++) * modulationTone / sampleRate ) * envelope
//            } else {
//                sample[i] = 0.0
//                j = 0
//            }

        }

        var idx = 0
        sample.forEach { dVal ->
            val value = (dVal * 32767).toInt()
            generatedSound[idx++] = (value.and(0x00ff)).toByte()
            generatedSound[idx++] = (value.and(0xff00).ushr(8)).toByte()
        }
    }

    init {
        Log.e(TAG, "Init")
    }

    private fun play() {
        // Log.e(TAG, "play($isPlay, $playThread)")
        if(!isPlay && playThread == null) {
            playThread = Thread {
                isPlay = true
                var step = 0
                try {
                    audioTrack = AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                    // USAGE_ALARM
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(sampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                        )
                        .setBufferSizeInBytes(generatedSound.size)
                        .build()
                    audioTrack?.setVolume(10.0f)
                    while (isPlay) {
                        // Log.e(TAG, "generateSound $step $frequency")
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
        isPlay = false
        playThread?.interrupt()
        playThread?.join()
        audioTrack?.stop()
        audioTrack?.release()
        playThread = null
        audioTrack = null
    }
}