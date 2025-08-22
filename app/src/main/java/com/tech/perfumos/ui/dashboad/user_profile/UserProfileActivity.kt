package com.tech.perfumos.ui.dashboad.user_profile

import android.app.Activity

import android.content.Intent
import android.os.Build

import android.util.Log
import android.view.View
import android.view.ViewGroup

import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.data.model.BaseModel

import com.tech.perfumos.databinding.ActivityUserProfileBinding
import com.tech.perfumos.databinding.BadgeDialogBinding
import com.tech.perfumos.databinding.BadgeItemViewBinding

import com.tech.perfumos.databinding.ReviewItemCardBinding
import com.tech.perfumos.databinding.SpinnerItemViewBinding
import com.tech.perfumos.ui.auth_folder.model.UserDataModel
import com.tech.perfumos.ui.base.BaseActivity

import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter

import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.BadgeModel
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.EditProfile
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.FavoriteAdapter
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.FavoriteList
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.ProfileCollectionAdapter
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.ProfileDataModel
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.ProfileFragmentVm
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.ProfileModel
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.ProfileViewModel
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.UserPerfumeList
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.UserReview
import com.tech.perfumos.ui.dashboad.fragment_folder.setting.SettingFragment
import com.tech.perfumos.ui.notification.NotificationActivity
import com.tech.perfumos.utils.BaseCustomDialog
import com.tech.perfumos.utils.EventsHandler
import com.tech.perfumos.utils.GridDividerItemDecoration

import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.showErrorToast
import com.tech.perfumos.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderEffectBlur
import org.json.JSONObject

@AndroidEntryPoint
class UserProfileActivity : BaseActivity<ActivityUserProfileBinding>() {
    private val viewModel: ProfileFragmentVm by viewModels()
    private lateinit var badgeDialog: BaseCustomDialog<BadgeDialogBinding>
    private var profileData: ProfileDataModel.Data? = null
    private var collectionList: ArrayList<UserPerfumeList?>? = ArrayList()
    private lateinit var tabAdapter: SimpleRecyclerViewAdapter<ProfileModel, SpinnerItemViewBinding>

    private val tabsItemList = ArrayList<ProfileModel>()
    private var wishList: ArrayList<UserPerfumeList?>? = ArrayList()
    private var userReview: ArrayList<UserReview?>? = ArrayList()
    private var userBadgeList: ArrayList<BadgeModel?>? = ArrayList()
    private var favoriteList: ArrayList<FavoriteList?>? = ArrayList()
    private var PAGE = 1
    private val LIMIT = 10
    private var isLoading = false
    private var isLastPage = false
    private val type: Int = 1
    override fun getLayoutResource(): Int {
        return R.layout.activity_user_profile
    }

    var userID :String?=null
    override fun onCreateView() {
        Utils.screenFillView(this)
        Glide.with(this).asGif().load(R.drawable.bg_animation)
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)
        observerClick()
        initObserver()
        initAdapter()
        badgeDialog()
        setTabs()
         userID = intent?.getStringExtra("USER_ID")
        Log.i("sdkfmnsdjfhajdkshfsjkdh", "onCreateView: "+userID.toString())
        viewModel.getProfileApiOtherUser(Constants.GET_PROFILE_API+"?userId=$userID&type=2")

//        EventsHandler.getCollection<Pair<String, Boolean>>(lifecycleScope) { event ->
//            Log.d("event", "profile fragment: ${event.first} , ${event.second}")
//
//            viewModel.getProfileApi(Constants.GET_PROFILE_API)
//            /*if (event.second) {
//            } else {
//            }*/
//        }
//        EventsHandler.getWishList<Pair<String, Boolean>>(lifecycleScope) { event ->
//            Log.d("event", "profile fragment: ${event.first} , ${event.second}")
//
//            viewModel.getProfileApi(Constants.GET_PROFILE_API)
//            /*if (event.second) {
//            } else {
//            }*/
//        }

    }

    private fun setTabs() {
        tabsItemList.clear()
        tabsItemList.add(
            ProfileModel(
                ContextCompat.getString(
                    this,
                    R.string.my_collection
                ), "0", 1
            )
        )
        tabsItemList.add(
            ProfileModel(
                ContextCompat.getString(this, R.string.wishlist),
                "0",
                0
            )
        )
        tabsItemList.add(
            ProfileModel(
                ContextCompat.getString(this, R.string.reviews),
                "0",
                0
            )
        )
        tabsItemList.add(
            ProfileModel(
                ContextCompat.getString(
                    this,
                    R.string.badges_earned
                ), "0", 0
            )
        )
        tabsItemList.add(
            ProfileModel(
                ContextCompat.getString(this, R.string.favorite),
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
                        val intent = Intent(this, PerfumeInfoActivity::class.java)
                        intent.putExtra("perfumeId", m.perfumeId?.id.toString())
                        startActivity(intent)
                    } else if (type == 4) {
                        badgeDialog.show()

                    }
                }
            }
        }*/

        collectionAdapter = ProfileCollectionAdapter(
            this,
            collectionList,
            object : ProfileCollectionAdapter.ItemClickListener {

                override fun onItemClickListener(perfumeId: String) {
//                    searchClickListener?.openPerfumeScreen(perfumeId)
                }

                override fun onEmptyItemClickListener(position: Int) {
//                    searchClickListener?.onSearchClick()
                }

            }
        )
        binding.collectionRv.adapter = collectionAdapter
        collectionAdapter.list = collectionList


        while (binding.collectionRv.itemDecorationCount > 0) {
            val decoration = binding.collectionRv.getItemDecorationAt(0)
            binding.collectionRv.removeItemDecoration(decoration)
        }

        if (type != 3 && type != 4) {
            binding.collectionRv.addItemDecoration(
                GridDividerItemDecoration(
                    this,
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
        favoriteAdapter = FavoriteAdapter(
            this,
            favoriteList,
            object : FavoriteAdapter.ItemClickListener {
                override fun onItemClickListener(model: FavoriteList) {
                    when (model.type) {
                        "perfume" -> {
                            //searchClickListener?.openPerfumeScreen(model.perfumeId?.id.toString())
                        }

                        "note" -> {

                        }

                        "perfumer" -> {

                        }

                        "article" -> {

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
            Constants.GET_PROFILE_REVIEW_API,
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
        viewModel.commonObserver.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading..")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        Constants.FOLLOW_PERSON -> {
                            val data: FollowModel? = Utils.parseJson(it.data.toString())
                            if(data?.isFollowing==true){
                                binding.FollowTV.text="Following"
                            }else{
                                binding.FollowTV.text="Follow"
                            }
                        }

                        Constants.GET_PROFILE_REVIEW_API -> {
                            try {
                                val data: ProfileViewModel? = Utils.parseJson(it.data.toString())
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

                                    }
                                } else {

                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                isLoading = false
                            }
                        }

                        Constants.GET_PROFILE_API -> {
                            try {
                                Log.d("ProfileDataModel", "ProfileDataModel: ${Gson().toJson(it)}")
                                val data: ProfileDataModel? = Utils.parseJson(it.data.toString())
                                Log.d("ProfileDataModel", "ProfileDataModel : ${data?.success}")

                                profileData = data?.data

                                if (profileData != null) {
                                    viewModel.hasLoadedData = true
                                    val currentUser = sharedPrefManager.getCurrentUser()
                                    val token = currentUser?.token
                                    if(data?.data?.isFollowing==true){
                                        binding.FollowTV.text="Following"
                                    }else{
                                        binding.FollowTV.text="Follow"
                                    }

                                    val userDataModel =
                                        mapProfileDataToUserDataModel(profileData!!, token)
                                    sharedPrefManager.saveUser(userDataModel)

                                    binding.apply {
                                        Glide.with(this@UserProfileActivity)
                                            .load("${Constants.BASE_URL_IMAGE}${profileData?.profileImage}")
                                            /*.placeholder(R.drawable.dummy_image)*/
                                            .error(R.drawable.dummy_image).into(userProfile)

                                        tvUserName.text = profileData?.fullname
                                        followerCountTv.text = profileData?.followers.toString()
                                        followingCountTv.text = profileData?.following.toString()
                                        tvRankPoint.text = profileData?.rankPoints.toString()


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

                                    binding.spinnerTab.text =
                                        "My Collection (${profileData?.collections?.size.toString()})"
                                    binding.rvTabs.visibility = View.GONE

                                    wishList = profileData?.wishlists

                                    userBadgeList = profileData?.badges

                                    favoriteList = profileData?.favorites

                                    if (profileData?.totalReviews != null && profileData?.averageRating != null) {
                                        val rating =
                                            String.format("%.2f", profileData?.averageRating)
                                        binding.count.text = rating

                                        binding.totalCount.text =
                                            Utils.formatReviewCount(profileData?.totalReviews)
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
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }
                R.id.followLL -> {
                    viewModel.postUserProfileApi(Constants.FOLLOW_PERSON + "/$userID")
                }

                R.id.settingIcon -> {
                    editProfileLauncher.launch(Intent(this, EditProfile::class.java))
                    /*val intent = Intent(this, EditProfile::class.java)
                    startActivity(intent)*/
                }

                R.id.editProfileIcon -> {
                    loadFragment(SettingFragment())
                }

                R.id.notificationIcon -> {
                    val intent = Intent(this, NotificationActivity::class.java)
                    startActivity(intent)
                }

                R.id.spinnerTab -> {
                    if (binding.rvTabs.isVisible) {
                        binding.rvTabs.visibility = View.GONE

                        binding.ivDrop.animate().rotation(90f).setDuration(300).start()

                    } else {
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
            this, R.layout.badge_dialog
        ) {
            when (it?.id) {
                R.id.ivClose -> {
                    badgeDialog.dismiss()
                }


            }
        }
        badgeDialog.create()
        badgeDialog.setCancelable(true)


        val windowBackground = this.window.decorView.background

        val blurView = badgeDialog.binding.blurView

        val rootView =
            this.window.decorView.findViewById<ViewGroup>(android.R.id.content)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView.setupWith(rootView, RenderEffectBlur())
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(16f)
        }


    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = this.supportFragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
        transaction.replace(R.id.homeSectionNav, fragment)
        transaction.addToBackStack("SETTINGS")
        transaction.commit()
    }


    private val editProfileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.getProfileApi(Constants.GET_PROFILE_API)
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
                        UserReview.PerfumeId(
                            id = p._id,
                            brand = p.brand,
                            image = p.image,
                            name = p.name
                        )
                    },
                    rating = it.rating,
                    review = it.review,
                    userId = it.userId?.let { u ->
                        UserReview.UserId(
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


    override fun getViewModel(): BaseViewModel {
        return viewModel
    }


}
data class FollowModel(
    val message: String,
    val success: Boolean,
    val isFollowing: Boolean
)