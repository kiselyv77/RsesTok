package com.example.rsestok.utilits.media

import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.UiThread
import com.example.rsestok.R
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.SIMPLE_CACHE
import com.example.rsestok.utilits.showToast
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


fun Context.toast(text: String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

class PlayerViewAdapter {

    companion object{
        // for hold all players generated
        private var playersMap: MutableMap<Int, SimpleExoPlayer>  = mutableMapOf()
        // for hold current player
        private var currentPlayingVideo: Pair<Int, SimpleExoPlayer>? = null

        fun releaseAllPlayers(){
            playersMap.map {
                it.value.release()
            }
        }

        // call when item recycled to improve performance
        fun releaseRecycledPlayers(index: Int){
            playersMap[index]?.release()
        }

        // call when scroll to pause any playing player
        fun pauseCurrentPlayingVideo(){
            if (currentPlayingVideo != null){
                currentPlayingVideo?.second?.playWhenReady = false
            }
        }

        fun playIndexThenPausePreviousPlayer(index: Int){
            if (playersMap.get(index)?.playWhenReady == false) {
                pauseCurrentPlayingVideo()
                playersMap.get(index)?.playWhenReady = true
                currentPlayingVideo = Pair(index, playersMap.get(index)!!)
            }
        }

        fun playPause(animPlay:ImageView, animPause:ImageView, position:Int){
            if(playersMap.get(position)?.playWhenReady == false && playersMap.get(position)?.playbackState != Player.STATE_BUFFERING){
                playIndexThenPausePreviousPlayer(position)
                animPlay.visibility = View.VISIBLE
                animPlay.startAnimation(AnimationUtils.loadAnimation(APP_ACTIVITY, R.anim.anim_play_pause))
                animPlay.visibility = View.INVISIBLE
            }
            else if(playersMap.get(position)?.playbackState != Player.STATE_BUFFERING){
                pauseCurrentPlayingVideo()
                animPause.visibility = View.VISIBLE
                animPause.startAnimation(AnimationUtils.loadAnimation(APP_ACTIVITY, R.anim.anim_play_pause))
                animPause.visibility = View.INVISIBLE
            }

        }

        /*
        *  url is a url of stream video
        *  progressbar for show when start buffering stream
        * thumbnail for show before video start
        * */
        fun PlayerView.loadVideo(
            url: String,
            callback: PlayerStateCallback,
            progressbar: CircularProgressView,
            thumbnail: ImageView,
            item_index: Int? = null,
            autoPlay: Boolean = false
        ) {

            progressbar.visibility = View.VISIBLE
            Log.d("debag_pager", "loadVideo()")
            if(item_index != null) releaseRecycledPlayers(item_index)


            val player = SimpleExoPlayer.Builder(context).build()

            val cacheDataSourceFactory = VideoCache.getInstance()?.let {
                CacheDataSourceFactory(it, DefaultHttpDataSourceFactory(Util.getUserAgent(context, "exo")))
            }

            val mediaItem = MediaItem.fromUri(url)

            val mediaSource = cacheDataSourceFactory?.let { ProgressiveMediaSource.Factory(it).createMediaSource(mediaItem) }

            if (mediaSource != null) {
                player.prepare(mediaSource, true, true)
            }




            player.playWhenReady = autoPlay
            player.repeatMode = Player.REPEAT_MODE_ALL
            // When changing track, retain the latest frame instead of showing a black screen
            setKeepContentOnPlayerReset(true)
            // We'll show the controller, change to true if want controllers as pause and start
            this.useController = false
            // Provide url to load the video from here


            this.player = player

            // add player with its index to map
            if (playersMap.containsKey(item_index)){
                playersMap.remove(item_index)}
            if (item_index != null){
                playersMap[item_index] = player}





            this.player!!.addListener(object : Player.EventListener {


                override fun onPlayerError(error: ExoPlaybackException) {
                    super.onPlayerError(error)
                    this@loadVideo.context.toast("Ошибка воспроизведения")
                    Log.d("debag_pager", "${error.message}")
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)

                    if (playbackState == Player.STATE_BUFFERING){
                        callback.onVideoBuffering(player)
                        // Buffering..
                        // set progress bar visible here
                        // set thumbnail visible
                        //thumbnail.visibility = View.VISIBLE
                        progressbar.visibility = View.VISIBLE

                    }

                    if (playbackState == Player.STATE_READY){
                        // [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
                        progressbar.visibility = View.GONE
                        // set thumbnail gone
                        thumbnail.visibility = View.GONE
                        callback.onVideoDurationRetrieved(this@loadVideo.player!!.duration, player)
                    }


                    if (playbackState == Player.STATE_READY && player.playWhenReady){
                        // [PlayerView] has started playing/resumed the video
                        callback.onStartedPlaying(player)
                    }
                }
            })
        }

    }
}