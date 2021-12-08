
package com.example.rsestok.ui.coments_chat.coments_recycling_view.views

import com.example.rsestok.models.ComentModel
import com.example.rsestok.models.MessageModel
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView
import com.example.rsestok.ui.single_chat.message_recycling_view.views.ViewVoiceMessage
import com.example.rsestok.utilits.TYPE_COMENT_TEXT
import com.example.rsestok.utilits.TYPE_MESSAGE_VIDEO
import com.example.rsestok.utilits.TYPE_MESSAGE_VOICE

class ComentViewFactory {
    companion object{
        fun getView(message: ComentModel): ComentView {
            return when (message.type) {
                TYPE_COMENT_TEXT -> ViewTextComent(message.id, message.text, message.type, message.from, message.timeStamp.toString(),message.video_id)
                else -> {ViewTextComent(message.id, message.text, message.type, message.from, message.timeStamp.toString(),message.video_id)}
            }

        }
    }
}