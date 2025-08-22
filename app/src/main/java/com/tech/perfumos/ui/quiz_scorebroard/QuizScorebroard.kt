package com.tech.perfumos.ui.quiz_scorebroard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.databinding.ActivityQuizBinding
import com.tech.perfumos.databinding.ActivityQuizScorebroardBinding
import com.tech.perfumos.databinding.ItemCardDailyLeaderboardBinding
import com.tech.perfumos.databinding.QuizTypeItemViewBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.CardDailyLeaderboardModel
import com.tech.perfumos.ui.dashboad.user_profile.UserProfileActivity
import com.tech.perfumos.ui.quiz.DetailsQuizActivity
import com.tech.perfumos.ui.quiz.JoinGameActivity
import com.tech.perfumos.ui.quiz.QuizActivity
import com.tech.perfumos.ui.quiz.QuizVm
import com.tech.perfumos.ui.quiz.model.CheckUserEligible
import com.tech.perfumos.ui.quiz.model.LeaderBoardList

import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class QuizScorebroard : BaseActivity<ActivityQuizScorebroardBinding>() {
    private val viewModel: QuizScorebroardVm by viewModels()
    private var leaderboardList: ArrayList<LeaderBoardList?>? = ArrayList()
    private var PAGE = 1
    private var isLoading = false
    private var isLastPage = false


    override fun getLayoutResource(): Int {
        return R.layout.activity_quiz_scorebroard
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        clickListener()
        initObserver()
        setUpRecyclerview()
        val quizId = intent.getStringExtra("quizDetailsId")

        Glide.with(this).asGif().load(R.drawable.bg_animation)
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)
        val hasMap = hashMapOf<String, Any>(
            "page" to PAGE,
            "limit" to 30,
            "quizId" to quizId.toString()
        )
        viewModel.getLeaderBoardApi(Constants.LEADERBOARD_API, hasMap)
    }

    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.tvJoinGame -> {
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


    var checkUserEligible: CheckUserEligible? = null
    private fun initObserver() {
        viewModel.commonObserver.observe(this) {
            when (it?.status) {

                Status.LOADING -> {
                    showLoading("Loading..")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        Constants.LEADERBOARD_API -> {
                            try {
                                Log.d(
                                    "PERFUME_RECOMMENDATIONS_API",
                                    "RecommendationModel: ${Gson().toJson(it)}"
                                )
                                val response: QuizScorebroardModel? =
                                    Utils.parseJson(it.data.toString())

                                if (response?.success == true) {
                                    //   val newPerfumeList = response.data ?: emptyList()

                                    // Pagination metadata
                                    val pagination = response.pagination
                                    if (pagination != null) {
                                        isLastPage = (pagination.perPage?.let { it1 ->
                                            pagination.currentPage?.times(
                                                it1
                                            )
                                        } ?: 0) >= (pagination.totalCount ?: 0)

//                                        isLastPage =if((pagination.totalCount?:0) == reviewAdapter.list.size) true else false
                                    }
                                    if (PAGE == 1) {
                                        leaderboardList?.clear()
                                    }
                                    for (i in response.data.indices) {
                                        leaderboardList?.add(
                                            LeaderBoardList(

                                                fullname = response.data[i].name,

                                                profileImage = response.data[i].profileImage,

                                                totalCorrectAnswers = response.data[i].correctAnswers,

                                                totalEarnedPoints = response.data[i].pointsEarned,

                                                userId = response.data[i].userId,


                                                )
                                        )
                                    }
                                    leadBoarAdapter.list = leaderboardList
                                    leadBoarAdapter.notifyDataSetChanged()


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

    private lateinit var leadBoarAdapter: SimpleRecyclerViewAdapter<LeaderBoardList, ItemCardDailyLeaderboardBinding>

    private fun setUpRecyclerview() {

        var itemListData = ArrayList<CardDailyLeaderboardModel>()
        itemListData.add(CardDailyLeaderboardModel(R.color.trophy_color1, "", "Arthur Morgan"))
        itemListData.add(CardDailyLeaderboardModel(R.color.trophy_color2, "", "Arthur Morgan"))
        itemListData.add(CardDailyLeaderboardModel(R.color.trophy_color3, "", "Arthur Morgan"))
        itemListData.add(
            CardDailyLeaderboardModel(
                R.color.trophy_color,
                "",
                "Arthur Morgan",
                R.color.trophy_color3,
                0.2f
            )
        )
        itemListData.add(
            CardDailyLeaderboardModel(
                R.color.trophy_color,
                "",
                "Arthur Morgan",
                R.color.trophy_color3,
                0.2f
            )
        )
        itemListData.add(
            CardDailyLeaderboardModel(
                R.color.trophy_color,
                "",
                "Arthur Morgan",
                R.color.trophy_color3,
                0.2f
            )
        )





        leadBoarAdapter = SimpleRecyclerViewAdapter(
            R.layout.item_card_daily_leaderboard, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.mainLayout -> {
                    Log.i("sdfasdfsadfasdf", "" + Gson().toJson(m))

                    val intent = Intent(this, UserProfileActivity::class.java).apply {
                        putExtra("USER_ID", m.userId.toString())
                    }
                    startActivity(intent)
                }
            }
        }
        binding.revLeadBoard.adapter = leadBoarAdapter
        leadBoarAdapter.list = leaderboardList


        binding.revLeadBoard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

            }
        }
        )
    }

}
