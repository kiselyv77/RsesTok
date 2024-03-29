package com.example.rsestok.ui.video_pager_fragment

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.rsestok.*
import com.example.rsestok.databinding.FragmentVideoPagerBinding
import com.example.rsestok.models.VideoModel
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.media.PlayerViewAdapter
import com.google.firebase.database.DatabaseReference
import androidx.annotation.RequiresApi
import com.example.rsestok.utilits.APP_NAV_CONTROLLER
import com.example.rsestok.utilits.app_listeners.AppChildEventListener
import com.example.rsestok.utilits.showToast


class VideoPagerFragment : Fragment() {

    private lateinit var video_pager_viewModel: VideoPagerViewModel
    private var _binding: FragmentVideoPagerBinding? = null

    private lateinit var  videoAdapter : VideoPagerAdapter
    private lateinit var listUsersUid : List<String>
    private lateinit var videoUri : String
    private var listReference = arrayListOf<DatabaseReference>()

    private lateinit var  refVideos : DatabaseReference
    private lateinit var refSubscriptions: DatabaseReference

    var position:Int = 1
    var listSubscribers = arrayListOf<String>()

    lateinit var childListener : AppChildEventListener



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.P)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        listUsersUid = arguments?.getStringArrayList("listUsersUid")!! //Список пользователей видео которых будут отображатся
        videoUri = arguments?.getString("videoUri")?: "" //эта ссылка на видео-файл передается из чата


        position = arguments?.getInt("position")!!
        listUsersUid.forEach{
            listReference.add(REF_DATABASE_ROOT.child(NODE_VIDEOS).child(it))// Создаем список ссылок на каждого пользователя в базе данных
        }


        video_pager_viewModel = ViewModelProvider(this).get(VideoPagerViewModel::class.java)

        _binding = FragmentVideoPagerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val attrib = APP_ACTIVITY.window.attributes
        attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        APP_ACTIVITY.goneNavView()
        initRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {

        val viewPage = binding.viewPage
        videoAdapter = VideoPagerAdapter(listSubscribers)
        childListener = AppChildEventListener{
            val video = it.getVideoModel()
            if(videoUri.length == 0){ //если эта строка ноль значит VideoPagerFragment добавляет все видеозаписи
                videoAdapter.addItemToBottom(video)
            }
            else { //Если эта строка больше нуля значит мы перешли из чата и будем добавлять только одно видео на которое тапнули в чате
                if(video.videoURI == videoUri){ // если пришедшая из чата ссылка на видео равна ссылке на видео из модели видео то добавляем
                    videoAdapter.addItemToBottom(video) // это совпадение всегда будет только одно
                }
            }
        }

        listReference.forEach{
            it.addChildEventListener(childListener) //Следим за каждым пользователем
        }


        viewPage.adapter = videoAdapter
        viewPage.postDelayed(
        {
            viewPage.setCurrentItem(position, false)//Этот костыль нужно убрать будет!!!
            viewPage.visibility = View.VISIBLE
        }, 30)
    }



    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStop() {
        super.onStop()
        PlayerViewAdapter.releaseAllPlayers()
        APP_ACTIVITY.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val attrib = APP_ACTIVITY.window.attributes
        attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        videoAdapter.remuveEventListeners()
        listReference.forEach{
            it.removeEventListener(childListener)
        }

    }

    override fun onPause() {
        super.onPause()
        PlayerViewAdapter.pauseCurrentPlayingVideo()
    }

}
