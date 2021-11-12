
package com.example.rsestok.ui.single_chat.message_recycling_view.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.databinding.InstagramTimelineItemRecyclerBinding
import com.example.rsestok.databinding.MessageItemTextBinding
import com.example.rsestok.databinding.MessageItemVideoBinding
import com.example.rsestok.databinding.MessageItemVoiceBinding
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView


class AppHolderFactory {
    companion object{
        fun  getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when(viewType){
                MessageView.MESSAGE_VOICE ->  {
                    val itemMessage = MessageItemVoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    HolderVoiceMessage(itemMessage)
                }
                MessageView.MESSAGE_VIDEO -> {
                    val itemMessage = MessageItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    HolderVideoMessage(itemMessage)

                }

                else -> {
                    val itemMessage = MessageItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    HolderTextMessage(itemMessage)
                }
            }
        }
    }
}
