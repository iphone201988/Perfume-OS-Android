package com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.data.api.Constants.BASE_URL_IMAGE
import com.tech.perfumos.data.api.Constants.GET_PROFILE_API
import com.tech.perfumos.data.api.Constants.GET_PROFILE_REVIEW_API
import com.tech.perfumos.databinding.BadgeDialogBinding
import com.tech.perfumos.databinding.BadgeItemViewBinding
import com.tech.perfumos.databinding.CollectionItemCountBinding
import com.tech.perfumos.databinding.FavoriteItemViewBinding
import com.tech.perfumos.databinding.FragmentProfileBinding
import com.tech.perfumos.databinding.ItemProfileTabsBinding
import com.tech.perfumos.databinding.ReviewItemCardBinding
import com.tech.perfumos.databinding.SpinnerItemViewBinding
import com.tech.perfumos.ui.auth_folder.model.UserDataModel
import com.tech.perfumos.ui.base.BaseFragment
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.camera_perfume.PerfumeInfoActivity
import com.tech.perfumos.ui.core.SearchClickListener
import com.tech.perfumos.ui.dashboad.fragment_folder.articles_folder.ArticleContent
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.UserReview.PerfumeId
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.UserReview.UserId
import com.tech.perfumos.ui.dashboad.fragment_folder.setting.SettingFragment
import com.tech.perfumos.ui.notification.NotificationActivity
import com.tech.perfumos.utils.BaseCustomDialog
import com.tech.perfumos.utils.CustomSpinnerAdapter
import com.tech.perfumos.utils.EventsHandler
import com.tech.perfumos.utils.GridDividerItemDecoration
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils.formatReviewCount
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import com.tech.perfumos.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderEffectBlur
import org.json.JSONObject
import androidx.core.view.isVisible



@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileFragmentVm by viewModels()
    private lateinit var badgeDialog: BaseCustomDialog<BadgeDialogBinding>
    private var profileData: ProfileDataModel.Data? = null
    private var collectionList: ArrayList<UserPerfumeList?>? = ArrayList()

    //private lateinit var tabAdapter: SimpleRecyclerViewAdapter<ProfileModel, ItemProfileTabsBinding>
    private lateinit var tabAdapter: SimpleRecyclerViewAdapter<ProfileModel, SpinnerItemViewBinding>

    private val tabsItemList = ArrayList<ProfileModel>()
    private var searchClickListener: SearchClickListener? = null
    var selectedTab = 1
    private var wishList: ArrayList<UserPerfumeList?>? = ArrayList()
    private var userReview: ArrayList<UserReview?>? = ArrayList()
    private var userBadgeList: ArrayList<BadgeModel?>? = ArrayList()
    private var favoriteList : ArrayList<FavoriteList?>? = ArrayList()
    private var PAGE = 1
    private val LIMIT = 10
    private var isLoading = false
    private var isLastPage = false
    private val type: Int = 1

    private lateinit var spinnerDate: Spinner

    override fun getLayoutResource(): Int {
        return R.layout.fragment_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
//        Utils.screenFillView(requireActivity())
        observerClick()
        initObserver()
        initAdapter()
        badgeDialog()
        setTabs()
        viewModel.getProfileApi(GET_PROFILE_API)

        Log.d("profileFragment", "onCreateView: ${viewModel.hasLoadedData}")

        EventsHandler.getCollection<Pair<String, Boolean>>(lifecycleScope) { event ->
            Log.d("event", "profile fragment: ${event.first} , ${event.second}")

            viewModel.getProfileApi(GET_PROFILE_API)
            /*if (event.second) {
            } else {
            }*/
        }
        EventsHandler.getWishList<Pair<String, Boolean>>(lifecycleScope) { event ->
            Log.d("event", "profile fragment: ${event.first} , ${event.second}")

            viewModel.getProfileApi(GET_PROFILE_API)
            /*if (event.second) {
            } else {
            }*/
        }

    }

    private fun setTabs() {
        tabsItemList.clear()
        tabsItemList.add(
            ProfileModel(
                ContextCompat.getString(
                    requireContext(),
                    R.string.my_collection
                ), "0", 1
            )
        )
        tabsItemList.add(
            ProfileModel(
                ContextCompat.getString(requireContext(), R.string.wishlist),
                "0",
                0
            )
        )
        tabsItemList.add(
            ProfileModel(
                ContextCompat.getString(requireContext(), R.string.reviews),
                "0",
                0
            )
        )
        tabsItemList.add(
            ProfileModel(
                ContextCompat.getString(
                    requireContext(),
                    R.string.badges_earned
                ), "0", 0
            )
        )
        tabsItemList.add(
            ProfileModel(
                ContextCompat.getString(requireContext(), R.string.favorite),
                "0",
                0
            )
        )

        tabAdapter.list = tabsItemList
        tabAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        hideLoading()
        super.onDestroy()
    }

    private fun initAdapter() {

        tabAdapter = SimpleRecyclerViewAdapter(
            R.layout.spinner_item_view, BR.bean
        ) { v, m, pos ->

            when (v.id) {

                R.id.clMain -> {
                    when (pos) {
                        0 -> {
                            binding.selectTap = 1
                            //setUpRecyclerview(1)
                            /*tabsItemList.forEach {
                                it.selected = 0
                            }
                            m.selected = 1*/
                            tabAdapter.notifyDataSetChanged()
                            if (collectionList.isNullOrEmpty()) {
                                binding.collectionRv.visibility = View.GONE
                                binding.noDataFound.visibility = View.VISIBLE

                            } else {
                                collectionAdapter.list = collectionList
                                collectionAdapter.notifyDataSetChanged()
                                binding.collectionRv.visibility = View.VISIBLE
                                binding.noDataFound.visibility = View.GONE
                            }
                            binding.spinnerTab.text = "${m.name} (${m.count})"
                            binding.rvTabs.visibility = View.GONE
                        }

                        1 -> {
                            binding.selectTap = 2
                            // setUpRecyclerview(2)
                            /*tabsItemList.forEach {
                                it.selected = 0
                            }
                            m.selected = 1*/
                            tabAdapter.notifyDataSetChanged()
                            if (wishList.isNullOrEmpty()) {
                                binding.collectionRv.visibility = View.GONE
                                binding.noDataFound.visibility = View.VISIBLE
                            } else {
                                collectionAdapter.list = wishList
                                collectionAdapter.notifyDataSetChanged()
                                binding.collectionRv.visibility = View.VISIBLE
                                binding.noDataFound.visibility = View.GONE
                            }
                            binding.spinnerTab.text = "${m.name} (${m.count})"
                            binding.rvTabs.visibility = View.GONE
                        }

                        2 -> {
                            binding.selectTap = 3
                            binding.collectionRv.visibility = View.GONE
                            /*//setUpRecyclerview(3)
                            tabsItemList.forEach {
                                it.selected = 0
                            }
                            m.selected = 1*/
                            tabAdapter.notifyDataSetChanged()
                            if (userReview.isNullOrEmpty()) {
                                binding.rvRecyclerviewReview.visibility = View.GONE
                                binding.noDataFound.visibility = View.VISIBLE
                            } else {
                                reviewAdapter.list = userReview
                                reviewAdapter.notifyDataSetChanged()
                                binding.noDataFound.visibility = View.GONE
                                binding.rvRecyclerviewReview.visibility = View.VISIBLE
                            }
                            binding.spinnerTab.text = "${m.name} (${m.count})"
                            binding.rvTabs.visibility = View.GONE
                        }

                        3 -> {
                            binding.selectTap = 4
                            //setUpRecyclerview(4)
                            /*tabsItemList.forEach {
                                it.selected = 0
                            }
                            m.selected = 1*/
                            tabAdapter.notifyDataSetChanged()
                            binding.collectionRv.visibility = View.GONE
                            if (userBadgeList.isNullOrEmpty()) {
                                binding.rvBadges.visibility = View.GONE
                                binding.noDataFound.visibility = View.VISIBLE
                            } else {
                                badgeAdapter.list = userBadgeList
                                badgeAdapter.notifyDataSetChanged()
                                binding.noDataFound.visibility = View.GONE
                            }
                            binding.spinnerTab.text = "${m.name} (${m.count})"
                            binding.rvTabs.visibility = View.GONE
                        }

                        4 -> {
                            binding.selectTap = 5
                            /*tabsItemList.forEach {
                                it.selected = 0
                            }
                            m.selected = 1*/
                            tabAdapter.notifyDataSetChanged()
                            binding.collectionRv.visibility = View.GONE
                           /* binding.collectionRv.visibility = View.GONE
                            binding.rvRecyclerviewReview.visibility = View.GONE
                            binding.rvBadges.visibility = View.GONE
                            binding.noDataFound.visibility = View.VISIBLE*/

                            if (wishList.isNullOrEmpty()) {
                                binding.rvFavorite.visibility = View.GONE
                                binding.noDataFound.visibility = View.VISIBLE
                            } else {
                                favoriteAdapter.list = favoriteList
                                favoriteAdapter.notifyDataSetChanged()
                                binding.rvFavorite.visibility = View.VISIBLE
                                binding.noDataFound.visibility = View.GONE
                            }
                            binding.spinnerTab.text = "${m.name} (${m.count})"
                            binding.rvTabs.visibility = View.GONE
                        }
                    }
                }
            }
        }
        binding.rvTabs.adapter = tabAdapter
        tabAdapter.list = tabsItemList

        /*collectionAdapter = SimpleRecyclerViewAdapter(
            R.layout.collection_item_count, BR.bean
        ) { v, m, pos ->

            when (v.id) {
                R.id.llMain -> {
                    if (type == 1 || type == 2) {
                        val intent = Intent(requireContext(), PerfumeInfoActivity::class.java)
                        intent.putExtra("perfumeId", m.perfumeId?.id.toString())
                        startActivity(intent)
                    } else if (type == 4) {
                        badgeDialog.show()

                    }
                }
            }
        }*/

        collectionAdapter = ProfileCollectionAdapter(requireContext(), collectionList, object : ProfileCollectionAdapter.ItemClickListener {

            override fun onItemClickListener(perfumeId: String) {
                searchClickListener?.openPerfumeScreen(perfumeId)
            }

            override fun onEmptyItemClickListener(position: Int) {
                searchClickListener?.onSearchClick()
            }

        }
        )
        binding.collectionRv.adapter = collectionAdapter
        collectionAdapter.list = collectionList

//        binding.collectionRv.setLayoutManager(GridLayoutManager(requireActivity(), spanCount))

        while (binding.collectionRv.itemDecorationCount > 0) {
            val decoration = binding.collectionRv.getItemDecorationAt(0)
            binding.collectionRv.removeItemDecoration(decoration)
        }

        if (type != 3 && type != 4) {
            binding.collectionRv.addItemDecoration(
                GridDividerItemDecoration(
                    requireActivity(),
                    1,
                    5
                )
            )
        }

        reviewAdapter = SimpleRecyclerViewAdapter(
            R.layout.review_item_card, BR.bean
        ) { v, m, pos ->


        }
        binding.rvRecyclerviewReview.adapter = reviewAdapter
        reviewAdapter.list = userReview

        /*favoriteAdapter = SimpleRecyclerViewAdapter(
            R.layout.favorite_item_view, BR.bean
        ) { v, m, pos ->
            when(v?.id){
                R.id.clMain->{
                    searchClickListener?.openPerfumeScreen(m?.perfumeId?.id.toString())
                }
            }

        }*/
        favoriteAdapter = FavoriteAdapter(requireContext(), favoriteList, object : FavoriteAdapter.ItemClickListener {
            override fun onItemClickListener(model: FavoriteList) {
                when(model.type){
                    "perfume"->{
                        searchClickListener?.openPerfumeScreen(model.perfumeId?.id.toString())
                    }
                    "note"->{
//                        val intent = Intent(requireActivity(), ArticleContent::class.java).apply {
//                            putExtra("articleId", model.id)
//                        }
//                        startActivity(intent)

                    }
                    "perfumer"->{

                    }
                    "article"->{

                    }
                }
            }
        })
        binding.rvFavorite.adapter = favoriteAdapter

        binding.rvRecyclerviewReview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
//                        PAGE++
//                        isLoading = true
//                        fetchReviews()
                    }
                }
            }
        })

        binding.nestedScrollview.setOnScrollChangeListener { v: NestedScrollView, _, _, scrollX, oldScrollX ->
            if (!v.canScrollVertically(1)) {
                // Reached the bottom
                if (!isLoading && !isLastPage) {


                    PAGE++
                    isLoading = true
                    fetchReviews()


                }
            }
        }
        fetchReviews()
        badgeAdapter = SimpleRecyclerViewAdapter(
            R.layout.badge_item_view, BR.bean
        ) { v, m, pos ->
            when (v.id) {

                R.id.llMain -> {
                    badgeDialog.show()

                }
            }

        }
        binding.rvBadges.adapter = badgeAdapter
        badgeAdapter.list = userBadgeList
    }

    private fun fetchReviews() {
        viewModel.getAllReviewWithPagination(
            GET_PROFILE_REVIEW_API,
            hashMapOf(
                "page" to PAGE,
                "limit" to LIMIT,
                "type" to "review"
            )
        )
    }



    private lateinit var collectionAdapter: ProfileCollectionAdapter

    private lateinit var reviewAdapter: SimpleRecyclerViewAdapter<UserReview, ReviewItemCardBinding>
    private lateinit var badgeAdapter: SimpleRecyclerViewAdapter<BadgeModel, BadgeItemViewBinding>
    private lateinit var favoriteAdapter: FavoriteAdapter

    private fun setUpRecyclerview(type: Int) {

        when (type) {
            1 -> {
                if (collectionList.isNullOrEmpty()) {
                    binding.collectionRv.visibility = View.GONE
                    binding.noDataFound.visibility = View.VISIBLE

                } else {
                    collectionAdapter.list = collectionList
                    collectionAdapter.notifyDataSetChanged()
                    binding.noDataFound.visibility = View.GONE
                    binding.collectionRv.visibility = View.VISIBLE
                }
            }

            2 -> {
                collectionAdapter.list = wishList
                collectionAdapter.notifyDataSetChanged()
            }

            3 -> {
                reviewAdapter.list = userReview
                reviewAdapter.notifyDataSetChanged()
            }

            4 -> {
                /*collectionAdapter.list = userBadgeList
                collectionAdapter.notifyDataSetChanged()*/
            }
        }
    }


    private fun initObserver() {
        viewModel.commonObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading..")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        GET_PROFILE_REVIEW_API -> {
//                            Log.d("ProfileDataModel", "ProfileDataModel: ${Gson().toJson(it)}")

                            try {
//                                Log.d("GetAllReview", "ProfileDataModel: ${Gson().toJson(it)}")
                                val data: ProfileViewModel? = parseJson(it.data.toString())
                                Log.d("GetAllReview", "ProfileDataModel: $data")
                                if (data != null) {


                                    val pagination = data.data?.pagination

                                    userReview = convertReviewsToUserReviews(data.data?.reviews)
                                    reviewAdapter.addToList(userReview)
                                    reviewAdapter.notifyDataSetChanged()
                                    if (pagination != null) {
                                        isLastPage = (pagination.perPage?.let { it1 ->
                                            pagination.currentPage?.times(
                                                it1
                                            )
                                        } ?: 0) >= (pagination.totalCount ?: 0)

//                                        isLastPage =if((pagination.totalCount?:0) == reviewAdapter.list.size) true else false
                                    }
                                } else {
                                    //showToast(data?.message.toString())
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                isLoading = false
                            }
                        }

                        GET_PROFILE_API -> {
                            try {
                                Log.d("ProfileDataModel", "ProfileDataModel: ${Gson().toJson(it)}")
                                val data: ProfileDataModel? = parseJson(it.data.toString())
                                Log.d("ProfileDataModel", "ProfileDataModel : ${data?.success}")

                                profileData = data?.data

                                if (profileData != null) {
                                    viewModel.hasLoadedData = true
                                    val currentUser = sharedPrefManager.getCurrentUser()
                                    val token = currentUser?.token

                                    val userDataModel =
                                        mapProfileDataToUserDataModel(profileData!!, token)
                                    sharedPrefManager.saveUser(userDataModel)

                                    binding.apply {
                                        Glide.with(this@ProfileFragment)
                                            .load("${BASE_URL_IMAGE}${profileData?.profileImage}")
                                            /*.placeholder(R.drawable.dummy_image)*/
                                            .error(R.drawable.dummy_image).into(userProfile)

                                        tvUserName.text = profileData?.fullname
                                        followerCountTv.text = profileData?.followers.toString()
                                        followingCountTv.text = profileData?.following.toString()
                                        tvRankPoint.text = profileData?.rankPoints.toString()

                                        /*myCollectionCount.text =
                                            profileData?.collections?.size.toString()
                                        wishlistCount.text = profileData?.wishlists?.size.toString()
                                        reviewsCount.text = *//*profileData?.reviews?.size*//*
                                            profileData?.totalReviews.toString()
                                        badgesEarnedCount.text =
                                            profileData?.badges?.size.toString()*/
                                    }

                                    tabsItemList[0].count =
                                        profileData?.collections?.size.toString()
                                    tabsItemList[1].count = profileData?.wishlists?.size.toString()
                                    tabsItemList[2].count = profileData?.totalReviews.toString()
                                    tabsItemList[3].count = profileData?.badges?.size.toString()
                                    tabsItemList[4].count = profileData?.totalFavorites.toString()


                                    tabAdapter.list = tabsItemList
                                    tabAdapter.notifyDataSetChanged()

                                    collectionList = profileData?.collections
                                    collectionAdapter.list = collectionList
                                    collectionAdapter.notifyDataSetChanged()

                                    binding.spinnerTab.text = "My Collection (${profileData?.collections?.size.toString()})"
                                    binding.rvTabs.visibility = View.GONE

                                    wishList = profileData?.wishlists
//                                    UserReview = profileData?.reviews
                                    userBadgeList = profileData?.badges

                                    favoriteList = profileData?.favorites

                                    if (profileData?.totalReviews != null && profileData?.averageRating != null) {
                                        val rating =
                                            String.format("%.2f", profileData?.averageRating)
                                        binding.count.text = rating
                                        //binding.count.text = profileData?.averageRating.toString()
                                        binding.totalCount.text =
                                            formatReviewCount(profileData?.totalReviews)
                                    }

                                    tabsItemList.forEach {
                                        it.selected = 0
                                    }
                                    tabsItemList[0].selected = 1
                                    tabAdapter.notifyDataSetChanged()
                                    binding.selectTap = 1

                                } else {
                                    showToast("Something went wrong")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            setUpRecyclerview(1)
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    try {
                        Log.d("ERROR", "initObserver: ${Gson().toJson(it)}")
                        val jsonObject = JSONObject(it.data.toString())
                        Log.d("ERROR", "initObserver: $jsonObject")

                        Log.d("ErrorMessage", jsonObject.getString("message").toString())
                        val message = jsonObject.getString("message")
                        showErrorToast(message)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {

                }
            }
        }
    }

    private fun observerClick() {
        binding.selectTap = 1
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {

                R.id.settingIcon -> {
                    editProfileLauncher.launch(Intent(requireActivity(), EditProfile::class.java))
                    /*val intent = Intent(requireActivity(), EditProfile::class.java)
                    startActivity(intent)*/
                }

                R.id.editProfileIcon -> {
                    loadFragment(SettingFragment())
                }
                R.id.notificationIcon -> {
                    val intent = Intent(requireActivity(), NotificationActivity::class.java)
                    startActivity(intent)
                }
                R.id.spinnerTab-> {
                    if(binding.rvTabs.isVisible){
                        binding.rvTabs.visibility = View.GONE

                        binding.ivDrop.animate().rotation(90f).setDuration(300).start()

                    }else{
                        binding.ivDrop.animate().rotation(270f).setDuration(300).start()
                        binding.rvTabs.visibility = View.VISIBLE

                    }

                }
            }
        }
    }

    private fun mapProfileDataToUserDataModel(
        profile: ProfileDataModel.Data,
        token: String?
    ): UserDataModel {
        return UserDataModel(
            dob = profile.dob,
            email = profile.email,
            enjoySmell = profile.enjoySmell,
            fullname = profile.fullname,
            gender = profile.gender,
            id = profile.id,
            isBlocked = profile.isBlocked,
            isDeleted = profile.isDeleted,
            isNotificationOn = profile.isNotificationOn,
            isVerified = profile.isVerified,
            language = profile.language,
            perfumeBudget = profile.perfumeBudget,
            perfumeStrength = profile.perfumeStrength,
            profileImage = profile.profileImage,
            reasonForWearPerfume = profile.reasonForWearPerfume,
            referralCode = profile.referralCode,
            referralSource = profile.referralSource,
            socialLinkedAccounts = profile.socialLinkedAccounts,
            step = profile.step,
            timezone = profile.timezone,
            token = token, // preserve the old token
            username = profile.username,
            tutorialProgess = profile.tutorialProgess,
            theme = profile.theme
        )
    }

    private fun badgeDialog() {
        badgeDialog = BaseCustomDialog<BadgeDialogBinding>(
            requireContext(), R.layout.badge_dialog
        ) {
            when (it?.id) {
                R.id.ivClose -> {
                    badgeDialog.dismiss()
                }


            }
        }
        badgeDialog.create()
        badgeDialog.setCancelable(true)


        val windowBackground = requireActivity().window.decorView.background

        val blurView = badgeDialog.binding.blurView

        val rootView =
            requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView.setupWith(rootView, RenderEffectBlur())
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(16f)
        }


    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
        transaction.replace(R.id.homeSectionNav, fragment)
        transaction.addToBackStack("SETTINGS")
        transaction.commit()
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

    private val editProfileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.getProfileApi(GET_PROFILE_API)
            }

        }

    private fun convertReviewsToUserReviews(reviews: List<ProfileViewModel.Data.Review?>?): ArrayList<UserReview?> {
        val userReviews = ArrayList<UserReview?>()

        reviews?.forEach { review ->
            review?.let {
                val userReview = UserReview(
                    createdAt = it.createdAt,
                    id = it._id,
                    perfumeId = it.perfumeId?.let { p ->
                        PerfumeId(
                            id = p._id,
                            brand = p.brand,
                            image = p.image,
                            name = p.name
                        )
                    },
                    rating = it.rating,
                    review = it.review,
                    userId = it.userId?.let { u ->
                        UserId(
                            id = u._id,
                            fullname = u.fullname,
                            profileImage = u.profileImage
                        )
                    },
                    title = it.title
                )
                userReviews.add(userReview)
            }
        }

        return userReviews
    }

    private fun selectSpinnerItemByValue(spinner: Spinner, value: String) {
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            val item = adapter.getItem(i)
            if (item is String && item == value) {
                spinner.setSelection(i)
                break
            }
        }
    }



}