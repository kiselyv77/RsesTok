package com.example.rsestok.ui.single_chat.message_recycling_view.view_holders

import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView


interface MessageHolder {
    fun drawMessage(view: MessageView)
    fun onAttach(view:MessageView)
    fun onDetach()
}