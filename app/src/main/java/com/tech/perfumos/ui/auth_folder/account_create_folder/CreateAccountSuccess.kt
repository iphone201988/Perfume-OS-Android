package com.tech.perfumos.ui.auth_folder.account_create_folder

import android.content.Intent
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tech.perfumos.R
import com.tech.perfumos.databinding.ActivityCreateAccountSuccessBinding
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.onboarding_folder.OnboardingActivity
import com.tech.perfumos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountSuccess : BaseActivity<ActivityCreateAccountSuccessBinding>() {
    val viewmodel: CreateAccountSuccessVm by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.activity_create_account_success
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
                R.id.getSuccessLayout -> {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                }
            }
        }
    }
}