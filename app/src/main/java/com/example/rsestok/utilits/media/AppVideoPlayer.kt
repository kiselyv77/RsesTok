package com.example.rsestok.utilits.media

import android.net.Uri
import android.os.Parcel
import com.example.rsestok.utilits.APP_ACTIVITY
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.android.exoplayer2.trackselection.TrackSelection

import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter




class AppVideoPlayer {
    lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var trackSelector: DefaultTrackSelector

    fun init() {
        trackSelector = DefaultTrackSelector(APP_ACTIVITY)



        simpleExoPlayer =
            SimpleExoPlayer.Builder(APP_ACTIVITY).setTrackSelector(trackSelector).build()
    }

    fun prepare(uri: String) {
        val mediaItem = MediaItem.fromUri(uri)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
    }

    fun play() {
        simpleExoPlayer.play()
    }

    fun pause() {
        simpleExoPlayer.pause()
    }

    fun release() {
        simpleExoPlayer.release()
    }

}