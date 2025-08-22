package com.tech.perfumos.ui.splash

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.tech.perfumos.R
import com.tech.perfumos.databinding.ActivityWelcomeBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.dashboad.DashboardActivity
import com.tech.perfumos.ui.get_started_folder.GetStartedActivity
import com.tech.perfumos.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {

    private val viewModel: WelcomeActivityVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_welcome
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        lifecycleScope.launch {
            delay(3000)

            if (sharedPrefManager.getUserToken() != null) {
                if (sharedPrefManager.getBoardingStep() == 8) {
                    Utils.routeToHomeDashboardActivity = 0
                    startActivity(Intent(this@WelcomeActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    //startActivity(Intent(this@WelcomeActivity, OnboardingActivity::class.java))
                    val intent = Intent(this@WelcomeActivity, LoginActivity::class.java).apply {
                        putExtra("from", "welcome")
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                }
            } else {
                startActivity(Intent(this@WelcomeActivity, GetStartedActivity::class.java))
                finish()
            }
        }
    }
}