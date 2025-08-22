package com.tech.perfumos.ui.quiz

import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants

import com.tech.perfumos.data.api.Constants.RECENT_TOP_PERFUME_API
import com.tech.perfumos.databinding.ActivityJoinPlayerBinding
import com.tech.perfumos.databinding.JoinedFriendItemViewBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.camera_perfume.model.NoteModel
import com.tech.perfumos.ui.dashboad.model.SearchHistoryModel
import com.tech.perfumos.ui.quiz.model.InviteFriendList
import com.tech.perfumos.ui.quiz.model.JoinPlayerModel

import com.tech.perfumos.ui.quiz.model.QuizDetails
import com.tech.perfumos.ui.quiz.model.StartQuesModel
import com.tech.perfumos.utils.CommonFunctionClass

import com.tech.perfumos.utils.SocketManagerHelper
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class JoinPlayerActivity : BaseActivity<ActivityJoinPlayerBinding>() {

    private val viewModel: QuizVm by viewModels()
    private var quizData: String? = null
    private var join: String? = null

    //    private var quizData: QuizDetails? = null
    private val inviteFriendList: ArrayList<InviteFriendList?> = ArrayList()


    override fun getLayoutResource(): Int {
        return R.layout.activity_join_player
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        Glide.with(this).asGif().load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)
        quizData = intent?.getStringExtra("roomID")
        join = intent?.getStringExtra("join")
        Handler(Looper.getMainLooper()).post {
            CommonFunctionClass.logPrint(tag = "JOINJOINJOINJOIN", response = join.toString())
            if (join.toString() == "1") {
                binding.tvJoinGame.text = "Waiting for other player to join"
            } else if (join.toString() == "2") {
                binding.tvJoinGame.text = "Wait for Host to Start Quiz..."
            }
        }

        binding.tvJoinGame.isEnabled = false
        CommonFunctionClass.logPrint(tag = "ROOM_ID", response = quizData.toString())
        clickListener()
        initObserver()
        setUpRecyclerview()
        Handler(Looper.getMainLooper()).post {
            socketConnect()
        }

//        timerCall()


    }

    private fun timerCall(dataJson: String) {

        val timer = object : CountDownTimer(10_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1_000
                binding.tvJoinGame.text = "Starting Quiz in $secondsLeft sec"
            }

            override fun onFinish() {
                val intent = Intent(this@JoinPlayerActivity, QuizPlayActivity::class.java).apply {
                    putExtra("playType", "multiple")
                    putExtra("QuizDetails", dataJson.toString())
                }
                startActivity(intent)

            }
        }
        timer.start()
    }

    private fun startQuiz() {
        showLoading("Loading..")
        CommonFunctionClass.logPrint(tag = "ROOM_ID", response = quizData.toString())
        val msg = JSONObject().apply { put("roomId", quizData.toString()) }
        SocketManagerHelper.emitEvent("startQuiz", msg)
    }

    private fun socketConnect() {
        Handler(Looper.getMainLooper()).post {
            SocketManagerHelper.init(sharedPrefManager.getUserID().toString())
            CommonFunctionClass.logPrint(tag = "ROOM_ID", response = quizData.toString())
            val msg = JSONObject().apply { put("roomId", quizData.toString()) }
            SocketManagerHelper.emitEvent("joinQuiz", msg)
        }
        SocketManagerHelper.listenEvent("joinQuiz", callback = { data ->
            CommonFunctionClass.logPrint(tag = "SOCKET_MANAGER joinQuiz->", "$data")
            val response: JoinPlayerModel? = parseJson(data.toString())
            if (response != null) {
                if (response.data != null) {
                    CommonFunctionClass.logPrint(
                        tag = "SOCKET_MANAGER joinQuiz->",
                        "${response.data.players.size}"
                    )
                    for (i in response.data.players.indices) {


                        Handler(Looper.getMainLooper()).post {
                            if (inviteFriendAdapter.list.none { it?.id == response.data.players[i].userId._id }) {

                                if (response.data.hostId.toString() == sharedPrefManager.getUserID()
                                        .toString()
                                ) {
                                    inviteFriendAdapter.addData(
                                        InviteFriendList(
                                            fullname = if (response.data.players[i].userId._id.toString() == response.data.hostId.toString())
                                                "You" else response.data.players[i].userId.fullname,
//                                        fullname =response.data.players[i].userId.fullname,
                                            id = response.data.players[i].userId._id,
                                            profileImage = Constants.BASE_URL_IMAGE + response.data.players[i].userId.profileImage,
                                            isSelected = false,
                                        )
                                    )
                                } else {
                                    inviteFriendAdapter.addData(
                                        InviteFriendList(
                                            fullname = if (response.data.players[i].userId._id.toString() == response.data.hostId.toString())
                                                "${response.data.players[i].userId.fullname} (Host)" else (if(response.data.players[i].userId._id.toString()== sharedPrefManager.getUserID())"You" else response.data.players[i].userId.fullname  ),
//                                        fullname =response.data.players[i].userId.fullname,
                                            id = response.data.players[i].userId._id,
                                            profileImage = Constants.BASE_URL_IMAGE + response.data.players[i].userId.profileImage,
                                            isSelected = false,
                                        )
                                    )
                                }


                            }
                        }

                    }
                }
                Handler(Looper.getMainLooper()).post {
                    binding.quizTypeTitle.text = response.data?.quizCategory?.title
                    binding.quizTypeTitle.text = response.data?.quizCategory?.title

                    binding.tvPlayerNo.text = "${response.data?.players?.size} ${
                        ContextCompat.getString(
                            this@JoinPlayerActivity,
                            R.string.players_have_joined
                        )
                    }"
                    Utils.loadImage(binding.ivProfileImage,response.data?.quizCategory?.image)
                    if (join == "1") {
                        if ((inviteFriendAdapter.list.size ?: 0) >= 2) {
                            binding.tvJoinGame.isEnabled = true
                            binding.tvJoinGame.text = "Start Quiz"
                        }
                    }
                }
            }
        })
        SocketManagerHelper.listenEvent("startQuiz", callback = { data ->
            CommonFunctionClass.logPrint(tag = "ANSWER_QUESTION", "${data}", true)
//            val response: StartQuesModel? = parseJson(data.toString())

//            if (join.toString() == "1") {
//                val intent = Intent(this, QuizPlayActivity::class.java).apply {
//                    putExtra("playType", "multiple")
//                    putExtra("QuizDetails", data.toString())
//                }
//                startActivity(intent)
//            }
            Handler(Looper.getMainLooper()).post {
                binding.tvJoinGame.isEnabled = false
                hideLoading()
                timerCall(data.toString())

            }
        })
    }


    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.tvJoinGame -> {
                    startQuiz()
//                    startActivity(Intent(this, QuizPlayActivity::class.java))
//                    finish()
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

    private lateinit var inviteFriendAdapter: SimpleRecyclerViewAdapter<InviteFriendList, JoinedFriendItemViewBinding>
    private fun setUpRecyclerview() {

        inviteFriendAdapter = SimpleRecyclerViewAdapter(
            R.layout.joined_friend_item_view, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.clMain -> {

                }
            }
        }
        binding.rvJoinedFriend.adapter = inviteFriendAdapter

    }
}