package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileOutputStream

class VoiceRecorderHelper(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    var currentOutputFile: File? = null
        private set

    fun startRecording(): File? {
        stopRecording()
        stopPlayback()
        return try {
            val file = File(context.cacheDir, "voice_memo_${System.currentTimeMillis()}.3gp")
            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder.setOutputFile(file.absolutePath)
            recorder.prepare()
            recorder.start()
            mediaRecorder = recorder
            currentOutputFile = file
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun stopRecording(): File? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            currentOutputFile
        } catch (e: Exception) {
            e.printStackTrace()
            mediaRecorder = null
            null
        }
    }

    fun startPlayback(file: File, onCompletion: () -> Unit = {}) {
        stopPlayback()
        try {
            val player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener {
                    stopPlayback()
                    onCompletion()
                }
                start()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
    }

    fun playRawBytes(bytes: ByteArray, onCompletion: () -> Unit = {}) {
        stopPlayback()
        try {
            val tempFile = File.createTempFile("temp_play", ".3gp", context.cacheDir)
            FileOutputStream(tempFile).use { it.write(bytes) }
            startPlayback(tempFile) {
                tempFile.delete()
                onCompletion()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
