package com.tech.perfumos.ui.auth_folder.password_changed_folder

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
import com.tech.perfumos.databinding.ActivityCreateNewPasswordBinding
import com.tech.perfumos.databinding.ActivityPasswordChangeSuccessBinding
import com.tech.perfumos.ui.auth_folder.create_new_password_folder.CreateNewPasswordActivityVm
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PasswordChangeSuccess : BaseActivity<ActivityPasswordChangeSuccessBinding>() {
    val viewmodel: PasswordChangeSuccessVm by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.activity_password_change_success
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
                R.id.getVerifyLayout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                R.id.back_btn -> {
                    finish()
                }
            }
        }
    }
}
