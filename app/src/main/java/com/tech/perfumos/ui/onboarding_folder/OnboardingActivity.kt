package com.tech.perfumos.ui.onboarding_folder


import android.animation.ObjectAnimator
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.BR
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.UPDATE_DATA_API
import com.tech.perfumos.databinding.ActivityOnboardingBinding
import com.tech.perfumos.databinding.OnboardingItemViewBinding
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.SimpleRecyclerViewAdapter
import com.tech.perfumos.ui.dashboad.DashboardActivity
import com.tech.perfumos.ui.dashboad.fragment_folder.change_language.ChangeLanguage
import com.tech.perfumos.ui.onboarding_folder.model.OnBoardingModel
import com.tech.perfumos.ui.splash.WelcomeActivityVM
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class OnboardingActivity : BaseActivity<ActivityOnboardingBinding>() {

    private val viewModel: WelcomeActivityVM by viewModels()
    private lateinit var onboardingAdapter: SimpleRecyclerViewAdapter<OnboardingModel, OnboardingItemViewBinding>
    private lateinit var genderAdapter: ChooseGenderAdapter
    private val genderList = ArrayList<GenderList>()
    val mostSmallList = ArrayList<OnboardingModel>()
    val reasonList = ArrayList<OnboardingModel>()
    val costList = ArrayList<OnboardingModel>()
    val hearAboutList = ArrayList<OnboardingModel>()
    private var onboardingData: OnBoardingModel? = null

    private var monthList: LinkedHashMap<String, String> = linkedMapOf()
    private var selectedGender: String = ""
    private var selectedSmellList: ArrayList<String> = ArrayList()
    private var selectedReason: String = ""
    private var selectedBudget: String = ""
    private var selectedRefSource: String = ""
    private var selectedDob: String = ""
    private var selectedPerfumeStg: String = "50"
    private var referredBy: String = ""

    private val monthMap = mapOf(
        "January" to "01", "February" to "02", "March" to "03", "April" to "04",
        "May" to "05", "June" to "06", "July" to "07", "August" to "08",
        "September" to "09", "October" to "10", "November" to "11", "December" to "12"
    )
    private var selectedYear: String? = null
    private var selectedMonth: String? = null
    private var selectedDay: String? = null

    private var currentPos = 1
    override fun getLayoutResource(): Int {
        return R.layout.activity_onboarding
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        onboardingData = sharedPrefManager.getOnboardingData()
        if (onboardingData != null) {
            onboardingData.let {
                selectedGender = (it?.data?.gender ?: "").toString()

            }
            selectedSmellList =
                (onboardingData?.data?.enjoySmell ?: emptyList()) as ArrayList<String>
            selectedReason = (onboardingData?.data?.reasonForWearPerfume ?: "").toString()
            selectedBudget = (onboardingData?.data?.perfumeBudget ?: "").toString()
            selectedRefSource = (onboardingData?.data?.referralSource ?: "").toString()
            selectedDob = onboardingData?.data?.dob ?: ""

            selectedPerfumeStg = (onboardingData?.data?.perfumeStrength ?: "50").toString()
            Log.d("selectedPerfumeStg", "onCreateView: $selectedPerfumeStg  ${onboardingData?.data?.perfumeStrength}")
            binding.skIntenseProgress.progress = selectedPerfumeStg.toInt() ?: 50


            if(onboardingData?.data?.dob != null){


            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = inputFormat.parse(onboardingData?.data?.dob)

// Format Date object to desired output format
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val output = outputFormat.format(date!!)
                Log.d("output", "onCreateView: ${output}")
                selectedDob = output


            }
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Utils.screenFillView(this)
        Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)

        initAdapter()
        setDataValueRv()
        initObserver()
        initView()
        clickListener()

        /*setupSpinner(binding.spinnerDate, (1..31).map { it.toString() })
        setupSpinner(binding.spinnerMonth, listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"))
        setupSpinner(binding.spinnerYear, (1980..2025).map { it.toString() })*/

        /*val thumb = findViewById<ImageView>(R.id.seek_thumb)
        val progressView = findViewById<View>(R.id.viewSecSeekBar)
        val seekBarBackground = findViewById<View>(R.id.viewManSeekBar)

        seekBarBackground.post {
            val seekBarStartX = seekBarBackground.left
            val seekBarWidth = seekBarBackground.width
            val thumbHalfWidth = thumb.width / 2
            thumb.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        // Raw X - position of the touch on screen
                        val touchX = event.rawX

                        // Convert to relative X inside the seek bar
                        val relativeX = (touchX - seekBarStartX - thumbHalfWidth)
                            .coerceIn(0f, seekBarWidth.toFloat() - thumb.width)

                        // Move the thumb
                        thumb.translationX = relativeX

                        // Update progress bar width
                        val params = progressView.layoutParams
                        params.width = (relativeX + thumbHalfWidth).toInt()
                        progressView.layoutParams = params
                    }
                }
                true
            }
        }*/
    }

    private fun initAdapter() {
        genderList.add(GenderList(R.drawable.male, "Male"))
        genderList.add(GenderList(R.drawable.female, "Female"))
        genderList.add(GenderList(R.drawable.female, "Prefer not to say"))

        genderAdapter =
            ChooseGenderAdapter(this, genderList, object : ChooseGenderAdapter.GenderSelection {
                override fun onSelect(text: String) {
                    Log.d("onSelect", "onSelect: $text")
                    selectedGender = text

                }

            })
        binding.onboardingRv.adapter = genderAdapter

        if (onboardingData != null) {
            onboardingData.let {
                when (it?.data?.gender) {
                    "male" -> {
                        genderAdapter.selectedPosition = 0
                    }

                    "female" -> {
                        genderAdapter.selectedPosition = 1
                    }

                    "prefer not to say" -> {
                        genderAdapter.selectedPosition = 2
                    }
                }
            }
        }

    }

    private fun initView() {

        //binding.skProgress.setOnTouchListener { _, _ -> true }
        currentPos = (sharedPrefManager.getBoardingStep() ?: 0) + 1
        Log.d("currentPos", "initView: $currentPos")
        val dayList = listOf("Day") + (1..31).map { it.toString() }
        val monthList = listOf("Month") + listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val yearList = listOf("Year") + (1950..2025).map { it.toString() }

        setupSpinner(binding.spinnerDate, dayList)
        setupSpinner(binding.spinnerMonth, monthList)
        setupSpinner(binding.spinnerYear, yearList)
        adapterInit(currentPos)
        textValue(currentPos)

        if(!selectedDob.isNullOrEmpty()){
            val dateArray  = selectedDob.split("-")

            dateArray.forEachIndexed { index, s ->

                if(index == 0){
                    selectedYear = s
                }else if(index == 1){
                    selectedMonth = s
                }else if(index == 2){
                    selectedDay = s
                }

                /* selectedYear = dateArray[0]
                 selectedMonth = dateArray[1]
                 selectedDay = dateArray[2]   */
            }

            Log.d("dateArray", "initView: $selectedDay ,  $selectedMonth $selectedYear")
            selectSpinnerItemByValue(binding.spinnerDate, selectedDay ?: "Day")
            selectSpinnerItemByValue(binding.spinnerMonth, selectedMonth ?: "Month")
            selectSpinnerItemByValue(binding.spinnerYear, selectedYear ?: "Year")

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
                        UPDATE_DATA_API -> {
                            try {
                                Log.d("response", "UPDATE_DATA_API: ${Gson().toJson(it)}")
                                val response: OnBoardingModel? = Utils.parseJson(it.data.toString())
                                //Log.d("LoginModel", "initObserver: ${data?.success}")
                                if (response?.success == true) {
                                    if (response.data != null) {
                                        sharedPrefManager.saveOnboardingData(response)
                                    }
                                    if (currentPos < 8) {
                                        sharedPrefManager.saveBoardingStep(currentPos)
                                        currentPos += 1
                                        Log.d("response", "currentPos:: $currentPos")
                                        textValue(currentPos)
                                        adapterInit(currentPos)
                                    } else {
                                        //Utils.isLogin = false
                                        Utils.routeToHomeDashboardActivity = 2
                                        sharedPrefManager.saveBoardingStep(currentPos)
                                        startActivity(Intent(this, DashboardActivity::class.java))
                                        finishAffinity()
                                    }

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
                        Log.d("ERROR", "initObserver: ${Gson().toJson(it)}")
                        val jsonObject = JSONObject(it.data.toString())
                        Log.d("ERROR", "initObserver: ${jsonObject}")

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

    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    if (currentPos > 1) {
                        textValue(currentPos - 1)
                        adapterInit(currentPos - 1)

                    } else {
                        onBackPressedDispatcher.onBackPressed()
                    }

                }

                R.id.languageLL -> {
                    startActivity(Intent(this, ChangeLanguage::class.java))
                }

                R.id.continueLL -> {
                    updateUserData()
                    /* if (currentPos < 8) {
                         textValue(currentPos + 1)
                         adapterInit(currentPos + 1)
                     } else {
                         //Utils.isLogin = false
                         startActivity(Intent(this, DashboardActivity::class.java))
                         finishAffinity()
                     }*/
                }
            }
        }

        binding.skIntenseProgress.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Handle progress change
                Log.d("SeekBar", "Progress changed to: $progress")
                // Example: Update a text view with the current value
                selectedPerfumeStg = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when user starts interacting with the SeekBar
                Log.d("SeekBar", "Tracking started")

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when user stops interacting with the SeekBar
                Log.d("SeekBar", "Tracking stopped")

            }
        })
    }

    override fun onBackPressed() {
        if (currentPos > 1) {
            textValue(currentPos - 1)
            adapterInit(currentPos - 1)

        } else {
            super.onBackPressed()
        }

    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        if (currentPos > 1) {
            textValue(currentPos - 1)
            adapterInit(currentPos - 1)

        } else {
            return super.getOnBackInvokedDispatcher()
        }
        return super.getOnBackInvokedDispatcher()
    }

    private fun updateUserData() {
        var request = hashMapOf<String, Any>()
        when (currentPos) {
            1 -> {
                if (selectedGender != "") {

                    request = hashMapOf(
                        "gender" to selectedGender,
                        "step" to currentPos
                    )
                    viewModel.updateData(UPDATE_DATA_API, request)
                } else {
                    showToast("Please select gender")
                }
            }

            2 -> {
                if (selectedSmellList.isNullOrEmpty()) {
                    showToast("Please select at least 1 smell")
                } else {
                    request = hashMapOf(
                        "step" to currentPos,
                        "enjoySmell" to selectedSmellList
                    )
                    viewModel.updateData(UPDATE_DATA_API, request)
                }
            }

            3 -> {
                if (selectedReason != "") {
                    request = hashMapOf(
                        "step" to currentPos,
                        "reasonForWearPerfume" to selectedReason
                    )
                    viewModel.updateData(UPDATE_DATA_API, request)
                } else {
                    showToast("Please select wear perfume reason")
                }

            }

            4 -> {
                request = hashMapOf(
                    "step" to currentPos,
                    "perfumeStrength" to selectedPerfumeStg
                )
                viewModel.updateData(UPDATE_DATA_API, request)
            }

            5 -> {
                if (selectedBudget != "") {
                    request = hashMapOf(
                        "step" to currentPos,
                        "perfumeBudget" to selectedBudget
                    )
                    viewModel.updateData(UPDATE_DATA_API, request)
                } else {
                    showToast("Please select budget")
                }
            }

            6 -> {
                if (selectedRefSource != "") {
                    request = hashMapOf(
                        "step" to currentPos,
                        "referralSource" to selectedRefSource
                    )
                    viewModel.updateData(UPDATE_DATA_API, request)
                } else {
                    showToast("Please select source")
                }
            }

            7 -> {
                if ( checkAndUpdateDate()) {
                    request = hashMapOf(
                        "step" to currentPos,
                        "dob" to selectedDob
                    )
                    viewModel.updateData(UPDATE_DATA_API, request)
                }

            }

            8 -> {
                if (binding.edReferralCode.text.toString().trim().isNullOrEmpty()) {
                    request = hashMapOf(
                        "step" to currentPos,
                        // "referredBy" to binding.edReferralCode.text.toString()
                    )
                    viewModel.updateData(UPDATE_DATA_API, request)
                }else{
                    showErrorToast("refer code is not valid")
                }
                /*if(binding.edReferralCode.text.isNullOrEmpty()){
                    showToast("please enter referral code")
                }else{
                    request = hashMapOf(
                        "step" to currentPos,
                        "referredBy" to binding.edReferralCode.text.toString()
                    )
                    viewModel.updateData(UPDATE_DATA_API, request)
                }*/

            }

            /*else -> {
                request = hashMapOf(
                    "step" to currentPos,
                    "enjoySmell" to selectedSmellList
                )
                viewModel.updateData(UPDATE_DATA_API, request)
            }*/

        }

    }

    private fun textValue(i: Int) {
        Log.d("textValue", "textValue: $i")

        when (i) {
            1 -> {
                binding.textHeading.text = "Choose your Gender"
                binding.textDescription.text = "( This wil be used to enhance your experience)"
            }

            2 -> {
                binding.textHeading.text = "Which smells do you\nenjoy the most?"
                binding.textDescription.text = "( Pick up to 3)"
            }

            3 -> {
                binding.textHeading.text = "What is the main reason you wear perfume?"
                binding.textDescription.text = ""
            }

            4 -> {
                binding.textHeading.text = "Do you like light perfumes or strong ones?"
                binding.textDescription.text = ""

            }

            5 -> {
                binding.textHeading.text = "How much do you usually spend on a bottle of perfume?"
                binding.textDescription.text = ""
            }

            6 -> {
                binding.textHeading.text = "Where did you hear about us?"
                binding.textDescription.text = ""
            }

            7 -> {
                binding.textHeading.text = "When were you born?"
                binding.textDescription.text = "( This wil be used to enhance your experience)"
            }

            8 -> {
                binding.textHeading.text = "Do you have referral code?"
                binding.textDescription.text = "(You can skip this step)"
            }

        }
    }

    private fun adapterInit(i: Int) {
        currentPos = i
        progressStatus((i * 12.5).toInt())
        //updateViewSecWidth(binding.viewMain, binding.viewSec, i = i)
        when (i) {
            4 -> {
                binding.clIntensity.visibility = View.VISIBLE
                binding.clDob.visibility = View.GONE
                binding.onboardingRv.visibility = View.GONE
            }

            7 -> {
                binding.onboardingRv.visibility = View.GONE
                binding.clDob.visibility = View.VISIBLE
                binding.clReferralCode.visibility = View.GONE
            }

            8 -> {
                binding.onboardingRv.visibility = View.GONE
                binding.clDob.visibility = View.GONE
                binding.clReferralCode.visibility = View.VISIBLE
            }

            1 -> {
                binding.clDob.visibility = View.GONE
                binding.clIntensity.visibility = View.GONE
                binding.onboardingRv.visibility = View.VISIBLE

                binding.onboardingRv.layoutManager = LinearLayoutManager(this)
                binding.onboardingRv.adapter = genderAdapter
                genderAdapter.notifyDataSetChanged()

            }

            else -> {
                binding.clDob.visibility = View.GONE
                binding.clIntensity.visibility = View.GONE
                binding.onboardingRv.visibility = View.VISIBLE
                setLayoutMangerRv(i)

                onboardingAdapter = SimpleRecyclerViewAdapter(
                    R.layout.onboarding_item_view, BR.bean
                ) { v, m, pos ->

                    when (v.id) {
                        R.id.clMain -> {
                            Log.d("clMainPos", "adapterInit:  $currentPos , $pos")
                            //onboardingAdapter.list.forEach { it.selected = false }
                            when (currentPos) {
                                2 -> {
                                    //mostSmallList.forEach { it.selected = false }

                                    if (selectedSmellList.size < 3) {
                                        m.selected = m.selected != true

                                        if (m.selected == true) {
                                            selectedSmellList.add(m.text)
                                        } else {
                                            selectedSmellList.remove(m.text)
                                        }
                                        Log.d("mSelected", "adapterInit: ${selectedSmellList.size}")
                                        onboardingAdapter.notifyDataSetChanged()
                                    } else {
                                        if (m.selected == true) {
                                            m.selected = false
                                            selectedSmellList.remove(m.text)
                                        }
                                        Log.d("mSelected", "adapterInit: ${selectedSmellList.size}")
                                        onboardingAdapter.notifyDataSetChanged()
                                    }
                                }

                                3 -> {
                                    reasonList.forEach { it.selected = false }
                                    m.selected = m.selected != true
                                    selectedReason = m.text
                                    onboardingAdapter.notifyDataSetChanged()
                                }

                                5 -> {
                                    costList.forEach { it.selected = false }
                                    m.selected = m.selected != true
                                    selectedBudget = m.text
                                    onboardingAdapter.notifyDataSetChanged()
                                }

                                6 -> {
                                    hearAboutList.forEach { it.selected = false }
                                    m.selected = m.selected != true
                                    selectedRefSource = m.text
                                    onboardingAdapter.notifyDataSetChanged()
                                }
                            }
                            /*onboardingAdapter.list.forEach { it.selected = false }
                            m.selected = m.selected != true
                            onboardingAdapter.notifyDataSetChanged()*/
                        }
                    }


                }
                binding.onboardingRv.adapter = onboardingAdapter
                when (i) {
                    2 -> {
                        onboardingAdapter.list = mostSmallList
                        onboardingAdapter.notifyDataSetChanged()
                    }

                    3 -> {
                        onboardingAdapter.list = reasonList
                        onboardingAdapter.notifyDataSetChanged()
                    }

                    5 -> {
                        onboardingAdapter.list = costList
                        onboardingAdapter.notifyDataSetChanged()
                    }

                    6 -> {
                        onboardingAdapter.list = hearAboutList
                        onboardingAdapter.notifyDataSetChanged()
                    }

                }
                //onboardingAdapter.list = setDataValueRv(i)
            }
        }
    }

    private fun setDataValueRv() {
        val itemListData = ArrayList<OnboardingModel>()

        itemListData.add(OnboardingModel(1, R.drawable.male, "Male", true, "#006AFA"))
        itemListData.add(OnboardingModel(1, R.drawable.female, "Female", true, "#E024FF"))
        itemListData.add(
            OnboardingModel(
                1,
                R.drawable.female,
                "Prefer not to say",
                false,
                ""
            )
        )
        ///////////////////////////
        mostSmallList.add(
            OnboardingModel(
                2,
                R.drawable.fresh_icon,
                "Fresh",
                true,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.enjoySmell?.contains("Fresh")
                    }
                } == true
            )
        )
        mostSmallList.add(
            OnboardingModel(
                2,
                R.drawable.sweet_icon,
                "Sweet",
                true,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.enjoySmell?.contains("Sweet")
                    }
                } == true
            )
        )
        mostSmallList.add(
            OnboardingModel(
                2,
                R.drawable.wood,
                "Woody",
                true,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.enjoySmell?.contains("Woody")
                    }
                } == true
            )
        )
        mostSmallList.add(
            OnboardingModel(
                2,
                R.drawable.floral_icon,
                "Floral",
                true,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.enjoySmell?.contains("Floral")
                    }
                } == true
            )
        )
        mostSmallList.add(
            OnboardingModel(
                2,
                R.drawable.spicy_icon,
                "Spicy",
                true,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.enjoySmell?.contains("Spicy")
                    }
                } == true
            )
        )
        mostSmallList.add(
            OnboardingModel(
                2,
                R.drawable.fruity_icon,
                "Fruity",
                true,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.enjoySmell?.contains("Fruity")
                    }
                } == true
            )
        )
        mostSmallList.add(
            OnboardingModel(
                2,
                R.drawable.musky_icon,
                "Musky",
                true,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.enjoySmell?.contains("Musky")
                    }
                } == true
            )
        )

        //////////////////////
        reasonList.add(
            OnboardingModel(
                3,
                R.drawable.fresh_icon,
                "Everyday",
                false,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.reasonForWearPerfume?.equals("Everyday")
                    }
                } == true
            )
        )
        reasonList.add(
            OnboardingModel(
                3,
                R.drawable.sweet_icon,
                "Work",
                false,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.reasonForWearPerfume?.equals("Work")
                    }
                } == true
            )
        )
        reasonList.add(
            OnboardingModel(
                3,
                R.drawable.wood,
                "Dates",
                false,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.reasonForWearPerfume?.equals("Dates")
                    }
                } == true
            )
        )
        reasonList.add(
            OnboardingModel(
                3,
                R.drawable.floral_icon,
                "Special Events",
                false,
                "", R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.reasonForWearPerfume?.equals("Special Events")
                    }
                } == true
            )
        )
        reasonList.add(
            OnboardingModel(
                3,
                R.drawable.spicy_icon,
                "Gym",
                false,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.reasonForWearPerfume?.equals("Gym")
                    }
                } == true
            )
        )
        reasonList.add(
            OnboardingModel(
                3,
                R.drawable.fruity_icon,
                "Just for me",
                false,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.reasonForWearPerfume?.equals("Just for me")
                    }
                } == true
            )
        )
        reasonList.add(
            OnboardingModel(
                3,
                R.drawable.musky_icon,
                "Other",
                false,
                "",
                R.drawable.radial_bg_item_view,
                onboardingData.let {
                    it?.data.let {
                        it?.reasonForWearPerfume?.equals("Other")
                    }
                } == true
            )
        )
////////////////////////////
        costList.add(
            OnboardingModel(
                5,
                R.drawable.fresh_icon,
                "Under \$50",
                false,
                "",
                R.drawable.radial_bg_item_view1,
                onboardingData.let {
                    it?.data.let {
                        it?.perfumeBudget?.equals("Under \$50")
                    }
                } == true
            )
        )
        costList.add(
            OnboardingModel(
                5,
                R.drawable.sweet_icon,
                "\$50 - \$100",
                false,
                "",
                R.drawable.radial_bg_item_view1,
                onboardingData.let {
                    it?.data.let {
                        it?.perfumeBudget?.equals("\$50 - \$100")
                    }
                } == true
            )
        )
        costList.add(
            OnboardingModel(
                5,
                R.drawable.wood,
                "\$100 - \$200",
                false,
                "",
                R.drawable.radial_bg_item_view1,
                onboardingData.let {
                    it?.data.let {
                        it?.perfumeBudget?.equals("\$100 - \$200")
                    }
                } == true
            )
        )
        costList.add(
            OnboardingModel(
                5,
                R.drawable.floral_icon,
                "Above \$200",
                false,
                "",
                R.drawable.radial_bg_item_view1,
                onboardingData.let {
                    it?.data.let {
                        it?.perfumeBudget?.equals("Above \$200")
                    }
                } == true
            )
        )
//////////////////////////////
        hearAboutList.add(
            OnboardingModel(
                6,
                R.drawable.fresh_icon,
                "Facebook",
                false,
                "",
                R.drawable.radial_bg_item_view1,
                onboardingData.let {
                    it?.data.let {
                        it?.referralSource?.equals("Facebook")
                    }
                } == true
            )
        )
        hearAboutList.add(
            OnboardingModel(
                6,
                R.drawable.sweet_icon,
                "Instagram",
                false,
                "",
                R.drawable.radial_bg_item_view1,
                onboardingData.let {
                    it?.data.let {
                        it?.referralSource?.equals("Instagram")
                    }
                } == true
            )
        )
        hearAboutList.add(
            OnboardingModel(
                6,
                R.drawable.wood,
                "Tiktok",
                false,
                "",
                R.drawable.radial_bg_item_view1,
                onboardingData.let {
                    it?.data.let {
                        it?.referralSource?.equals("Tiktok")
                    }
                } == true
            )
        )
        hearAboutList.add(
            OnboardingModel(
                6,
                R.drawable.floral_icon,
                "App Store",
                false,
                "",
                R.drawable.radial_bg_item_view1,
                onboardingData.let {
                    it?.data.let {
                        it?.referralSource?.equals("App Store")
                    }
                } == true
            )
        )
        hearAboutList.add(
            OnboardingModel(
                6,
                R.drawable.floral_icon,
                "Friend or Family",
                false,
                "", R.drawable.radial_bg_item_view1,
                onboardingData.let {
                    it?.data.let {
                        it?.referralSource?.equals("Friend or Family")
                    }
                } == true
            )
        )


    }

    private fun setLayoutMangerRv(type: Int) {
        when (type) {
            1 -> {
                binding.onboardingRv.layoutManager = LinearLayoutManager(this)
            }

            2 -> {
                binding.onboardingRv.layoutManager = GridLayoutManager(this, 2)
            }

            3 -> {
                binding.onboardingRv.layoutManager = GridLayoutManager(this, 2)
            }

            4 -> {
                binding.onboardingRv.layoutManager = LinearLayoutManager(this)
            }

            5 -> {
                binding.onboardingRv.layoutManager = LinearLayoutManager(this)
            }

            6 -> {
                binding.onboardingRv.layoutManager = LinearLayoutManager(this)
            }
        }

    }

    private fun checkAndUpdateDate() :Boolean{
        if (!selectedYear.isNullOrEmpty() && !selectedMonth.isNullOrEmpty() && !selectedDay.isNullOrEmpty()
            && selectedYear != "Year" && selectedMonth != "Month" && selectedDay != "Day"
        ) {
            //val month = selectedMonth!!.padStart(2, '0')
            val monthNum = monthMap[selectedMonth] ?: run {
                showToast("Invalid month selected.")
                return false
            }
            val day = selectedDay!!.padStart(2, '0')
            selectedDob = "${selectedYear}-${monthNum}-${day}"

            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            sdf.isLenient = false
            try {
                val selectedDate = sdf.parse(selectedDob)
                val today = java.util.Calendar.getInstance().time

                if (selectedDate != null && selectedDate.before(today)) {
                    // Calculate date 13 years ago
                    val calendar = java.util.Calendar.getInstance()
                    calendar.time = today
                    calendar.add(java.util.Calendar.YEAR, -13)
                    val minAllowedDob = calendar.time

                    if (selectedDate.after(minAllowedDob)) {
                        showErrorToast("You must be at least 13 years old.")
                        Log.d("selectedDate", "User less than 13 years old: $selectedDob")
                        return false
                    } else {
                        Log.d("selectedDate", "Selected Date: $selectedDob")
                        // Valid DOB, continue processing
                        return true
                    }
                } else {
                    Log.d("selectedDate", "Invalid date: $selectedDob")
                    showErrorToast("Date of birth must be before today.")
                    return false
                }
            } catch (e: Exception) {
                Log.d("selectedDate", "Parse error: ${e.message} $selectedDob")
                // Show error or handle invalid date
                return false
            }
        }else{
            showErrorToast("Date of birth is required")
            return false
        }
    }

    private fun setupSpinner(spinner: Spinner, data: List<String>) {
        val adapter = object :
            ArrayAdapter<String>(this, R.layout.item_spinner_text, R.id.spinnerText, data) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.item_spinner_dropdown, parent, false)
                val text = view.findViewById<TextView>(R.id.spinnerText)
                text.text = getItem(position)
                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.item_spinner_text, parent, false)
                val text = view.findViewById<TextView>(R.id.spinnerText)
                text.text = getItem(position)
                return view
            }
        }
        spinner.adapter = adapter

        binding.spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedValue = parent.getItemAtPosition(position) as String
                Log.d("selectedValue", "onItemSelected: Selected Value: $selectedValue")
                // You can use selectedValue as needed
                selectedDay = parent.getItemAtPosition(position) as String
                //checkAndUpdateDate()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("selectedValue", "onNothingSelected ")
            }
        }



        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedValue = parent.getItemAtPosition(position) as String
                Log.d("selectedValue", "onItemSelected: Selected Value: $selectedValue")

                // You can use selectedValue as needed
                selectedMonth = parent.getItemAtPosition(position) as String
                //checkAndUpdateDate()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("selectedValue", "onNothingSelected ")
            }
        }

        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedValue = parent.getItemAtPosition(position) as String
                Log.d("selectedValue", "onItemSelected: Selected Value: $selectedValue")
                // You can use selectedValue as needed
                selectedYear = parent.getItemAtPosition(position) as String
               // checkAndUpdateDate()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("selectedValue", "onNothingSelected ")
            }
        }

        //////////////////


    }

    private fun progressStatus(i: Int) {
        Log.d("progressStatus", "progressStatus: $i")
        ObjectAnimator.ofInt(binding.skProgress, "progress", binding.skProgress.progress, i).apply {
            duration = 500
            start()
        }
        if (i == 100) {
            binding.skProgress.showHideProgressShadow(false)
        } else {
            binding.skProgress.showHideProgressShadow(true)
        }
    }

    fun updateViewSecWidth(viewMain: View, viewSec: View, i: Int) {
        viewMain.post {
            val mainWidth = viewMain.width
            if (i > 0) {
                val partWidth = mainWidth / 7
                val targetWidth = partWidth * i

                val layoutParams = viewSec.layoutParams
                layoutParams.width = targetWidth
                viewSec.layoutParams = layoutParams
            }
        }
    }

    fun selectSpinnerItemByValue(spinner: Spinner, value: String) {
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
