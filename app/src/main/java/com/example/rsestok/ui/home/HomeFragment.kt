package com.example.rsestok.ui.home

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.rsestok.*
import com.example.rsestok.databinding.FragmentHomeBinding
import com.example.rsestok.databinding.FragmentVideoPagerBinding
import com.example.rsestok.models.UserModel
import com.example.rsestok.ui.video_pager_fragment.VideoPagerAdapter
import com.example.rsestok.ui.video_pager_fragment.VideoPagerFragment
import com.example.rsestok.ui.video_pager_fragment.VideoPagerViewModel
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.app_listeners.AppChildEventListener
import com.example.rsestok.utilits.app_listeners.AppValueEventListener
import com.example.rsestok.utilits.media.PlayerViewAdapter
import com.example.rsestok.utilits.showToast
import com.google.firebase.database.DatabaseReference

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var  videoAdapter : VideoPagerAdapter
    private var listUsersUid = listOf<UserModel>()
    private var listReference = arrayListOf<DatabaseReference>()

    private lateinit var  refVideos : DatabaseReference
    private lateinit var refSubscriptions: DatabaseReference

    var position:Int = 1
    var listSubscribers = arrayListOf<String>()
    lateinit var childListener : AppChildEventListener

    private lateinit var usersListener: AppValueEventListener
    private val refUsers = REF_DATABASE_ROOT.child(NODE_USERS)


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.P)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()

    }

    override fun onStart() {
        super.onStart()
        initRecyclerView() }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        val viewPage = binding.viewPage
        usersListener = AppValueEventListener{
            listUsersUid = it.children.map{ it.getUserModel() }
            listUsersUid.forEach{
                listReference.add(REF_DATABASE_ROOT.child(NODE_VIDEOS).child(it.id))
                listReference.forEach{
                    it.addChildEventListener(childListener)
                }
            }
        }
        refUsers.addListenerForSingleValueEvent(usersListener)
        videoAdapter = VideoPagerAdapter(listSubscribers)
        childListener = AppChildEventListener{
            val video = it.getVideoModel()
            videoAdapter.addItemToBottom(video)
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

        videoAdapter.remuveEventListeners()
        listReference.forEach{
            it.removeEventListener(childListener)
        }
        refUsers.removeEventListener(usersListener)

    }

    override fun onPause() {
        super.onPause()
        PlayerViewAdapter.pauseCurrentPlayingVideo()
    }

}