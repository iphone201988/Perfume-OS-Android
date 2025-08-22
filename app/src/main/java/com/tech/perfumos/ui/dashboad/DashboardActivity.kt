package com.tech.perfumos.ui.dashboad


import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.RECENT_TOP_PERFUME_API
import com.tech.perfumos.data.local.SharedPrefManager
import com.tech.perfumos.databinding.ActivityDashboadBinding
import com.tech.perfumos.databinding.SearchPerfumeDialogBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.permission.PermissionHandler
import com.tech.perfumos.ui.base.permission.Permissions
import com.tech.perfumos.ui.camera_perfume.CameraActivity
import com.tech.perfumos.ui.camera_perfume.PerfumeInfoActivity
import com.tech.perfumos.ui.camera_perfume.model.PerfumeInfoModel
import com.tech.perfumos.ui.core.SearchClickListener
import com.tech.perfumos.ui.dashboad.fragment_folder.articles_folder.ArticlesFragment
import com.tech.perfumos.ui.dashboad.fragment_folder.compare.CompareFragment
import com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.HomeFragment
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.ProfileFragment
import com.tech.perfumos.ui.dashboad.fragment_folder.setting.SettingFragment
import com.tech.perfumos.ui.dashboad.model.SearchHistoryModel
import com.tech.perfumos.utils.BaseCustomDialog
import com.tech.perfumos.utils.CommonFunctionClass
import com.tech.perfumos.utils.SocketManagerHelper
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.Utils.toggleTheme
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderEffectBlur
import org.json.JSONObject

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboadBinding>(), SearchClickListener {
    val viewmodel: DashboardVm by viewModels()
    private lateinit var reviewDialog: BaseCustomDialog<SearchPerfumeDialogBinding>
    private lateinit var bottomSheet: SearchBottomSheet
    private var searchHistoryData: SearchHistoryModel.SearchHistoryData? = null
    private var exit = false
    private var isBottomSheetData = false
    private var currentFragmentTag: String = "HOME"
    private val fragmentTags = listOf("HOME", "ARTICLE", "PROFILE", "SETTINGS")
    private val fragmentMap = mapOf(
        "HOME" to HomeFragment(),
        "ARTICLE" to ArticlesFragment(),
        "PROFILE" to ProfileFragment(),
        "SETTINGS" to SettingFragment()
    )

    private val mainMenuFragments = setOf("HOME", "ARTICLE", "PROFILE", "SETTINGS")

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        val fragmentToOpen = intent.getStringExtra("open_fragment")

        when (fragmentToOpen) {
            "profile" -> {
                loadFragment(SettingFragment(), false)
            }

            "PerfumeInfo" -> {

                /*val data = sharedPrefManager.getCompareList()
                data?.forEach {
                    if(it != null){
                        Log.d("Dqwf", "onCreateView:${it}, ${it.id}")
                    }else{
                        Log.d("Dqwf", "onCreateView: null")
                    }
                }
                if (this::bottomSheet.isInitialized) {
                    bottomSheet.dismiss()
                }
                val perfumeInfoData =
                    intent.getSerializableExtra("perfumeData") as? PerfumeInfoModel.PerfumeInfoData
                val fragment = CompareFragment()
                val bundle = Bundle().apply {
                    putSerializable("perfumeData", perfumeInfoData)
                }
                fragment.arguments = bundle
                loadFragment(fragment, false)*/
            }
        }

    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_dashboad
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    override fun onCreateView() {
        Utils.bottomAppBar = binding.bottomAppBar
        Utils.bottomNavigationView = binding.bottomNavigationView
        Utils.fabCamera = binding.fabCamera

        /* if(sharedPrefManager.getDarkMode() ?: false){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val rationale = "notification permissions"
            val options = Permissions.Options()
            Permissions.check(this, Manifest.permission.POST_NOTIFICATIONS,
                0,
                object : PermissionHandler() {
                    override fun onGranted() {

                    }

                })
        }
        binding.bottomNavigationView.post {
            val menuView = binding.bottomNavigationView.getChildAt(0) as? ViewGroup
            if (menuView != null && menuView.childCount > 0) {
                Utils.navGraphWidth = menuView.getChildAt(0).width.toFloat()
                Utils.navGraphHeight = menuView.getChildAt(0).height.toFloat()
            }
        }

        Log.d("Dqwf", "onCreateView: ")
        val list: ArrayList<PerfumeInfoModel.PerfumeInfoData?> = ArrayList()
        list.add(null)
        list.add(null)
        sharedPrefManager.saveCompareList(list)

        viewmodel.getRecentTopPerfumeApi(RECENT_TOP_PERFUME_API)
        binding.bottomNavigationView.post {
            // Get the BottomNavigationMenuView (container of menu items)
            Utils.targetViews.clear()
            val menuView = binding.bottomNavigationView.getChildAt(0) as? ViewGroup
            if (menuView != null) {
                for (i in 0 until menuView.childCount) {
                    val itemView = menuView.getChildAt(i)
                    val location = IntArray(2)
                    itemView.getLocationOnScreen(location)
                    val x = location[0]
                    val y = location[1]
                    Utils.targetViews.add(itemView)
                    // Optionally get the title of the menu item
                    val menuItemTitle = binding.bottomNavigationView.menu.getItem(i).title

                    Log.d("MenuItemCoords", "Item '$menuItemTitle' is at X: $x, Y: $y")
                }
            }
        }

        Utils.screenFillView(this)
        clickListener()
        drawer()
        setFragment(1)
        initOnBackPressed()
        initObserver()
        reviewDialog()
        Glide.with(this).asGif().load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)
        mainMenuFragments.forEach {
            Log.d("mainMenuFragments", "onCreateView: $it")
        }
        Handler(Looper.getMainLooper()).post {
            socketConnect()
        }
    }

    private fun socketConnect() {
        if (sharedPrefManager.getUserID() == null) {
            showErrorToast("Your login section is expire, Please login again")
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
            sharedPrefManager.clear()
        } else {
            SocketManagerHelper.init(sharedPrefManager.getUserID().toString())
            SocketManagerHelper.listenEvent("userProfile", callback = {
                CommonFunctionClass.logPrint(tag = "SOCKET_PROFILE", "DATA->$it")
            })
        }
    }


    private var currentFragmentId = R.id.home_menu
    private fun setFragment(selected: Int) {

        when (selected) {
            1 -> {
                loadFragment(HomeFragment(), true)
            }

            2 -> {

                loadFragment(ArticlesFragment(), true)
            }

            3 -> {

                loadFragment(CompareFragment(), true)
            }

            4 -> {

                loadFragment(ProfileFragment(), true)
            }

            else -> {

                loadFragment(HomeFragment(), false)
            }
        }
    }


    /* fun switchToFragment(tag: String) {
         val fragmentToShow = fragments[tag] ?: return
         supportFragmentManager.beginTransaction().apply {
             hide(activeFragment)
             show(fragmentToShow)
         }.commit()
         activeFragment = fragmentToShow
     }*/

    private fun drawer() {
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu[2].isEnabled = false
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == currentFragmentId) {
                return@setOnItemSelectedListener true // Ignore if already selected
            }
            currentFragmentId = item.itemId
            when (item.itemId) {
                R.id.home_menu -> setFragment(1)
                R.id.search_menu -> setFragment(2)
                R.id.chat_menu -> setFragment(3)
                R.id.profile_menu -> setFragment(4)
                else -> setFragment(1)
            }
            true
        }
    }

    /** replace fragment function **//*private fun loadFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
        transaction.replace(R.id.homeSectionNav, fragment, tag)
        transaction.addToBackStack(null)
        transaction.commit()

        //currentFragmentTag = fragment.tag ?: ""

    }*/

    private var currentMainFragmentTag: String? = null
    private fun loadFragment(fragment: Fragment, isMainFragment: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
        transaction.replace(R.id.homeSectionNav, fragment)

        if (!isMainFragment) {
            transaction.addToBackStack(null) // Only add detail fragments to backstack
        } else {
            // Clear backstack when loading a main fragment
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            currentMainFragmentTag = fragment::class.java.simpleName

        }
        if (fragment is SettingFragment) {
            binding.bottomNavigationView.selectedItemId = R.id.profile_menu
        } else {
            setBottomNavSelected(fragment)
        }
        transaction.commit()
    }

    /*private fun loadFragment(fragment: Fragment, tag: String, isMainFragment: Boolean) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)

        // Try to find existing fragment by tag
        var fragmentToShow = fragmentManager.findFragmentByTag(tag)
        if (fragmentToShow == null) {
            fragmentToShow = fragment
            transaction.replace(R.id.homeSectionNav, fragmentToShow, tag)
        } else {
            transaction.replace(R.id.homeSectionNav, fragmentToShow, tag)
        }

        if (!isMainFragment) {
            transaction.addToBackStack(null)
        } else {
            // Clear backstack for main fragments
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            currentMainFragmentTag = tag
        }

        transaction.commit()
    }*/

    private fun setBottomNavSelected(fragment: Fragment?) {
        val itemId = when (fragment) {
            is HomeFragment -> R.id.home_menu
            is ArticlesFragment -> R.id.search_menu
            is CompareFragment -> R.id.chat_menu
            is ProfileFragment -> R.id.profile_menu
            //is SettingFragment -> R.id.profile_menu
            else -> 0 // Deselect or keep current
        }
        if (itemId != 0) {
            binding.bottomNavigationView.selectedItemId = itemId
        }
    }

    /*private fun setBottomNavSelected(tag: String) {
        val itemId = when (tag) {
            "Home" -> R.id.home_menu
            "SearchFragment" -> R.id.search_menu
            "ChatFragment" -> R.id.chat_menu
            "ProfileFragment" -> R.id.profile_menu
            else -> R.id.menu_home // Default to home
        }
        bottomNavigationView.selectedItemId = itemId
    }*/

    /** activity back press handle **/
    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.fabCamera -> {
                    startActivity(Intent(this, CameraActivity::class.java))
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
                        RECENT_TOP_PERFUME_API -> {
                            try {
                                Log.d("response", "RECENT_TOP_PERFUME_API: ${Gson().toJson(it)}")
                                val data: SearchHistoryModel? = parseJson(it.data.toString())
                                Log.d("response", "RECENT_TOP_PERFUME_API : ${data?.success}")

                                if (data?.data != null) {
                                    searchHistoryData = data.data
                                    if (isBottomSheetData) {
                                        if (searchHistoryData != null) {
                                            if (!this::bottomSheet.isInitialized) {
                                                bottomSheet =
                                                    SearchBottomSheet.newInstance(searchHistoryData!!)
                                            }
                                            bottomSheet.onPerfumeClick = { perfumeId ->
                                                // Handle click as you want, e.g.:

                                                val intent =
                                                    Intent(this, PerfumeInfoActivity::class.java)
                                                intent.putExtra("perfumeId", perfumeId)
                                                perfumeInfoLauncher.launch(intent)
                                            }
                                            bottomSheet.show(
                                                supportFragmentManager,
                                                bottomSheet.tag
                                            )
                                            isBottomSheetData = false
                                        }
                                    }
                                } else {
                                    showToast(data?.message)
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

    private fun initOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {/*val currentFragment = supportFragmentManager.findFragmentById(R.id.homeSectionNav)
                val currentTag = currentFragment?.tag ?: ""
                Log.d(
                    "hoomeFragment",
                    "handleOnBackPressed: ${supportFragmentManager.backStackEntryCount}, $currentTag"
                )

                if (currentTag in mainMenuFragments) {
                    if (currentTag == "HOME") {
                        if (exit) {
                            finishAffinity()
                        } else {
                            showToast("Tap again to exit")
                            exit = true
                            Handler(Looper.getMainLooper()).postDelayed({ exit = false }, 2000)
                        }
                    } else {
                        // If current is a main menu fragment but not home, load home fragment
                        loadFragment(HomeFragment(),"HOME" )
                    }
                } else {
                    // Current fragment is a details fragment
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        supportFragmentManager.popBackStack()
                    } else {
                        // No back stack, load home fragment or finish
                        loadFragment(HomeFragment(), "HOME")
                    }
                }*/

                val fragmentManager = supportFragmentManager
                Log.d(
                    "backStackEntryCount",
                    "handleOnBackPressed: ${fragmentManager.backStackEntryCount}"
                )

                // If there are detail fragments in backstack, pop them first
                if (fragmentManager.backStackEntryCount > 0) {
                    fragmentManager.popBackStack()
                } else {
                    // If current main fragment is not Home, go to Home
                    if (currentMainFragmentTag != "HomeFragment") {
                        loadFragment(HomeFragment(), true)
                    } else {
                        if (exit) {
                            finishAffinity()
                        } else {
                            showToast("Tap again to exit")
                            exit = true
                            Handler(Looper.getMainLooper()).postDelayed({ exit = false }, 2000)
                        }
                    }
                }

                /*if (isHome) {
                    if (exit) {
                        finishAffinity()
                    } else {
                        showToast("Tap again to exit")
                        exit = true
                        Handler(Looper.getMainLooper()).postDelayed({ exit = false }, 2000)
                    }
                } else {
                    isHome = true
                    loadFragment(HomeFragment())
                }*/
            }
        })
    }

    override fun onSearchClick() {
        if (searchHistoryData != null) {

            /*    val bottomSheet = SearchBottomSheet()
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)*/

            bottomSheet = SearchBottomSheet.newInstance(searchHistoryData!!)

            bottomSheet.onPerfumeClick = { perfumeId ->
                // Handle click as you want, e.g.:
                val intent = Intent(this, PerfumeInfoActivity::class.java)
                intent.putExtra("perfumeId", perfumeId)
                perfumeInfoLauncher.launch(intent)
            }
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)

        } else {
            isBottomSheetData = true
            viewmodel.getRecentTopPerfumeApi(RECENT_TOP_PERFUME_API)

        }

        /* val intent = Intent(this, SearchActivity::class.java)
         startActivity(intent)
         overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)*/

    }

    override fun onDestroy() {
        super.onDestroy()
        Utils.targetViews.clear()
    }


    override fun onThemeSwitch(dark: Boolean) {
        toggleTheme(dark)
    }

    override fun writeReviewClick() {
        reviewDialog.show()
    }

    override fun loadFragment(fragment: Fragment) {
        this.loadFragment(fragment, true)
    }

    override fun openPerfumeScreen(perfumeId: String) {
        val intent = Intent(this, PerfumeInfoActivity::class.java)
        intent.putExtra("perfumeId", perfumeId)
        perfumeInfoLauncher.launch(intent)
    }

    private fun reviewDialog() {
        reviewDialog = BaseCustomDialog<SearchPerfumeDialogBinding>(
            this, R.layout.search_perfume_dialog
        ) {
            when (it?.id) {
                R.id.tvTakePhoto -> {
                    startActivity(Intent(this, CameraActivity::class.java))
                    reviewDialog.dismiss()
                }

                R.id.tvSearch -> {
                    if (searchHistoryData != null) {
                        bottomSheet = SearchBottomSheet.newInstance(searchHistoryData!!)
                        bottomSheet.onPerfumeClick = { perfumeId ->
                            // Handle click as you want, e.g.:
                            val intent = Intent(this, PerfumeInfoActivity::class.java)
                            intent.putExtra("perfumeId", perfumeId)
                            perfumeInfoLauncher.launch(intent)
                        }
                        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                        reviewDialog.dismiss()
                    } else {
                        isBottomSheetData = true
                        viewmodel.getRecentTopPerfumeApi(RECENT_TOP_PERFUME_API)
                        reviewDialog.dismiss()

                    }
                }
            }
        }
        reviewDialog.create()
        reviewDialog.setCancelable(true)


        val windowBackground = window.decorView.background

        val blurView = reviewDialog.binding.blurView

        val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView.setupWith(rootView, RenderEffectBlur()).setFrameClearDrawable(windowBackground)
                .setBlurRadius(16f)
        }


    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupBlurView() {


        /*blurView.setupWith(binding.flTabs, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)*/

    }


    private val perfumeInfoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                Log.d("perfumeInfoLauncher", "perfumeInfoLauncher: ")


                val data = sharedPrefManager.getCompareList()
                data?.forEach {
                    if (it != null) {
                        Log.d("Dqwf", "perfumeInfoLauncher:${it}, ${it.id}")
                    } else {
                        Log.d("Dqwf", "perfumeInfoLauncher: null")
                    }

                }

                if (this::bottomSheet.isInitialized) {
                    bottomSheet.dismiss()
                }
                val perfumeInfoData =
                    it.data?.getSerializableExtra("perfumeData") as? PerfumeInfoModel.PerfumeInfoData
                val fragment = CompareFragment()
                val bundle = Bundle().apply {
                    putSerializable("perfumeData", perfumeInfoData)
                }
                fragment.arguments = bundle
                loadFragment(fragment, false)
            }
        }
}