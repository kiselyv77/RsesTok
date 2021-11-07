package com.example.rsestok.models

data class MessageModel(
    var id:String = "",
    var text:String = "",
    var type:String= "",
    var from: String = "",
    var timeStamp: Any = "",
    var fileUrl:String = "emphy"
)
