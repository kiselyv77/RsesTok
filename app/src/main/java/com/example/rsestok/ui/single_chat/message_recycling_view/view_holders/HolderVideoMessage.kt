package com.example.rsestok.ui.single_chat.message_recycling_view.view_holders

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.CURRENT_UID
import com.example.rsestok.R
import com.example.rsestok.databinding.InstagramTimelineItemRecyclerBinding
import com.example.rsestok.databinding.MessageItemVideoBinding
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView
import com.example.rsestok.utilits.APP_NAV_CONTROLLER
import com.example.rsestok.utilits.asTime
import com.example.rsestok.utilits.downloadAndSetImage
import com.example.rsestok.utilits.showToast

class HolderVideoMessage(itemMessage: MessageItemVideoBinding): RecyclerView.ViewHolder(itemMessage.root), MessageHolder {
    private val blockUserVideo = itemMessage.blockUserImage
    private val chatUserVideoTime = itemMessage.chatUserTimeImage

    private val blockReceivedVideo = itemMessage.blockReceivedImage
    private val chatReceivedVideoTime = itemMessage.chatReceivedTimeImage

    private val imageUser = itemMessage.userImage
    private val imageReceived = itemMessage.receivedImage


    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockUserVideo.visibility = View.VISIBLE
            blockReceivedVideo.visibility = View.GONE
            chatUserVideoTime.text = view.timeStamp.asTime()
            imageUser.downloadAndSetImage(view.fileUrl)
            blockUserVideo.setOnClickListener{
                val bundle:Bundle = Bundle()
                bundle.putString("uid",view.userVideoId)
                bundle.putInt("position", 0)
                APP_NAV_CONTROLLER.navigate(R.id.navigation_video_pager, bundle)
            }
        } else {
            blockReceivedVideo.visibility = View.VISIBLE
            blockUserVideo.visibility = View.GONE
            chatReceivedVideoTime.text = view.timeStamp.asTime()
            imageReceived.downloadAndSetImage(view.fileUrl)
            blockReceivedVideo.setOnClickListener{
                val bundle:Bundle = Bundle()
                bundle.putString("uid",view.userVideoId)
                bundle.putInt("position", 0)
                APP_NAV_CONTROLLER.navigate(R.id.navigation_video_pager, bundle)
            }
        }


    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }

}