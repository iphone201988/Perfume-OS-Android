package com.tech.perfumos.ui.dashboad.fragment_folder.term_of_use

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tech.perfumos.R
import com.tech.perfumos.databinding.ActivityPrivacyBinding
import com.tech.perfumos.databinding.ActivityTermOfUseBinding
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.camera_perfume.CameraActivity
import com.tech.perfumos.ui.dashboad.fragment_folder.privacy_folder.PrivacyActivityVm
import com.tech.perfumos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermOfUseActivity : BaseActivity<ActivityTermOfUseBinding>() {
    val viewmodel: TermOfUseActivityVm by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.activity_term_of_use
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        clickListener()
        Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)
    }

    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    finish()
                }
            }
        }
    }
}