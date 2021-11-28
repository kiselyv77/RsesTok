package com.example.rsestok.ui.single_chat.message_recycling_view.view_holders

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.CURRENT_UID
import com.example.rsestok.NODE_VIDEOS
import com.example.rsestok.R
import com.example.rsestok.REF_DATABASE_ROOT
import com.example.rsestok.databinding.MessageItemVideoBinding
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView
import com.example.rsestok.utilits.*

class HolderVideoMessage(itemMessage: MessageItemVideoBinding): RecyclerView.ViewHolder(itemMessage.root), MessageHolder {
    private val blockUserVideo = itemMessage.blockUserImage
    private val chatUserVideoTime = itemMessage.chatUserTimeImage

    private val blockReceivedVideo = itemMessage.blockReceivedImage
    private val chatReceivedVideoTime = itemMessage.chatReceivedTimeImage

    private val imageUser = itemMessage.userImage
    private val imageReceived = itemMessage.receivedImage


    override fun drawMessage(view: MessageView, messageList: MutableList<MessageView>) {
        val listVideoMessage = messageList.filter {it.type == TYPE_MESSAGE_VIDEO}
        val listUsersUid = arrayListOf<String>()
        val videoUri = view.videoURI
        listVideoMessage.forEach {listUsersUid.add(it.userVideoId)}
        if (view.from == CURRENT_UID) {
            blockUserVideo.visibility = View.VISIBLE
            blockReceivedVideo.visibility = View.GONE
            chatUserVideoTime.text = view.timeStamp.asTime()
            imageUser.downloadAndSetImage(view.fileUrl)
            blockUserVideo.setOnClickListener{
                val bundle:Bundle = Bundle()
                bundle.putInt("position", 2)
                bundle.putStringArrayList("listUsersUid", listUsersUid)
                bundle.putString("videoUri", videoUri)
                APP_NAV_CONTROLLER.navigate(R.id.navigation_video_pager, bundle)
            }
        } else {
            blockReceivedVideo.visibility = View.VISIBLE
            blockUserVideo.visibility = View.GONE
            chatReceivedVideoTime.text = view.timeStamp.asTime()
            imageReceived.downloadAndSetImage(view.fileUrl)
            blockReceivedVideo.setOnClickListener{
                val bundle:Bundle = Bundle()
                bundle.putInt("position", 2)
                bundle.putStringArrayList("listUsersUid", listUsersUid)
                bundle.putString("videoUri", videoUri)
                APP_NAV_CONTROLLER.navigate(R.id.navigation_video_pager, bundle)
            }
        }


    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }

}