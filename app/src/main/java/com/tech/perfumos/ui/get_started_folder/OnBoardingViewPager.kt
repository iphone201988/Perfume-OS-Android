package com.tech.perfumos.ui.get_started_folder

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

import com.tech.perfumos.R

class OnBoardingViewPager(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        val fragment = FirstPageFragment()
        fragment.arguments = Bundle().apply {
            val dataList = getDataList()
            val imageList = getImageList()
            putStringArrayList("dataList", ArrayList(dataList))
            putIntegerArrayList("imageList", ArrayList(imageList))
            putInt("position", position)
        }
        return fragment
    }


    private fun getDataList(): List<String> {
        val list = ArrayList<String>()
        list.add("Track, and manage inventory effortlessly")
        list.add("Boost productivity with rapid 1D barcode scanning")
        list.add("Learn more about products by scanning QR code")
        list.add("Verify various forms of identification documents")

        return list
    }

    private fun getImageList(): List<Int> {
        val list = ArrayList<Int>()
        list.add(R.drawable.get_started_dot_1)
        list.add(R.drawable.get_started_dot_2)
        list.add(R.drawable.get_started_dot_3)
        list.add(R.drawable.get_started_dot_4)

        return list
    }

    override fun getItemCount(): Int {
        return 4
    }

}

