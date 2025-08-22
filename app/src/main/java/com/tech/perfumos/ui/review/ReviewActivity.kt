package com.tech.perfumos.ui.review

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.ADD_COLLECTION_API
import com.tech.perfumos.data.api.Constants.ADD_REVIEW_API
import com.tech.perfumos.data.api.Constants.ADD_WISHLIST_API
import com.tech.perfumos.data.api.Constants.GET_PERFUMER_ALL_REVIEWS
import com.tech.perfumos.data.api.Constants.PERFUME_API
import com.tech.perfumos.data.model.GetAllReview
import com.tech.perfumos.databinding.ActivityReviewBinding
import com.tech.perfumos.databinding.ReviewDialogBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.camera_perfume.ReviewAdapter
import com.tech.perfumos.ui.camera_perfume.model.PerfumeInfoModel
import com.tech.perfumos.ui.camera_perfume.model.ReviewModel
import com.tech.perfumos.utils.BaseCustomDialog
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils.formatReviewCount
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.seekbar.OnSeekChangeListener
import com.tech.perfumos.utils.seekbar.SeekParams
import com.tech.perfumos.utils.seekbar.TickSeekBar
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderEffectBlur
import org.json.JSONObject
import java.util.Locale

@AndroidEntryPoint
class ReviewActivity : BaseActivity<ActivityReviewBinding>() {
    private var perfumeInfo: PerfumeInfoModel? = null
    private var isCollection: Boolean = false
    private var isWishList: Boolean = false
    private var newReviewList: ArrayList<ReviewModel?>? = ArrayList()
    private var reviewList: ArrayList<ReviewModel?>? = ArrayList()
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var reviewDialog: BaseCustomDialog<ReviewDialogBinding>
    private val tabsItemList = ArrayList<String>()
    private var PAGE = 1
    private val LIMIT = 10
    private var isLoading = false
    private var isLastPage = false

    private val viewmodel: ReviewVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_review
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView() {
        // initView()
        initAnimation()
        clickListener()
        initObserver()
        reviewDialog()

    }

    @SuppressLint("DefaultLocale")
    private fun initObserver() {
        viewmodel.commonObserver.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading..")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        PERFUME_API -> {
                            try {
                                Log.d("ProfileDataModel", "ProfileDataModel: ${Gson().toJson(it)}")
                                val data: PerfumeInfoModel? = parseJson(it.data.toString())

                                if (data?.success == true) {
                                    perfumeInfo = data
                                    initView()
                                    fetchReviews()

                                } else {
                                    showToast(data?.message.toString())
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        GET_PERFUMER_ALL_REVIEWS -> {
                            try {
                                Log.d("GetAllReview", "ProfileDataModel: ${Gson().toJson(it)}")
                                val data: GetAllReview? = parseJson(it.data.toString())

                                if (data?.success == true) {
                                    val newReviews = data.data?.reviews ?: emptyList()

                                    // Pagination metadata
                                    val pagination = data.data?.pagination
                                    if (pagination != null) {
                                        isLastPage = (pagination.perPage?.let { it1 ->
                                            pagination.currentPage?.times(
                                                it1
                                            )
                                        } ?: 0) >= (pagination.totalCount ?: 0)
                                    }

                                    if (PAGE == 1) {
                                        reviewList?.clear()
                                    }

                                    reviewList?.addAll(newReviews)
                                    reviewAdapter.notifyDataSetChanged()
                                    binding.tvReview.text =
                                        formatReviewCount(data.data?.pagination?.totalCount)

                                    /*val rating = String.format("%.2f", data.data?.reviews.averageRating)
                                    binding.tvRating.text = rating*/

                                } else {
                                    showToast(data?.message.toString())
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                isLoading = false
                            }
                        }


                        "ADD_TO_COLLECTION" -> {
                            try {
                                Log.d(
                                    "response", "ADD_TO_COLLECTION: ${Gson().toJson(it)}"
                                )
                                // val data: LoginModel? = Utils.parseJson(it.data.toString())

                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: ${jsonObject}")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())
                                    isCollection = !isCollection

                                    if (isCollection) {
                                        binding.tvAddCollection.text = ContextCompat.getString(
                                            this, R.string.addedInCollection
                                        )
                                    } else {
                                        binding.tvAddCollection.text = ContextCompat.getString(
                                            this, R.string.add_to_collection
                                        )
                                    }
                                } else {
                                    showErrorToast(jsonObject.getString("message").toString())
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        "ADD_TO_WISHLIST" -> {
                            try {
                                Log.d(
                                    "response", "ADD_TO_WISHLIST: ${Gson().toJson(it)}"
                                )
                                // val data: LoginModel? = Utils.parseJson(it.data.toString())

                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: ${jsonObject}")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())
                                    isWishList = !isWishList
                                    if (isWishList) {
                                        binding.tvAddWishlist.text =
                                            ContextCompat.getString(this, R.string.add_in_wishlist)
                                    } else {
                                        binding.tvAddWishlist.text =
                                            ContextCompat.getString(this, R.string.add_to_wishlist)
                                    }
                                } else {
                                    showErrorToast(jsonObject.getString("message").toString())
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        ADD_REVIEW_API -> {
                            reviewDialog.dismiss()
                            try {
                                Log.d(
                                    "response", "ADD_REVIEW_API: ${Gson().toJson(it)}"
                                )
                                val jsonObject = JSONObject(it.data.toString())
                                val success = jsonObject.getBoolean("success")
                                val data = jsonObject.getJSONObject("data")
                                if (success) {
                                    val reviewData: ReviewModel? = parseJson(data.toString())
                                    showToast(jsonObject.getString("message").toString())
                                    Log.d("dasdasdasd", "initObserver: ${Gson().toJson(it)}")
                                    if (reviewData != null) {/*reviewList?.add(reviewData)
                                        reviewAdapter.list = reviewList*/
                                        newReviewList?.add(reviewData)
                                        reviewAdapter.addNewItem(reviewData)
                                        //reviewAdapter.notifyDataSetChanged()

                                        binding.apply {
                                            perfumeInfo?.data?.totalReviewsAndRatings?.totalReviews =
                                                (perfumeInfo?.data?.totalReviewsAndRatings?.totalReviews
                                                    ?: 0) + 1
                                            val reviewCount =
                                                formatReviewCount(perfumeInfo?.data?.totalReviewsAndRatings?.totalReviews)
                                            tvReview.text = reviewCount
                                            perfumeInfo?.data?.totalReviewsAndRatings?.averageRating =
                                                (perfumeInfo?.data?.totalReviewsAndRatings?.averageRating
                                                    ?: 0.0) + 1
                                            val rating = String.format(
                                                "%.2f",
                                                perfumeInfo?.data?.totalReviewsAndRatings?.averageRating
                                            )
                                            tvRating.text = rating
                                        }
                                    } else {
                                        Log.d("dasdasdasd", "emplty : $reviewData")
                                    }
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
                }

                else -> {
                }
            }
        }
    }

    private fun initAnimation() {
        Glide.with(this).asGif().load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)

        val requestMap = hashMapOf<String, Any>(
            "perfumeId" to intent.getStringExtra("perfumeId").toString(), "isSearch" to true
        )
        viewmodel.getPerfumeApi(PERFUME_API, requestMap)

        reviewAdapter = ReviewAdapter(this, reviewList)
        binding.rvReview.adapter = reviewAdapter

        binding.rvReview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                        fetchReviews()
                    }
                }
            }
        })

    }

    private fun clickListener() {
        viewmodel.onClick.observe(this, Observer {
            when (it?.id) {

                R.id.back -> {
                    val resultIntent = Intent()
                    resultIntent.putExtra("newReviewList", newReviewList)
                    setResult(RESULT_OK, resultIntent)
                    finish()

                }

                R.id.tv_write_review -> {
                    reviewDialog.show()
                }

                R.id.tvAddCollection -> {
                    val requestMap = hashMapOf<String, Any>(
                        //"collection" to perfumeInfo?.data?.id.toString(),
                    )

                    viewmodel.addToCollectionApi(
                        "$ADD_COLLECTION_API${perfumeInfo?.data?.id}", requestMap
                    )
                }

                R.id.tvAddWishlist -> {
                    val requestMap = hashMapOf<String, Any>(
                        //"collection" to perfumeInfo?.data?.id.toString(),
                    )

                    viewmodel.addToWishlistApi(
                        "$ADD_WISHLIST_API${perfumeInfo?.data?.id}", requestMap
                    )
                }
            }
        })

    }

    private fun fetchReviews() {
        viewmodel.getAllReviewWithPagination(
            GET_PERFUMER_ALL_REVIEWS,
            hashMapOf(
                "page" to PAGE,
                "limit" to LIMIT,
                "perfumeId" to intent.getStringExtra("perfumeId").toString()
            )
        )
    }


    private fun initView() {
        if (perfumeInfo != null) {
            perfumeInfo?.data.let {
                binding.apply {
                    Glide.with(this@ReviewActivity).load(it?.image).into(perfumeImg)

                    it?.let { data ->
                        data.name?.takeIf { it.isNotBlank() }?.let { tvName.text = it }
                        data.brand?.takeIf { it.isNotBlank() }?.let { tvType.text = it }
                    }


                    isCollection = it?.isCollection ?: false
                    isWishList = it?.isWishlist ?: false

                    if (isCollection) {
                        binding.tvAddCollection.text = ContextCompat.getString(
                            this@ReviewActivity, R.string.addedInCollection
                        )
                    } else {
                        binding.tvAddCollection.text = ContextCompat.getString(
                            this@ReviewActivity, R.string.add_to_collection
                        )
                    }

                    if (isWishList) {
                        binding.tvAddWishlist.text = ContextCompat.getString(
                            this@ReviewActivity, R.string.add_in_wishlist
                        )
                    } else {
                        binding.tvAddWishlist.text = ContextCompat.getString(
                            this@ReviewActivity, R.string.add_to_wishlist
                        )
                    }

                    val reviewCount = formatReviewCount(it?.totalReviewsAndRatings?.totalReviews)
                    tvReview.text = reviewCount

                    val rating = String.format("%.2f", it?.totalReviewsAndRatings?.averageRating)
                    tvRating.text = rating
                }

            }
        }
    }


    /*@RequiresApi(Build.VERSION_CODES.S)
    private fun reviewDialog() {
        reviewDialog = BaseCustomDialog<ReviewDialogBinding>(
            this, R.layout.review_dialog
        ) { view ->
            when (view?.id) {
                R.id.tv_write_review -> {
                    val rating = reviewDialog.binding.ratingBar.rating
                    val requestMap = hashMapOf<String, Any>(
                        "perfumeId" to perfumeInfo?.data?.id.toString(),
                        "rating" to rating,
                        "review" to reviewDialog.binding.tvDesc.text.toString(),
                    )
                    viewmodel.postReviewApi(ADD_REVIEW_API, requestMap)

                }

                R.id.ivClose -> {
                    reviewDialog.dismiss()
                }

            }
        }
        reviewDialog.create()
        reviewDialog.setCancelable(true)


        //set background, if your root layout doesn't have one
        val windowBackground = window.decorView.background
        val reviewDialogBlurView = reviewDialog.binding.blurView

        val rootView1 = window.decorView.findViewById<ViewGroup>(android.R.id.content)

        // Use RenderEffectBlur for Android 12+ (API 31+)
        reviewDialogBlurView.setupWith(rootView1, RenderEffectBlur())
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(29f) // Try 16-20 for visible blur

    }*/


    private fun reviewDialog() {
        val requestMap = hashMapOf<String, Any>()
        var longevity: String? = ""
        var silliAge: String? = ""
        var gender: String? = ""
        var priceValue: String? = ""
        reviewDialog = BaseCustomDialog<ReviewDialogBinding>(
            this, R.layout.review_dialog
        ) { view ->
            when (view?.id) {
                R.id.tv_write_review -> {

                    if (reviewDialog.binding.etReviewTitle.text.trim().toString().isNullOrEmpty()) {
                        Toast.makeText(this, "Title can not be empty", Toast.LENGTH_SHORT).show()
                    } else if (longevity == "") {
                        showErrorToast("Please select longevity")
                    } else if (silliAge == "") {
                        showErrorToast("Please select sill age")
                    } else if (gender == "") {
                        showErrorToast("Please select gender")
                    } else if (priceValue == "") {
                        showErrorToast("Please select price value")
                    } else if (reviewDialog.binding.tvDesc.text.trim().toString().isNullOrEmpty()) {
                        Toast.makeText(this, "Please write a review", Toast.LENGTH_SHORT).show()
                    } else {


                        val rating = reviewDialog.binding.ratingBar.rating
                        requestMap.put("perfumeId", perfumeInfo?.data?.id.toString())
                        requestMap.put("rating", rating)
                        requestMap.put(
                            "review",
                            reviewDialog.binding.etReviewTitle.text.trim().toString()
                        )
                        requestMap.put("review", reviewDialog.binding.tvDesc.text.toString())

                        requestMap.put("longevity", longevity!!)
                        requestMap.put("sillage", silliAge!!)
                        requestMap.put("gender", gender!!)
                        requestMap.put("price", priceValue!!)
                        viewmodel.postReviewApi(ADD_REVIEW_API, requestMap)
                        reviewDialog.binding.tvDesc.setText("")
                    }

                }

                R.id.spinnerTab -> {
                    reviewDialog.binding.rvTabs.visibility = View.VISIBLE
                }

                R.id.ivClose -> {
                    reviewDialog.dismiss()
                }

            }
        }
        reviewDialog.create()
        reviewDialog.setCancelable(true)

        reviewDialog.setOnDismissListener {
            reviewDialog.binding.etReviewTitle.setText("")
            reviewDialog.binding.tvDesc.setText("")


        }

        val longevityList = arrayOf("Very Weak", "Weak", "Moderate", "Long lasting", "Eternal")
        reviewDialog.binding.skLongevity.customTickTexts(longevityList)
        reviewDialog.binding.skLongevity.tickCount = longevityList.size

        val sillAgeList = arrayOf("Intimate", "Moderate", "Strong", "Enormous")
        reviewDialog.binding.skSillAge.customTickTexts(sillAgeList)
        reviewDialog.binding.skSillAge.tickCount = sillAgeList.size

        val genderList = arrayOf("Female", "More Female", "Unisex", "More Male", "Male")
        reviewDialog.binding.skGender.customTickTexts(genderList)
        reviewDialog.binding.skGender.tickCount = genderList.size

        val priceList = arrayOf("Overpriced", "Ok", "Good Value", "Great Value")
        reviewDialog.binding.skPrice.customTickTexts(priceList)
        reviewDialog.binding.skPrice.tickCount = priceList.size

        reviewDialog.binding.skLongevity.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                seekParams?.let {
                    longevity =
                        it.tickText.lowercase(Locale.ROOT)           // Text from tick if defined
                    Log.d("SeekBar", "Longevity: $longevity")
                    //tvDosage.text = tickText ?: "0"
                }
            }

            override fun onStartTrackingTouch(seekBar: TickSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: TickSeekBar?) {
            }
        }

        reviewDialog.binding.skSillAge.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                seekParams?.let {
                    silliAge =
                        it.tickText.lowercase(Locale.ROOT)                  // Text from tick if defined
                    Log.d("SeekBar", "SillAge: $silliAge")
                }
            }

            override fun onStartTrackingTouch(seekBar: TickSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: TickSeekBar?) {

            }
        }


        reviewDialog.binding.skGender.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                seekParams?.let {
                    gender =
                        it.tickText.lowercase(Locale.ROOT)                  // Text from tick if defined
                    Log.d("SeekBar", "gender: $gender")
                }
            }

            override fun onStartTrackingTouch(seekBar: TickSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: TickSeekBar?) {
            }
        }


        reviewDialog.binding.skPrice.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                seekParams?.let {
                    priceValue =
                        it.tickText.lowercase(Locale.ROOT)                  // Text from tick if defined
                    Log.d("SeekBar", "Price value: $priceValue")
                }
            }

            override fun onStartTrackingTouch(seekBar: TickSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: TickSeekBar?) {
            }
        }


        //set background, if your root layout doesn't have one
        val windowBackground = window.decorView.background
        val reviewDialogBlurView = reviewDialog.binding.blurView

        val rootView1 = window.decorView.findViewById<ViewGroup>(android.R.id.content)

        // Use RenderEffectBlur for Android 12+ (API 31+)
        reviewDialogBlurView.setupWith(rootView1, RenderEffectBlur())
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(29f) // Try 16-20 for visible blur

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val resultIntent = Intent()
        Log.d("DQWDqwdqw", "clickListener: ${newReviewList?.size}")
        resultIntent.putExtra("newReviewList", newReviewList)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

}
