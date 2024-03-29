
package com.example.rsestok.ui.single_chat.message_recycling_view.views

interface MessageView {
    val id:String
    val from: String
    val timeStamp:String
    val fileUrl:String
    val text:String
    val userVideoId:String
    val type:String
    var videoURI:String

    companion object{
        val MESSAGE_IMAGE:Int
            get() = 0
        val MESSAGE_TEXT:Int
            get() = 1
        val MESSAGE_VOICE:Int
            get() = 2
        val MESSAGE_FILE:Int
            get() = 3
        val MESSAGE_VIDEO:Int
            get() = 4
    }

    fun getTypeView():Int
}