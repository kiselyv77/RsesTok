
package com.example.rsestok.ui.coments_chat.coments_recycling_view.views

interface ComentView {
    val id:String
    val from: String
    val timeStamp:String
    val fileUrl:String
    val text:String
    val userVideoId:String
    val type:String
    var videoURI:String

    companion object{
        val COMENT_IMAGE:Int
            get() = 0
        val COMENT_TEXT:Int
            get() = 1
        val COMENT_VOICE:Int
            get() = 2
        val COMENT_FILE:Int
            get() = 3
        val COMENT_VIDEO:Int
            get() = 4
    }

    fun getTypeView():Int
}