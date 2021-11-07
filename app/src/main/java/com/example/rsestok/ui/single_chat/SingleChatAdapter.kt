package com.example.rsestok.ui.single_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.ui.single_chat.message_recycling_view.view_holders.AppHolderFactory
import com.example.rsestok.ui.single_chat.message_recycling_view.view_holders.MessageHolder
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView


class SingleChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listMessageCache = mutableListOf<MessageView>()


    private val holderList = mutableListOf<MessageHolder>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolder).drawMessage(listMessageCache[position])
    }


    override fun getItemCount(): Int = listMessageCache.size

    override fun getItemViewType(position: Int): Int {
        return listMessageCache[position].getTypeView()
    }

    fun addItemToBottom(item:MessageView, onSuccess: ()-> Unit){
        if(!listMessageCache.contains(item)){
            listMessageCache.add(item)
            notifyItemInserted(listMessageCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item:MessageView, onSuccess: ()-> Unit){
        if(!listMessageCache.contains(item)){
            listMessageCache.add(item)
            listMessageCache.sortBy{ it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }


    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onAttach(listMessageCache[holder.adapterPosition])
        holderList.add(holder)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onDetach()
        holderList.remove(holder)
        super.onViewDetachedFromWindow(holder)
    }

    fun destroy(){
        holderList.forEach {
            it.onDetach()
        }
    }
    fun clear(){
        val n = listMessageCache.size
        listMessageCache.clear()
        notifyItemRangeRemoved(0,n)
    }
}

