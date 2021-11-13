package com.example.rsestok.ui.video_pager_fragment

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.models.VideoModel
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.downloadAndSetImage
import com.example.rsestok.utilits.media.PlayerStateCallback
import com.example.rsestok.utilits.media.PlayerViewAdapter
import com.example.rsestok.utilits.media.PlayerViewAdapter.Companion.loadVideo
import com.google.android.exoplayer2.Player

import android.view.*
import androidx.collection.arrayMapOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rsestok.*
import com.example.rsestok.databinding.*
import com.example.rsestok.ui.search.SearchAdapterSendVideo
import com.example.rsestok.utilits.APP_NAV_CONTROLLER
import com.example.rsestok.utilits.app_listeners.AppValueEventListener
import com.example.rsestok.utilits.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DatabaseReference


class VideoPagerAdapter(var listSubscribers: ArrayList<String>) : RecyclerView.Adapter<VideoPagerAdapter.VideoHolder>(),
    PlayerStateCallback {


    val listVideos = mutableListOf<VideoModel>()


    val mapListeners = arrayMapOf<DatabaseReference, AppValueEventListener>()

    private lateinit var likeListener: AppValueEventListener



    inner class VideoHolder(item: TiktokTimelineItemRecyclerBinding): RecyclerView.ViewHolder(item.root){
        val playerView = item.itemVideoExoplayer
        val progressBar = item.progressBar
        val thumbnail = item.thumbnail
        val animPlay = item.animPlay
        val animPause = item.animPause

        val moreOptions = item.moreOptions

        val btnComment = item.btnComment
        val textCountComenst = item.textCountComents

        val btnLike = item.btnLike
        val textCountLikes = item.textCountLikes

        var flagBtnLike = false

        var title = item.title
        var description = item.desc

        var btnSend = item.btnSend
        var imageProfile = item.profileImage }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder  {
        val chatItem = TiktokTimelineItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = VideoHolder(chatItem)
        return holder
    }


    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        if (holder.playerView.player?.playbackState == null){
            holder.thumbnail.downloadAndSetImage(listVideos[position].thumbnailUrl, R.drawable.back_white)
            holder.playerView.loadVideo(listVideos[position].videoURI, this, holder.progressBar, holder.thumbnail, position, false)
        }
        holder.title.text = listVideos[position].title
        holder.description.text = listVideos[position].description
        holder.textCountLikes.text = listVideos[position].likes.size.toString()
        holder.itemView.setOnClickListener {
            PlayerViewAdapter.playPause(holder.animPlay, holder.animPause, position)
        }
        holder.moreOptions.setOnClickListener{
            if(listVideos[position].userId == CURRENT_UID){
                showDialogForYourAccount(position)
            }
            else{
                showDialogForNotYourAccount(position)
            }

        }
        holder.btnComment.setOnClickListener{
            showComents(position)
        }
        if(listVideos[position].likes.containsValue(CURRENT_UID)){
            holder.flagBtnLike = true
        }

        holder.btnSend.setOnClickListener{
            showDialogSendVideo(position)
        }
        
        likeListener = AppValueEventListener {

            if(it.getVideoModel().likes.containsValue(CURRENT_UID)){
                holder.flagBtnLike = true
                holder.btnLike.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_like_on))
                holder.textCountLikes.setTextColor(APP_ACTIVITY.getColor(R.color.color_like))
            }
            else{
                holder.btnLike.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_like))
                holder.textCountLikes.setTextColor(APP_ACTIVITY.getColor(R.color.white))
            }
             holder.btnLike.setOnClickListener {
                if(holder.flagBtnLike == false){
                    likeVideo(listVideos[position], CURRENT_UID)
                    holder.flagBtnLike = true
                }
                else{
                    removeLikeVideo(listVideos[position], CURRENT_UID)
                    holder.flagBtnLike = false
                }

            }
            holder.textCountLikes.text = it.getVideoModel().likes.size.toString()

        }
        val refVideos = REF_DATABASE_ROOT.child(NODE_VIDEOS).child(listVideos[position].userId).child(listVideos[position].id)
        refVideos.addValueEventListener(likeListener)
        mapListeners[refVideos] = likeListener

        holder.imageProfile.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("uid", listVideos[position].userId)
            if(listVideos[position].userId == CURRENT_UID){
                APP_NAV_CONTROLLER.navigate(R.id.navigation_profile, bundle)
            }
            else{
                APP_NAV_CONTROLLER.navigate(R.id.navigation_user_profile, bundle)
        }
        }

        val refPhoto = REF_DATABASE_ROOT.child(NODE_USERS).child(listVideos[position].userId)
        val photoListener = AppValueEventListener {
            holder.imageProfile.downloadAndSetImage(it.getUserModel().profilePhotoUri)

        }
        refPhoto.addValueEventListener(photoListener)
        mapListeners[refPhoto] = photoListener





    }

    private fun showComents(position: Int) {
        val bottomSheetDialogComents = BottomSheetDialog(APP_ACTIVITY)
        val dialogBindingComents = LayoutComentsBinding.inflate(LayoutInflater.from(bottomSheetDialogComents.context), null ,false)
        bottomSheetDialogComents.setContentView(dialogBindingComents.root)
        bottomSheetDialogComents.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogComents.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        bottomSheetDialogComents.show()
    }


    private fun showDialogForYourAccount(position: Int) {
        val bottomSheetDialogMenu = BottomSheetDialog(APP_ACTIVITY)
        val dialogBindingMenu = MenuVideoForYourAccountBinding.inflate(LayoutInflater.from( bottomSheetDialogMenu.context), null ,false)
        bottomSheetDialogMenu.setContentView(dialogBindingMenu.root)
        bottomSheetDialogMenu.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogMenu.show()

        dialogBindingMenu.btnDelete.setOnClickListener{
            bottomSheetDialogMenu.dismiss()
            deleteVideo(listVideos[position]){
                showToast("Видео удалено")
                APP_NAV_CONTROLLER.popBackStack()
            }
        }
        dialogBindingMenu.btnEdit.setOnClickListener{
            bottomSheetDialogMenu.dismiss()
            showToast("Редактировать")
        }
    }

    private fun showDialogForNotYourAccount(position: Int){
        val bottomSheetDialogMenu = BottomSheetDialog(APP_ACTIVITY)
        val dialogBindingMenu = MenuVideoForNotYourAccountBinding.inflate(LayoutInflater.from( bottomSheetDialogMenu.context), null ,false)
        bottomSheetDialogMenu.setContentView(dialogBindingMenu.root)
        bottomSheetDialogMenu.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogMenu.show()
        dialogBindingMenu.btnComplaint.setOnClickListener{
            showToast("Жалоба ебатт")
            bottomSheetDialogMenu.dismiss()
        }
    }

    private fun showDialogSendVideo(position: Int){
        val adapter = SearchAdapterSendVideo(listVideos[position].thumbnailUrl, listVideos[position].userId, listVideos[position].id)
        val bottomSheetSend = BottomSheetDialog(APP_ACTIVITY)
        val dialogBindingSend = BottomSheetSendBinding.inflate(LayoutInflater.from( bottomSheetSend.context), null ,false)
        dialogBindingSend.listUsers.adapter = adapter
        dialogBindingSend.listUsers.layoutManager = LinearLayoutManager(APP_ACTIVITY)
        bottomSheetSend.setContentView(dialogBindingSend.root)
        bottomSheetSend.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetSend.show()
        APP_NAV_CONTROLLER.addOnDestinationChangedListener(NavController.OnDestinationChangedListener { controller, destination, arguments ->
             if (destination.id != R.id.navigation_video_pager) {
                 bottomSheetSend.dismiss() }})

        val refSubscribers = REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_SUBSCRIBERS)
        val subscribersListener = AppValueEventListener {
            listSubscribers = it.children.map{ it.getStringList() } as ArrayList<String>
        }
        refSubscribers.addListenerForSingleValueEvent(subscribersListener)
        mapListeners[refSubscribers] = subscribersListener

        val refUsers = REF_DATABASE_ROOT.child(NODE_USERS)
        val userListener = AppValueEventListener{
            val listUsers = it.children.map{ it.getUserModel() }.filter{ listSubscribers.contains(it.id)}
            adapter.updateListItems(listUsers)
        }
        refUsers.addListenerForSingleValueEvent(userListener)
        mapListeners[refUsers] = userListener



    }

    override fun getItemCount(): Int = listVideos.size

    fun addItemToBottom(item:VideoModel){
        if(!listVideos.contains(item)){
            listVideos.add(item)
            notifyItemInserted(listVideos.size)
        }
    }




    override fun onVideoDurationRetrieved(duration: Long, player: Player) {
    }

    override fun onVideoBuffering(player: Player) {

    }

    override fun onStartedPlaying(player: Player) {

    }

    override fun onFinishedPlaying(player: Player) {

    }

    override fun onViewAttachedToWindow(holder: VideoHolder) {
        super.onViewAttachedToWindow(holder)
        PlayerViewAdapter.playIndexThenPausePreviousPlayer(holder.layoutPosition)
    }

    fun remuveEventListeners(){
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }

    }

}