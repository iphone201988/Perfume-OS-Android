package com.tech.perfumos.ui.camera_perfume


import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.data.api.Constants.ADD_COLLECTION_API
import com.tech.perfumos.data.api.Constants.ADD_REVIEW_API
import com.tech.perfumos.data.api.Constants.ADD_WISHLIST_API
import com.tech.perfumos.data.api.Constants.PERFUME_API
import com.tech.perfumos.databinding.AccordsItemViewBinding
import com.tech.perfumos.databinding.ActivityPerfumeInfoBinding
import com.tech.perfumos.databinding.NotesItemViewBinding
import com.tech.perfumos.databinding.ReviewDialogBinding
import com.tech.perfumos.databinding.SimpleSpinnerItemViewBinding
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.camera_perfume.model.AccordsModel
import com.tech.perfumos.ui.camera_perfume.model.NotesList
import com.tech.perfumos.ui.camera_perfume.model.PerfumeInfoModel
import com.tech.perfumos.ui.camera_perfume.model.ReviewModel
import com.tech.perfumos.ui.camera_perfume.model.SimilarPerfume
import com.tech.perfumos.ui.dashboad.DashboardActivity
import com.tech.perfumos.ui.review.ReviewActivity
import com.tech.perfumos.utils.BaseCustomDialog
import com.tech.perfumos.utils.CustomGradientSeekBar
import com.tech.perfumos.utils.EventsHandler
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils.formatReviewCount
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.charts.CircularWebRadarChartRenderer
import com.tech.perfumos.utils.seekbar.OnSeekChangeListener
import com.tech.perfumos.utils.seekbar.SeekParams
import com.tech.perfumos.utils.seekbar.TickSeekBar
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale

@AndroidEntryPoint
class PerfumeInfoActivity : BaseActivity<ActivityPerfumeInfoBinding>() {

    val viewmodel: CameraVm by viewModels()

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var similarPerfumeAdapter: SimilarPerfumeAdapter
    private lateinit var sameBrandAdapter: SimilarPerfumeAdapter
    private lateinit var accordsAdapter: SimpleRecyclerViewAdapter<AccordsModel, AccordsItemViewBinding>
    private lateinit var topNotesAdapter: SimpleRecyclerViewAdapter<NotesList, NotesItemViewBinding>
    private lateinit var middleNotesAdapter: SimpleRecyclerViewAdapter<NotesList, NotesItemViewBinding>
    private lateinit var baseNotesAdapter: SimpleRecyclerViewAdapter<NotesList, NotesItemViewBinding>
    private lateinit var reviewDialog: BaseCustomDialog<ReviewDialogBinding>

    private var isCollection: Boolean = false
    private var isWishList: Boolean = false

    private var perfumeInfo: PerfumeInfoModel? = null

    private var reviewList: ArrayList<ReviewModel?>? = ArrayList()
    private var similarPerfumeList: ArrayList<SimilarPerfume?>? = ArrayList()
    private var sameBrandList: ArrayList<SimilarPerfume?>? = ArrayList()
    private var topNotesList: ArrayList<NotesList?>? = ArrayList()
    private var middleNotesList: ArrayList<NotesList?>? = ArrayList()
    private var baseNotesList: ArrayList<NotesList?>? = ArrayList()
    private var allNotesList: ArrayList<NotesList?>? = ArrayList()

    private var SeasonValue: ArrayList<Float> = ArrayList()
    private var SeasonLabels: ArrayList<String> = ArrayList()


    //private val similarPerfumeList = ArrayList<SimpleDummyModel>()
    private val accordsList = ArrayList<AccordsModel>()

    private val tabsItemList = ArrayList<String>()

    private var sunScaleSize: Float = 1.0f
    private var moonScaleSize: Float = 1.0f

    var hasSunAnimated = false
    var hasMoonAnimated = false

    override fun getLayoutResource(): Int {
        return R.layout.activity_perfume_info
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    private var id_perfume: String = ""
    override fun onCreateView() {
        initAdapter()

        perfumeInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("perfumeInfo", PerfumeInfoModel::class.java)
        } else {
            intent.getSerializableExtra("perfumeInfo") as PerfumeInfoModel?
        }

        setupBlurView()
        clickListener()
        //initSimilarPerfumeList()
        initObserver()
        reviewDialog()

        if (perfumeInfo != null) {
            initView()
        } else {
            id_perfume = intent.getStringExtra("perfumeId").toString()
            val requestMap = hashMapOf<String, Any>(
                "perfumeId" to intent.getStringExtra("perfumeId").toString(), "isSearch" to true
            )
            viewmodel.getPerfumeApi(PERFUME_API, requestMap)
        }
        Glide.with(this).asGif().load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)

        EventsHandler.getCollection<Pair<String, Boolean>>(lifecycleScope) { event ->
            Log.d("event", "onCreateView: ${event.first} , ${event.second}")
            //isCollection = event.second

            perfumeInfo?.data.let {
                if (it?.id.equals(event.first)) {
                    isCollection = event.second
                    Log.d("event", "isCollection: $isCollection")
                    if (isCollection) {
                        binding.tvAddCollection.text = ContextCompat.getString(
                            this, R.string.addedInCollection
                        )
                    } else {
                        binding.tvAddCollection.text = ContextCompat.getString(
                            this, R.string.add_to_collection
                        )
                    }
                }
            }
        }
        EventsHandler.getWishList<Pair<String, Boolean>>(lifecycleScope) { event ->
            Log.d("event", "onCreateView: ${event.first} , ${event.second}")
            //isCollection = event.second

            perfumeInfo?.data.let {
                if (it?.id.equals(event.first)) {
                    isWishList = event.second
                    Log.d("event", "isCollection: $isWishList")
                    if (isWishList) {
                        binding.tvAddWishlist.text =
                            ContextCompat.getString(this, R.string.add_in_wishlist)
                    } else {
                        binding.tvAddWishlist.text =
                            ContextCompat.getString(this, R.string.add_to_wishlist)
                    }
                }
            }

        }
    }

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
                            runOnUiThread {
                                binding.noRecordFoundMainRv.visibility = View.GONE
                                binding.noRecordFoundSeason.visibility = View.GONE
                                binding.noRecordFoundDesc.visibility = View.GONE
                                binding.noRecordFoundRelease.visibility = View.GONE

                                binding.similarNoDataFound.visibility = View.GONE
                                binding.MoreBrandNoDataFound.visibility = View.GONE

                                binding.classificationChart.visibility = View.VISIBLE

                                binding.tvDesc.visibility = View.VISIBLE
                                binding.clPerfumer.visibility = View.VISIBLE
                                binding.tvViewAllReview.visibility = View.VISIBLE
                                binding.tvReleaseYear.visibility = View.VISIBLE

                            }
                            try {
                                Log.d("ProfileDataModel", "ProfileDataModel: ${Gson().toJson(it)}")
                                val data: PerfumeInfoModel? = parseJson(it.data.toString())

                                if (data?.success == true) {

                                    perfumeInfo = data
                                    initView()

                                } else {
                                    showToast(data?.message.toString())
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        "ADD_TO_COLLECTION" -> {
                            try {
                                Log.d(
                                    "response", "ADD_TO_COLLECTION: ${Gson().toJson(it)}"
                                )
                                // val data: LoginModel? = Utils.parseJson(it.data.toString())

                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: $jsonObject")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())
                                    isCollection = !isCollection
                                    /*if (isCollection) {
                                        binding.tvAddCollection.text = ContextCompat.getString(
                                            this, R.string.addedInCollection
                                        )
                                    } else {
                                        binding.tvAddCollection.text = ContextCompat.getString(
                                            this, R.string.add_to_collection
                                        )
                                    }*/

                                    lifecycleScope.launch {
                                        EventsHandler.addCollection(
                                            Pair(
                                                perfumeInfo?.data?.id,
                                                isCollection
                                            )
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
                                Log.d("ERROR", "initObserver: $jsonObject")

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

                                    lifecycleScope.launch {
                                        EventsHandler.addWishList(
                                            Pair(
                                                perfumeInfo?.data?.id,
                                                isWishList
                                            )
                                        )
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
                                    Log.d("ReviewModel", "initObserver: ${Gson().toJson(it)}")

                                    if (reviewData != null) {/*reviewList?.add(reviewData)
                                        reviewAdapter.list = reviewList*/
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
                                            tvTabReview.text = reviewCount
                                            tvTabRating.text = rating
                                        }
                                        runOnUiThread {
                                            //  reviewList?.add(reviewData)
                                            reviewAdapter.addNewItem(reviewData)
                                            reviewAdapter.notifyDataSetChanged()

                                            if (reviewList.isNullOrEmpty()) {
                                                binding.tvViewAllReview.visibility = View.GONE
                                                binding.noReviewFound.visibility = View.VISIBLE
                                            } else {
                                                binding.tvViewAllReview.visibility = View.VISIBLE
                                                binding.noReviewFound.visibility = View.GONE

                                            }
                                            /*if (reviewList?.size == 0) {
                                                binding.tvViewAllReview.visibility = View.GONE
                                            } else {
                                                binding.tvViewAllReview.visibility = View.VISIBLE
                                            }*/
                                        }

                                    } else {
                                        Log.d("empty", "empty : ")
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
                    val apiCallElement = it.data?.get("apiCall")
                    Log.d("fvsv", "initObserver: Status.ERROR ${apiCallElement?.toString()}")

                    if (apiCallElement != null && apiCallElement.toString().equals(PERFUME_API)) {
                        runOnUiThread {
                            binding.noRecordFoundMainRv.visibility = View.VISIBLE
                            binding.noRecordFoundSeason.visibility = View.VISIBLE
                            binding.noRecordFoundDesc.visibility = View.VISIBLE
                            binding.noRecordFoundRelease.visibility = View.VISIBLE

                            binding.similarNoDataFound.visibility = View.VISIBLE
                            binding.MoreBrandNoDataFound.visibility = View.VISIBLE
                            binding.classificationChart.visibility = View.GONE

                            binding.tvDesc.visibility = View.GONE
                            binding.clPerfumer.visibility = View.GONE
                            binding.tvReleaseYear.visibility = View.GONE
                            binding.tvViewAllReview.visibility = View.GONE
                        }
                    } else {
                        Log.d("fvsv", "initObserver: false")
                    }

                    hideLoading()
                }

                else -> {
                }
            }
        }
    }


    private fun initSimilarPerfumeList() {

        accordsList.add(
            AccordsModel(
                name = "Aromatic",
                progressStartColor = "#375DA8",
                progressEndColor = "#55A9E4",
                thumbColor = "#53A1D8",
                labelColor = "#FFFFFF",
                progress = 90
            )
        )
        accordsList.add(
            AccordsModel(
                name = "Fresh spicy",
                progressStartColor = "#70A430",
                progressEndColor = "#7FDD02",
                thumbColor = "#79CD0B",
                labelColor = "#FFFFFF",
                progress = 40
            )
        )
        accordsList.add(
            AccordsModel(
                name = "Warm spicy",
                progressStartColor = "#EA4D18",
                progressEndColor = "#FCBB5D",
                thumbColor = "#F3B55C",
                labelColor = "#FFFFFF",
                progress = 70
            )
        )
        accordsList.add(
            AccordsModel(
                name = "Musky",
                progressStartColor = "#A31887",
                progressEndColor = "#F02D92",
                thumbColor = "#E82C91",
                labelColor = "#FFFFFF",
                progress = 60
            )
        )
        accordsList.add(
            AccordsModel(
                name = "Woody",
                progressStartColor = "#B54D00",
                progressEndColor = "#FF9C52",
                thumbColor = "#F49550",
                labelColor = "#FFFFFF",
                progress = 50
            )
        )
        accordsList.add(
            AccordsModel(
                name = "Herbal",
                progressStartColor = "#3D0C5E",
                progressEndColor = "#A43DDB",
                thumbColor = "#A43DDB",
                labelColor = "#FFFFFF",
                progress = 40
            )
        )
        accordsList.add(
            AccordsModel(
                name = "Citrus",
                progressStartColor = "#BABF1A",
                progressEndColor = "#FBFF6F",
                thumbColor = "#EAED69",
                labelColor = "#FFFFFF",
                progress = 60
            )
        )
        accordsList.add(
            AccordsModel(
                name = "Lavander",
                progressStartColor = "#9315A6",
                progressEndColor = "#EA61FF",
                thumbColor = "#EA61FF",
                labelColor = "#FFFFFF",
                progress = 30
            )
        )
        accordsList.add(
            AccordsModel(
                name = "Amber",
                progressStartColor = "#C23911",
                progressEndColor = "#FF8D6B",
                thumbColor = "#F08466",
                labelColor = "#FFFFFF",
                progress = 20
            )
        )
    }

    private fun initAdapter() {


        /*similarAdapter = SimpleRecyclerViewAdapter(
            R.layout.single_perfume_item_view, BR.bean
        ) { v, m, pos ->
        }
        binding.rvSimilarPerfumes.adapter = similarAdapter*/
        similarPerfumeAdapter = SimilarPerfumeAdapter(
            this, similarPerfumeList, object : SimilarPerfumeAdapter.ClickListener {
                override fun onClick(
                    position: Int, model: SimilarPerfume
                ) {
                    val intent =
                        Intent(this@PerfumeInfoActivity, PerfumeInfoActivity::class.java).apply {
                            putExtra("perfumeId", model.id)
                        }
                    startActivity(intent)
                }
            })
        binding.rvSimilarPerfumes.adapter = similarPerfumeAdapter

        /*similarAdapter = SimpleRecyclerViewAdapter(
            R.layout.single_perfume_item_view, BR.bean
        ) { v, m, pos ->
        }
        binding.rvMoreBrandPerfumes.adapter = similarAdapter*/
        sameBrandAdapter = SimilarPerfumeAdapter(
            this, sameBrandList, object : SimilarPerfumeAdapter.ClickListener {
                override fun onClick(
                    position: Int, model: SimilarPerfume
                ) {
                    val intent =
                        Intent(this@PerfumeInfoActivity, PerfumeInfoActivity::class.java).apply {
                            putExtra("perfumeId", model.id)
                        }
                    startActivity(intent)
                }

            })
        binding.rvMoreBrandPerfumes.adapter = sameBrandAdapter

        topNotesAdapter = SimpleRecyclerViewAdapter(
            R.layout.notes_item_view, BR.bean
        ) { v, m, pos ->
            when(v?.id){
                R.id.rlMain->{


                        val intent = Intent(this, PerfumerCitrussActivity::class.java).apply {
                            putExtra("noteId", m.noteId?.id.toString())
                        }
                        startActivity(intent)


                }
            }


        }
        binding.rvTopNotes.adapter = topNotesAdapter
        topNotesAdapter.list = topNotesList

        middleNotesAdapter = SimpleRecyclerViewAdapter(
            R.layout.notes_item_view, BR.bean
        ) { v, m, pos ->

            when(v?.id){
                R.id.rlMain->{

                        val intent = Intent(this, PerfumerCitrussActivity::class.java).apply {
                            putExtra("noteId", m.noteId?.id.toString())
                        }
                        startActivity(intent)

                }
            }

        }
        binding.rvMiddleNotes.adapter = middleNotesAdapter
        middleNotesAdapter.list = middleNotesList

        baseNotesAdapter = SimpleRecyclerViewAdapter(
            R.layout.notes_item_view, BR.bean
        ) { v, m, pos ->

            when(v?.id){
                R.id.rlMain->{

                        val intent = Intent(this, PerfumerCitrussActivity::class.java).apply {
                            putExtra("noteId", m.noteId?.id.toString())
                        }
                        startActivity(intent)


                }
            }

        }
        binding.rvBaseNotes.adapter = baseNotesAdapter
        baseNotesAdapter.list = baseNotesList



        accordsAdapter = SimpleRecyclerViewAdapter(
            R.layout.accords_item_view, BR.bean
        ) { v, m, pos ->

            val seekBar = v.findViewById<CustomGradientSeekBar>(R.id.customSeekBar)
            seekBar.isEnabled = false
            when (v.id) {

                R.id.customSeekBar -> {

                }
            }
        }
        binding.rvAccords.adapter = accordsAdapter
        accordsAdapter.list = accordsList


        reviewAdapter = ReviewAdapter(this, reviewList)
        binding.rvReview.adapter = reviewAdapter

    }

    private fun initView() {

        if (perfumeInfo != null) {
            perfumeInfo?.data.let {
                binding.apply {
                    Glide.with(this@PerfumeInfoActivity).load(it?.image).into(perfumeImg)

                    it?.let { data ->
                        data.name?.takeIf { it.isNotBlank() }?.let { tvName.text = it }
                        data.brand?.takeIf { it.isNotBlank() }?.let { tvType.text = it }
                        data.description?.takeIf { it.isNotBlank() }?.let { tvDesc.setText(it) }
                        data.year?.let { tvReleaseYear.text = it.toString() }
                    }

                    it?.mainAccords?.forEach { accords ->
                        accordsList.add(
                            AccordsModel(
                                name = accords?.name.toString(),
                                progressStartColor = rgbToHex(accords?.backgroundColor.toString()),
                                progressEndColor = rgbToHex(accords?.backgroundColor.toString()),
                                thumbColor = rgbToHex(accords?.backgroundColor.toString()),
                                labelColor = "#000000",
                                progress = accords?.width?.removeSuffix("%")?.toDoubleOrNull()
                                    ?.toInt() ?: 0
                            )
                        )
                    }

                    it?.seasons?.forEach {
                        val showText = it?.name?.replaceFirstChar { it.uppercase() }
                        SeasonLabels.add(showText.toString())
                        SeasonValue.add((it?.width ?: "0.0%").removeSuffix("%").toFloat())

                    }
                    SeasonLabels.forEach {
                        Log.d("SeasonLabels", "initView: $it ")
                    }
                    isCollection = it?.isCollection ?: false
                    isWishList = it?.isWishlist ?: false

                    if (isCollection) {
                        binding.tvAddCollection.text = ContextCompat.getString(
                            this@PerfumeInfoActivity, R.string.addedInCollection
                        )
                    } else {
                        binding.tvAddCollection.text = ContextCompat.getString(
                            this@PerfumeInfoActivity, R.string.add_to_collection
                        )
                    }

                    if (isWishList) {
                        binding.tvAddWishlist.text = ContextCompat.getString(
                            this@PerfumeInfoActivity, R.string.add_in_wishlist
                        )
                    } else {
                        binding.tvAddWishlist.text = ContextCompat.getString(
                            this@PerfumeInfoActivity, R.string.add_to_wishlist
                        )
                    }

                    if (accordsList.isNullOrEmpty()) {
                        rvAccords.visibility = View.GONE
                        noRecordFoundMainRv.visibility = View.VISIBLE
                    } else {
                        accordsAdapter.list = accordsList.take(5)
                        accordsAdapter.notifyDataSetChanged()
                        rvAccords.visibility = View.VISIBLE
                        noRecordFoundMainRv.visibility = View.GONE
                    }

                    topNotesList = it?.notes?.top?.take(5) // safer alternative to slice
                        ?.let { ArrayList(it) }

                    middleNotesList = it?.notes?.middle?.take(5) // safer alternative to slice
                        ?.let { ArrayList(it) }
                    baseNotesList = it?.notes?.base?.take(5) // safer alternative to slice
                        ?.let { ArrayList(it) }
                    allNotesList = it?.notes?.notes?.take(5) // safer alternative to slice
                        ?.let { ArrayList(it) }

                    if (it?.notes?.notes.isNullOrEmpty()) {
                        clNote.visibility = View.VISIBLE


                        if (it?.notes?.top.isNullOrEmpty()) {
                            ivNoteTop.visibility = View.GONE
                            tvNoteTop.visibility = View.GONE
                            //tvNotes.visibility = View.GONE
                            clNoteTop.visibility = View.GONE

                            view.visibility = View.GONE
                        } else {
                            topNotesAdapter.list = topNotesList
                            topNotesAdapter.notifyDataSetChanged()
                        }

                        if (it?.notes?.middle.isNullOrEmpty()) {
                            ivNoteMid.visibility = View.GONE
                            tvNoteMid.visibility = View.GONE
                            clNoteMid.visibility = View.GONE
                            view3.visibility = View.GONE
                        } else {
                            middleNotesAdapter.list = middleNotesList
                            middleNotesAdapter.notifyDataSetChanged()
                        }

                        if (it?.notes?.base.isNullOrEmpty()) {
                            ivNoteBottom.visibility = View.GONE
                            tvNoteBottom.visibility = View.GONE
                            clNoteBase.visibility = View.GONE
                            view4.visibility = View.GONE

                        } else {
                            baseNotesAdapter.list = baseNotesList
                            baseNotesAdapter.notifyDataSetChanged()
                        }

                        if (it?.notes?.top.isNullOrEmpty() && it?.notes?.middle.isNullOrEmpty() && it?.notes?.base.isNullOrEmpty() && it?.notes?.notes.isNullOrEmpty()) {
                            noNotesFound.visibility = View.VISIBLE
                        } else {
                            noNotesFound.visibility = View.GONE
                        }

                    } else {
                        clNote.visibility = View.VISIBLE
                        topNotesAdapter.list = allNotesList
                        topNotesAdapter.notifyDataSetChanged()

                        ivNoteTop.setImageResource(R.drawable.iv_notes)
                        tvNoteTop.text = "Notes"
                        view.visibility = View.GONE

                        ivNoteMid.visibility = View.GONE
                        tvNoteMid.visibility = View.GONE
                        clNoteMid.visibility = View.GONE
                        view3.visibility = View.GONE

                        ivNoteBottom.visibility = View.GONE
                        tvNoteBottom.visibility = View.GONE
                        clNoteBase.visibility = View.GONE
                        view4.visibility = View.GONE
                    }

                    if (it?.perfumers.isNullOrEmpty()) {
                        clPerfumer.visibility = View.GONE
                    } else {
                        clPerfumer.visibility = View.VISIBLE
                        tvPerfumerName.text = it?.perfumers?.get(0)?.name
                        val perfumerImage =
                            if ((it?.perfumers?.get(0)?.image ?: "").contains("http")) {
                                it?.perfumers?.get(0)?.image
                            } else {
                                "${Constants.BASE_URL_IMAGE}${it?.perfumers?.get(0)?.image}"
                            }
                        Glide.with(this@PerfumeInfoActivity).load(perfumerImage)
                            .into(ivPerfumerProfile)
                    }

                    if (it?.occasions.isNullOrEmpty()) {
                        noRecordFoundOcasion.visibility = View.VISIBLE
                        llOcasion.visibility = View.GONE
                    } else {
                        llOcasion.visibility = View.VISIBLE
                        noRecordFoundOcasion.visibility = View.GONE


                        /*ocsDay.text = "${it?.occasions?.find { it?.name.equals("day") }?.width}"
                        ocsEvening.text =
                            "${it?.occasions?.find { it?.name.equals("night") }?.width}"*/

                        it?.occasions?.forEach { occasion ->
                            val widthStr = occasion?.width?.trim() ?: "0"
                            val widthDouble = widthStr.removeSuffix("%").toDoubleOrNull() ?: 0.0
                            val roundedWidth = String.format("%.0f", widthDouble)

                            Log.d("roundedWidth", "initView: $roundedWidth , $widthStr scale ${roundedWidth.toInt().div(100f)}")

                            val minScale = 0.4f
                            val maxScale = 1.0f

                            val fraction = roundedWidth.toInt() / 100f

                            val clampedFraction = fraction.coerceIn(0f, 1f)

                            val scalePercent  = minScale + (maxScale - minScale) * clampedFraction

                            //val scalePercent = roundedWidth.toInt().div(100f)
                            when (occasion?.name?.lowercase()) {
                                "day" -> {
                                    ocsDay.text = "$roundedWidth%"
                                    sunScaleSize = scalePercent
                                    //ivSun.animate().scaleX(scalePercent).scaleY(scalePercent).setDuration(300).start()
                                }
                                "night" -> {
                                    ocsEvening.text = "$roundedWidth%"
                                    moonScaleSize = scalePercent
                                    //ivMoon.animate().scaleX(scalePercent).scaleY(scalePercent).setDuration(300).start()
                                }
                            }
                        }

                    }

                    if (it?.seasons.isNullOrEmpty()) {
                        noRecordFoundSeason.visibility = View.VISIBLE
                        classificationChart.visibility = View.GONE
                    } else {
                        noRecordFoundSeason.visibility = View.GONE
                        classificationChart.visibility = View.VISIBLE
                        loadSeasonChart()
                    }

                    reviewList = it?.reviews
                    //reviewList = mapApiReviewToModel(it?.reviews)

                    if (reviewList.isNullOrEmpty()) {
                        binding.tvViewAllReview.visibility = View.GONE
                        binding.noReviewFound.visibility = View.VISIBLE
                    } else {
                        binding.tvViewAllReview.visibility = View.VISIBLE
                        binding.noReviewFound.visibility = View.GONE
                    }
                    if (reviewList?.size == 0) {
                        binding.tvViewAllReview.visibility = View.GONE
                    } else {
                        binding.tvViewAllReview.visibility = View.VISIBLE
                    }
                    reviewAdapter.list = reviewList
                    reviewAdapter.notifyDataSetChanged()

                    if (!it?.similar.isNullOrEmpty()) {
                        similarPerfumeList = it?.similar
                        similarPerfumeAdapter.list = similarPerfumeList
                        similarPerfumeAdapter.notifyDataSetChanged()
                        rvSimilarPerfumes.visibility = View.VISIBLE
                        similarNoDataFound.visibility = View.GONE
                    } else {
                        rvSimilarPerfumes.visibility = View.GONE
                        similarNoDataFound.visibility = View.VISIBLE
                    }

                    if (!it?.similar.isNullOrEmpty()) {
                        sameBrandList = it?.sameBrand
                        sameBrandAdapter.list = sameBrandList
                        sameBrandAdapter.notifyDataSetChanged()
                        rvMoreBrandPerfumes.visibility = View.VISIBLE
                        MoreBrandNoDataFound.visibility = View.GONE
                    } else {
                        rvMoreBrandPerfumes.visibility = View.GONE
                        MoreBrandNoDataFound.visibility = View.VISIBLE
                    }

                    middleNotesList = it?.notes?.middle
                    baseNotesList = it?.notes?.base
                    allNotesList = it?.notes?.notes


                    val reviewCount = formatReviewCount(it?.totalReviewsAndRatings?.totalReviews)
                    tvReview.text = reviewCount

                    val rating = String.format("%.2f", it?.totalReviewsAndRatings?.averageRating)
                    tvRating.text = rating

                    tvTabReview.text = reviewCount
                    tvTabRating.text = rating

                }
            }
            //loadOcasionChart()
        }
    }

    private fun setupBlur() {
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView.background

        val blurView = findViewById<BlurView>(R.id.blurView)
        val radius = 20f

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView.setupWith(rootView, RenderEffectBlur()).setFrameClearDrawable(windowBackground)
                .setBlurRadius(5f)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupBlurView() {

        //set background, if your root layout doesn't have one
        val windowBackground = window.decorView.background

        val blurView = binding.blurView

        val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)

        // Use RenderEffectBlur for Android 12+ (API 31+)
        blurView.setupWith(rootView, RenderEffectBlur()).setFrameClearDrawable(windowBackground)
            .setBlurRadius(16f) // Try 16-20 for visible blur

        /*blurView.setupWith(binding.flTabs, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)*/

    }

    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.back -> {
                    super.onBackPressedDispatcher.onBackPressed()
                }

                R.id.tv_write_review -> {
                    reviewDialog.show()
                }

                /*R.id.tv_fresh -> {
                    startActivity(Intent(this, PerfumerProfileActivity::class.java))
                }*/

                R.id.tvViewAllReview -> {
                    val intent = Intent(
                        this, ReviewActivity::class.java
                    )
                    intent.putExtra("perfumeId", perfumeInfo?.data?.id)
                    reviewLauncher.launch(intent)

                }

                /* R.id.tv_citrus -> {
                     startActivity(Intent(this, PerfumerProfileActivity::class.java))
                 }*/

                R.id.cl_perfumer -> {
                    val intent = Intent(this, PerfumerProfileActivity::class.java).apply {
                        //putExtra("PerfumerData", perfumeInfo?.data?.perfumers?.get(0))
                        putExtra(
                            "PerfumerId",
                            perfumeInfo?.data?.perfumers?.get(0)?.perfumerId?.id.toString()
                        )
                    }
                    startActivity(intent)
                }

                R.id.tvNotes -> {
                    binding.apply {
                        tvInfo.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.unselected_color
                            )
                        )
                        tvTabRating.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.unselected_color
                            )
                        )
                        tvTabReview.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.unselected_color
                            )
                        )
                        tvNotes.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.heading_color
                            )
                        )
                        viewNote.visibility = View.VISIBLE
                        viewInfo.visibility = View.GONE
                    }
                    binding.mainScrollView.post {
                        binding.mainScrollView.smoothScrollTo(0, binding.clNote.top)
                    }
                }

                R.id.tvInfo -> {
                    binding.apply {
                        tvNotes.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.unselected_color
                            )
                        )
                        tvTabRating.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.unselected_color
                            )
                        )
                        tvTabReview.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.unselected_color
                            )
                        )
                        tvInfo.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.heading_color
                            )
                        )
                        viewNote.visibility = View.GONE
                        viewInfo.visibility = View.VISIBLE
                    }
                    binding.mainScrollView.post {
                        binding.mainScrollView.smoothScrollTo(0, binding.clOcasion.top)
                    }
                }

                R.id.rlReview -> {
                    binding.apply {
                        tvNotes.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.unselected_color
                            )
                        )
                        tvTabRating.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.heading_color
                            )
                        )
                        tvTabReview.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.heading_color
                            )
                        )
                        tvInfo.setTextColor(
                            ContextCompat.getColor(
                                this@PerfumeInfoActivity, R.color.unselected_color
                            )
                        )
                        viewNote.visibility = View.GONE
                        viewInfo.visibility = View.GONE
                    }
                    binding.mainScrollView.post {
                        binding.mainScrollView.smoothScrollTo(0, binding.clReview.top)
                    }
                }


                R.id.tvAddCollection -> {
                    val requestMap = hashMapOf<String, Any>(
                        //"collection" to perfumeInfo?.data?.id.toString(),
                    )
                    viewmodel.addToCollectionApi(
                        "${ADD_COLLECTION_API}${perfumeInfo?.data?.id}", requestMap
                    )
                }

                R.id.tvAddWishlist -> {
                    val requestMap = hashMapOf<String, Any>(
                        //"collection" to perfumeInfo?.data?.id.toString(),
                    )

                    viewmodel.addToWishlistApi(
                        "${ADD_WISHLIST_API}${perfumeInfo?.data?.id}", requestMap
                    )
                }

                R.id.iv_compare -> {

                    val intent = Intent(this, DashboardActivity::class.java).apply {
                        putExtra("perfumeData", perfumeInfo?.data)
                        putExtra("open_fragment", "PerfumeInfo")
                        //flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }

            }
        }

        /*binding.ivSun.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Make sure to remove listener to avoid repeated calls
                binding.ivSun.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Check if view is visible
                if (binding.ivSun.isVisible) {
                    // Start animation
                    binding.ivSun.animate().scaleX(sunScaleSize).scaleY(sunScaleSize).setDuration(1000).start()
                }
            }
        })

        binding.ivMoon.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Make sure to remove listener to avoid repeated calls
                binding.ivMoon.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Check if view is visible
                if (binding.ivMoon.isVisible) {
                    // Start animation
                    binding.ivMoon.animate().scaleX(moonScaleSize).scaleY(moonScaleSize).setDuration(1000).start()
                }
            }
        })*/

        binding.mainScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->

            val rect = Rect()
            val isSunVisible = binding.ivSun.getLocalVisibleRect(rect) && binding.ivSun.visibility == View.VISIBLE
            val isMoonVisible = binding.ivMoon.getLocalVisibleRect(rect) && binding.ivMoon.visibility == View.VISIBLE

            if (isSunVisible && !hasSunAnimated) {
                // Animate scale when ivSun is visible
                binding.ivSun.animate().scaleX(sunScaleSize).scaleY(sunScaleSize).setDuration(1000).start()
                hasSunAnimated = true
            }
            if (isMoonVisible && !hasMoonAnimated) {
                // Animate scale when ivSun is visible
                binding.ivMoon.animate().scaleX(moonScaleSize).scaleY(moonScaleSize).setDuration(1000).start()
                hasMoonAnimated = true
            }


        })
    }

    //private lateinit var tabAdapter: SimpleRecyclerViewAdapter<String, SimpleSpinnerItemViewBinding>
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
                    }
                    else if (longevity  == "") {
                       showErrorToast("Please select longevity")
                    }
                    else if (silliAge  == "") {
                        showErrorToast("Please select sill age")
                    }
                    else if (gender  == "") {
                        showErrorToast("Please select gender")
                    }
                    else if (priceValue  == "") {
                        showErrorToast("Please select price value")
                    }
                    else if (reviewDialog.binding.tvDesc.text.trim().toString().isNullOrEmpty()) {
                        Toast.makeText(this, "Please write a review", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val rating = reviewDialog.binding.ratingBar.rating
                        requestMap.put("perfumeId" , perfumeInfo?.data?.id.toString())
                        requestMap.put("rating", rating)
                        requestMap.put("title", reviewDialog.binding.etReviewTitle.text.trim().toString())
                        requestMap.put("review", reviewDialog.binding.tvDesc.text.toString())

                        requestMap.put("longevity" , longevity!!)
                        requestMap.put("sillage" , silliAge!!)
                        requestMap.put("gender" , gender!!)
                        requestMap.put("price" , priceValue!!)

                        viewmodel.postReviewApi(ADD_REVIEW_API, requestMap)
                    }

                }

                R.id.spinnerTab -> {
                   // reviewDialog.binding.rvTabs.visibility = View.VISIBLE
                    if(reviewDialog.binding.rvTabs.isVisible){
                        reviewDialog.binding.rvTabs.visibility = View.GONE

                        //reviewDialog.binding.ivDrop.animate().rotation(90f).setDuration(300).start()

                    }else{
                        /*reviewDialog.binding.ivDrop.animate().rotation(270f).setDuration(300).start()
                        reviewDialog.binding.rvTabs.visibility = View.VISIBLE*/

                    }
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
            //reviewDialog.binding.selectTap = 0

            reviewDialog.binding.skLongevity.setProgress(0f)
            reviewDialog.binding.skSillAge.setProgress(0f)
            reviewDialog.binding.skGender.setProgress(0f)
            reviewDialog.binding.skPrice.setProgress(0f)
        }

       // reviewDialog.binding.selectTap = 0
        /*tabsItemList.clear()
        tabsItemList.addAll(arrayListOf("Longevity", "Sillage", "Gender", "Price Value"))*/

        /*tabAdapter = SimpleRecyclerViewAdapter(
            R.layout.simple_spinner_item_view, BR.bean
        ) { v, m, pos ->

            when (v.id) {

                R.id.clMain -> {
                    reviewDialog.binding.selectTap = pos
                    when (pos) {
                        0 -> {

                            reviewDialog.binding.spinnerTab.text = m
                            reviewDialog.binding.rvTabs.visibility = View.GONE
                        }

                        1 -> {


                            reviewDialog.binding.spinnerTab.text = m
                            reviewDialog.binding.rvTabs.visibility = View.GONE
                        }

                        2 -> {


                            reviewDialog.binding.spinnerTab.text = m
                            reviewDialog.binding.rvTabs.visibility = View.GONE
                        }

                        3 -> {

                            reviewDialog.binding.spinnerTab.text = m
                            reviewDialog.binding.rvTabs.visibility = View.GONE
                        }

                        4 -> {

                            reviewDialog.binding.spinnerTab.text = m
                            reviewDialog.binding.rvTabs.visibility = View.GONE
                        }
                    }
                }
            }
        }

        tabAdapter.list = tabsItemList
        reviewDialog.binding.rvTabs.adapter = tabAdapter*/


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
                    longevity = it.tickText.lowercase(Locale.ROOT)           // Text from tick if defined

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
                    silliAge = it.tickText.lowercase(Locale.ROOT)                  // Text from tick if defined

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
                    gender = it.tickText.lowercase(Locale.ROOT)                  // Text from tick if defined

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
                    priceValue = it.tickText.lowercase(Locale.ROOT)                  // Text from tick if defined

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

    /**private fun loadOcasionChart() {
        val labels = listOf("Daily", "Evening", "Business", "Night Out", "Leisure")
        val values = listOf(10f, 9f, 4f, 8f, 2f)
        val entries = values.map { RadarEntry(it) }

        val maxValue = values.maxOrNull() ?: 5f
// Create dataset
        val dataSet = RadarDataSet(entries, "Usage").apply {
//            color = Color.CYAN
//            fillColor = Color.CYAN
            setDrawFilled(true)
            // fillDrawable = ContextCompat.getDrawable(this@PerfumeInfoActivity, R.drawable.radar_gradient)
            fillAlpha = 180
            lineWidth = 2f
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            setDrawValues(false)
        }

// Prepare data
        val data = RadarData(dataSet)
        binding.classificationChart.data = data

        val typeface = ResourcesCompat.getFont(this, R.font.alice_regular)
        binding.classificationChart.xAxis?.typeface = typeface

        // Configure axis labels
        binding.classificationChart.xAxis?.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            textSize = 12f
            textColor = ContextCompat.getColor(this@PerfumeInfoActivity, R.color.themeTextView)
        }

        // Y-Axis (optional: hide labels)
        binding.classificationChart.yAxis?.apply {
            setDrawLabels(false)
            axisMinimum = 0f
            axisMaximum = maxValue // Set to highest value
            labelCount = 3
        }

        // Chart styling
        binding.classificationChart.description?.isEnabled = false
        binding.classificationChart.legend?.isEnabled = false
        binding.classificationChart.webColor = ContextCompat.getColor(this, R.color.gray)
        binding.classificationChart.webLineWidth = 1f
        binding.classificationChart.webColorInner = ContextCompat.getColor(this, R.color.gray)
        binding.classificationChart.webLineWidthInner = 1f
        binding.classificationChart.setBackgroundColor(
            ContextCompat.getColor(
                this, R.color.transparent
            )
        )

        binding.classificationChart.renderer = CircularWebRadarChartRenderer(
            binding.classificationChart,
            binding.classificationChart.animator!!,
            binding.classificationChart.viewPortHandler!!
        )

        binding.classificationChart.invalidate()
    }*/

    private fun loadSeasonChart() {
        /*SeasonLabels.addAll(listOf("Winter", "Fall", "Night Out", "Spring"))
        SeasonValue.addAll(listOf(2f, 5f, 7f, 4f))*/
        /*val labels = listOf("Winter", "Fall", "Night Out", "Spring")
        val values = listOf(2f, 5f, 7f, 4f)*/
        val entries = SeasonValue.map { RadarEntry(it) }

        val maxValue = SeasonValue.maxOrNull() ?: 5f
// Create dataset
        val dataSet = RadarDataSet(entries, "Usage").apply {
            /*color = Color.CYAN
            fillColor = Color.CYAN*/
            setDrawFilled(true)
            // fillDrawable = ContextCompat.getDrawable(this@PerfumeInfoActivity, R.drawable.radar_gradient)
            fillAlpha = 180
            lineWidth = 2f
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            setDrawValues(false)
        }

// Prepare data
        val data = RadarData(dataSet)
        binding.classificationChart.data = data

        val typeface = ResourcesCompat.getFont(this, R.font.alice_regular)
        binding.classificationChart.xAxis?.typeface = typeface
        binding.classificationChart.apply {
            rotationAngle = 45f   // Fix start at 90° (top)
            isRotationEnabled = false  // Prevent user rotation
        }
        // Configure axis labels
        binding.classificationChart.xAxis?.apply {
            valueFormatter = IndexAxisValueFormatter(SeasonLabels)
            textSize = 12f
            textColor = ContextCompat.getColor(this@PerfumeInfoActivity, R.color.themeTextView)
        }

        // Y-Axis (optional: hide labels)
        binding.classificationChart.yAxis?.apply {
            setDrawLabels(false)
            axisMinimum = 0f
            axisMaximum = maxValue // Set to highest value
            labelCount = 3
        }

        // Chart styling
        binding.classificationChart.description?.isEnabled = false
        binding.classificationChart.legend?.isEnabled = false
        binding.classificationChart.webColor = ContextCompat.getColor(this, R.color.gray)
        binding.classificationChart.webLineWidth = 1f
        binding.classificationChart.webColorInner = ContextCompat.getColor(this, R.color.gray)
        binding.classificationChart.webLineWidthInner = 1f
        binding.classificationChart.setBackgroundColor(
            ContextCompat.getColor(
                this, R.color.transparent
            )
        )

        binding.classificationChart.setExtraOffsets(24f, 24f, 24f, 24f)

        binding.classificationChart.renderer = CircularWebRadarChartRenderer(
            binding.classificationChart,
            binding.classificationChart.animator!!,
            binding.classificationChart.viewPortHandler!!
        )

        binding.classificationChart.invalidate()
    }

    private val reviewLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {

                val reviewList =
                    it.data?.getSerializableExtra("newReviewList") as? ArrayList<ReviewModel>
                Log.d("resultCode", ":${Gson().toJson(reviewList)}")

                if (!reviewList.isNullOrEmpty()) {

                    binding.apply {
                        perfumeInfo?.data?.totalReviewsAndRatings?.totalReviews =
                            (perfumeInfo?.data?.totalReviewsAndRatings?.totalReviews
                                ?: 0) + reviewList.size
                        val reviewCount =
                            formatReviewCount(perfumeInfo?.data?.totalReviewsAndRatings?.totalReviews)
                        tvReview.text = reviewCount
                        perfumeInfo?.data?.totalReviewsAndRatings?.averageRating =
                            (perfumeInfo?.data?.totalReviewsAndRatings?.averageRating
                                ?: 0.0) + reviewList.size
                        val rating = String.format(
                            "%.2f",
                            perfumeInfo?.data?.totalReviewsAndRatings?.averageRating
                        )
                        tvRating.text = rating
                        tvTabReview.text = reviewCount
                        tvTabRating.text = rating
                    }
                    runOnUiThread {

                        reviewList?.forEach { reviewData ->

                            reviewAdapter.addNewItem(reviewData)
                            reviewAdapter.notifyDataSetChanged()

                            if (reviewList.isNullOrEmpty()) {
                                binding.tvViewAllReview.visibility = View.GONE
                            } else {
                                binding.tvViewAllReview.visibility = View.VISIBLE
                            }
                            if (reviewList.size == 0) {
                                binding.tvViewAllReview.visibility = View.GONE
                            } else {
                                binding.tvViewAllReview.visibility = View.VISIBLE
                            }

                        }

                    }

                }

            }
        }

    private fun mapApiReviewToModel(review: ArrayList<ReviewModel?>?): ArrayList<ReviewModel?>? {
        review?.forEach {
            val data = ReviewModel(
                authorImage = it?.authorImage,
                authorName = it?.authorName,
                createdAt = it?.createdAt,
                datePublished = it?.datePublished,
                id = it?.id,
                perfumeId = it?.perfumeId,
                rating = it?.rating,
                review = it?.review,
                updatedAt = it?.updatedAt,
                userId = it?.userId,
                v = it?.v,
                isExpand = (it?.review?.length ?: 0) > 120,
                title = it?.title
            )
            reviewList?.add(data)
        }
        return reviewList
    }

    private fun rgbToHex(rgb: String): String {
        val regex = Regex("""rgb\((\d+),\s*(\d+),\s*(\d+)\)""")
        val match = regex.find(rgb)

        return match?.destructured?.let { (r, g, b) ->
            String.format("#%02X%02X%02X", r.toInt(), g.toInt(), b.toInt())
        } ?: "#000000" // fallback color
    }

    fun scalePercentFromString(percentageString: String): Float {
        val minScale = 0.4f
        val maxScale = 1.4f

        // Remove '%' and convert to float
        val rawPercent = percentageString.trim().removeSuffix("%").toFloatOrNull() ?: 0f

        // Convert percent value to fraction (e.g., 100 → 1.0)
        val fraction = rawPercent / 100f

        // Map fraction from [0, 1+] to [minScale, maxScale]
        // Clamp fraction between 0 and 1 for safety
        val clampedFraction = fraction.coerceIn(0f, 1f)

        // Linearly interpolate between minScale and maxScale
        return minScale + (maxScale - minScale) * clampedFraction
    }
}