package com.example.rsestok.ui.coments_chat.coments_recycling_view.view_holders

import com.example.rsestok.ui.coments_chat.coments_recycling_view.views.ComentView
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView


interface ComentHolder {
    fun drawMessage(view: ComentView, messageList: MutableList<ComentView> = arrayListOf())
    fun onAttach(view:ComentView)
    fun onDetach()
}