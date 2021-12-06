
package com.example.rsestok.ui.coments_chat.coments_recycling_view.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.databinding.*
import com.example.rsestok.ui.coments_chat.coments_recycling_view.views.ComentView
import com.example.rsestok.ui.single_chat.message_recycling_view.view_holders.HolderTextMessage
import com.example.rsestok.ui.single_chat.message_recycling_view.view_holders.HolderVoiceMessage
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView


class ComentHolderFactory {
    companion object{
        fun  getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when(viewType){
                ComentView.COMENT_TEXT ->  {
                    val itemComent = ComentItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    HolderComentText(itemComent)
                }
                else -> {
                    val itemComent = ComentItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    HolderComentText(itemComent)
                }

            }
        }
    }
}
