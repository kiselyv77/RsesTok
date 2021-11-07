package com.example.rsestok.ui.profile.user_profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rsestok.*
import com.example.rsestok.databinding.FragmentUserProfileBinding
import com.example.rsestok.models.VideoModel
import com.example.rsestok.ui.profile.ProfileVideoAdapter
import com.example.rsestok.ui.profile.ProfileViewModel
import com.example.rsestok.utilits.*
import com.example.rsestok.utilits.app_listeners.AppValueEventListener
import com.example.rsestok.utilits.factory.ViewModelFactory
import com.google.firebase.database.DatabaseReference

class UserProfileFragment : Fragment() {

    private lateinit var userProfileViewModel: ProfileViewModel
    private var _binding: FragmentUserProfileBinding? = null
    private val refUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)


    private lateinit var videoAdapter : ProfileVideoAdapter

    private lateinit var listenerVideos: AppValueEventListener
    private lateinit var listVideos : List<VideoModel>


    lateinit var subscriptionsListener: AppValueEventListener

    private lateinit var  refVideos : DatabaseReference

    private var flagBtn = false


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var uid:String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        uid = arguments?.getString("uid")
        refVideos = REF_DATABASE_ROOT.child(NODE_VIDEOS).child(uid!!)


        userProfileViewModel = ViewModelProvider(this, ViewModelFactory(APP_ACTIVITY.application, uid!!)).get(
            ProfileViewModel::class.java)

        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        initProfile()
        initToolbar()
        initRecyclerView()

        return  binding.root
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.toolbar_back)
        binding.toolbar.setNavigationOnClickListener(View.OnClickListener { // Your code
            APP_NAV_CONTROLLER.popBackStack()
        })
    }
    fun initProfile(){
        userProfileViewModel.username.observe(viewLifecycleOwner, Observer {
            binding.textProfileName.text = it
        })

        userProfileViewModel.fullName.observe(viewLifecycleOwner, Observer {
            binding.toolbar.title = it
        })
        userProfileViewModel.listSubscribers.observe(viewLifecycleOwner, Observer {
            binding.subscribersText.text = it.size.toString()
        })
        userProfileViewModel.listSubscriptions.observe(viewLifecycleOwner, Observer {
            binding.subscriptionsText.text = it.size.toString()
        })
        userProfileViewModel.listLikes.observe(viewLifecycleOwner, Observer {
            binding.likesText.text = it.size.toString()
        })

        userProfileViewModel.description.observe(viewLifecycleOwner, Observer {
            binding.description.text = it
        })

        userProfileViewModel.profileImage.observe(viewLifecycleOwner, Observer {
            binding.profileImage.downloadAndSetImage(it)
        })
        binding.btnSubscribers.setOnClickListener {
            val bundle:Bundle = Bundle()
            userProfileViewModel.listSubscribers.observe(viewLifecycleOwner, Observer {
                bundle.putStringArrayList("list", it)
                APP_NAV_CONTROLLER.navigate(R.id.navigation_list_subscribers, bundle)
            })
        }
        binding.btnSubscriptions.setOnClickListener {
            val bundle:Bundle = Bundle()
            userProfileViewModel.listSubscriptions.observe(viewLifecycleOwner, Observer {
                bundle.putStringArrayList("list", it)
                APP_NAV_CONTROLLER.navigate(R.id.navigation_list_subscribers, bundle)
            })
        }

        subscriptionsListener = AppValueEventListener {
            binding.btnSubscribe.visibility = View.VISIBLE
            if(it.children.map{ it.getStringList() }.contains(uid!!)){
                binding.btnSubscribe.setBackgroundColor(Color.GRAY)
                binding.btnSubscribe.setText("Отписатся")
                flagBtn = true
            }
            binding.btnSubscribe.setOnClickListener {
                if(flagBtn == false){
                    subscribe(uid!!){
                        binding.btnSubscribe.setBackgroundColor(Color.GRAY)
                        flagBtn = true
                    }

                }
                else{
                    unsubscribe(uid!!){
                        binding.btnSubscribe.setBackgroundColor(resources.getColor(R.color.purple_500))
                        flagBtn = false
                        binding.btnSubscribe.setText("Добавить")
                    }
                }
            }

        }
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_SUBSCRIPTIONS).addValueEventListener(subscriptionsListener)


        binding.btnMessage.setOnClickListener {
            val bundle:Bundle = Bundle()

            bundle.putString("uid", uid)
            APP_NAV_CONTROLLER.navigate(R.id.navigation_single_chat, bundle)
        }

    }

    private fun initRecyclerView() {
        val rcView = binding.rcView
        rcView.setHasFixedSize(true)
        val layoutManager =  GridLayoutManager(APP_ACTIVITY, 3)

        rcView.layoutManager = layoutManager
        videoAdapter = ProfileVideoAdapter(uid!!)
        listenerVideos = AppValueEventListener{
            listVideos = it.children.map{ it.getVideoModel() }
            videoAdapter.updateListItems(listVideos)


            if (listVideos.size == 0){
                binding.ifVideoEmphy.visibility = View.VISIBLE
            }
            else{
                binding.ifVideoEmphy.visibility = View.GONE
            }
        }
        refVideos.addValueEventListener(listenerVideos)
        rcView.adapter = videoAdapter


    }


    override fun onDestroy() {
        super.onDestroy()
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_SUBSCRIPTIONS).removeEventListener(subscriptionsListener)
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.goneNavView()
    }

    override fun onStop() {
        super.onStop()
        APP_ACTIVITY.visibleNavView()
    }

}