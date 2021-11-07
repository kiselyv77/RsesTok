package com.example.rsestok.ui.profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rsestok.*
import com.example.rsestok.models.UserModel
import com.example.rsestok.utilits.app_listeners.AppValueEventListener

class ProfileViewModel(val application: Application, val uid:String) : ViewModel() {

    private val _fullName = MutableLiveData<String>()
    var fullName: LiveData<String> = _fullName

    private val _username = MutableLiveData<String>()
    var username: LiveData<String> = _username

    private val _listSubscribers = MutableLiveData<ArrayList<String>>()
    var listSubscribers: LiveData<ArrayList<String>> = _listSubscribers

    private val _listSubscriptions = MutableLiveData<ArrayList<String>>()
    var listSubscriptions: LiveData<ArrayList<String>> = _listSubscriptions

    private val _listLikes = MutableLiveData<ArrayList<String>>()
    var listLikes: LiveData<ArrayList<String>> = _listLikes

    private val _description = MutableLiveData<String>()
    var description: LiveData<String> = _description

    private val _profileImage = MutableLiveData<String>()
    var profileImage: LiveData<String> = _profileImage

    init {
        REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
            .addValueEventListener(AppValueEventListener {
                USER = it.getValue(UserModel::class.java) ?: UserModel()
                if (USER.username.isEmpty()) {
                    USER.username = CURRENT_UID
                }
                REF_DATABASE_ROOT.child(NODE_USERS).child(uid).child(CHILD_SUBSCRIPTIONS)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        _listSubscriptions.value = it.children.map{ it.getStringList() } as ArrayList<String>

                })
                REF_DATABASE_ROOT.child(NODE_USERS).child(uid).child(CHILD_SUBSCRIBERS)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        _listSubscribers.value = it.children.map{ it.getStringList() } as ArrayList<String>
                })

                REF_DATABASE_ROOT.child(NODE_USERS).child(uid).child(NIDE_LIKES)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        _listLikes.value = it.children.map{ it.getStringList() } as ArrayList<String>
                    })

                _fullName.value = USER.fullname
                _username.value = USER.username

                _description.value = USER.description
                _profileImage.value = USER.profilePhotoUri
            })
    }
}