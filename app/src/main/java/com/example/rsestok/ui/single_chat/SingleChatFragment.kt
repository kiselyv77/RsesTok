package com.example.rsestok.ui.single_chat
import SingleChatViewModel
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.rsestok.*
import com.example.rsestok.databinding.FragmentSingleChatBinding
import com.example.rsestok.ui.single_chat.message_recycling_view.views.AppViewFactory
import com.example.rsestok.utilits.*
import com.example.rsestok.utilits.app_listeners.AppChildEventListener
import com.example.rsestok.utilits.factory.SingleChatFactory
import com.example.rsestok.utilits.media.AppVoiceRecorder
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleChatFragment() : Fragment() {

    private lateinit var singleChatViewModel: SingleChatViewModel
    private var _binding: FragmentSingleChatBinding? = null
    private var smoothScrollToPosition = true

    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessageListener: AppChildEventListener
    private var countMessages = 15
    private var isScrolling = false
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var voiceRecorder: AppVoiceRecorder


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var uid:String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        uid = arguments?.getString("uid")
        singleChatViewModel = ViewModelProvider(this, SingleChatFactory(APP_ACTIVITY.application, uid!!)).get(SingleChatViewModel::class.java)

        _binding = FragmentSingleChatBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initFields()
        initRecyclerView()
        initToolbar()
        APP_ACTIVITY.goneNavView()
    }



    @SuppressLint("ClickableViewAccessibility")
    fun initFields(){
        layoutManager = LinearLayoutManager(this.context)
        swipeRefreshLayout = binding.swipeRefresh
        voiceRecorder = AppVoiceRecorder()
        binding.chatInputMessage.addTextChangedListener(AppTextWatcher{
            if(binding.chatInputMessage.text.isEmpty())
            {
                binding.sendBtn.visibility = View.INVISIBLE
                binding.voiceBtn.visibility = View.VISIBLE
            }
            else
            {
                binding.sendBtn.visibility = View.VISIBLE
                binding.voiceBtn.visibility = View.INVISIBLE
            }
        })

        binding.sendBtn.setOnClickListener {
            smoothScrollToPosition = true
            val message = binding.chatInputMessage.text.toString()
            if (message.isEmpty()) showToast("Введите сообщение")
            else sendMessage(message, uid!!, TYPE_MESSAGE_TEXT) {
                saveMainList(uid!!, TYPE_CHAT)
                binding.chatInputMessage.setText("")
            }

        }

        CoroutineScope(Dispatchers.IO).launch{
            binding.voiceBtn.setOnTouchListener { v, event ->
                if(checkPermission(RECORD_AUDIO)){
                    if(event.action == MotionEvent.ACTION_DOWN){
                        //record
                        binding.chatInputMessage.setText("Запись")
                        binding.sendBtn.visibility = View.INVISIBLE
                        binding.voiceBtn.visibility = View.VISIBLE
                        binding.voiceBtn.setColorFilter(ContextCompat.getColor(APP_ACTIVITY, R.color.white))
                        voiceRecorder.startRecord(getMessageKey(uid!!))
                    } else if(event.action == MotionEvent.ACTION_UP){
                        //stop record
                        binding.chatInputMessage.setText("")
                        binding.voiceBtn.setColorFilter(null)
                        voiceRecorder.stopRecord{file, messageKey ->
                            uploadFileToStorage(Uri.fromFile(file), messageKey, uid!!, TYPE_MESSAGE_VOICE)
                            smoothScrollToPosition = true
                        }
                    }
                }
                true
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.chatRcView
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(uid!!)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = layoutManager
        mMessageListener = AppChildEventListener{
            val message = it.getMessageModel()
            if(smoothScrollToPosition){
                mAdapter.addItemToBottom(AppViewFactory.getView(message)){
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            }else{
                mAdapter.addItemToTop(AppViewFactory.getView(message)){
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }


        mRefMessages.limitToLast(countMessages).addChildEventListener(mMessageListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(isScrolling&&dy<0&& layoutManager.findLastVisibleItemPosition() <= 3){
                    updateMessageData()
                }
            }
        })
        swipeRefreshLayout.setOnRefreshListener{updateMessageData()}
    }

    private fun updateMessageData() {
        smoothScrollToPosition = false
        isScrolling = false
        countMessages += 10
        mRefMessages.removeEventListener(mMessageListener)
        mRefMessages.limitToLast(countMessages).addChildEventListener(mMessageListener)
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.goneNavView()
    }

    override fun onStop(){
        super.onStop()
        APP_ACTIVITY.visibleNavView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mRefMessages.removeEventListener(mMessageListener)
    }


    private fun initToolbar() {
        singleChatViewModel.name.observe(viewLifecycleOwner,Observer {
            binding.toolbarSingleChat.title = it
        })
        binding.toolbarSingleChat.setNavigationIcon(R.drawable.toolbar_back)
        binding.toolbarSingleChat.setNavigationOnClickListener(View.OnClickListener { // Your code
            APP_NAV_CONTROLLER.popBackStack()
        })

        binding.toolbarSingleChat.inflateMenu(R.menu.single_chat_actions_menu)

        binding.toolbarSingleChat.menu.getItem(0).setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                clearChat(uid!!){
                    showToast("чат отчищен")
                    mAdapter.clear()
                }
                return true
            }
        })
        binding.toolbarSingleChat.menu.getItem(1).setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                deleteChat(uid!!){
                    showToast("чат удален")
                    APP_NAV_CONTROLLER.popBackStack()
                }
                return true
            }
        })
    }

}

