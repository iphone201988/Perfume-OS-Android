package com.tech.perfumos.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.JOIN_QUIZ_API
import com.tech.perfumos.data.api.Constants.RECENT_TOP_PERFUME_API
import com.tech.perfumos.data.api.Constants.SEND_QUIZ_INVITE_API
import com.tech.perfumos.databinding.ActivityJoinGameBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.dashboad.model.SearchHistoryModel
import com.tech.perfumos.ui.qrcode_scanner.QrcodeScanner
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class JoinGameActivity : BaseActivity<ActivityJoinGameBinding>() {
    private val viewModel: QuizVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_join_game
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        clickListener()
        initObserver()
        Glide.with(this).asGif().load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)


    }


    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.tvScanQr -> {
                    startActivity(Intent(this, QrcodeScanner::class.java))
                }

                R.id.tvJoinGame -> {
                    if (binding.edPin.text?.trim().toString().isNullOrEmpty()) {
                        showErrorToast("Please enter pin")
                    } else {
                        val hashMap = hashMapOf<String, Any>(
                            "roomId" to (binding.edPin.text?.trim().toString()
                                    )
                        )
                        viewModel.sendInvite(JOIN_QUIZ_API, hashMap)
                    }
                }

            }
        }
    }


    private fun initObserver() {
        viewModel.commonObserver.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading..")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        JOIN_QUIZ_API -> {
                            try {
                                Log.d("response", "RECENT_TOP_PERFUME_API: ${Gson().toJson(it)}")
                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: $jsonObject")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())

                                    val intent =
                                        Intent(this, JoinPlayerActivity::class.java).apply {
                                            putExtra("join", "2")
//                                            putExtra("join", "3")
                                            putExtra(
                                                "roomID",
                                                (binding.edPin.text?.trim().toString())
                                            )
                                        }
                                    startActivity(intent)

                                } else {
                                    showErrorToast(jsonObject.getString("message").toString())
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
}