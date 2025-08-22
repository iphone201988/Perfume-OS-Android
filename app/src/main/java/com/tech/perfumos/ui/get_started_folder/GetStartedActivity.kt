package com.tech.perfumos.ui.get_started_folder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tech.perfumos.R
import com.tech.perfumos.databinding.ActivityGetStartedBinding
import com.tech.perfumos.databinding.ActivityWelcomeBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.onboarding_folder.OnboardingActivity
import com.tech.perfumos.ui.splash.WelcomeActivityVM
import com.tech.perfumos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GetStartedActivity : BaseActivity<ActivityGetStartedBinding>() {
    private val viewModel: WelcomeActivityVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_get_started
    }
    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    private val handler = Handler()
    private val AUTO_SCROLL_DELAY: Long = 3000 // Time between automatic scrolls in milliseconds
    private var userScrollStarted = false
    private var lastUserScrollPosition = 0
    private val runnable = object : Runnable {
        override fun run() {
            if (!userScrollStarted) {
                lastUserScrollPosition =
                    if (lastUserScrollPosition == (binding.viewPager2.adapter?.itemCount ?: 0)) {
                        0
                    } else {
                        lastUserScrollPosition + 1
                    }
                binding.viewPager2.setCurrentItem(lastUserScrollPosition, false)
            }
            handler.postDelayed(this, AUTO_SCROLL_DELAY)
        }
    }

    override fun onCreateView() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Utils.screenFillView(this)
        Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)

        initViewPager()
        initTabLayout()
        clickListener()

    }

    private fun clickListener() {
        viewModel.onClick.observe(this){
            when(it?.id){
                R.id.getStartedLayout ->{
                    startActivity(Intent(this@GetStartedActivity, LoginActivity::class.java))
                }
            }
        }
    }

    private fun initViewPager() {
        val viewPager = OnBoardingViewPager(this)
        binding.viewPager2.adapter = viewPager
        binding.viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
//        handler.postDelayed(runnable, AUTO_SCROLL_DELAY)
    }

    private fun initTabLayout() {
        binding.textViewPager.text = "Scan, discover and find \n your perfect scent in \nseconds "
        val viewPagerAdapter = OnBoardingViewPager(this)
        binding.viewPager2.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->

        }.attach()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.i("TabChanged", "Selected tab position: ${tab.position}")
                when (tab.position) {
                    0 ->{
                        binding.textDescription.visibility= View.GONE
                        binding.textViewPager.text = "Scan, discover and find \n your perfect scent in \nseconds "
                        binding.textDescription.text = ""
                    }
                    1 -> {
                        binding.textDescription.visibility= View.VISIBLE
                        binding.textViewPager.text = "Snap and Discover"
                        binding.textDescription.text = "Unlock the perfume’s story —\n" +
                                "discover the perfumer, brand, and\n" +
                                "fragrance details in seconds."
                    }
                    2 -> {
                        binding.textDescription.visibility= View.VISIBLE
                        binding.textViewPager.text = "Share your Scent Story"
                        binding.textDescription.text = "Write reviews, rate perfumes, and join\n" +
                                "a community of perfume lovers"
                    }
                    3 ->{
                        binding.textDescription.visibility= View.VISIBLE
                        binding.textViewPager.text = "Level up your Nose"
                        binding.textDescription.text = "Show off your perfume collection,\n" +
                                "test your fragrance knowledge, and climb the ranks!"
                    }
                    else -> binding.textViewPager.text = "Scan, discover and find \n" +
                            " your perfect scent in \n" +
                            "seconds"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            userScrollStarted = state == ViewPager2.SCROLL_STATE_DRAGGING
            if (!userScrollStarted) {
                lastUserScrollPosition = binding.viewPager2.currentItem
            }
        }
    }
}