package com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.BASE_URL_IMAGE
import com.tech.perfumos.data.api.Constants.UPDATE_PROFILE_API
import com.tech.perfumos.databinding.ActivityEditProfileBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.permission.PermissionHandler
import com.tech.perfumos.ui.base.permission.Permissions
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.UriToFile
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.showErrorToast
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


@AndroidEntryPoint
class EditProfile : BaseActivity<ActivityEditProfileBinding>() {
    val viewModel: EditProfileVm by viewModels()
    private var imageUri: Uri? = null
    private lateinit var photoFile: File

    override fun getLayoutResource(): Int {
        return R.layout.activity_edit_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    /*override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }*/

    override fun onCreateView() {
        Utils.screenFillView(this)
        initView()
        clickListener()
        initObserver()
        Glide.with(this)
            .asGif()
            .load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.bgAnim)
    }

    private fun initView() {
        val data = sharedPrefManager.getCurrentUser()
        binding.apply {
            nameEt.setText(data?.fullname.toString())
            edUsername.setText(data?.username.toString())
            emailAddressET.setText(data?.email.toString())
            Glide.with(this@EditProfile).load("$BASE_URL_IMAGE${data?.profileImage}")
                /*.placeholder(R.drawable.dummy_image)*/.error(R.drawable.dummy_image).into(userProfile)
        }
    }

    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    finish()
                }

                R.id.userProfile -> {
                    selectImage()
                }

                R.id.tvUpdate -> {

                    if(validation()){
                        updateProfile()
                    }
                    /*if (binding.nameEt.text.isNullOrEmpty()) {
                        showToast("Please enter name")
                    } else if (binding.edUsername.text.isNullOrEmpty()) {
                        showToast("Please enter name")
                    } else {
                        updateProfile()
                    }*/


                }
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
                        UPDATE_PROFILE_API -> {
                            try {
                                Log.d("response", "UPDATE_PROFILE_API: ${Gson().toJson(it)}")
                                /* val data: LoginModel? = Utils.parseJson(it.data.toString())
                                 Log.d("LoginModel", "initObserver: ${data?.success}")*/

                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: ${jsonObject}")

                                val success = jsonObject.getBoolean("success")
                                if(success){
                                    showToast( jsonObject.getString("message").toString())
                                    val intent = Intent()
                                    setResult(RESULT_OK, intent)
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

    private fun validation(): Boolean{

        if(binding.nameEt.text?.toString().isNullOrEmpty()){
            showErrorToast("Please enter fullname")
            return false
        }else{
            val (isValid, errorMessage) = validateFullName(binding.nameEt.text.toString().trim())
            if (!isValid) {
                showErrorToast(errorMessage ?: "Invalid Full Name.")
                return false
            }
        }

        if(binding.edUsername.text.isNullOrEmpty()){
            showErrorToast("Please enter username")
            return false
        }
        else{
            val (isValid, errorMessage) = isValidUsername(binding.edUsername.text.toString())
            if (!isValid) {
                showErrorToast(errorMessage ?: "Invalid username.")
                return false
            }
        }



    return true
    }

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

    private fun updateProfile() {
        var image: MultipartBody.Part? = null

        if (imageUri != null) {
            val filePath = UriToFile.getPathFromUri(this, imageUri!!)
            val file = File(filePath!!)
            val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file!!)
            image = MultipartBody.Part.createFormData("file", file.name, requestBody)

        } else {
            //return
        }

        val data = HashMap<String, RequestBody>()
        data["fullname"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), binding.nameEt.text.toString())
        if (binding.edUsername.text.toString() != sharedPrefManager.getCurrentUser()?.username.toString()) {
            data["username"] = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                binding.edUsername.text.toString()
            )
        }
        viewModel.updateProfile(UPDATE_PROFILE_API, data, image)
    }

    private fun startCrop(sourceUri: Uri) {
        val destinationFileName = "cropped_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, destinationFileName))

        val options = UCrop.Options().apply {
            setFreeStyleCropEnabled(true) // allow free drag to crop
            setHideBottomControls(true)
            setToolbarColor(resources.getColor(R.color.purple_500))
            setStatusBarColor(resources.getColor(R.color.purple_700))
            setActiveControlsWidgetColor(resources.getColor(R.color.teal_200))
        }

        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withMaxResultSize(1080, 1080)

        cropImageLauncher.launch(uCrop.getIntent(this))
    }

    // Crop result
    private val cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val resultUri = UCrop.getOutput(result.data!!)
                resultUri?.let {
                    Log.d("setImageURI", ": $it")
                    Glide.with(this)
                        .load(it)
                        .into(binding.userProfile)
                    //binding.userProfile.setImageURI(it)
                    imageUri = it
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
                cropError?.printStackTrace()
                Toast.makeText(this, "Crop failed: ${cropError?.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    private fun selectImage() {
        val options = arrayOf<CharSequence>(
            "Take Photo",
            "Choose From Gallery",
            "Cancel"
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo")
        builder.setItems(
            options
        ) { dialog: DialogInterface, item: Int ->
            if (options[item] == "Take Photo") {
                openCamera()
                dialog.dismiss()
            } else if (options[item] == "Choose From Gallery") {
                openGallery()
                dialog.dismiss()
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }

        }
        builder.show()
    }


    private fun openCamera() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Permissions.check(this, Manifest.permission.CAMERA, 0, object : PermissionHandler() {
                override fun onGranted() {
                    openMediaIntent()
                }
            })
        } else {
            openMediaIntent()
        }
    }


    private fun openGallery() {
        val permission: Array<String>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.CAMERA
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Permissions.check(this, permission, "0", null, object : PermissionHandler() {
                    override fun onGranted() {
                        openGalleryIntent()
                    }
                })
            } else {
                openGalleryIntent()
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Permissions.check(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    0,
                    object : PermissionHandler() {
                        override fun onGranted() {
                            openGalleryIntent()
                        }
                    })
            } else {
                openGalleryIntent()
            }
        }
    }

    private fun openMediaIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = Utils.createImageFile(this)
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            photoFile
        )

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        cameraLauncher.launch(cameraIntent)
        /////////////////////////////////////
        /*val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = ImageUtils.createImageFile(this)
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            photoFile
        )
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        cameraLauncher.launch(cameraIntent)*/

    }

    private var cameraLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode === Activity.RESULT_OK) {
                try {
                    val selectedImageUri: Uri = photoFile.absoluteFile.toUri()
                    startCrop(selectedImageUri)

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                /*try {
                    val photo = result.data?.extras?.get("data") as Bitmap
                    val selectedImageUri = result.data?.data

                    if (photo != null) {
                        val file = ImageUtils.saveBitmapInCache(this, photo)
                        imageUri = file?.absoluteFile?.toUri()!!
                        //binding.ivimage.setImageBitmap(photo)
                        uploadPhoto()
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }*/
            }
        }

    private fun openGalleryIntent() {
        //val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val i = Intent()
        i.type = "image/*"
        i.putExtra(Intent.ACTION_PICK, true)
        i.action = Intent.ACTION_GET_CONTENT
        galleryIntent.launch(i)
    }

    private var galleryIntent: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val selectedImageUri: Uri? = result.data?.data
                    if (selectedImageUri != null) {
                        startCrop(selectedImageUri)
                    }


                } catch (ex: Exception) {
                    Toast.makeText(this, "Image size is too large", Toast.LENGTH_SHORT).show()
                    ex.printStackTrace()
                }
            }
        }

}