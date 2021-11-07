package com.example.rsestok.ui.listUsers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rsestok.NODE_USERS
import com.example.rsestok.R
import com.example.rsestok.REF_DATABASE_ROOT
import com.example.rsestok.databinding.ListUsersBinding
import com.example.rsestok.getUserModel
import com.example.rsestok.models.UserModel
import com.example.rsestok.ui.search.SearchAdapter
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.APP_NAV_CONTROLLER
import com.example.rsestok.utilits.AppSearch
import com.example.rsestok.utilits.app_listeners.AppChildEventListener
import com.example.rsestok.utilits.app_listeners.AppValueEventListener

class FragmentListUsers : Fragment() {

    private val refUsers = REF_DATABASE_ROOT.child(NODE_USERS)

    private lateinit var searchViewModel: ListUsersViewModel
    private var _binding: ListUsersBinding? = null

    private lateinit var listenerUsers: AppValueEventListener

    val adapter = SearchAdapter()
    var listSubscribers = ArrayList<UserModel>()
    lateinit var searchView : SearchView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        searchViewModel = ViewModelProvider(this).get(ListUsersViewModel::class.java)

        _binding = ListUsersBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initToolbar()




        return root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerView()

        APP_ACTIVITY.goneNavView()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        refUsers.removeEventListener(listenerUsers)
        listSubscribers.clear()
    }


    private fun initRecyclerView() {
        val rcView = binding.rcView

        listenerUsers = AppValueEventListener{
            listSubscribers = it.children.map{ it.getUserModel() }.filter {it.fullname.toLowerCase().contains(searchView.query)} as ArrayList<UserModel>
            val list = arguments?.getStringArrayList("list")

            listSubscribers.forEach {
                if (list!!.contains(it.id)){
                    adapter.updateListItems(listSubscribers)
                }
            }


        }

        refUsers.addValueEventListener(listenerUsers)
        rcView.adapter = adapter
        rcView.layoutManager = LinearLayoutManager(APP_ACTIVITY)
    }
    private fun initToolbar(){
        searchViewModel.title.observe(viewLifecycleOwner, Observer {
            binding.toolbar.title = it
        })
        binding.toolbar.setNavigationIcon(R.drawable.toolbar_back)
        binding.toolbar.setNavigationOnClickListener(View.OnClickListener { // Your code
            APP_NAV_CONTROLLER.popBackStack()
        })

        binding.toolbar.inflateMenu(R.menu.search_bar)
        searchView = binding.toolbar.menu.findItem(R.id.appSearchBar).actionView as SearchView
        searchView.maxWidth = 5000
        searchView.queryHint = "Поиск"
        searchView.setOnQueryTextListener(AppSearch { newText->
            val filteredListSubscribers = listSubscribers.filter {it.fullname.toLowerCase().contains(newText.toString())}
            adapter.updateListItems(filteredListSubscribers)
        })
    }

}



