package com.example.rsestok.ui.single_chat.message_recycling_view.view_holders

import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.CURRENT_UID
import com.example.rsestok.databinding.MessageItemVoiceBinding
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView
import com.example.rsestok.utilits.media.AppVoicePlayer
import com.example.rsestok.utilits.asTime

class HolderVoiceMessage(itemMessage: MessageItemVoiceBinding) : RecyclerView.ViewHolder(itemMessage.root), MessageHolder {

    private val mVoicePlayer =  AppVoicePlayer()
    private val blockUserVoice = itemMessage.blockUserVoice
    private val chatUserVoiceTime = itemMessage.chatUserTimeVoice

    private val blockReceivedVoice = itemMessage.blockReceivedVoice
    private val chatReceivedVoiceTime = itemMessage.chatReceivedTimeVoice

    private val btnPlayUser = itemMessage.btnPlayUser
    private val btnStopUser = itemMessage.btnStopUser

    private val btnPlayReceived = itemMessage.btnPlayReceiving
    private val btnStopReceived = itemMessage.btnStopReceiving

    private val progressBarUser = itemMessage.progressBarUser
    private val progressBarReceived = itemMessage.progressBarReceived




    override fun drawMessage(view: MessageView, messageList: MutableList<MessageView>) {
        if (view.from == CURRENT_UID) {
            blockUserVoice.visibility = View.VISIBLE
            blockReceivedVoice.visibility = View.GONE
            chatUserVoiceTime.text = view.timeStamp.asTime()
        } else {
            blockReceivedVoice.visibility = View.VISIBLE
            blockUserVoice.visibility = View.GONE
            chatReceivedVoiceTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
        mVoicePlayer.init()
        if(view.from == CURRENT_UID)
        {
            btnPlayUser.setOnClickListener{
                btnPlayUser.visibility = View.GONE
                btnStopUser.visibility = View.VISIBLE
                btnStopUser.setOnClickListener{
                    stop {
                        btnStopUser.setOnClickListener(null)
                        btnPlayUser.visibility = View.VISIBLE
                        btnStopUser.visibility = View.GONE
                    }
                }

                play(progressBarUser , view){
                    btnPlayUser.visibility = View.VISIBLE
                    btnStopUser.visibility = View.GONE
                }

            }
        }
        else
        {
            btnPlayReceived.setOnClickListener{
                btnPlayReceived.visibility = View.GONE
                btnStopReceived.visibility = View.VISIBLE
                btnStopReceived.setOnClickListener{
                    stop {
                        btnStopReceived.setOnClickListener(null)
                        btnPlayReceived.visibility = View.VISIBLE
                        btnStopReceived.visibility = View.GONE
                    }
                }
                play(progressBarReceived, view){
                    btnPlayReceived.visibility = View.VISIBLE
                    btnStopReceived.visibility = View.GONE
                }
            }
        }
    }

    private fun play(progress_bar: ProgressBar, view: MessageView, function: () -> Unit) {
        mVoicePlayer.playToSorage(progress_bar, view.id, view.fileUrl){
            function()
        }
    }

    private fun stop(function: () -> Unit) {
        mVoicePlayer.stop(){
            function()
        }
    }

    override fun onDetach() {
        btnPlayUser.setOnClickListener(null)
        btnPlayReceived.setOnClickListener(null)
        //mVoicePlayer.release()
    }

}