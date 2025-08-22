package com.tech.perfumos.ui.dashboad.fragment_folder.home_folder

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.BASE_URL_IMAGE
import com.tech.perfumos.data.api.Constants.GET_HOME_FRAGMENT
import com.tech.perfumos.data.api.Constants.GET_PROFILE_API
import com.tech.perfumos.data.api.Constants.UPDATE_DATA_API
import com.tech.perfumos.databinding.FragmentHomeBinding
import com.tech.perfumos.databinding.ItemCardDailyLeaderboard2Binding
import com.tech.perfumos.databinding.ItemCardDailyLeaderboardBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseFragment
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.camera_perfume.CameraActivity
import com.tech.perfumos.ui.core.SearchClickListener
import com.tech.perfumos.ui.dashboad.DashboardActivity
import com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.model.HomeFragementModel
import com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.model.Leaderboard
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.ProfileDataModel
import com.tech.perfumos.ui.dashboad.fragment_folder.setting.SettingFragment
import com.tech.perfumos.ui.quiz.QuizActivity
import com.tech.perfumos.ui.quiz.model.LeaderBoardList
import com.tech.perfumos.utils.SpotlightViewUtil
import com.tech.perfumos.utils.SpotlightViewUtilModel
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.formatReviewCount
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.Utils.preventMultipleClick
import com.tech.perfumos.utils.event.SingleActionEvent
import com.tech.perfumos.utils.showErrorToast
import com.tech.perfumos.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeFragmentVm by viewModels()

    private var searchClickListener: SearchClickListener? = null

    companion object {
        val scrollList = SingleActionEvent<Int>()
    }
    override fun onCreateView(view: View) {
        binding.isSelected = 1
        binding.ivSearch.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.TvBlack60))
        clickListener()
        initObserver()
        setUpRecyclerview()
        observerScroll()

        viewModel.getProfileApi(GET_PROFILE_API)
        Log.i("3t32t23t", "onCreateView: " + sharedPrefManager.getOnboardingComplete()!!.toInt())
    }

    override fun onResume() {
        super.onResume()

        Glide.with(this@HomeFragment)
            .load("${BASE_URL_IMAGE}${sharedPrefManager.getCurrentUser()?.profileImage}")
            /*.placeholder(R.drawable.dummy_image)*/.error(R.drawable.dummy_image)
            .into(binding.userProfile)
        binding.nameUser.text = sharedPrefManager.getCurrentUser()?.fullname.toString()
    }
    private var leaderboardList: ArrayList<Leaderboard?>? = ArrayList()
    private fun initObserver() {
        viewModel.commonObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading..")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        GET_HOME_FRAGMENT ->{
                            val response: HomeFragementModel? = parseJson(it.data.toString())
                            if (response?.success == true) {
                                val newPerfumeList = response.leaderboard ?: emptyList()
                                leaderboardList?.addAll(newPerfumeList)
                                leadBoarAdapter.list = leaderboardList
                                leadBoarAdapter.notifyDataSetChanged()
                            } else {
                                showToast(response?.message.toString())
                            }
                        }
                        GET_PROFILE_API -> {
                            try {
                                val data: ProfileDataModel? = parseJson(it.data.toString())
                                if (data?.data?.tutorialProgess != null) {
                                    Utils.route = data.data?.tutorialProgess!!.toInt() ?: 1
                                    sharedPrefManager.setOnboardingComplete("${Utils.route}")
                                    if (Utils.route < 10) {
                                        sharedPrefManager.setOnboardingCompleteBool(false)
                                    } else {
                                        sharedPrefManager.setOnboardingCompleteBool(true)
                                    }
                                    onboardingSportLight()

                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            viewModel.getHomeFragmentApi(GET_HOME_FRAGMENT)
                        }
                        UPDATE_DATA_API -> {
                            try {
                                Log.d("response", "UPDATE_DATA_API: ${Gson().toJson(it)}")
                                /* val data: LoginModel? = Utils.parseJson(it.data.toString())
                                 Log.d("LoginModel", "initObserver: ${data?.success}")*/

                                val response = it.data
                                Log.d("response", "initObserver: $response")
                                Log.d("response", "initObserver: ${response?.get("success")}")
                                if (it.data?.get("success")?.asBoolean == true) {
                                    Log.d(
                                        "response",
                                        "initObserver: ${response?.get("success")} "
                                    )
                                } else {
                                    showErrorToast(it.data?.get("message")?.asString.toString())
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
                            requireActivity().startActivity(Intent(requireActivity(), LoginActivity::class.java))
                            requireActivity().finishAffinity()
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


    private fun onboardingSportLight() {
        // Safely get user progress as Int
        val navRouteSportLightCount = sharedPrefManager.getOnboardingComplete()?.toInt() ?: 1
//        val navRouteSportLightCount = sharedPrefManager.getCurrentUser()?.tutorialProgess ?: 0

        // Update width
        binding.viewSecCompleted.post {
            updateViewWidth(binding.viewSecCompleted, navRouteSportLightCount)
        }

        // Log current state
        val navRouteSportLight =
            sharedPrefManager.getOnboardingCompleteBool() // make sure this is initialized!
        Log.d(
            "navRouteSportLightCount",
            "onboardingSportLight: completed = $navRouteSportLight, count = $navRouteSportLightCount"
        )

        // Show spotlight only if not completed and count == 0
        if (navRouteSportLightCount == 1 && !navRouteSportLight && Utils.routeToHomeDashboardActivity == 2) {
            SpotlightViewUtil.applySpotlightEffect(
                context = requireActivity(),
                targetViews = listOf(
                    Utils.bottomAppBar,
                    Utils.fabCamera,
                    binding.searchLL,
                    binding.scanPerfumeCC,
                    binding.completeCC,
                    binding.writeReviewCC,
                    binding.dailyLeaderboardLl,
                    binding.toggleSwitchCC
                ),
                textList = arrayListOf(
                    SpotlightViewUtilModel(
                        "Snap or upload a photo of a\nperfume bottle to get information", ""
                    ),
                    SpotlightViewUtilModel(
                        "Search",
                        "Type a perfume name, brand or fragrance note to get started"
                    ),
                    SpotlightViewUtilModel(
                        "Today's Quest",
                        "Explore new tasks to do and daily quests here"
                    ),
                    SpotlightViewUtilModel(
                        "Leaderboard",
                        "Find out where you rank against your friends"
                    ),
                    SpotlightViewUtilModel("Homepage", "Go back to Homepage"),
                    SpotlightViewUtilModel(
                        "Explore",
                        "Explore what’s new and other articles written by the community"
                    ),
                    SpotlightViewUtilModel("Compare", "Compare 2 perfumes side by side"),
                    SpotlightViewUtilModel("Your Profile", "Rank, Follower, Collections"),
                    SpotlightViewUtilModel("Appearance", "Light or Dark mode"),
                    SpotlightViewUtilModel(
                        "Daily Challenge",
                        "Test your perfume knowledge and climb the ranks"
                    )
                ),
                overlayAlpha = 0.7f,
                animate = true,
                sharedPref = sharedPrefManager
            )
        } else {
            // If already completed or in progress
            if (navRouteSportLight) {
                binding.book.setImageDrawable(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.crown_icone)
                )
                binding.completeTheTutorial.text = "Take the Daily Challenge"
                binding.rightCompleted.text = "0% Completed"
                binding.viewSecCompleted.visibility = View.GONE
                binding.completeCC.background = ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.bg_home_gradient_stroke
                )
            }
        }
    }


    private fun updateViewWidth(view: View, step: Int) {
        Log.i("navRouteSportLight", "${step}: ")
        /*if(step==9){
            Utils.updateTheTutorial.postValue(2)

        }*/
        binding.viewMainCompleted.post {
            val screenWidth = /*Resources.getSystem().displayMetrics.widthPixels*/
                binding.viewMainCompleted.width
            val layoutParams = view.layoutParams
            layoutParams.width = when (step) {
                in 1..8 -> (screenWidth / 9) * step
                9 -> ViewGroup.LayoutParams.MATCH_PARENT
                else -> layoutParams.width // Keep unchanged if step is invalid
            }
            view.layoutParams = layoutParams

        }
        if (!Utils.isCompleted) {
            if (!sharedPrefManager.getOnboardingCompleteBool()) {
                val progressPercent = (step.coerceIn(0, 9) / 9f) * 100
                binding.rightCompleted.text = "${progressPercent.toInt()}% Completed"
            }else{
                binding.rightCompleted.text = "0% Completed"
            }

        } else {
            binding.rightCompleted.text = "0% Completed"
        }

    }

    private fun observerScroll() {

        Utils.lastApiCall.observe(viewLifecycleOwner) { message ->
            val request: HashMap<String, Any> = hashMapOf("tutorialProgess" to 10)


           viewModel.updateData(UPDATE_DATA_API, request)
        }
        Utils.updateTheTutorial.observe(viewLifecycleOwner) { message ->

            if (!Utils.isCompleted) {
                Log.d("updateTheTutorial", "observerScroll: completed false ${Utils.route}")

                    val request: HashMap<String, Any> = hashMapOf(
                        "tutorialProgess" to Utils.route)
                    viewModel.updateData(UPDATE_DATA_API, request)

            } else {
                Log.d("updateTheTutorial", "observerScroll: completed true ${Utils.route}")
            }
            Handler(Looper.getMainLooper()).post {
                binding.viewSecCompleted.post {
                    updateViewWidth(
                        binding.viewSecCompleted,
                        sharedPrefManager.getOnboardingComplete()!!.toInt()
                    )
                }

                message?.let {
                    when (it) {
                        1 -> {
                            Handler(Looper.getMainLooper()).post {
                                //Utils.route = 1
                                val drawable = ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.crown_icone
                                )
                                binding.book.setImageDrawable(drawable)
                                binding.completeTheTutorial.text = "Take the Daily Challenge"
                                binding.rightCompleted.text = "0% Completed"
                                binding.viewSecCompleted.visibility = View.GONE
                                //binding.gradientStrokeView.visibility = View.GONE
                                binding.completeCC.background = ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.home_welcome_drawable
                                )

                            }
                        }
                        6 -> {
                            Handler(Looper.getMainLooper()).post {
                                //Utils.route = 1
                                val drawable = ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.crown_icone
                                )
                                binding.book.setImageDrawable(drawable)
                                binding.completeTheTutorial.text = "Take the Daily Challenge"
                                binding.rightCompleted.text = "0% Completed"
                                binding.viewSecCompleted.visibility = View.GONE
                                //binding.gradientStrokeView.visibility = View.GONE
                                binding.completeCC.background = ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.home_welcome_drawable
                                )

                            }
                        }

                        2 -> {
                            //  binding.gradientStrokeView.visibility = View.VISIBLE
                            //binding.completeCC.setBackgroundColor(Color.TRANSPARENT)
                            val drawable =
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.crown_icone
                                )
                            binding.book.setImageDrawable(drawable)
                            binding.completeTheTutorial.text = "Take the Daily Challenge"
                            binding.rightCompleted.text = "0% Completed"
                            binding.viewSecCompleted.visibility = View.GONE
                            binding.completeCC.background = ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.bg_home_gradient_stroke
                            )
                        }

                        5 -> {
                            val drawable2 = ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.home_welcome_drawable
                            )
                            //  binding.gradientStrokeView.visibility = View.GONE
                            binding.completeCC.background = drawable2
                            val drawable =
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.crown_icone
                                )
                            binding.book.setImageDrawable(drawable)
                            binding.completeTheTutorial.text = "Take the Daily Challenge"
                            binding.rightCompleted.text = "0% Completed"
                            binding.viewSecCompleted.visibility = View.GONE
                        }

                        3 -> {
                            val drawable2 = ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.home_welcome_drawable
                            )
                            binding.writeReviewCC.background = drawable2
                        }

                        4 -> {
                            val drawable2 = ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.home_welcome_drawable
                            )
                            binding.writeReviewCC.background = drawable2

                        }

                        else -> {
                            //binding.gradientStrokeView.visibility = View.VISIBLE
                            /*binding.completeCC.background=null
                            binding.completeCC.setBackgroundColor(Color.TRANSPARENT)*/
                            val drawable =
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.crown_icone
                                )
                            binding.book.setImageDrawable(drawable)
                            binding.completeTheTutorial.text = "Take the Daily Challenge"
                            binding.rightCompleted.text = "0% Completed"
                            binding.viewSecCompleted.visibility = View.GONE

                            Log.d("route", "observerScroll: ${Utils.route}")
                        }
                    }
                }
            }
        }
        scrollList.observe(viewLifecycleOwner) { message ->
            message?.let {
                when (message) {
                    1 -> {
//                        val scrollAmountInDp = 500
//                        val scale = resources.displayMetrics.density
//                        val scrollAmountInPx = (scrollAmountInDp * scale).toInt()
//                        binding.customScrollview.post {
//                            binding. customScrollview.smoothScrollBy(0, scrollAmountInPx)
//                        }
                        val screenHeightPx = binding.mainView.height
                        binding.customScrollview.post {
                            binding.customScrollview.smoothScrollBy(0, screenHeightPx / 2)
                        }
                    }

                    else -> {
                        binding.customScrollview.post {
                            binding.customScrollview.smoothScrollTo(0, 0)
                        }
                    }

                }
            }
        }

    }


    private lateinit var leadBoarAdapter: SimpleRecyclerViewAdapter<Leaderboard, ItemCardDailyLeaderboard2Binding>
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
            R.layout.item_card_daily_leaderboard_2, BR.bean
        ) { v, m, pos ->

        }
        binding.revLeadBoard.adapter = leadBoarAdapter
        //leadBoarAdapter.list = itemListData
    }


    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    private fun clickListener() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.toggleSwitch -> {
                    binding.isSelected = if (binding.isSelected == 1) 2 else 1
                    Log.d("isSelected", "clickListener: ${binding.isSelected}")
                    val dark = binding.isSelected == 1
                    searchClickListener?.onThemeSwitch(dark)

                }

                R.id.scanPerfumeCC -> {
                    startActivity(Intent(requireContext(), CameraActivity::class.java))
                }

                R.id.clBadge -> {
                    startActivity(Intent(requireContext(), EarnBadgeActivity::class.java))
                }

                R.id.completeCC -> {
                    //Utils.updateTheTutorial.postValue(5)
                    if (!sharedPrefManager.getOnboardingCompleteBool()) {

                        SpotlightViewUtil.applySpotlightEffect(
                            context = requireActivity(),
                            targetViews = listOf(
//                    Utils.bottomNavigationView,
                                Utils.bottomAppBar,
                                Utils.fabCamera,
                                binding.searchLL,
                                binding.scanPerfumeCC,
                                binding.completeCC,
                                binding.writeReviewCC,
                                binding.dailyLeaderboardLl,
                                binding.toggleSwitchCC
                            ),
                            textList = arrayListOf(
                                SpotlightViewUtilModel(
                                    "Snap or upload a photo of a\nperfume bottle to get information",
                                    ""
                                ),
                                SpotlightViewUtilModel(
                                    "Search",
                                    "Type a perfume name, brand or fragrance note to get started"
                                ),
                                SpotlightViewUtilModel(
                                    "Today's Quest",
                                    "Explore new tasks to do and daily quests here"
                                ),
                                SpotlightViewUtilModel(
                                    "Leaderboard",
                                    "Find out where you rank against your friends"
                                ),
                                SpotlightViewUtilModel("Homepage", "Go back to Homepage"),
                                SpotlightViewUtilModel(
                                    "Explore",
                                    "Explore what’s new and other articles written by the community"
                                ),
                                SpotlightViewUtilModel(
                                    "Compare",
                                    "Compare 2 perfumes side by side"
                                ),
                                SpotlightViewUtilModel(
                                    "Your Profile",
                                    "Rank, Follower, Collections"
                                ),
                                SpotlightViewUtilModel("Appearance", "Light or Dark mode"),
                                SpotlightViewUtilModel(
                                    "Daily Challenge",
                                    "Test your perfume knowledge and climb the ranks"
                                ),
                            ),
                            overlayAlpha = 0.7f, // Semi-transparent overlay (0.0f to 1.0f)
                            animate = true,// Fade-in animation
                            sharedPref = sharedPrefManager
                        )
                    }
                    else{
                        startActivity(Intent(requireContext(), QuizActivity::class.java))
                    }
                }

                R.id.settingIcon -> {
                    searchClickListener?.loadFragment(SettingFragment())
                }

                R.id.searchLL, R.id.ivSearch, R.id.tvSearch -> {
                    Log.d("searchClickListener", "clickListener: searchClickListener")
                    searchClickListener?.onSearchClick()
                    binding.apply {
                        searchLL.preventMultipleClick()
                        ivSearch.preventMultipleClick()
                        tvSearch.preventMultipleClick()
                    }

                }

                R.id.writeReviewCC -> {
                    searchClickListener?.writeReviewClick()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SearchClickListener) {
            searchClickListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        searchClickListener = null
    }

    override fun onDestroy() {
        hideLoading()
        super.onDestroy()
    }


}