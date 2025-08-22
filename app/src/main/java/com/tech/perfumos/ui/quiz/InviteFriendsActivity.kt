package com.tech.perfumos.ui.quiz

import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.GET_FOLLOWERS_API
import com.tech.perfumos.data.api.Constants.SEND_QUIZ_INVITE_API
import com.tech.perfumos.databinding.ActivityInviteFriendsBinding
import com.tech.perfumos.databinding.InviteFriendItemViewBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.quiz.model.InviteFriendList
import com.tech.perfumos.ui.quiz.model.InviteFriendModel
import com.tech.perfumos.ui.quiz.model.QuizDetails
import com.tech.perfumos.ui.quiz.model.SendQuizInviteRequest
import com.tech.perfumos.utils.CommonFunctionClass
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class InviteFriendsActivity : BaseActivity<ActivityInviteFriendsBinding>() {
    private val viewModel: QuizVm by viewModels()
    private var quizData: QuizDetails? = null

    var inviteFriendList: ArrayList<InviteFriendList?>? = ArrayList()
    var selectedFriendsList = mutableListOf<String>()

    override fun getLayoutResource(): Int {
        return R.layout.activity_invite_friends
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        clickListener()
        initObserver()
        setUpRecyclerview()
        /*Glide.with(this).asGif().load(R.drawable.bg_animation)
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)*/

        quizData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("QuizDetails", QuizDetails::class.java)
        } else {
            intent.getSerializableExtra("QuizDetails") as QuizDetails?
        }


        val requestMap = hashMapOf<String, Any>()
        viewModel.getFollowersApi(GET_FOLLOWERS_API, requestMap)

    }

    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.tvSendInvite -> {
                    /* val intent = Intent(this, JoinPlayerActivity::class.java).apply {
                         putExtra("join", false)
                         putExtra("quizType", quizData)
                     }
                     startActivity(intent)*/

                    if (!selectedFriendsList.isNullOrEmpty()) {

                        val body = SendQuizInviteRequest(
                            quizId =  quizData?.id.toString(),
                            userIds = selectedFriendsList // pass as List<String>
                        )
                        viewModel.sendInvite(SEND_QUIZ_INVITE_API, body)

                        /*val hashMap = hashMapOf<String, Any>(
                            "quizId" to quizData?.id.toString(),
                            "userIds" to selectedFriendsList,
                        )
                        viewModel.sendInvite(SEND_QUIZ_INVITE_API, hashMap)*/
                    } else {
                        showErrorToast("Please select friends to send invite")
                    }

                }

                R.id.tvShareQrCode -> {
                    val intent = Intent(this, GenerateQrCodeActivity::class.java).apply {
                        putExtra("roomId", quizData?.roomId.toString())
                    }

                    startActivity(intent)
                }
            }
        }
        binding.tvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    val list = inviteFriendList?.filter { it?.fullname?.contains(s)!! }

                    inviteFriendAdapter.list = list
                    inviteFriendAdapter.notifyDataSetChanged()
                } else {
                    inviteFriendAdapter.list = inviteFriendList
                    inviteFriendAdapter.notifyDataSetChanged()
                }
            }

        })
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
                        GET_FOLLOWERS_API -> {
                            try {
                                Log.d("response", "RECENT_TOP_PERFUME_API: ${Gson().toJson(it)}")
                                val response: InviteFriendModel? = parseJson(it.data.toString())
                                Log.d("response", "RECENT_TOP_PERFUME_API : ${response?.success}")

                                if (response?.success == true) {

                                    if (!response.data.isNullOrEmpty()) {
                                        inviteFriendList = response.data
                                        inviteFriendAdapter.list = inviteFriendList
                                    }
                                } else {

                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        SEND_QUIZ_INVITE_API -> {
                            try {
                                Log.d("response", "RECENT_TOP_PERFUME_API: ${Gson().toJson(it)}")
                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: $jsonObject")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())
                                    CommonFunctionClass.logPrint(tag = "ROOM_ID", response = quizData?.roomId.toString())
                                    val intent =
                                        Intent(this, JoinPlayerActivity::class.java).apply {
                                            putExtra("join", "1")
                                            putExtra("roomID", quizData?.roomId.toString())
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


    private lateinit var inviteFriendAdapter: SimpleRecyclerViewAdapter<InviteFriendList, InviteFriendItemViewBinding>
    private fun setUpRecyclerview() {

        inviteFriendAdapter = SimpleRecyclerViewAdapter(
            R.layout.invite_friend_item_view, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.clMain -> {

                }

                R.id.cvSelect -> {
                    if (m.isSelected) {
                        m.isSelected = false
                        selectedFriendsList.remove(m.id.toString())
                    } else {
                        m.isSelected = true
                        selectedFriendsList.add(m.id.toString())
                    }
                }
            }
        }
        binding.rvInviteFriend.adapter = inviteFriendAdapter
        inviteFriendAdapter.list = inviteFriendList

    }

}