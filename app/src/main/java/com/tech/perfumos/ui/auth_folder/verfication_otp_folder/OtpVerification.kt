package com.tech.perfumos.ui.auth_folder.verfication_otp_folder

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tech.perfumos.R
import com.tech.perfumos.databinding.ActivityForgetPasswordBinding
import com.tech.perfumos.databinding.ActivityOtpVerificationBinding
import com.tech.perfumos.ui.auth_folder.create_new_password_folder.CreateNewPasswordActivity
import com.tech.perfumos.ui.auth_folder.forget_password.ForgetPasswordVm
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OtpVerification : BaseActivity<ActivityOtpVerificationBinding>() {
    val viewmodel: OtpVerificationVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_otp_verification
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    private lateinit var otpETs: Array<AppCompatEditText?>
    var isOtpComplete = false
    override fun onCreateView() {
        Utils.screenFillView(this)
        clickListener()
        initOnClick()
        initView()

        Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)
    }

    private fun initOnClick() {

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                Log.d("Keyboard", "Visible")
            } else {
                binding.apply {
                    otpET1.clearFocus()
                    otpET2.clearFocus()
                    otpET3.clearFocus()
                    otpET4.clearFocus()
                }

            }
        }

        binding.otpET1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    binding.otpET1.setBackgroundResource(R.drawable.green_btn_bg)
                } else {
                    binding.otpET1.setBackgroundResource(R.drawable.track_background)
                }
            }

        })

        binding.otpET2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    binding.otpET2.setBackgroundResource(R.drawable.green_btn_bg)
                } else {
                    binding.otpET2.setBackgroundResource(R.drawable.track_background)
                }
            }

        })

        binding.otpET3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    binding.otpET3.setBackgroundResource(R.drawable.green_btn_bg)
                } else {
                    binding.otpET3.setBackgroundResource(R.drawable.track_background)
                }
            }

        })

        binding.otpET4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    binding.otpET4.setBackgroundResource(R.drawable.green_btn_bg)
                } else {
                    binding.otpET4.setBackgroundResource(R.drawable.track_background)
                }
            }

        })


    }

    private fun initView() {
        otpETs = arrayOf(
            binding.otpET1,
            binding.otpET2,
            binding.otpET3,
            binding.otpET4
        )
        otpETs.forEachIndexed { index, editText ->
            editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty() && index != otpETs.size - 1) {
                        otpETs[index + 1]?.requestFocus()
                    }

                    // Check if all OTP fields are filled
                    isOtpComplete = otpETs.all { it!!.text?.isNotEmpty() == true }

                }
            })

            editText?.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text?.isEmpty() == true && index != 0) {
                        otpETs[index - 1]?.apply {
                            text?.clear()  // Clear the previous EditText before focusing
                            requestFocus()
                        }
                    }
                }
                // Check if all OTP fields are filled
                isOtpComplete = otpETs.all { it!!.text?.isNotEmpty() == true }

                false
            }
        }
    }

    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.getVerifyLayout -> {
                    startActivity(Intent(this, CreateNewPasswordActivity::class.java))
                }

                R.id.back_btn -> {
                    finish()
                }
                R.id.resendTv2 -> {
                    showToast("otp is sent again")
                    /*startActivity(Intent(this, LoginActivity::class.java))
                    finish()*/
                }
            }
        }
    }
}