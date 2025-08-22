package com.tech.perfumos.ui.auth_folder.create_folder


import android.content.Intent
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.util.Util
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.SIGN_UP_API
import com.tech.perfumos.databinding.ActivitySignUpBinding
import com.tech.perfumos.ui.auth_folder.account_create_folder.CreateAccountSuccess
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.auth_folder.model.SignUpModel
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class SignUpActivity : BaseActivity<ActivitySignUpBinding>() {
    val viewmodel: SignUpVm by viewModels()
    private var isPasswordVisible = false
    private var isConfPasswordVisible = false

    var fcmToken:String  = ""
    override fun getLayoutResource(): Int {
        return R.layout.activity_sign_up
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        clickListener()
        initObserver()
        Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            fcmToken = token
        }
        Log.d("fmcToken", "getDeviceToken: ${fcmToken}")
    }

    private fun initObserver() {
        viewmodel.commonObserver.observe(this){
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading..")
                }
                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        SIGN_UP_API -> {
                            try {
                                Log.d("SignUpModel", "initObserver: ${Gson().toJson(it)}")
                                val data: SignUpModel? = Utils.parseJson(it.data.toString())
                                Log.d("SignUpModel", "initObserver: ${data?.success}")
                                if (data?.success == true) {
                                    if(data.data != null){
                                        sharedPrefManager.saveUser(data.data!!)
                                    }
                                    if(data.data?.tutorialProgess.toString() < "9"){
                                        sharedPrefManager.setOnboardingCompleteBool(false)
                                    }else{
                                        sharedPrefManager.setOnboardingCompleteBool(true)
                                    }

                                    sharedPrefManager.saveUserToken(data.data?.token.toString())
                                    sharedPrefManager.saveUserData(data.data?.id.toString())
                                    sharedPrefManager.saveBoardingStep(data.data?.step ?: 0)
                                    startActivity(Intent(this, CreateAccountSuccess::class.java))
                                    finish()
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
                else ->{

                }
            }
        }
    }

    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.resendTv2 -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                R.id.ivShow -> {
                    isPasswordVisible = !isPasswordVisible
                    setPasswordVisibility()
                }

                R.id.ivConfShow -> {
                    isConfPasswordVisible = !isConfPasswordVisible
                    setCOnfPasswordVisibility()
                }


                R.id.getVerifyLayout -> {
                    //startActivity(Intent(this, CreateAccountSuccess::class.java))

                    if(validation()) {
                        val requestMap = hashMapOf<String, Any>(
                            "email" to binding.emailAddress.text.toString().trim().lowercase(),
                            "password" to binding.password.text.toString(),
                            "username" to binding.username.text.toString(),
                            "fullname" to binding.fullname.text.toString().trim(),
                            "deviceToken" to fcmToken,
                            "deviceType" to 2
                        )
                        viewmodel.signUpApi(SIGN_UP_API, requestMap)
                    }
                }
                R.id.back_btn -> {
                    finish()
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

    private fun setCOnfPasswordVisibility() {

        if (isConfPasswordVisible) {
            // Show password
            binding.passwordConfirm.transformationMethod = null
            binding.ivConfShow.setImageResource(R.drawable.icon_eye_hide)
        } else {
            // Hide password
            binding.passwordConfirm.transformationMethod = PasswordTransformationMethod()
            binding.ivConfShow.setImageResource(R.drawable.icon_eye)
        }
        // Move cursor to the end of the text
        binding.passwordConfirm.setSelection(binding.passwordConfirm.text?.length!!)
    }

    private fun validation(): Boolean{

        Log.d(
            "validation_email",
            "validation: ${
                Patterns.EMAIL_ADDRESS.matcher(binding.emailAddress.text.toString()).matches()
            }"
        )
        if(binding.username.text.isNullOrEmpty()){
            showErrorToast("Please enter username")
            return false
        }
        else{
            val (isValid, errorMessage) = isValidUsername(binding.username.text.toString())
            if (!isValid) {
                showErrorToast(errorMessage ?: "Invalid username.")
                return false
            }
        }

        if(binding.username.text.toString().contains(" ")){
            showErrorToast("Username should not contains space")
            return false
        }
        else if(binding.fullname.text?.toString()?.trim().isNullOrEmpty()){
            showErrorToast("Please enter fullname")
            return false
        }else{
            val (isValid, errorMessage) = validateFullName(binding.fullname.text.toString().trim())
            if (!isValid) {
                showErrorToast(errorMessage ?: "Invalid Full Name.")
                return false
            }
        }
        if(binding.emailAddress.text.isNullOrEmpty() || binding.emailAddress.text.toString().length > 254  || !Patterns.EMAIL_ADDRESS.matcher(binding.emailAddress.text.toString()).matches()){
            showErrorToast("Please enter valid email address")
            return false
        }
        else if(binding.password.text.isNullOrEmpty()){
            showErrorToast("Please enter password")
            return false
        }
        else if(binding.passwordConfirm.text.isNullOrEmpty()){
            showErrorToast("Please enter confirm password")
            return false
        }
        else if (!binding.password.text.toString()
                .equals(binding.passwordConfirm.text.toString())
        ) {
            showErrorToast("Password and confirm password do not match. ")
            return false
        }
        /*else if(binding.username.text.toString().length < 3){
            showErrorToast("Username must be the greater then 3 characters")
            return false
        }*/
        /*else if(!isValidUsername(binding.username.text.toString())){
            showErrorToast("Contains invalid characters in username")
            return false
        }*/

        return true
    }
    /*fun isValidUsername(username: String): Boolean {
        val regex = Regex("^[a-zA-Z][a-zA-Z0-9@$!%*?&_-]{3,}$")
        return regex.matches(username)
    }*/

    private fun isValidUsername(username: String): Pair<Boolean, String?> {
        if (username.isEmpty()) {
            return Pair(false, "Username cannot be empty.")
        }
        if (!username[0].isLetter()) {
            return Pair(false, "Username should start with an alphabet.")
        }
        if (username.length < 4) {
            return Pair(false, "Username must be at least 4 characters long.")
        }
        if (!username.matches(Regex("^[a-zA-Z][a-zA-Z0-9@$!%*?&_-]*$"))) {
            return Pair(false, "Username contains invalid characters.")
        }
        return Pair(true, null)
    }

    private fun validateFullName(fullName: String): Pair<Boolean, String?> {
        if (fullName.length < 2 || fullName.length > 50) {
            return Pair(false, "Full Name must be between 2 and 50 characters.")
        }
        if (fullName.any { it.isDigit() }) {
            return Pair(false, "Full Name should not contain numbers.")
        }
        if (!fullName.all { it.isLetter()  || it.isWhitespace() }) {
            return Pair(false, "Full Name should not contain special characters.")
        }

        return Pair(true, null) // Name is valid
    }
}