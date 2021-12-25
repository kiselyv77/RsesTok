package com.example.rsestok.utilits.media

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.rsestok.models.VideoModel

class VideoPagerAdapter2(fragment: Fragment, val dataList: MutableList<VideoModel> = mutableListOf()) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        return VideoViewFragment()
    }
}