package com.example.rsestok.utilits.media

import android.media.MediaPlayer
import android.media.MediaPlayer.OnBufferingUpdateListener
import android.media.MediaPlayer.OnCompletionListener
import android.widget.ProgressBar
import com.example.rsestok.getFileFromStorage
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.showToast
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class AppVoicePlayer {
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mFile: File
    private lateinit var mediaObserver: MediaObserver


    fun playToSorage(progress_bar: ProgressBar, messageKey:String, fileUrl:String, function: () -> Unit) {
        mediaObserver = MediaObserver(progress_bar, mMediaPlayer)
        mFile = File(APP_ACTIVITY.filesDir, messageKey)
        if(mFile.exists()&&mFile.length()>0&&mFile.isFile){
            startPlay(progress_bar){
                function()
            }
        }
        else {
            mFile.createNewFile()
            getFileFromStorage(mFile, fileUrl){
                startPlay(progress_bar){
                    function()
                }
            }
        }

        Thread(mediaObserver).start()
    }


    private fun startPlay(progress_bar: ProgressBar, function: () -> Unit) {
        try{
            mMediaPlayer.setDataSource(mFile.absolutePath)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
            mMediaPlayer.setOnCompletionListener(OnCompletionListener { mp ->
                progress_bar.progress = 0
                mediaObserver.stop()

                // TODO Auto-generated method stub
                stop {
                    function()
                }
            })

            mMediaPlayer.setOnBufferingUpdateListener(OnBufferingUpdateListener { mp, percent ->
                progress_bar.progress = percent
            })


        }
        catch(e: Exception){
            showToast(e.message.toString())
        }

    }

    fun stop(function: () -> Unit) {
        try{
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            function()
        }
        catch(e: Exception){
            showToast(e.message.toString())
        }
    }

    fun release(){
        mMediaPlayer.release()
    }

    fun init(){
        mMediaPlayer = MediaPlayer()
    }
    private class MediaObserver(val progress_bar: ProgressBar, val mediaPlayer:MediaPlayer) : Runnable {
        private val stop: AtomicBoolean = AtomicBoolean(false)
        fun stop() {
            stop.set(true)
        }

        override fun run() {
            while (!stop.get()) {
                progress_bar.progress =
                    (mediaPlayer.currentPosition.toDouble() / mediaPlayer.duration
                        .toDouble() * 100).toInt()
                try {
                    Thread.sleep(16)
                } catch (ex: java.lang.Exception) {

                }
            }
        }
    }
}


