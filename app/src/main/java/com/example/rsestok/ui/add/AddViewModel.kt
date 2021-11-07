package com.example.rsestok.ui.add

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.media.AppVideoPlayer

class AddViewModel : ViewModel() {
    private val videoPlayer = AppVideoPlayer()

    private val _uri = MutableLiveData<String>().apply {
        value = "Add"
    }
    val uri: LiveData<String> = _uri



}