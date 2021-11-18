package com.example.rsestok.ui.messenger

import MessengerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.collection.arrayMapOf
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rsestok.*
import com.example.rsestok.databinding.FragmentMessengerBinding
import com.example.rsestok.models.ChatModel
import com.example.rsestok.utilits.*
import com.example.rsestok.utilits.app_listeners.AppValueEventListener
import com.google.firebase.database.DatabaseReference

class MessengerFragment : Fragment() {

    private lateinit var messengerViewModel: MessengerViewModel
    private var _binding: FragmentMessengerBinding? = null

    private lateinit var rcView: RecyclerView
    private lateinit var adapter: MessengerAdapter
    private val refChatList = REF_DATABASE_ROOT.child(NODE_CHAT_LIST).child(CURRENT_UID)
    private val refUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var listChats = listOf<ChatModel>()
    private var listChatsInit = arrayListOf<ChatModel>()
    val mapListeners = arrayMapOf<DatabaseReference, AppValueEventListener>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        messengerViewModel = ViewModelProvider(this).get(MessengerViewModel::class.java)

        _binding = FragmentMessengerBinding.inflate(inflater, container, false)


        initToolbar()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerView() }


    fun initRecyclerView(){
        rcView = binding.rcView
        adapter = MessengerAdapter()
        //1 request
        val request1 = AppValueEventListener{
            listChats = it.children.map { it.getChatModel() }
            listChats.forEach {model->
                when(model.type){
                    TYPE_CHAT -> showChat(model)
                }
            }
        }

        refChatList.addListenerForSingleValueEvent(request1)
        mapListeners[refChatList] = request1

        rcView.adapter = adapter
        rcView.layoutManager = LinearLayoutManager(APP_ACTIVITY) }

    private fun showChat(model: ChatModel) {

        refUsers.child(model.id).addValueEventListener(AppValueEventListener{
            val newModel = it.getChatModel()

            //2 request
            val request2 = AppValueEventListener{
                val tempList = it.children.map { it.getMessageModel() }
                if(tempList.isEmpty()){ newModel.lastMessage = "Чат отчищен" }
                else if (tempList[0].type == TYPE_MESSAGE_VOICE){  newModel.lastMessage = "Голосовое сообщение"}
                else if (tempList[0].type == TYPE_MESSAGE_VIDEO){  newModel.lastMessage = "Видео"}
                else{ newModel.lastMessage = tempList[0].text }
                adapter.addItemToBottom(newModel)
            }
            refMessages.child(model.id).limitToLast(1).addValueEventListener(request2)
            mapListeners[refMessages] = request2
        })
    }

    private fun initToolbar() {
        messengerViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.toolbar.title = it
        })

        binding.toolbar.inflateMenu(R.menu.search_bar)
        val searchView = binding.toolbar.menu.findItem(R.id.appSearchBar).actionView as SearchView
        searchView.maxWidth = 5000
        searchView.queryHint = "Поиск"
        searchView.setOnQueryTextListener(AppSearch{newText->
//            val filteredListUsers = adapter.listChats.filter {it.fullname.toLowerCase().contains(newText.toString())}
//            adapter.updateListItems(filteredListUsers)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }
}

