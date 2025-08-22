package com.tech.perfumos.ui.quiz


import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.camera.core.processing.SurfaceProcessorNode.In
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.CHECK_USER_ELIGIBLE_API
import com.tech.perfumos.data.api.Constants.LEADERBOARD_API
import com.tech.perfumos.data.api.Constants.PERFUME_RECOMMENDATIONS_API
import com.tech.perfumos.data.api.Constants.QUIZ_CATEGORY_API
import com.tech.perfumos.databinding.ActivityQuizBinding
import com.tech.perfumos.databinding.ItemCardDailyLeaderboardBinding
import com.tech.perfumos.databinding.QuizTypeItemViewBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.dashboad.fragment_folder.articles_folder.model.RecommendationModel
import com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.CardDailyLeaderboardModel
import com.tech.perfumos.ui.dashboad.user_profile.UserProfileActivity
import com.tech.perfumos.ui.quiz.model.CheckUserEligible
import com.tech.perfumos.ui.quiz.model.LeaderBoardList
import com.tech.perfumos.ui.quiz.model.LeaderboardModel
import com.tech.perfumos.ui.quiz.model.QuizCategoryList
import com.tech.perfumos.ui.quiz.model.QuizCategoryModel
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import com.tech.perfumos.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class QuizActivity : BaseActivity<ActivityQuizBinding>() {
    private val viewModel: QuizVm by viewModels()

    private var leaderboardList: ArrayList<LeaderBoardList?>? = ArrayList()
    private var quizCategoryList: ArrayList<QuizCategoryList?>? = ArrayList()
    private var tabPosition: Int = 0
    private var PAGE = 1
    private val LIMIT = 10
    private var isLoading = false
    private var isLastPage = false


    override fun getLayoutResource(): Int {
        return R.layout.activity_quiz
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        clickListener()
        initObserver()
        setUpRecyclerview()

        val routeNav = intent?.getStringExtra("route_nav")
        if (routeNav == "Done") {
            setTabUI(1)

        } else if (routeNav == "SeeScoreboard") {
            setTabUI(0)

        } else {
            setTabUI(0)
        }

        Glide.with(this).asGif().load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)

        val requestMap = hashMapOf<String, Any>()
        viewModel.getQuizCategory(QUIZ_CATEGORY_API, requestMap)


    }

    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.tvScoreboard -> {
                    setTabs(0)
                    binding.tvScoreboard.setBackgroundResource(R.drawable.bg_rounded_white_storke)
                    binding.tvScoreboard.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.headerColor)
                    binding.tvScoreboard.setTextColor(ContextCompat.getColor(this, R.color.white))

                    binding.tvQuizPlay.setBackgroundResource(R.drawable.bg_rounded_storke)
                    binding.tvQuizPlay.backgroundTintList = null
                    binding.tvQuizPlay.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.headerColor
                        )
                    )

                    binding.tvRanked.setBackgroundResource(R.drawable.bg_rounded_storke)
                    binding.tvRanked.backgroundTintList = null
                    binding.tvRanked.setTextColor(ContextCompat.getColor(this, R.color.headerColor))
                }

                R.id.tvQuizPlay -> {
                    setTabs(1)
                    binding.tvQuizPlay.setBackgroundResource(R.drawable.bg_rounded_white_storke)
                    binding.tvQuizPlay.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.headerColor)
                    binding.tvQuizPlay.setTextColor(ContextCompat.getColor(this, R.color.white))

                    binding.tvScoreboard.setBackgroundResource(R.drawable.bg_rounded_storke)
                    binding.tvScoreboard.backgroundTintList = null
                    binding.tvScoreboard.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.headerColor
                        )
                    )

                    binding.tvRanked.setBackgroundResource(R.drawable.bg_rounded_storke)
                    binding.tvRanked.backgroundTintList = null
                    binding.tvRanked.setTextColor(ContextCompat.getColor(this, R.color.headerColor))
                }

                R.id.tvRanked -> {
                    setTabs(2)
                    binding.tvRanked.setBackgroundResource(R.drawable.bg_rounded_white_storke)
                    binding.tvRanked.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.headerColor)
                    binding.tvRanked.setTextColor(ContextCompat.getColor(this, R.color.white))

                    binding.tvScoreboard.setBackgroundResource(R.drawable.bg_rounded_storke)
                    binding.tvScoreboard.backgroundTintList = null
                    binding.tvScoreboard.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.headerColor
                        )
                    )

                    binding.tvQuizPlay.setBackgroundResource(R.drawable.bg_rounded_storke)
                    binding.tvQuizPlay.backgroundTintList = null
                    binding.tvQuizPlay.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.headerColor
                        )
                    )
                }

                R.id.tvJoinGame -> {
                    startActivity(Intent(this, JoinGameActivity::class.java))
                }

            }
        }
    }

    private fun setTabUI(selectedTab: Int) {
        val tabs = listOf(binding.tvScoreboard, binding.tvQuizPlay, binding.tvRanked)

        tabs.forEachIndexed { index, textView ->
            if (index == selectedTab) {
                // Selected Tab
                textView.setBackgroundResource(R.drawable.bg_rounded_white_storke)
                textView.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.headerColor)
                textView.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                // Unselected Tab
                textView.setBackgroundResource(R.drawable.bg_rounded_storke)
                textView.backgroundTintList = null
                textView.setTextColor(ContextCompat.getColor(this, R.color.headerColor))
            }
        }

        setTabs(selectedTab) // keep your existing logic
    }


    private fun setTabs(tabs: Int) {
        tabPosition = tabs
        when (tabs) {
            0 -> {
                binding.tvRankedFalse.visibility = View.GONE
                binding.quizTypeTitle.visibility = View.GONE
                binding.tvJoinGame.visibility = View.GONE
                binding.revLeadBoard.visibility = View.VISIBLE
                binding.revLeadBoard.adapter = leadBoarAdapter
                leadBoarAdapter.list = leaderboardList
                leadBoarAdapter.notifyDataSetChanged()
            }

            1 -> {
                binding.tvRankedFalse.visibility = View.GONE
                binding.quizTypeTitle.visibility = View.VISIBLE
                binding.revLeadBoard.visibility = View.VISIBLE
                binding.tvJoinGame.visibility = View.VISIBLE
                binding.revLeadBoard.adapter = quizPlayAdapter
                quizPlayAdapter.list = quizCategoryList
                quizPlayAdapter.notifyDataSetChanged()
            }

            2 -> {
                if (checkUserEligible != null) {
                    if (checkUserEligible?.success == true) {

                        if (checkUserEligible?.data?.isRankedQuizUnlocked == true) {
                            binding.tvRankedFalse.visibility = View.GONE
                            binding.revLeadBoard.visibility = View.VISIBLE
                            binding.tvJoinGame.visibility = View.VISIBLE
                            binding.quizTypeTitle.visibility = View.VISIBLE

                        } else {
                            binding.tvRankedFalse.visibility = View.VISIBLE
                            binding.revLeadBoard.visibility = View.GONE


                            binding.tvJoinGame.visibility = View.GONE
                            binding.quizTypeTitle.visibility = View.GONE
                        }
                    }
                }

//                binding.revLeadBoard.visibility = View.VISIBLE
                binding.revLeadBoard.adapter = quizPlayAdapter
                quizPlayAdapter.list = quizCategoryList
                quizPlayAdapter.notifyDataSetChanged()
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
                        QUIZ_CATEGORY_API -> {
                            try {
                                Log.d(
                                    "QUIZ_CATEGORY_API",
                                    "QuizCategoryModel: ${Gson().toJson(it)}"
                                )
                                val response: QuizCategoryModel? = parseJson(it.data.toString())

                                if (response?.success == true) {
                                    quizCategoryList = response.data

                                    if (quizCategoryList.isNullOrEmpty()) {
                                        binding.noQuizFound.visibility = View.VISIBLE
                                        binding.revLeadBoard.visibility = View.GONE
                                    } else {
                                        binding.noQuizFound.visibility = View.GONE
                                        binding.revLeadBoard.visibility = View.VISIBLE


                                        quizPlayAdapter.list = quizCategoryList
                                        quizPlayAdapter.notifyDataSetChanged()
                                    }


                                } else {
                                    showToast(response?.message.toString())
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            val hasMap = hashMapOf<String, Any>(
                                "page" to PAGE,
                                "limit" to LIMIT
                            )
                            viewModel.getLeaderBoardApi(LEADERBOARD_API, hasMap)

                        }

                        LEADERBOARD_API -> {
                            try {
                                Log.d(
                                    "PERFUME_RECOMMENDATIONS_API",
                                    "RecommendationModel: ${Gson().toJson(it)}"
                                )
                                val response: LeaderboardModel? = parseJson(it.data.toString())

                                if (response?.success == true) {
                                    val newPerfumeList = response.data ?: emptyList()

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

                                    leaderboardList?.addAll(newPerfumeList)
                                    leadBoarAdapter.list = leaderboardList
                                    //likePerfumeAdapter.addToList(mightLikePerfumeList)
                                    leadBoarAdapter.notifyDataSetChanged()


                                } else {
                                    showToast(response?.message.toString())
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                isLoading = false
                            }
                            viewModel.getCheckUserEligibleApi(CHECK_USER_ELIGIBLE_API)
                        }

                        CHECK_USER_ELIGIBLE_API -> {
                            try {

                                val response: CheckUserEligible? = parseJson(it.data.toString())
                                checkUserEligible = response


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
    private lateinit var quizPlayAdapter: SimpleRecyclerViewAdapter<QuizCategoryList, QuizTypeItemViewBinding>
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


        quizPlayAdapter = SimpleRecyclerViewAdapter(
            R.layout.quiz_type_item_view, BR.bean
        ) { v, m, pos ->

            when (v.id) {
                R.id.clMain -> {
                    val intent = Intent(this, DetailsQuizActivity::class.java).apply {
                        putExtra("quizType", m)
//                        putExtra("mode", if (tabPosition == 1) "quick" else "quick")
                        putExtra("mode",if(tabPosition==1) "quick" else "ranked")
                    }
                    startActivity(intent)
                }
            }


        }
        binding.revLeadBoard.adapter = quizPlayAdapter
        quizPlayAdapter.list = quizCategoryList

        leadBoarAdapter = SimpleRecyclerViewAdapter(
            R.layout.item_card_daily_leaderboard, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.mainLayout -> {
                    Log.i("sdfasdfsadfasdf", ""+Gson().toJson(m))
//                    startActivity(Intent(this, UserProfileActivity::class.java))
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
                // Reached the bottom
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {

                        PAGE++
                        isLoading = true

                        val hasMap = hashMapOf<String, Any>(
                            "page" to PAGE,
                            "limit" to LIMIT
                        )
                        viewModel.getLeaderBoardApi(LEADERBOARD_API, hasMap)


                    }
                }
            }
        }
        )
    }

}