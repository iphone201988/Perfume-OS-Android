package com.tech.perfumos.ui.quiz

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.RECENT_TOP_PERFUME_API
import com.tech.perfumos.databinding.ActivityScoreBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.dashboad.model.SearchHistoryModel
import com.tech.perfumos.ui.quiz.model.QuizDetails
import com.tech.perfumos.ui.quiz.model.SubmitQuizModel
import com.tech.perfumos.ui.quiz_scorebroard.QuizScorebroard
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ScoreActivity : BaseActivity<ActivityScoreBinding>() {
    private val viewModel: QuizVm by viewModels()

    var quizResult: SubmitQuizModel? = null
    var playType: String? = null
    override fun getLayoutResource(): Int {
        return R.layout.activity_score
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
    var quizDetailsId:String?=null
    override fun onCreateView() {
        Utils.screenFillView(this)
        clickListener()
        initObserver()
        Glide.with(this).asGif().load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)

        if (intent.hasExtra("playType") && intent.getStringExtra("playType").equals("solo")) {
            //      binding.tvJoinGame.visibility = View.GONE

        } else {
            //  binding.tvJoinGame.visibility = View.VISIBLE
        }
        playType=  intent.getStringExtra("playType")

          quizDetailsId= intent.getStringExtra("quizDetailsId")
        quizResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("quizResult", SubmitQuizModel::class.java)
        } else {
            intent.getSerializableExtra("quizResult") as SubmitQuizModel?
        }
        Log.i("asdasdsadfasdfasd", ": "+Gson().toJson(quizResult))
        Log.i("asdasdsadfasdfasd", ": "+intent.getStringExtra("playType"))
//        binding.tvQuizResult.text = quizResult?.pointsEarned.toString()
//        binding.tvYourScore.text = quizResult?.pointsEarned.toString()
        if (quizResult != null) {
            binding.bean = quizResult
        }
    }


    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.tvScoreboard -> {
                    if(playType.toString().trim() == "multiple"){
                        val intent = Intent(this, QuizScorebroard::class.java).apply {
                            putExtra("quizId", "SeeScoreboard")
                            putExtra("quizDetailsId", quizDetailsId)
                        }
                        startActivity(intent)
                        finish()
                    }
                    else{

                        val intent = Intent(this, QuizActivity::class.java).apply {
                            putExtra("route_nav", "SeeScoreboard")
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
                        finish()
                    }

                }

                R.id.tvDone -> {
                    val intent = Intent(this, QuizActivity::class.java).apply {
                        putExtra("route_nav", "Done")
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                    finish()
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
                        RECENT_TOP_PERFUME_API -> {
                            try {
                                Log.d("response", "RECENT_TOP_PERFUME_API: ${Gson().toJson(it)}")
                                val data: SearchHistoryModel? = parseJson(it.data.toString())
                                Log.d("response", "RECENT_TOP_PERFUME_API : ${data?.success}")

                                if (data?.data != null) {

                                } else {
                                    showToast(data?.message)
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