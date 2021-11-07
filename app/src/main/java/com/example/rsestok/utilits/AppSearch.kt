package com.example.rsestok.utilits

import android.widget.SearchView

class AppSearch(val Success:(String?)-> Unit): SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }
    override fun onQueryTextChange(newText: String?): Boolean {
        Success(newText)
        return true
    }
}