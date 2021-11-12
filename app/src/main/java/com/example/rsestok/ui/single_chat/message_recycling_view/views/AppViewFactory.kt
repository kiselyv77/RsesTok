
package com.example.rsestok.ui.single_chat.message_recycling_view.views

import com.example.rsestok.models.MessageModel
import com.example.rsestok.utilits.TYPE_MESSAGE_VIDEO
import com.example.rsestok.utilits.TYPE_MESSAGE_VOICE

class AppViewFactory {
    companion object{
        fun getView(message: MessageModel):MessageView{
            return when (message.type) {
                TYPE_MESSAGE_VOICE -> ViewVoiceMessage(message.id, message.from, message.timeStamp.toString(), message.fileUrl, message.text, message.userVideoId)
                TYPE_MESSAGE_VIDEO -> ViewVideoMessage(message.id, message.from, message.timeStamp.toString(), message.fileUrl, message.text, message.userVideoId)
                else -> ViewTextMessage(message.id, message.from, message.timeStamp.toString(), message.fileUrl, message.text, message.userVideoId)
            }

        }
    }
}