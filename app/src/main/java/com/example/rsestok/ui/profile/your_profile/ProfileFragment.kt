package com.example.rsestok.ui.profile.your_profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rsestok.*
import com.example.rsestok.databinding.FragmentProfileBinding
import com.example.rsestok.models.VideoModel
import com.example.rsestok.ui.profile.ProfileVideoAdapter
import com.example.rsestok.ui.profile.ProfileViewModel
import com.example.rsestok.utilits.*
import com.example.rsestok.utilits.app_listeners.AppValueEventListener
import com.example.rsestok.utilits.factory.ViewModelFactory
import com.example.rsestok.utilits.media.PlayerViewAdapter
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null
    private lateinit var launcherCropImage: ActivityResultLauncher<Intent>

    private lateinit var videoAdapter : ProfileVideoAdapter

    private lateinit var listenerVideos: AppValueEventListener
    private lateinit var listVideos : List<VideoModel>

    private val refVideos = REF_DATABASE_ROOT.child(NODE_VIDEOS).child(CURRENT_UID)

    private var isScrolled:Boolean = false



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        profileViewModel = ViewModelProvider(this, ViewModelFactory(APP_ACTIVITY.application, AUTH.currentUser!!.uid)).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        initProfile()
        initToolbar()
        initRecyclerView()



        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        initFunc()
    }



    fun initToolbar(){
        binding.toolbar.inflateMenu(R.menu.profile_actions_menu)
        binding.toolbar.menu.getItem(0).setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                signOut()
                return true
            }
        })
        binding.toolbar.menu.getItem(1).setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                DialogChangeFullname(binding.toolbar.title.toString()).show()

                return true
            }
        })
        binding.toolbar.menu.getItem(2).setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                DialogChangeUsername(binding.textProfileName.text.toString()).show()
                return true
            }
        })
        binding.toolbar.menu.getItem(3).setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                DialogChangeDescription(binding.description.text.toString()).show()
                return true
            }
        })
        binding.toolbar.titleMarginStart = 10
    }


    fun initProfile(){
        profileViewModel.username.observe(viewLifecycleOwner, Observer {
            binding.textProfileName.text = it
        })

        profileViewModel.fullName.observe(viewLifecycleOwner, Observer {
            binding.toolbar.title = it
        })
        profileViewModel.listSubscribers.observe(viewLifecycleOwner, Observer {
            binding.subscribersText.text = it.size.toString()
        })
        profileViewModel.listSubscriptions.observe(viewLifecycleOwner, Observer {
            binding.subscriptionsText.text = it.size.toString()
        })
        profileViewModel.listLikes.observe(viewLifecycleOwner, Observer {
            binding.likesText.text = it.size.toString()
        })

        profileViewModel.description.observe(viewLifecycleOwner, Observer {
            binding.description.text = it
        })

        profileViewModel.profileImage.observe(viewLifecycleOwner, Observer {
            binding.profileImage.downloadAndSetImage(it)
        })

        binding.profileImage.setOnClickListener {
            changePhotoUser()
        }
        binding.addVideo.setOnClickListener {
            APP_NAV_CONTROLLER.navigate(R.id.navigation_add)
        }


        binding.btnSubscribers.setOnClickListener {
            val bundle:Bundle = Bundle()
            profileViewModel.listSubscribers.observe(viewLifecycleOwner, Observer {
                bundle.putStringArrayList("list", it)
                APP_NAV_CONTROLLER.navigate(R.id.navigation_list_subscribers, bundle)

            })
        }
        binding.btnSubscriptions.setOnClickListener {
            val bundle:Bundle = Bundle()
            profileViewModel.listSubscriptions.observe(viewLifecycleOwner, Observer {
                bundle.putStringArrayList("list", it)
                APP_NAV_CONTROLLER.navigate(R.id.navigation_list_subscribers, bundle)
            })
        }
    }

    private fun initFunc() {
        launcherCropImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK && result.data != null){
                val uri = CropImage.getActivityResult(result.data).uri
                val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE)
                    .child(CURRENT_UID)

                putFiletoStorage(uri, path){
                    getUrlFromStorage(path){
                        putUrlToDataBase(it){
                            showToast("Данные обновленны")
                            USER.profilePhotoUri = it
                            binding.profileImage.downloadAndSetImage(it)
                        }
                    }
                }
            }
        }

    }

    private fun initRecyclerView() {
        val rcView = binding.rcView
        rcView.setHasFixedSize(true)
        val layoutManager =  GridLayoutManager(APP_ACTIVITY, 3)




        rcView.layoutManager = layoutManager
        videoAdapter = ProfileVideoAdapter(AUTH.currentUser!!.uid)
        listenerVideos = AppValueEventListener{
            listVideos = it.children.map{ it.getVideoModel() }
            videoAdapter.updateListItems(listVideos)

            if (listVideos.size == 0){
                binding.ifVideoEmphy.visibility = View.VISIBLE
                binding.addVideo.visibility = View.VISIBLE
            }
            else{
                binding.ifVideoEmphy.visibility = View.GONE
                binding.addVideo.visibility = View.GONE
            }
        }
        refVideos.addValueEventListener(listenerVideos)
        rcView.adapter = videoAdapter


    }

    private fun changePhotoUser() {
        val intent = CropImage.activity()
            .setAspectRatio(1,1)
            .setRequestedSize(300, 300)
            .setCropShape(CropImageView.CropShape.OVAL)
            .getIntent(APP_ACTIVITY)
        launcherCropImage.launch(intent)
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.visibleNavView()
    }
    override fun onPause(){
        super.onPause()
        PlayerViewAdapter.releaseAllPlayers()
    }

    override fun onDestroy() {
        super.onDestroy()
        refVideos.removeEventListener(listenerVideos)

    }


}