package com.example.rsestok.ui.add

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationBuilderWithBuilderAccessor
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arthenica.mobileffmpeg.ExecuteCallback
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.rsestok.*
import com.example.rsestok.databinding.FragmentAddBinding
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.APP_NAV_CONTROLLER
import com.example.rsestok.utilits.media.AppVideoPlayer
import com.example.rsestok.utilits.media.AppVoicePlayer
import com.example.rsestok.utilits.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import androidx.core.content.ContextCompat.getSystemService

import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.content.ContextCompat
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationManagerCompat

import androidx.core.content.ContextCompat.getSystemService
import com.example.rsestok.utilits.NOTIFICATION_ID


class AddFragment : Fragment() {

    private lateinit var addViewModel: AddViewModel
    lateinit var launcherAttachVideo: ActivityResultLauncher<Intent>
    lateinit var launcherCaptureVideo: ActivityResultLauncher<Intent>
    lateinit var launcherAttachAudio: ActivityResultLauncher<Intent>
    private var _binding: FragmentAddBinding? = null
    private val videoPlayer = AppVideoPlayer()
    private val mVoicePlayer =  AppVoicePlayer()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private lateinit var binding: FragmentAddBinding
    var uri = Uri.EMPTY
    var uri_compress_video = Uri.EMPTY

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        addViewModel = ViewModelProvider(this).get(AddViewModel::class.java)

        createNotificationChannel()

        binding = FragmentAddBinding.inflate(inflater, container, false)
        //APP_ACTIVITY.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //APP_ACTIVITY.window.statusBarColor = Color.BLACK


        videoPlayer.init()
        binding.playerView.player = videoPlayer.simpleExoPlayer
        binding.playerView.hideController()
        binding.playerView.useController = false



        binding.btnPick.setOnClickListener{
            addVideo()
        }

        binding.btnCapture.setOnClickListener{
            captureVideo()
        }

        binding.uploadVideo.setOnClickListener{
            if(uri!= Uri.EMPTY && binding.title.text.isNotEmpty() && binding.description.text.isNotEmpty()){
                val id_notify = uri.hashCode()
                val builder = NotificationCompat.Builder(APP_ACTIVITY, NOTIFICATION_ID)
                    .setSmallIcon(R.mipmap.ic_launcher2)
                    .setContentTitle(binding.title.text)
                    .setContentText("Видео загружается")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(false)

                with(NotificationManagerCompat.from(APP_ACTIVITY)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(id_notify, builder.build())
                }

                val mRetriever = MediaMetadataRetriever()
                mRetriever.setDataSource(APP_ACTIVITY, uri)

                val path = APP_ACTIVITY.getExternalFilesDir(null).toString()

                val file_start = File(path, "Видео.mp4")
                val file_final = File(path, "Видео_сжатое.mp4")
                val inputStream = APP_ACTIVITY.contentResolver.openInputStream(uri!!)
                inputStream!!.copyTo(file_start.outputStream())
                inputStream.close()

                FFmpeg.executeAsync("-y -i ${file_start.absolutePath} -vf scale=720:-1 -acodec copy -threads 12 ${file_final.absolutePath}" , object : ExecuteCallback{
                    override fun apply(executionId: Long, returnCode: Int) {
                        uri_compress_video = file_final.toUri()
                        val image = mRetriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST) as Bitmap
                        uploadVideoToStarage(CURRENT_UID,binding.title.text.toString() ,binding.description.text.toString(),uri_compress_video ,
                            getVideoKey(CURRENT_UID), image)
                        {
                            NotificationManagerCompat.from(APP_ACTIVITY).cancel(uri.hashCode())
                        }
                    }

                })
                APP_NAV_CONTROLLER.popBackStack()
            }
            else{
                showToast("Выберите видео и заполните все поля")
            }
        }


        addViewModel.uri.observe(viewLifecycleOwner, Observer {

        })
        return binding.root
    }



    private fun captureVideo() {
        if (APP_ACTIVITY.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) { // First check if camera is available in the device
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            launcherCaptureVideo.launch(intent)
        }
    }

    private fun addVideo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        launcherAttachVideo.launch(intent)
    }

    private  fun addAudio(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        launcherAttachAudio.launch(intent)
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "chanel"
            val descriptionText = "desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_ID, name, importance).apply {
                description = descriptionText
            }
        val notificationManager: NotificationManager =
            APP_ACTIVITY.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    }

    override fun onResume() {
        super.onResume()
        //APP_ACTIVITY.goneNavView()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        initLaunchers()
    }

    private fun initLaunchers() {
        launcherAttachVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK && result.data != null){
                uri = result.data!!.data
                videoPlayer.prepare(uri.toString())
                hideButton()
                binding.changeVideo.setOnClickListener {
                    showButton()
                }
            }
        }

        launcherCaptureVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK && result.data != null){
                uri = result.data!!.data
                videoPlayer.prepare(uri.toString())
                hideButton()
                binding.changeVideo.setOnClickListener {
                    showButton()
                }
            }
        }
        launcherAttachAudio = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK && result.data != null){
                uri = result.data!!.data
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
         videoPlayer.release()
        APP_ACTIVITY.window.statusBarColor = Color.WHITE

    }

    override fun onStop() {
        super.onStop()
        APP_ACTIVITY.visibleNavView()

    }

    fun hideButton(){
        binding.playerView.useController = true
        binding.btnPick.visibility = View.GONE
        binding.btnCapture.visibility = View.GONE
        binding.separator.visibility = View.GONE

    }

    fun showButton(){
        binding.playerView.useController = false
        binding.btnPick.visibility = View.VISIBLE
        binding.btnCapture.visibility = View.VISIBLE
        binding.separator.visibility = View.VISIBLE


    }

}