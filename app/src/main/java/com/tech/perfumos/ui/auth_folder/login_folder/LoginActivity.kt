package com.tech.perfumos.ui.auth_folder.login_folder


import android.app.Activity
import android.content.Intent
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.LOGIN_API
import com.tech.perfumos.data.api.Constants.SOCIAL_LOGIN_API
import com.tech.perfumos.databinding.ActivityLoginBinding
import com.tech.perfumos.ui.auth_folder.account_create_folder.CreateAccountSuccess
import com.tech.perfumos.ui.auth_folder.create_folder.SignUpActivity
import com.tech.perfumos.ui.auth_folder.forget_password.ForgetPassword
import com.tech.perfumos.ui.auth_folder.model.LoginModel
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.dashboad.DashboardActivity
import com.tech.perfumos.ui.onboarding_folder.OnboardingActivity
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    val viewmodel: LoginActivityVm by viewModels()
    private var isPasswordVisible = false
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 123

    private var fcmToken: String? = null
    var provider = ""

    override fun getLayoutResource(): Int {
        return R.layout.activity_login
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    override fun onCreateView() {
        handleIntent(intent)
        Utils.screenFillView(this)
        clickListener()
        initObserver()
        Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            fcmToken = token
            Log.d("fcmToken", "onCreateView: $fcmToken")
        }
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            val message = it.getStringExtra("from")
            // Update your UI or perform actions with the new data
            if (message == "welcome") {
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
        }
    }

    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.createNewAccount -> {

                    startActivity(Intent(this, SignUpActivity::class.java))
                }

                R.id.ivShow -> {
                    isPasswordVisible = !isPasswordVisible
                    setPasswordVisibility()
                }

                R.id.forgetPassword -> {
                    startActivity(Intent(this, ForgetPassword::class.java))
                }

                R.id.google_id_card -> {
                    signInGoogle()
                }

                R.id.loginButton -> {
                    /*sharedPrefManager.saveUserToken("sacasc")
                    sharedPrefManager.saveBoardingStep(8)
                   startActivity(Intent(this, DashboardActivity::class.java))*/

                    if (validation()) {
                        val requestMap = hashMapOf<String, Any>(
                            "username" to binding.username.text.toString(),
                            "password" to binding.password.text.toString(),
                            "deviceToken" to fcmToken.toString(),
                            "deviceType" to "2",
                        )
                        viewmodel.loginApi(LOGIN_API, requestMap)
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
                        LOGIN_API -> {
                            try {
                                Log.d("LoginModel", "initObserver: ${Gson().toJson(it)}")
                                val data: LoginModel? = Utils.parseJson(it.data.toString())
                                Log.d("userId", "initObserver: ${data?.success} userId = ${data?.data?.id}")
                                if (data?.success == true) {
                                    val userData = data.data

                                    sharedPrefManager.saveUserToken(userData?.token.toString())
                                    sharedPrefManager.saveUserData(data.data?.id.toString())
                                    if (data.data != null) {
                                        sharedPrefManager.saveUser(data.data!!)
                                    }
                                    sharedPrefManager.saveBoardingStep(userData?.step ?: 0)

                                    if (userData?.tutorialProgess.toString() < "9") {
                                        sharedPrefManager.setOnboardingCompleteBool(false)
                                    } else {
                                        sharedPrefManager.setOnboardingCompleteBool(true)
                                    }

                                    if (userData?.step == 8) {
                                        Utils.routeToHomeDashboardActivity = 1
                                        startActivity(Intent(this, DashboardActivity::class.java))
                                    } else {

                                        startActivity(Intent(this, OnboardingActivity::class.java))
                                    }
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        SOCIAL_LOGIN_API -> {
                            try {
                                Log.d("SOCIAL_LOGIN_API", "initObserver: ${Gson().toJson(it)}")
                                val data: LoginModel? = Utils.parseJson(it.data.toString())
                                Log.d("SOCIAL_LOGIN_API", "initObserver: ${data?.success}")
                                if (data?.success == true) {
                                    val userData = data.data

                                    sharedPrefManager.saveUserToken(userData?.token.toString())
                                    sharedPrefManager.saveUserData(data.data?.id.toString())
                                    if (data.data != null) {
                                        sharedPrefManager.saveUser(data.data!!)
                                    }
                                    sharedPrefManager.saveBoardingStep(userData?.step ?: 0)

                                    if (userData?.tutorialProgess.toString() < "9") {
                                        sharedPrefManager.setOnboardingCompleteBool(false)
                                    } else {
                                        sharedPrefManager.setOnboardingCompleteBool(true)
                                    }
                                    if (provider == "") {


                                        if (userData?.step == 8) {
                                            Utils.routeToHomeDashboardActivity = 1
                                            startActivity(
                                                Intent(
                                                    this,
                                                    DashboardActivity::class.java
                                                )
                                            )
                                        } else {
                                            startActivity(
                                                Intent(
                                                    this,
                                                    OnboardingActivity::class.java
                                                )
                                            )
                                        }
                                    } else {
                                        if (userData?.step == 0) {
                                            startActivity(
                                                Intent(
                                                    this,
                                                    CreateAccountSuccess::class.java
                                                )
                                            )
                                            finish()
                                        } else {
                                            if (userData?.step == 8) {
                                                Utils.routeToHomeDashboardActivity = 1
                                                startActivity(
                                                    Intent(
                                                        this,
                                                        DashboardActivity::class.java
                                                    )
                                                )
                                            } else {
                                                startActivity(
                                                    Intent(
                                                        this,
                                                        OnboardingActivity::class.java
                                                    )
                                                )
                                            }
                                        }
                                    }
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
                        val message = jsonObject.getString("message").toString()
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

    private fun setPasswordVisibility() {
        if (isPasswordVisible) {
            // Show password
            binding.password.transformationMethod = null
            binding.ivShow.setImageResource(R.drawable.icon_eye_hide)
        } else {
            // Hide password
            binding.password.transformationMethod = PasswordTransformationMethod()
            binding.ivShow.setImageResource(R.drawable.icon_eye)
        }
        // Move cursor to the end of the text
        binding.password.setSelection(binding.password.text?.length!!)
    }


    private fun validation(): Boolean {
        if (binding.username.text.toString().isNullOrEmpty()) {
            //binding.username.setError("Please enter username")
            showErrorToast("Please enter username")
            return false
        } else{
            if(binding.username.text.toString().contains(" ")){
                showErrorToast("Username should not contains space")
                return false
            }
        }
        if (binding.password.text.isNullOrEmpty()) {
            //binding.password.setError("Please enter password")
            showErrorToast("Please enter password")
            return false
        }
        else if (binding.password.text.toString().contains(" ")) {
            //binding.password.setError("Please enter password")
            showErrorToast("Empty space are not allowed in the password filed")
            return false
        }
        return true
    }


    private fun signInGoogle() {
        provider = "google"
        googleSignInClient.signOut()
        /*   val signInIntent = googleSignInClient.signInIntent
           startActivityForResult(signInIntent, RC_SIGN_IN)*/

        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
            Log.d("resultGoogle", ": $result")
        }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            if (account != null) {

                val requestMap = hashMapOf<String, Any>(
                    "email" to account.email.toString(),
                    "socialId" to account.id.toString(),
                    "provider" to provider,
                    "fullname" to account.displayName.toString(),
                    "deviceToken" to fcmToken.toString(),
                    "deviceType" to "2",
                )
                viewmodel.socialLoginApi(SOCIAL_LOGIN_API, requestMap)

            } else {
                showErrorToast("Something went wrong")
            }

        } catch (e: ApiException) {
            showToast(e.message.toString())
            //updateUI(null)
        }
    }

}