package com.tech.perfumos.ui.get_started_folder

import android.view.View
import androidx.fragment.app.viewModels
import com.tech.perfumos.R
import com.tech.perfumos.databinding.FragmentFirstPageBinding


import com.tech.perfumos.ui.base.BaseFragment
import com.tech.perfumos.ui.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstPageFragment : BaseFragment<FragmentFirstPageBinding>() {

    private val viewModel : FirstPageFragmentVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_first_page
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {

        val dataList : ArrayList<String> = arguments?.getStringArrayList("dataList") as ArrayList<String>
        val imageList : ArrayList<Int> = arguments?.getIntegerArrayList("imageList") as ArrayList<Int>
        val position = arguments?.getInt("position", -1)


        if(imageList != null && position != null && position >= 0 && position < imageList.size){
            binding.img.setImageResource(imageList[position])
        }

    }
}