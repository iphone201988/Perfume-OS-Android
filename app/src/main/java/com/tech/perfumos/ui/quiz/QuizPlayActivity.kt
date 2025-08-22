package com.tech.perfumos.ui.quiz

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tech.perfumos.BR
import com.tech.perfumos.R

import com.tech.perfumos.data.api.Constants.SUBMIT_QUIZ_API
import com.tech.perfumos.databinding.ActivityQuizPlayBinding
import com.tech.perfumos.databinding.AnsItemViewBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.dashboad.model.SearchHistoryModel
import com.tech.perfumos.ui.quiz.model.AnsListModel
import com.tech.perfumos.ui.quiz.model.QuizDetails
import com.tech.perfumos.ui.quiz.model.QuizQuestion
import com.tech.perfumos.ui.quiz.model.StartQuesModel
import com.tech.perfumos.ui.quiz.model.SubmitAnswers
import com.tech.perfumos.ui.quiz.model.SubmitQuizModel
import com.tech.perfumos.ui.quiz.model.SubmitQuizRequest
import com.tech.perfumos.utils.GridSpacingItemDecoration
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class QuizPlayActivity : BaseActivity<ActivityQuizPlayBinding>() {
    private val viewModel: QuizVm by viewModels()

    private var questionList: ArrayList<QuizQuestion?>? = ArrayList()
    private var ansList = ArrayList<AnsListModel>()

    //    private var quizDetails: QuizDetails? = null
    private var quizDetailsId: String? = null

    private var submitQuizRequest: SubmitQuizRequest? = null


    private var quizPos = 0
    override fun getLayoutResource(): Int {
        return R.layout.activity_quiz_play
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

        setUpRecyclerview()
        /*startQuiz(quizPos)
        progressStatus(((1.toFloat() / questionList.size) * 100).toInt())*/

        if (intent.hasExtra("playType") && intent.getStringExtra("playType").equals("solo")) {
            val quizDetails: QuizDetails? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra("QuizDetails", QuizDetails::class.java)
                } else {
                    intent.getSerializableExtra("QuizDetails") as QuizDetails?
                }

            if (!quizDetails?.questions.isNullOrEmpty()) {
                Log.i("asdasdsadfasdfasd", ""+Gson().toJson(quizDetails))
//                quizDetailsId=quizDetails.
                questionList = quizDetails?.questions
                quizPos = 0
                showQuestion(quizPos)
                progressStatus((((quizPos + 1).toFloat() / questionList?.size!!) * 100).toInt())
                /* questionList?.forEach {
                     it?.options?.forEach { option->
                         ansList.add(AnsListModel(option.toString(),
                             option.equals(it.correctAnswer), false))
                     }
                 }*/
                quizDetailsId = quizDetails?.id
            }
        } else {
            val quizData = intent?.getStringExtra("QuizDetails")
            val response: StartQuesModel? = parseJson(quizData.toString())
            quizDetailsId = response?.getQuiz?._id.toString()
//            questionList = Gson().toJson(response?.nameValuePairs!!.getQuiz.nameValuePairs.questions.values)
           /* val gson = Gson()
            val listType = object : TypeToken<List<QuizQuestion>>() {}.type

            val wrapperList: List<QuizQuestion> = gson.fromJson(Gson().toJson(response?.nameValuePairs!!.getQuiz.nameValuePairs.questions.values), listType)
            questionList =ArrayList(wrapperList)*/
            val gson = Gson()
            val listType = object : TypeToken<List<QuizQuestion>>() {}.type
            val wrapperList: List<QuizQuestion> = gson.fromJson(
                gson.toJson(response?.getQuiz?.questions),
                listType
            )
            questionList = ArrayList(wrapperList)
            quizPos = 0
            showQuestion(quizPos)
            progressStatus((((quizPos + 1).toFloat() / questionList?.size!!) * 100).toInt())
        }
    }

    private fun showQuestion(index: Int) {
        if (index in questionList?.indices!!) {
            val question = questionList?.get(index)
            // Set up UI for the single question
            binding.tvQues.text = question?.questionText
            ansList.clear()
            question?.options?.forEach { option ->
                ansList.add(
                    AnsListModel(
                        option.toString(),
                        option == question.correctAnswer,
                        false
                    )
                )
            }

            ansAdapter.list = ansList
            ansAdapter.setShowAnswers(false)
            ansAdapter.notifyDataSetChanged()
            // Reset selection and enable answer submit
            //  binding.submitButton.isEnabled = false
        } else {
            // Quiz ended
            //showQuizResult()
        }
    }

    private fun startQuiz(pos: Int) {
        if (!questionList.isNullOrEmpty() && (pos < questionList!!.size)) {

            binding.tvQues.text = questionList!![pos]?.questionText ?: ""



            ansAdapter.list = ansList
            ansAdapter.setShowAnswers(false)
            ansAdapter.notifyDataSetChanged()


        }

    }


    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.tvNext -> {

                    if (submitQuizRequest != null) {
                        ansAdapter.setShowAnswers(true)
                        ansAdapter.notifyDataSetChanged()
                        viewModel.submitQuizApi(SUBMIT_QUIZ_API, submitQuizRequest!!)
                    } else {
                        showErrorToast("Please select option")
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
                        SUBMIT_QUIZ_API -> {
                            try {
                                Log.d("response", "RECENT_TOP_PERFUME_API: ${Gson().toJson(it)}")
                                val response: SubmitQuizModel? = parseJson(it.data.toString())
                                Log.d("response", "RECENT_TOP_PERFUME_API : ${response?.success}")

                                if (response?.success == true) {
                                    submitQuizRequest = null
                                    quizPos += 1
                                    if (quizPos < questionList?.size!!) {

                                        /*ansAdapter.setShowAnswers(true)
                                        ansAdapter.notifyDataSetChanged()*/
                                        Handler(Looper.getMainLooper()).postDelayed({

                                            showQuestion(quizPos)
                                            progressStatus((((quizPos + 1).toFloat() / questionList?.size!!) * 100).toInt())
                                        }, 1000)
                                        /*   val selectedAnswer = ansList.find { it.ansSelected }?.ansSelected ?: false
                                        if (selectedAnswer) {
                                        } else {
                                            showErrorToast("Please select option")
                                        }*/

                                    } else {
                                        ansAdapter.setShowAnswers(true)
                                        ansAdapter.notifyDataSetChanged()

                                        val solo =
                                            if (intent.hasExtra("playType") && intent.getStringExtra(
                                                    "playType"
                                                ).equals("solo")
                                            ) "solo" else "multiple"
                                        val intent = Intent(this, ScoreActivity::class.java).apply {
                                            putExtra("playType", solo)
                                            putExtra("quizResult", response)
                                            putExtra("quizDetailsId", quizDetailsId)
                                        }
                                        startActivity(intent)
                                        finish()


                                    }

                                } else {
                                    showToast(response?.message)
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

    private fun progressStatus(i: Int) {
        Log.d("progressStatus", "progressStatus: $i")
        ObjectAnimator.ofInt(binding.skProgress, "progress", binding.skProgress.progress, i).apply {
            duration = 500
            start()
        }
        if (i == 100) {
            binding.skProgress.showHideProgressShadow(false)
        } else {
            binding.skProgress.showHideProgressShadow(true)
        }
        binding.tvQuesCount.text = "${quizPos + 1}/${questionList?.size}"
    }


    private lateinit var ansAdapter: SimpleRecyclerViewAdapter<AnsListModel, AnsItemViewBinding>
    private fun setUpRecyclerview() {

        ansAdapter = SimpleRecyclerViewAdapter(
            R.layout.ans_item_view, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.clMain -> {
                    if (!ansAdapter.showsAns) {
                        ansList.forEach {
                            it.ansSelected = false
                        }
                        m.ansSelected = true
                        // ansAdapter.setShowAnswers(true)
                        ansAdapter.notifyDataSetChanged()

                        submitQuizRequest = SubmitQuizRequest(
                            quizDetailsId.toString()/*quizDetails?.id.toString()*/, SubmitAnswers(
                                questionList?.get(quizPos)?.id.toString(), m.ans
                            )
                        )


                    }
                }
            }
        }
        binding.rvAns.adapter = ansAdapter
        ansAdapter.list = ansList

        val spacingInPixels = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
        binding.rvAns.addItemDecoration(
            GridSpacingItemDecoration(
                2, spacingInPixels, true
            )
        )

    }
}