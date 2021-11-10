package com.example.rsestok.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.R
import com.example.rsestok.databinding.InstagramTimelineItemRecyclerBinding
import com.example.rsestok.models.VideoModel
import com.example.rsestok.ui.profile.ProfileVideoAdapter.ProfileVideoHolder
import com.example.rsestok.utilits.APP_NAV_CONTROLLER
import com.example.rsestok.utilits.downloadAndSetImage
import com.example.rsestok.utilits.media.PlayerStateCallback
import com.example.rsestok.utilits.showToast
import com.google.android.exoplayer2.Player

class ProfileVideoAdapter(val uid:String): RecyclerView.Adapter<ProfileVideoHolder>(),
    PlayerStateCallback {


    var listVideos = mutableListOf<VideoModel>()



    inner class ProfileVideoHolder(item: InstagramTimelineItemRecyclerBinding): RecyclerView.ViewHolder(item.root){
        val playerView = item.itemVideoExoplayer
        val progressBar = item.progressBar
        val thumbnail = item.thumbnail
        val animPlay = item.animPlay
        val animPause = item.animPause
        var isPlaying = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileVideoHolder  {
        val chatItem = InstagramTimelineItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ProfileVideoHolder(chatItem)
        return holder
    }

    override fun onBindViewHolder(holder: ProfileVideoHolder, position: Int) {
        holder.thumbnail.downloadAndSetImage(listVideos[position].thumbnailUrl, R.drawable.back_white)

        holder.itemView.setOnClickListener {
//            if(holder.playerView.player?.playbackState == null){
//                holder.playerView.loadVideo(listVideos[position].videoURI, this, holder.progressBar, holder.thumbnail, position, false)
//            }
//            else{
//                PlayerViewAdapter.playPause(holder.animPlay,holder.animPause,position)
//            }
            val bundle = Bundle()

            bundle.putString("uid", uid)
            bundle.putInt("position", position)
            APP_NAV_CONTROLLER.navigate(R.id.navigation_video_pager, bundle)
        }
    }

    override fun getItemCount(): Int = listVideos.size

    fun updateListItems(newListUsers: List<VideoModel>) {
        listVideos.clear()
        listVideos.addAll(newListUsers)
        notifyDataSetChanged()
    }




    override fun onVideoDurationRetrieved(duration: Long, player: Player) {
    }

    override fun onVideoBuffering(player: Player) {

    }

    override fun onStartedPlaying(player: Player) {

    }

    override fun onFinishedPlaying(player: Player) {

    }


}

