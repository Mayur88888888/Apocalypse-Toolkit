package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.sin
import kotlin.random.Random

class AudioGenerator {
    private var activeTrack: AudioTrack? = null
    private var synthJob: Job? = null

    fun stop() {
        synthJob?.cancel()
        synthJob = null
        try {
            activeTrack?.stop()
            activeTrack?.release()
        } catch (_: Exception) {}
        activeTrack = null
    }

    /**
     * Plays synthetic continuous tone or sweeping sound (Siren, Whistle, Dog Deterrent, Ultrasonic, Air Horn, White Noise)
     */
    fun startContinuousTone(
        type: SoundType,
        scope: CoroutineScope,
        pitchFactor: Float = 1.0f
    ) {
        stop()
        synthJob = scope.launch(Dispatchers.Default) {
            val sampleRate = 44100
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(minBufSize, 4096)
            
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            activeTrack = track
            track.play()

            val buffer = ShortArray(bufferSize)
            var phase = 0.0
            var phaseSweep = 0.0

            while (isActive) {
                for (i in buffer.indices) {
                    when (type) {
                        SoundType.SIREN -> {
                            // Sweep 400Hz to 1200Hz
                            phaseSweep += 0.0001
                            val freq = (800.0 + 400.0 * sin(phaseSweep * pitchFactor)).toDouble()
                            phase += 2.0 * Math.PI * freq / sampleRate
                            buffer[i] = (sin(phase) * 28000).toInt().toShort()
                        }
                        SoundType.WHISTLE -> {
                            // High pitch 2800Hz whistle
                            val freq = 2800.0 * pitchFactor
                            phase += 2.0 * Math.PI * freq / sampleRate
                            buffer[i] = (sin(phase) * 26000).toInt().toShort()
                        }
                        SoundType.DOG_DETERRENT -> {
                            // High frequency 18500Hz
                            val freq = 18500.0 * pitchFactor
                            phase += 2.0 * Math.PI * freq / sampleRate
                            buffer[i] = (sin(phase) * 30000).toInt().toShort()
                        }
                        SoundType.ULTRASONIC -> {
                            // 20000Hz tone
                            val freq = 20000.0
                            phase += 2.0 * Math.PI * freq / sampleRate
                            buffer[i] = (sin(phase) * 31000).toInt().toShort()
                        }
                        SoundType.AIR_HORN -> {
                            // Low dual frequency 220Hz + 440Hz square wave harmonics
                            val freq1 = 220.0 * pitchFactor
                            val freq2 = 330.0 * pitchFactor
                            phase += 2.0 * Math.PI * freq1 / sampleRate
                            val val1 = if (sin(phase) > 0) 20000 else -20000
                            val val2 = (sin(2.0 * Math.PI * freq2 * i / sampleRate) * 10000).toInt()
                            buffer[i] = (val1 + val2).coerceIn(-32767, 32767).toShort()
                        }
                        SoundType.WHITE_NOISE -> {
                            // Random noise for sleep / signal masking
                            buffer[i] = (Random.nextInt(-12000, 12000)).toShort()
                        }
                        SoundType.RAIN -> {
                            // Filtered noise with periodic soft drops
                            val noise = Random.nextInt(-10000, 10000)
                            buffer[i] = (noise * 0.4).toInt().toShort()
                        }
                    }
                }
                track.write(buffer, 0, buffer.size)
            }
        }
    }

    /**
     * Plays CPR metronome pulse at specified BPM (100 - 120 bpm)
     */
    fun startCprMetronome(bpm: Int, scope: CoroutineScope, onBeat: () -> Unit) {
        stop()
        synthJob = scope.launch(Dispatchers.Default) {
            val delayMs = (60_000.0 / bpm).toLong()
            while (isActive) {
                playClick()
                withContext(Dispatchers.Main) { onBeat() }
                delay(delayMs)
            }
        }
    }

    private fun playClick() {
        val sampleRate = 44100
        val durationMs = 50
        val numSamples = sampleRate * durationMs / 1000
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        val freq = 1000.0

        for (i in buffer.indices) {
            phase += 2.0 * Math.PI * freq / sampleRate
            val envelope = (1.0 - i.toDouble() / numSamples)
            buffer[i] = (sin(phase) * 28000 * envelope).toInt().toShort()
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()
    }

    /**
     * Plays a single Morse code beep pulse (dit or dah)
     */
    suspend fun playMorseBeep(durationMs: Long, frequencyHz: Double = 800.0) {
        withContext(Dispatchers.Default) {
            val sampleRate = 44100
            val numSamples = (sampleRate * durationMs / 1000).toInt()
            val buffer = ShortArray(numSamples)
            var phase = 0.0

            for (i in buffer.indices) {
                phase += 2.0 * Math.PI * frequencyHz / sampleRate
                // Soft attack and decay to eliminate click artifact
                val attack = if (i < 200) i / 200.0 else if (i > numSamples - 200) (numSamples - i) / 200.0 else 1.0
                buffer[i] = (sin(phase) * 26000 * attack).toInt().toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            delay(durationMs)
            track.stop()
            track.release()
        }
    }

    enum class SoundType {
        SIREN, WHISTLE, DOG_DETERRENT, ULTRASONIC, AIR_HORN, WHITE_NOISE, RAIN
    }
}
