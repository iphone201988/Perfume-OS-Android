package com.tech.perfumos.ui.dashboad.fragment_folder.change_password_folder


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.provider.ContactsContract.CommonDataKinds.Email
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson

import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.FORGOT_PASSWORD_API
import com.tech.perfumos.data.api.Constants.RESET_PASSWORD_API
import com.tech.perfumos.data.api.Constants.VERIFY_OTP_API

import com.tech.perfumos.databinding.ActivityChangePasswordBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.showErrorToast
import com.tech.perfumos.utils.showToast

import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class ChangePassword : BaseActivity<ActivityChangePasswordBinding>() {
    val viewmodel: ChangePasswordVm by viewModels()
    private var isPasswordVisible = false
    private var isConfPasswordVisible = false

    private lateinit var email: String
    private var type: Int = 3
    override fun getLayoutResource(): Int {
        return R.layout.activity_change_password
    }

    private lateinit var otpETs: Array<AppCompatEditText?>
    var isOtpComplete = false
    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)


        initView()
        initOnClick()
        initObserver()

    }

    private fun initObserver() {
        viewmodel.commonObserver.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading..")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        VERIFY_OTP_API -> {
                            try {
                                Log.d(
                                    "VERIFY_OTP_API",
                                    "VERIFY_OTP_API: ${Gson().toJson(it)}"
                                )
                                // val data: LoginModel? = Utils.parseJson(it.data.toString())

                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: ${jsonObject}")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())

                                    val token = jsonObject.getString("token")
                                    sharedPrefManager.saveUserToken(token)
                                    binding.layoutVisible = 1

                                }


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        RESET_PASSWORD_API -> {
                            try {
                                Log.d(
                                    "response",
                                    "RESET_PASSWORD_API: ${Gson().toJson(it)}"
                                )
                                // val data: LoginModel? = Utils.parseJson(it.data.toString())

                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: ${jsonObject}")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())
                                    if(intent.getIntExtra("from", 1) == 0){
                                        sharedPrefManager.clear()
                                    }

                                    binding.layoutVisible = 2

                                }


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        FORGOT_PASSWORD_API -> {
                            try {
                                Log.d(
                                    "FORGOT_PASSWORD_API",
                                    "FORGOT_PASSWORD_API: ${Gson().toJson(it)}"
                                )
                                // val data: LoginModel? = Utils.parseJson(it.data.toString())

                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: $jsonObject")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    try {
                        Log.e("initObserverJSONObject", "initObserver: ${it}")
                        val jsonObject = JSONObject(it.data.toString())
                        val message = jsonObject.getString("message")
                        showErrorToast(message)
                        if (it.code == 401) {
                            showErrorToast("Your login section is expire, Please login again")
                            startActivity(Intent(this, LoginActivity::class.java))
                            finishAffinity()
                            sharedPrefManager.clear()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {

                }
            }
        }
    }

    private fun initOnClick() {
        binding.layoutVisible = 0
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    finish()

                }

                R.id.ivShow -> {
                    isPasswordVisible = !isPasswordVisible
                    setPasswordVisibility()
                }

                R.id.ivConfShow -> {
                    isConfPasswordVisible = !isConfPasswordVisible
                    setCOnfPasswordVisibility()
                }

                R.id.btnVerify -> {

                        val otp =  binding.otpET1.text.toString()+binding.otpET2.text.toString()+binding.otpET3.text.toString()+binding.otpET4.text.toString()
                        Log.d("otpLength", "initOnClick: ${otp.length}")

                    if(otp.length <4){
                        showErrorToast("Please enter valid otp")
                    }else{
                        val requestMap = hashMapOf<String, Any>(
                            "email" to email,
                            "otp" to otp,
                        )
                        viewmodel.verifyOtpApi(VERIFY_OTP_API, requestMap)
                    }

                }

                R.id.getPassLayout -> {

                    if(binding.newPassword.text.isNullOrEmpty()){
                        showErrorToast("Please enter new password")
                    }else if(binding.confirmPassword.text.isNullOrEmpty()){
                        showErrorToast("Please enter confirm password")
                    }else if(binding.newPassword.text.toString() != binding.confirmPassword.text.toString()){
                        showErrorToast("Password didn't match")
                    }else{
                        val requestMap = hashMapOf<String, Any>(
                            "password" to binding.newPassword.text.toString(),
                        )
                        viewmodel.verifyOtpApi(RESET_PASSWORD_API, requestMap)
                    }

                }

                R.id.resendTv2 -> {
                    val requestMap = hashMapOf<String, Any>(
                        "email" to email,
                        "type" to 6,
                    )
                    viewmodel.resendApi(FORGOT_PASSWORD_API, requestMap)
                }
                R.id.getSuccessLayout -> {
                    val resultIntent = Intent()
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }

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
        email = intent.getStringExtra("email").toString()
        type = intent.getIntExtra("type", 3)
        Log.d("email", "initView: email $email, type $type from ${intent.getIntExtra("from", 0)}")
        if(intent.getIntExtra("from", 0) == 0){
            binding.btnBack.text = ContextCompat.getString(this, R.string.back_to_login)
            binding.clBackground.visibility = View.VISIBLE
        }else{
            binding.btnBack.text = ContextCompat.getString(this, R.string.back_to_homepage)
            binding.clBackground.visibility = View.GONE
        }

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

    private fun setPasswordVisibility() {
        if (isPasswordVisible) {
            // Show password
            binding.newPassword.transformationMethod = null
            binding.ivShow.setImageResource(R.drawable.icon_eye_hide)
        } else {
            // Hide password
            binding.newPassword.transformationMethod = PasswordTransformationMethod()
            binding.ivShow.setImageResource(R.drawable.icon_eye)
        }
        // Move cursor to the end of the text
        binding.newPassword.setSelection(binding.newPassword.text?.length!!)
    }

    private fun setCOnfPasswordVisibility() {

        if (isConfPasswordVisible) {
            // Show password
            binding.confirmPassword.transformationMethod = null
            binding.ivConfShow.setImageResource(R.drawable.icon_eye_hide)
        } else {
            // Hide password
            binding.confirmPassword.transformationMethod = PasswordTransformationMethod()
            binding.ivConfShow.setImageResource(R.drawable.icon_eye)
        }
        // Move cursor to the end of the text
        binding.confirmPassword.setSelection(binding.confirmPassword.text?.length!!)
    }

}