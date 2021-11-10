package com.example.rsestok.ui.search

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.AUTH
import com.example.rsestok.R
import com.example.rsestok.databinding.FragmentUserProfileBinding
import com.example.rsestok.databinding.ItemUserBinding
import com.example.rsestok.models.UserModel
import com.example.rsestok.models.VideoModel
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.UserStatus
import com.example.rsestok.utilits.downloadAndSetImage
import com.example.rsestok.utilits.showToast

class SearchAdapter: AbstractAdapter() {
    override fun onBindViewHolder(holder: SearchUserHolder, position: Int) {
        holder.itemView.setOnClickListener{
            val navController = APP_ACTIVITY.findNavController(R.id.nav_host_fragment_activity_main)
            val bundle:Bundle = Bundle()
            bundle.putString("uid", listUsers[position].id)

            if(listUsers[position].id==AUTH.currentUser?.uid){
                navController.navigate(R.id.navigation_profile, bundle)
            }
            else{
                navController.navigate(R.id.navigation_user_profile, bundle)
            }

        }
        holder.fullName.text = listUsers[position].fullname
        holder.userPhoto.downloadAndSetImage(listUsers[position].profilePhotoUri)
        holder.userStatus.text =  listUsers[position].status
        if(listUsers[position].status == UserStatus.ONLINE.state) {
            holder.userStatus.setTextColor(Color.GREEN)
        }
        else{
            holder.userStatus.setTextColor(Color.GRAY)
        }
    }

}


class SearchAdapterSendVideo:AbstractAdapter(){
    override fun onBindViewHolder(holder: SearchUserHolder, position: Int) {
        holder.itemView.setOnClickListener{
            val navController = APP_ACTIVITY.findNavController(R.id.nav_host_fragment_activity_main)
            val bundle:Bundle = Bundle()
            bundle.putString("uid", listUsers[position].id)
            //navController.navigate(R.id.navigation_profile, bundle)

            showToast("Отправка видео в лс") }

        holder.fullName.text = listUsers[position].fullname
        holder.userPhoto.downloadAndSetImage(listUsers[position].profilePhotoUri)
        holder.userStatus.text =  listUsers[position].status
        if(listUsers[position].status == UserStatus.ONLINE.state) {
            holder.userStatus.setTextColor(Color.GREEN)
        }
        else{
            holder.userStatus.setTextColor(Color.GRAY)
        }

    }
}

abstract class AbstractAdapter: RecyclerView.Adapter<AbstractAdapter.SearchUserHolder>(){
    val listUsers = mutableListOf<UserModel>()
    class SearchUserHolder(item:ItemUserBinding): RecyclerView.ViewHolder(item.root){
        val fullName = item.fullnameTextContacts
        val userPhoto = item.contactUserPhoto
        val userStatus = item.statusTextContacts
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractAdapter.SearchUserHolder {
        val chatItem = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = AbstractAdapter.SearchUserHolder(chatItem)
        return holder
    }

    override fun getItemCount(): Int = listUsers.size

    fun updateListItems(newListUsers: List<UserModel>) {
        listUsers.clear()
        listUsers.addAll(newListUsers)
        notifyDataSetChanged()
    }

    fun addItemToBottom(item: UserModel){
        if(!listUsers.contains(item)){
            listUsers.add(item)
            notifyItemInserted(listUsers.size)
        }
    }


}