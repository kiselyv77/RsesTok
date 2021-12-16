package com.example.rsestok.utilits.media

import android.view.View
import android.widget.ImageView
import com.example.rsestok.models.VideoModel
import com.example.rsestok.ui.video_pager_fragment.VideoPagerAdapter
import com.example.rsestok.utilits.media.PlayerViewAdapter.Companion.loadVideo
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class PlayerViewManager(val listVideos: MutableList<VideoModel>) {
        lateinit var listHolders: MutableList<VideoPagerAdapter.VideoHolder>

        fun VideoPagerAdapter.VideoHolder.initVideos(position:Int){
                listHolders.add(this)
        }

}