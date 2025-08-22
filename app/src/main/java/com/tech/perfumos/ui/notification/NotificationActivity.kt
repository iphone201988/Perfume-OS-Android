package com.tech.perfumos.ui.notification

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.data.api.Constants.NOTIFICATION_API
import com.tech.perfumos.databinding.ActivityNotificationBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.notification.model.NotificationItem
import com.tech.perfumos.ui.notification.model.NotificationModel
import com.tech.perfumos.ui.notification.model.Quiz
import com.tech.perfumos.ui.quiz.JoinPlayerActivity
import com.tech.perfumos.utils.CommonFunctionClass
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class NotificationActivity : BaseActivity<ActivityNotificationBinding>() {
    private val viewModel: NotificationVm by viewModels()
    private lateinit var notificationAdapter: NotificationAdapter
    private var notificationList: ArrayList<NotificationItem> = ArrayList()
    private var tab = 0

    private var PAGE = 1
    private val LIMIT = 10
    private var isLoading = false
    private var isLastPage = false

    override fun getLayoutResource(): Int {
        return R.layout.activity_notification
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        setAdapter()
        clickListener()
        initObserver()
        setTab()
        Glide.with(this).asGif().load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)

        viewModel.getNotification(
            NOTIFICATION_API,
            hashMapOf(
                "status" to "all",
                "page" to PAGE,
                "limit" to LIMIT,
            )
        )

    }

    private fun setTab() {
        if (tab == 0) {
            binding.apply {
                allView.visibility = View.VISIBLE
                unreadView.visibility = View.INVISIBLE

                tvAll.setTextColor(resources.getColor(R.color.select_tab_text))
                tvUnread.setTextColor(resources.getColor(R.color.unselect_tab_text))

                /*notificationAdapter.items = notificationList
                notificationAdapter.notifyDataSetChanged()*/
            }
        } else {
            binding.apply {
                allView.visibility = View.INVISIBLE
                unreadView.visibility = View.VISIBLE

                tvUnread.setTextColor(resources.getColor(R.color.select_tab_text))
                tvAll.setTextColor(resources.getColor(R.color.unselect_tab_text))

                val allNotificationList = filterNotifications(notificationList, isRead = false, /*type = "quizInvite"*/)
                notificationAdapter.items = allNotificationList
                notificationAdapter.notifyDataSetChanged()
            }
        }
    }
    var roomId:String?=null

    private fun setAdapter() {
        notificationAdapter = NotificationAdapter(this , notificationList){

            when (it) {
                is NotificationItem.QuizInviteItem -> {
                    roomId=it.quiz?.roomId.toString()
                    val hashMap = hashMapOf<String, Any>(
                        "roomId" to it.quiz?.roomId.toString()
                    )
                    viewModel.sendInvite(Constants.JOIN_QUIZ_API, hashMap)
                }
            }
        }
        binding.rvNotification.adapter = notificationAdapter


        binding.rvNotification.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        PAGE++
                        isLoading = true

                        val requestMap = hashMapOf<String, Any>(
                            "status" to "all",
                            "page" to PAGE,
                            "limit" to LIMIT
                        )
                        viewModel.getNotification(NOTIFICATION_API, requestMap)

                    }
                }
            }
        })

    }

    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.clAll -> {
                    tab = 0
                    setTab()
                }

                R.id.clUnread -> {
                    tab = 1
                    setTab()
                }

                R.id.tvMarkRead -> {
                    /*notificationList.forEach {
                        it?. = true
                    }*/
                    notificationAdapter.items = notificationList
                    notificationAdapter.notifyDataSetChanged()
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
                        Constants.JOIN_QUIZ_API -> {
                            try {
                                Log.d("response", "RECENT_TOP_PERFUME_API: ${Gson().toJson(it)}")
                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: $jsonObject")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    CommonFunctionClass.logPrint(tag = "ROOM_ID", response = roomId.toString())
                                    showToast(jsonObject.getString("message").toString())

                                    val intent =
                                        Intent(this, JoinPlayerActivity::class.java).apply {
                                            putExtra("join", "2")
                                            putExtra("roomID", roomId.toString())

                                        }
                                    startActivity(intent)



                                } else {
                                    showErrorToast(jsonObject.getString("message").toString())
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        NOTIFICATION_API -> {
                            try {
                                Log.d(
                                    "NOTIFICATION_API",
                                    "NotificationModel: ${Gson().toJson(it)}"
                                )
                                val response: NotificationModel? = parseJson(it.data.toString())

                                if (response?.success == true) {
                                    //val newNotificationList = response.data ?: emptyList()
                                    val newNotificationList =
                                        parseNotifications(response.data) ?: emptyList()

                                    // Pagination metadata
                                    val pagination = response.pagination
                                    binding.tvAllCount.text =
                                        response.pagination?.totalCount.toString()
                                    binding.tvUnreadCount.text =
                                        response.unreadNotifications.toString()
                                    if (pagination != null) {
                                        isLastPage = (pagination.perPage?.let { it1 ->
                                            pagination.currentPage?.times(
                                                it1
                                            )
                                        } ?: 0) >= (pagination.totalCount ?: 0)
//                                        isLastPage =if((pagination.totalCount?:0) == reviewAdapter.list.size) true else false
                                    }
                                    if (PAGE == 1) {
                                        notificationList.clear()
                                    }
                                    notificationList.addAll(newNotificationList)
                                    notificationAdapter.items = notificationList
                                    notificationAdapter.notifyDataSetChanged()


                                } else {
                                    showToast(response?.message.toString())
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                isLoading = false
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

    private fun parseNotifications(apiData: ArrayList<NotificationModel.Data?>?): List<NotificationItem> {
        val items = mutableListOf<NotificationItem>()

        apiData?.forEach { n ->
            when (n?.type) {
                "quizInvite" -> {
                    items.add(
                        NotificationItem.QuizInviteItem(
                            id = n.id.toString(),
                            title = n.title.toString(),
                            message = n.message.toString(),
                            quizId = n.quizId ?: "",
                            isRead = n.isRead,
                            createdAt = n.createdAt,
                            quiz = n.quiz?.let {
                                Quiz(
                                    id = it.id,
                                    roomId = it.roomId,
                                    mode = it.mode,
                                    quizType = it.quizType,
                                    quizCategory = it.quizCategory,
                                    totalQuestions = it.totalQuestions,
                                    status = it.status,
                                    createdAt = it.createdAt,
                                    updatedAt = it.updatedAt,
                                    hostId = it.hostId,
                                    playType = it.playType,
                                    questions = it.questions,
                                    v = it.v,
                                    players = it.players
                                )
                            }
                        )
                    )
                }
                "follow" -> {
                    items.add(
                        NotificationItem.FollowItem(
                            title = n.title.toString(),
                            message = n.message.toString(),
                            id = n.id.toString(),
                            createdAt = n.createdAt,
                            followId = n.followId,
                            followUser = n.follow?.followUser,
                            userId = n.userId,
                            v = n.v,
                            isRead = n.isRead,
                        )
                    )
                }
                "alert" -> {
                    items.add(
                        NotificationItem.AlertItem(
                            id = n.id.toString(),
                            title = n.title.toString(),
                            message = n.message.toString(),
                            isRead = n.isRead,
                            createdAt = n.createdAt,
                        )
                    )
                }
            }
        }

        return items
    }

    fun filterNotifications(
        notifications: List<NotificationItem>,
        isRead: Boolean? = null,
        type: String? = null
    ): List<NotificationItem> {
        return notifications.filter { notif ->
            val matchesRead = when (isRead) {
                null -> true // no read/unread filter
                else -> when (notif) {
                    is NotificationItem.QuizInviteItem -> (notif.isRead == isRead) // see note below on adding isRead
                    is NotificationItem.FollowItem -> (notif.isRead == isRead)
                    is NotificationItem.AlertItem -> (notif.isRead == isRead)
                }
            }

            val matchesType = type.isNullOrEmpty() || when (notif) {
                is NotificationItem.QuizInviteItem -> type == "quizInvite"
                is NotificationItem.FollowItem -> type == "follow"
                is NotificationItem.AlertItem -> type == "alert"
            }

            matchesRead && matchesType
        }
    }
}