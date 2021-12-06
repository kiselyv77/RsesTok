package com.example.rsestok.ui.coments_chat.coments_recycling_view.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.CURRENT_UID
import com.example.rsestok.NODE_USERS
import com.example.rsestok.REF_DATABASE_ROOT
import com.example.rsestok.databinding.ComentItemTextBinding
import com.example.rsestok.databinding.MessageItemTextBinding
import com.example.rsestok.getUserModel
import com.example.rsestok.ui.coments_chat.coments_recycling_view.views.ComentView
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView
import com.example.rsestok.utilits.app_listeners.AppValueEventListener
import com.example.rsestok.utilits.asTime
import com.example.rsestok.utilits.downloadAndSetImage
import com.example.rsestok.utilits.showToast

class HolderComentText(itemMessage: ComentItemTextBinding) : RecyclerView.ViewHolder(itemMessage.root), ComentHolder {
    private val blockUserMessage = itemMessage.blockUserMessage
    private val chatUserMessage = itemMessage.chatUserMessage
    private val chatUserMessageTime = itemMessage.chatUserTime

    private val blockReceivedMessage = itemMessage.blockReceivedMessage
    private val chatReceivedMessage = itemMessage.chatReceivedMessage
    private val chatReceivedMessageTime = itemMessage.chatReceivedTime
    private val profileImageReceived = itemMessage.profileImageComent


    override fun drawMessage(view: ComentView, messageList: MutableList<ComentView>) {
        if (view.from == CURRENT_UID) {
            blockUserMessage.visibility = View.VISIBLE
            blockReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text = view.timeStamp.asTime()
        } else {
            blockReceivedMessage.visibility = View.VISIBLE
            blockUserMessage.visibility = View.GONE
            chatReceivedMessage.text = view.text
            chatReceivedMessageTime.text = view.timeStamp.asTime()
            val refPhoto = REF_DATABASE_ROOT.child(NODE_USERS).child(view.from)
            val photoListener = AppValueEventListener {
                profileImageReceived.downloadAndSetImage(it.getUserModel().profilePhotoUri)

            }
            refPhoto.addListenerForSingleValueEvent(photoListener)
        }
    }

    override fun onAttach(view: ComentView) {


    }

    override fun onDetach() {

    }


}