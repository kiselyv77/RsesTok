package com.example.rsestok.ui.video_pager_fragment


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoPagerViewModel() : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Video pager"
    }
    val text: LiveData<String> = _text
}