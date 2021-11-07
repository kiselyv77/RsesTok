package com.example.rsestok.utilits

import com.example.rsestok.*

enum class UserStatus(val state:String) {
    ONLINE("в сети"),
    OFFLINE("не в сети"),
    TYPING("печатает...");

    companion object{
        fun updateState(appStates:UserStatus){
            if(AUTH.currentUser != null){
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_STATUS)
                    .setValue(appStates.state)
                    .addOnSuccessListener { USER.status = appStates.state }
                    .addOnFailureListener{ showToast(it.message.toString())}
            }
        }
    }

}