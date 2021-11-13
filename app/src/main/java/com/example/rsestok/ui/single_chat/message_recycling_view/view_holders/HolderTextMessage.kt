
package com.example.rsestok.ui.single_chat.message_recycling_view.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.CURRENT_UID
import com.example.rsestok.databinding.MessageItemTextBinding
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView
import com.example.rsestok.utilits.asTime


class HolderTextMessage(itemMessage: MessageItemTextBinding) : RecyclerView.ViewHolder(itemMessage.root), MessageHolder {
    private val blockUserMessage = itemMessage.blockUserMessage
    private val chatUserMessage = itemMessage.chatUserMessage
    private val chatUserMessageTime = itemMessage.chatUserTime

    private val blockReceivedMessage = itemMessage.blockReceivedMessage
    private val chatReceivedMessage = itemMessage.chatReceivedMessage
    private val chatReceivedMessageTime = itemMessage.chatReceivedTime


    override fun drawMessage(view: MessageView, messageList: MutableList<MessageView>) {
        if (view.from == CURRENT_UID) {
            blockUserMessage.visibility = View.VISIBLE
            blockReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text = view.timeStamp.toString().asTime()
        } else {
            blockReceivedMessage.visibility = View.VISIBLE
            blockUserMessage.visibility = View.GONE
            chatReceivedMessage.text = view.text
            chatReceivedMessageTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }


}