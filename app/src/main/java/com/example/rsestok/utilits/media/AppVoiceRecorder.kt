package com.example.rsestok.utilits.media

import android.media.MediaRecorder
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.showToast
import java.io.File

class AppVoiceRecorder {
    private val mediaRecorder = MediaRecorder()
    private lateinit var file: File
    private lateinit var mMessageKey: String
    fun startRecord(messageKey: String) {
        try {
            mMessageKey = messageKey
            createFileForRecord()
            prepareMediaRecorder()
            mediaRecorder.start()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    private fun prepareMediaRecorder() {
        mediaRecorder.reset()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
        mediaRecorder.setOutputFile(file.absolutePath)
        mediaRecorder.prepare()
    }

    private fun createFileForRecord() {
        file = File(APP_ACTIVITY.filesDir, mMessageKey)
        file.createNewFile()
    }

    fun stopRecord(onSuccess: (file: File, messageKey: String) -> Unit) {
        try {
            mediaRecorder.stop()
            onSuccess(file, mMessageKey)
        } catch (e: Exception) {
            showToast(e.message.toString())
            file.delete()
        }

    }

    fun releaseRecorder() {
        try {
            mediaRecorder.release()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

}