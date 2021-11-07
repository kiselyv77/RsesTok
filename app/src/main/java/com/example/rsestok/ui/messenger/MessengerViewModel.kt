package com.example.rsestok.ui.messenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MessengerViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Messenger"
    }
    val text: LiveData<String> = _text
}