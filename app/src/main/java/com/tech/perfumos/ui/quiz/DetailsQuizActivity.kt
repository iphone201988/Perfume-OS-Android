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
import com.tech.perfumos.data.api.Constants

import com.tech.perfumos.data.api.Constants.QUIZ_CATEGORY_API
import com.tech.perfumos.data.api.Constants.RECENT_TOP_PERFUME_API
import com.tech.perfumos.databinding.ActivityDetailsQuizBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.dashboad.model.SearchHistoryModel
import com.tech.perfumos.ui.quiz.model.CreateQuizModel
import com.tech.perfumos.ui.quiz.model.QuizCategoryList
import com.tech.perfumos.ui.quiz.model.QuizCategoryModel
import com.tech.perfumos.utils.CommonFunctionClass
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class DetailsQuizActivity : BaseActivity<ActivityDetailsQuizBinding>() {
    private val viewModel: QuizVm by viewModels()
    private var quizData: QuizCategoryList? = null
    private var mode:String?="quick"
    private var playSolo: Boolean = false
    override fun getLayoutResource(): Int {
        return R.layout.activity_details_quiz
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

        setData()
    }

    private fun setData() {
        quizData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("quizType", QuizCategoryList::class.java)
        } else {
            intent.getSerializableExtra("quizType") as QuizCategoryList?
        }
        mode = intent.getStringExtra("mode")?: "quick"
        if (quizData != null) {

            val url  = quizData?.image
            if (url != null) {
                val imageUrl = if (url.contains("http")) {
                    url
                } else {
                    "${Constants.BASE_URL_IMAGE}$url"
                }
                Glide.with(this).load(imageUrl).into(binding.ivProfileImage)
            } else {
                Glide.with(this).load(R.drawable.earn_badge_img).into(binding.ivProfileImage)
            }

            binding.apply {
                quizTypeTitle.text = quizData?.title
                tvDesc.text = quizData?.description
                tvPlayedCount.text = "0"
            }
        }
    }

    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.tvPlaySolo -> {
                    playSolo = true

                    val hashMap = hashMapOf<String, Any>(
                        "mode" to mode.toString(),//ranked
//                        "mode" to "quick",//ranked
                        "quizType" to quizData?.type.toString(),
                        "playType" to "solo",
                        "quizCategory" to quizData?.id.toString()
                    )
                    viewModel.createQuizApi(Constants.CREATE_QUIZ_API, hashMap)
                }

                R.id.tvPlayWithFriend -> {
                    val hashMap = hashMapOf<String, Any>(
                        "mode" to mode.toString(),//ranked
//                        "mode" to "quick",
                        "quizType" to quizData?.type.toString(),
                        "playType" to "multiple",
                        "quizCategory" to quizData?.id.toString()
                    )
                    viewModel.createQuizApi(Constants.CREATE_QUIZ_API, hashMap)

                    /*val intent = Intent(this, InviteFriendsActivity::class.java).apply {
                        putExtra("quizType", quizData)
                    }
                    startActivity(intent)*/
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
                        Constants.CREATE_QUIZ_API -> {
                            try {
                                Log.d(
                                    "CREATE_QUIZ_API",
                                    "CreateQuizModel: ${Gson().toJson(it)}"
                                )
                                val response: CreateQuizModel? = parseJson(it.data.toString())

                                if (response?.success == true) {
                                    val quizDetails = response.data
                                    if(playSolo){
                                        CommonFunctionClass.logPrint(tag = "ANSWER_QUESTION", "${response}", true)
                                        val intent = Intent(this, QuizPlayActivity::class.java).apply {
                                            val quesQuestion = response.data?.questions
                                            putExtra("playType", "solo")
                                            putExtra("QuizDetails", quizDetails)
                                        }
                                        startActivity(intent)
                                    }else{

                                        val intent = Intent(this, InviteFriendsActivity::class.java).apply {
                                            putExtra("QuizDetails", quizDetails)
                                        }
                                        startActivity(intent)
                                    }
                                } else {
                                    showToast(response?.message.toString())
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