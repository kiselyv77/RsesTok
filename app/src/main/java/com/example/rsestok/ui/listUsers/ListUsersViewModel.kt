package com.example.rsestok.ui.listUsers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListUsersViewModel : ViewModel() {

    private val _title = MutableLiveData<String>().apply {
        value = "Поиск пользователей"
    }
    val title: LiveData<String> = _title
}