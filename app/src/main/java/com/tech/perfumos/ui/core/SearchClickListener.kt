package com.tech.perfumos.ui.core

import androidx.fragment.app.Fragment
import com.tech.perfumos.ui.camera_perfume.model.PerfumeInfoModel

interface SearchClickListener {
    fun onSearchClick()
    fun onThemeSwitch(dark : Boolean)
    fun writeReviewClick()
    fun loadFragment(fragment: Fragment)
    fun openPerfumeScreen(perfumeId: String)

}