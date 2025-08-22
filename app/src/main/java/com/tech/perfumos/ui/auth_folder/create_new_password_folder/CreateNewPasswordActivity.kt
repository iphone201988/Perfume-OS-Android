package com.tech.perfumos.ui.auth_folder.create_new_password_folder
import android.content.Intent
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tech.perfumos.R
import com.tech.perfumos.databinding.ActivityCreateNewPasswordBinding
import com.tech.perfumos.databinding.ActivityOtpVerificationBinding
import com.tech.perfumos.ui.auth_folder.password_changed_folder.PasswordChangeSuccess
import com.tech.perfumos.ui.auth_folder.verfication_otp_folder.OtpVerificationVm
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateNewPasswordActivity : BaseActivity<ActivityCreateNewPasswordBinding>() {
    val viewmodel: CreateNewPasswordActivityVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_create_new_password
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
                    startActivity(Intent(this, PasswordChangeSuccess::class.java))
                }
                R.id.back_btn -> {
                    finish()
                }
            }
        }
    }
}