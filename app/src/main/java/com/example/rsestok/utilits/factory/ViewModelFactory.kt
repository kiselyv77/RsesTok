package com.example.rsestok.utilits.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rsestok.ui.profile.ProfileViewModel

class ViewModelFactory(val application: Application, val uid:String):
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(application, uid) as T
    }

}