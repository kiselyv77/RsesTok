import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.R
import com.example.rsestok.databinding.ItemChatBinding
import com.example.rsestok.models.ChatModel
import com.example.rsestok.models.UserModel
import com.example.rsestok.utilits.*

class MessengerAdapter: RecyclerView.Adapter<MessengerAdapter.ChatHolder>() {

    var listChats = mutableListOf<ChatModel>()
    var listIdChats = mutableListOf<String>()
    class ChatHolder(chatItem:ItemChatBinding): RecyclerView.ViewHolder(chatItem.root){
        val name = chatItem.fullnameTextContacts
        val lastMessage = chatItem.lastMessage
        val photo = chatItem.contactUserPhoto
        val status = chatItem.statusTextContacts

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        val chatItem = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ChatHolder(chatItem)
        holder.itemView.setOnClickListener{
            when(listChats[holder.adapterPosition].type){
                TYPE_CHAT -> showToast("чат")
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        holder.name.text = listChats[position].fullname
        holder.lastMessage.text = listChats[position].lastMessage
        holder.status.text = listChats[position].status
        holder.photo.downloadAndSetImage(listChats[position].profilePhotoUri)
        if(listChats[position].status == UserStatus.ONLINE.state) {
            holder.status.setTextColor(Color.GREEN)
        }
        else{
            holder.status.setTextColor(Color.GRAY)
        }
        holder.itemView.setOnClickListener{
            val bundle:Bundle = Bundle()
            val navController = APP_ACTIVITY.findNavController(R.id.nav_host_fragment_activity_main)

            bundle.putString("uid", listChats[position].id)
            navController.navigate(R.id.navigation_single_chat, bundle)
        }
    }

    override fun getItemCount(): Int = listChats.size

    fun updateListChats(item:ChatModel){
        listChats.add(item)
        notifyItemInserted(listChats.size)
    }

    fun updateListItems(newListUsers: List<ChatModel>) {
        listChats.clear()
        listChats.addAll(newListUsers)
        notifyDataSetChanged()
    }

    fun addItem(item: ChatModel){
        if(listIdChats.contains(item.id)){
            notifyItemChanged(listIdChats.indexOf(item.id))
            Log.d("messenger_adapter_debug", "replace")
        }
        else{
            if(!listChats.contains(item)){
                listChats.add(item)
                listIdChats.add(item.id)
                notifyItemInserted(listChats.size)
                Log.d("messenger_adapter_debug", "addToBottom")
            }
        }
    }



}