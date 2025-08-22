package com.tech.perfumos.ui.auth_folder.forget_password

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.FORGOT_PASSWORD_API
import com.tech.perfumos.databinding.ActivityForgetPasswordBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.dashboad.fragment_folder.change_password_folder.ChangePassword
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class ForgetPassword : BaseActivity<ActivityForgetPasswordBinding>() {
    val viewmodel: ForgetPasswordVm by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.activity_forget_password
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        clickListener()
        initObserver()
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
                    if (binding.edEmail.text.toString().trim().isNullOrEmpty()) {
                        showErrorToast("Please enter valid email address")
                    }
                    else {

                        val requestMap = hashMapOf<String, Any>(
                            "email" to binding.edEmail.text.toString().trim().lowercase(),
                            "type" to 3,
                        )

                        viewmodel.restPasswordApi(FORGOT_PASSWORD_API, requestMap)
                    }
                    //startActivity(Intent(this, OtpVerification::class.java))
                }

                R.id.back_btn -> {
                    finish()
                }

                R.id.resendTv2 -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }
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
                                    val intent = Intent(
                                        this,
                                        ChangePassword::class.java
                                    )
                                    intent.putExtra("email", binding.edEmail.text.toString())
                                    intent.putExtra("type", 3)
                                    intent.putExtra("from", 0)
                                    startActivity(intent)
                                    finish()

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
                        Log.d("ERROR", "initObserver: ${Gson().toJson(it)}")
                        val jsonObject = JSONObject(it.data.toString())
                        Log.d("ERROR", "initObserver: $jsonObject")

                        Log.d("ErrorMessage", jsonObject.getString("message").toString())
                        val message = jsonObject.getString("message").toString()
                        showErrorToast(message)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {

                }
            }
        }
    }
}