package com.example.rsestok.ui.video_pager_fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.AbsListView
import androidx.collection.arrayMapOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.*
import com.example.rsestok.databinding.*
import com.example.rsestok.models.VideoModel
import com.example.rsestok.ui.search.SearchAdapterSendVideo
import com.example.rsestok.ui.single_chat.SingleChatAdapter
import com.example.rsestok.ui.single_chat.message_recycling_view.views.AppViewFactory
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.APP_NAV_CONTROLLER
import com.example.rsestok.utilits.app_listeners.AppChildEventListener
import com.example.rsestok.utilits.app_listeners.AppValueEventListener
import com.example.rsestok.utilits.downloadAndSetImage
import com.example.rsestok.utilits.media.PlayerStateCallback
import com.example.rsestok.utilits.media.PlayerViewAdapter
import com.example.rsestok.utilits.media.PlayerViewAdapter.Companion.loadVideo
import com.example.rsestok.utilits.showToast
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DatabaseReference
import android.view.MotionEvent
import com.example.rsestok.ui.coments_chat.ComentsChatAdapter
import com.example.rsestok.ui.coments_chat.coments_recycling_view.views.ComentViewFactory
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


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
        var comentCount:Int = 0

        var flagBtnLike = false

        var title = item.title
        var description = item.desc

        var btnSend = item.btnSend
        var imageProfile = item.profileImage

        var btnClose = item.btnClose
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder  {

        val chatItem = TiktokTimelineItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = VideoHolder(chatItem)
        return holder
    }


    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (holder.playerView.player?.playbackState != Player.STATE_READY){
            holder.thumbnail.downloadAndSetImage(listVideos[position].thumbnailUrl, R.drawable.back_black)
            holder.playerView.loadVideo(listVideos[position].videoURI, this, holder.progressBar, holder.thumbnail, position, false)
        }


        holder.title.text = listVideos[position].title
        holder.description.text = listVideos[position].description
        holder.textCountLikes.text = listVideos[position].likes.size.toString()
        holder.btnClose.setOnClickListener{
            APP_NAV_CONTROLLER.popBackStack()
        }
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
        val refComents = REF_DATABASE_ROOT.child(NODE_COMENTS).child(listVideos[position].id)
        val comentsCountListener = AppValueEventListener {
            holder.comentCount = it.children.map{it.getMessageModel()}.size
            holder.textCountComenst.text = holder.comentCount.toString()
        }
        refComents.addValueEventListener(comentsCountListener)
        mapListeners[refComents] = comentsCountListener

        holder.btnComment.setOnClickListener{
            showComents(position)
        }


        APP_NAV_CONTROLLER.addOnDestinationChangedListener(NavController.OnDestinationChangedListener{controller, destination, arguments ->
            if (destination.id == R.id.navigation_video_pager || destination.id == R.id.navigation_single_chat) {
                holder.btnClose.visibility = View.VISIBLE
                holder.btnClose.setOnClickListener{
                    APP_NAV_CONTROLLER.popBackStack()
                }

            }
        })




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
        refPhoto.addListenerForSingleValueEvent(photoListener)
        mapListeners[refPhoto] = photoListener





    }

    private fun showComents(position: Int) {
        val bottomSheetDialogComents = BottomSheetDialog(APP_ACTIVITY)
        APP_NAV_CONTROLLER.addOnDestinationChangedListener(NavController.OnDestinationChangedListener{controller, destination, arguments ->
            if (destination.id != R.id.navigation_video_pager && destination.id != R.id.navigation_home) {
                bottomSheetDialogComents.dismiss()
            }
        })
        val dialogBindingComents = LayoutComentsBinding.inflate(LayoutInflater.from(bottomSheetDialogComents.context), null ,false)
        bottomSheetDialogComents.setContentView(dialogBindingComents.root)
        bottomSheetDialogComents.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogComents.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val refComents_for_toolbar = REF_DATABASE_ROOT.child(NODE_COMENTS).child(listVideos[position].id)
        val comentsCountListener = AppValueEventListener {
            val comentCount = it.children.map{it.getMessageModel()}.size
            dialogBindingComents.toolbar.title = "${comentCount} кометариев"
            dialogBindingComents.toolbar.titleMarginStart = 10
        }
        refComents_for_toolbar.addValueEventListener(comentsCountListener)
        mapListeners[refComents_for_toolbar] = comentsCountListener

            dialogBindingComents.toolbar.setNavigationIcon(R.drawable.ic_close)
        dialogBindingComents.toolbar.setNavigationOnClickListener(View.OnClickListener { // Your code
            bottomSheetDialogComents.dismiss()
        })

        dialogBindingComents.sendBtn.setOnClickListener {
            if(dialogBindingComents.chatInputComent.text.length != 0){
                sendComent(listVideos[position], dialogBindingComents.chatInputComent.text.toString())
                dialogBindingComents.chatInputComent.setText("")
            }
        }

        val rcView = dialogBindingComents.rcView
        val linearLayoutManager = LinearLayoutManager(bottomSheetDialogComents.context)
        rcView.setHasFixedSize(true)
        rcView.isNestedScrollingEnabled = true
        rcView.layoutManager = linearLayoutManager
        val adapterComents = ComentsChatAdapter()
        adapterComents.setHasStableIds(true)
        rcView.adapter = adapterComents



        val comentsListener = AppChildEventListener{
            val coment = it.getComentModel()
            adapterComents.addItemToBottom(ComentViewFactory.getView(coment)){
                rcView.smoothScrollToPosition(adapterComents.itemCount)
            }

        }

        val refComents = REF_DATABASE_ROOT.child(NODE_COMENTS).child(listVideos[position].id)
        refComents.addChildEventListener(comentsListener)

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
            deleteVideo(listVideos[position]){}
            showToast("Видео удалено")
            deleteItem(position)

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
        val adapter = SearchAdapterSendVideo(listVideos[position].thumbnailUrl, listVideos[position].userId, listVideos[position].videoURI)
        val bottomSheetSend = BottomSheetDialog(APP_ACTIVITY)
        val dialogBindingSend = BottomSheetSendBinding.inflate(LayoutInflater.from( bottomSheetSend.context), null ,false)
        dialogBindingSend.listUsers.adapter = adapter
        dialogBindingSend.listUsers.layoutManager = LinearLayoutManager(APP_ACTIVITY)
        bottomSheetSend.setContentView(dialogBindingSend.root)
        bottomSheetSend.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetSend.show()
        APP_NAV_CONTROLLER.addOnDestinationChangedListener(NavController.OnDestinationChangedListener { controller, destination, arguments ->
             if (destination.id != R.id.navigation_video_pager && destination.id != R.id.navigation_home) {
                 bottomSheetSend.dismiss()
             }
        }
        )

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

    fun deleteItem(position: Int){
        listVideos.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listVideos.size)

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
        Log.d("debag_pager", "attach:playIndexThenPausePreviousPlayer():${holder.layoutPosition}")
    }

    override fun onViewDetachedFromWindow(holder: VideoHolder) {
        super.onViewDetachedFromWindow(holder)
        //Log.d("debag_pager", "detach:releaseRecycledPlayers():${holder.layoutPosition}")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        Log.d("debag_pager", "onDetachedFromRecyclerView")
    }


    fun remuveEventListeners(){
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }

    }

}