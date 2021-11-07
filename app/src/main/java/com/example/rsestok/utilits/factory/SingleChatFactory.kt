package com.example.rsestok.utilits.factory

import SingleChatViewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SingleChatFactory(val application: Application, val uid:String):
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SingleChatViewModel(application, uid) as T
    }

}