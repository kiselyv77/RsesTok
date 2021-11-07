package com.example.rsestok.models

data class ChatModel (
    var id:String = "",
    var type:String = "",
    var lastMessage:String = "",
    var fullname:String= "",
    var username: String = "",
    var status: String = "",
    var profilePhotoUri:String = "emphy",
)