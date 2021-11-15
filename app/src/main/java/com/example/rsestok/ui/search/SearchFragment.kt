package com.example.rsestok.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
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
import com.example.rsestok.utilits.APP_ACTIVITY
import com.example.rsestok.utilits.AppSearch
import com.example.rsestok.utilits.DialogChangeFullname
import com.example.rsestok.utilits.app_listeners.AppChildEventListener
import com.example.rsestok.utilits.app_listeners.AppValueEventListener
import com.example.rsestok.utilits.showToast
import com.google.android.exoplayer2.ExoPlayer


class SearchFragment : Fragment() {

    private val refUsers = REF_DATABASE_ROOT.child(NODE_USERS)

    private lateinit var searchViewModel: SearchViewModel
    private var _binding: ListUsersBinding? = null
    private lateinit var listenerUsers: AppValueEventListener
    val adapter = SearchAdapter()
    private lateinit var listUsers : List<UserModel>
    lateinit var searchView : SearchView


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = ListUsersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initToolbar()

        searchViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        return root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerView()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        refUsers.removeEventListener(listenerUsers)

    }


    private fun initRecyclerView() {
        val rcView = binding.rcView

        listenerUsers = AppValueEventListener{
            listUsers = it.children.map{ it.getUserModel() }.filter {it.fullname.toLowerCase().contains(searchView.query)}


            adapter.updateListItems(listUsers)

        }


        refUsers.addValueEventListener(listenerUsers)
        rcView.adapter = adapter
        rcView.layoutManager = LinearLayoutManager(APP_ACTIVITY)
    }
    private fun initToolbar(){
        searchViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.toolbar.title = it
        })
        binding.toolbar.inflateMenu(R.menu.search_bar)
        searchView = binding.toolbar.menu.findItem(R.id.appSearchBar).actionView as SearchView
        searchView.maxWidth = 5000
        searchView.queryHint = "Поиск"
        searchView.setOnQueryTextListener(AppSearch {newText->
            val filteredListUsers = listUsers.filter {it.fullname.toLowerCase().contains(newText.toString())}
            adapter.updateListItems(filteredListUsers)

        })
    }
}



