package com.example.rsestok.ui.coments_chat.coments_recycling_view.view_holders

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.*
import com.example.rsestok.databinding.ComentItemTextBinding
import com.example.rsestok.databinding.MessageItemTextBinding
import com.example.rsestok.ui.coments_chat.coments_recycling_view.views.ComentView
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView
import com.example.rsestok.utilits.*
import com.example.rsestok.utilits.app_listeners.AppValueEventListener
import com.google.firebase.database.DatabaseReference

class HolderComentText(itemMessage: ComentItemTextBinding) : RecyclerView.ViewHolder(itemMessage.root), ComentHolder {
    private val blockUserMessage = itemMessage.blockUserMessage
    private val chatUserMessage = itemMessage.chatUserMessage
    private val chatUserMessageTime = itemMessage.chatUserTime

    private val blockReceivedMessage = itemMessage.blockReceivedMessage
    private val chatReceivedMessage = itemMessage.chatReceivedMessage
    private val chatReceivedMessageTime = itemMessage.chatReceivedTime
    private val profileImageReceived = itemMessage.profileImageComent
    private val btnLikeUser = itemMessage.btnLikeUser
    private val btnLikeReceived = itemMessage.btnLikeReceived

    private val textCountLikesUser = itemMessage.textCountLikesUser
    private val textCountLikesReceived = itemMessage.textCountLikesReceived

    private lateinit var likeListenerUser:AppValueEventListener
    private lateinit var likeListenerReceived:AppValueEventListener
    private lateinit var refComents:DatabaseReference



    override fun drawMessage(view: ComentView, messageList: MutableList<ComentView>) {
        refComents = REF_DATABASE_ROOT.child(NODE_COMENTS).child(NIDE_LIKES).child(view.id)
        if (view.from == CURRENT_UID) {
            blockUserMessage.visibility = View.VISIBLE
            blockReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text = view.timeStamp.asTime()
            val likeListenerUser = AppValueEventListener {
                val listLikes = it.children.map{it.getStringList()}
                if(listLikes.contains(CURRENT_UID)){
                    btnLikeUser.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_like_on_coments))
                    textCountLikesUser.setTextColor(APP_ACTIVITY.getColor(R.color.color_like))
                }
                else{
                    btnLikeUser.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_like_coments))
                    textCountLikesUser.setTextColor(APP_ACTIVITY.getColor(R.color.white))

                }
                btnLikeUser.setOnClickListener {
                    if(listLikes.contains(CURRENT_UID)){
                        remuveLikeComent(view.id)
                        btnLikeUser.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_like_coments))
                        textCountLikesUser.setTextColor(APP_ACTIVITY.getColor(R.color.white))
                    }
                    else{
                        likeComent(view.id)
                        btnLikeUser.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_like_on_coments))
                        textCountLikesUser.setTextColor(APP_ACTIVITY.getColor(R.color.color_like))
                    }
                }
                textCountLikesUser.text = listLikes.size.toString()
            }
            refComents.addValueEventListener(likeListenerUser)

        } else {
            blockReceivedMessage.visibility = View.VISIBLE
            blockUserMessage.visibility = View.GONE
            chatReceivedMessage.text = view.text
            chatReceivedMessageTime.text = view.timeStamp.asTime()
            val likeListenerReceived = AppValueEventListener {
                val listLikes = it.children.map{it.getStringList()}
                if(listLikes.contains(CURRENT_UID)){
                    btnLikeReceived.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_like_on_coments))
                    textCountLikesReceived.setTextColor(APP_ACTIVITY.getColor(R.color.color_like))
                }
                else{
                    btnLikeReceived.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_like_coments))
                    textCountLikesReceived.setTextColor(APP_ACTIVITY.getColor(R.color.white))
                }
                btnLikeReceived.setOnClickListener {
                    if(listLikes.contains(CURRENT_UID)){
                        remuveLikeComent(view.id)
                        btnLikeReceived.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_like_coments))
                        textCountLikesReceived.setTextColor(APP_ACTIVITY.getColor(R.color.white))

                    }
                    else{
                        likeComent(view.id)
                        btnLikeReceived.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_like_on_coments))
                        textCountLikesReceived.setTextColor(APP_ACTIVITY.getColor(R.color.color_like))
                    }
                }
                textCountLikesReceived.text = listLikes.size.toString()

            }
            refComents.addValueEventListener(likeListenerReceived)
            profileImageReceived.setOnClickListener {
                    val bundle: Bundle = Bundle()
                    bundle.putString("uid", view.from)
                    if(view.from == CURRENT_UID){
                        APP_NAV_CONTROLLER.navigate(R.id.navigation_profile, bundle)
                    }
                    else{
                        APP_NAV_CONTROLLER.navigate(R.id.navigation_user_profile, bundle)
                    }
            }
            val refPhoto = REF_DATABASE_ROOT.child(NODE_USERS).child(view.from)
            val photoListener = AppValueEventListener {
                profileImageReceived.downloadAndSetImage(it.getUserModel().profilePhotoUri)
            }
            refPhoto.addListenerForSingleValueEvent(photoListener)
        }
    }

    override fun onAttach(view: ComentView) {


    }

    override fun onDetach(view: ComentView) {
//        if (view.from == CURRENT_UID){
//            refComents.removeEventListener(likeListenerUser)
//        }
//        else{
//            refComents.removeEventListener(likeListenerReceived)
//        }


    }


}