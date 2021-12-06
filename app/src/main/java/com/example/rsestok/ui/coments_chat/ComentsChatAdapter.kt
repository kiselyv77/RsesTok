package com.example.rsestok.ui.coments_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.ui.coments_chat.coments_recycling_view.view_holders.ComentHolder
import com.example.rsestok.ui.coments_chat.coments_recycling_view.view_holders.ComentHolderFactory
import com.example.rsestok.ui.coments_chat.coments_recycling_view.views.ComentView
import com.example.rsestok.ui.single_chat.message_recycling_view.view_holders.AppHolderFactory
import com.example.rsestok.ui.single_chat.message_recycling_view.view_holders.MessageHolder
import com.example.rsestok.ui.single_chat.message_recycling_view.views.MessageView
import com.example.rsestok.utilits.showToast


class ComentsChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listMessageCache = mutableListOf<ComentView>()


    private val holderList = mutableListOf<ComentHolder>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return ComentHolderFactory.getHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as ComentHolder).drawMessage(listMessageCache[position], listMessageCache)
    }


    override fun getItemCount(): Int = listMessageCache.size

    override fun getItemViewType(position: Int): Int {
        return listMessageCache[position].getTypeView()
    }

    fun addItemToBottom(item:ComentView, onSuccess: ()-> Unit){
        if(!listMessageCache.contains(item)){
            listMessageCache.add(item)
            notifyItemInserted(listMessageCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item:ComentView, onSuccess: ()-> Unit){
        if(!listMessageCache.contains(item)){
            listMessageCache.add(item)
            listMessageCache.sortBy{ it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }


    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as ComentHolder).onAttach(listMessageCache[holder.adapterPosition])
        holderList.add(holder)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as ComentHolder).onDetach()
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

