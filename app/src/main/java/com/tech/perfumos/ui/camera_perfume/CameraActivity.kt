package com.tech.perfumos.ui.camera_perfume


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tech.perfumos.BuildConfig
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants.PERFUME_API
import com.tech.perfumos.databinding.ActivityCameraBinding
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.permission.PermissionHandler
import com.tech.perfumos.ui.base.permission.Permissions
import com.tech.perfumos.ui.camera_perfume.model.PerfumeInfoModel
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.Utils.uriToBitmap
import com.tech.perfumos.utils.showErrorToast
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.File
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraActivity : BaseActivity<ActivityCameraBinding>() {

    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraProvider: ProcessCameraProvider
    private var cameraControls: Camera? = null
    var isFlashOn = false

    private var imageUri: Uri? = null
    private var filePath: String? = null

    val viewmodel: CameraVm by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.activity_camera
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        initView()
        clickListener()
        initObserver()

       /* binding.ivGif.visibility = View.VISIBLE
        Glide.with(this)
            .asGif()
            .load(R.drawable.logo_gif)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivGif)*/

    }

    private fun initView() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }


    }

    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {
                R.id.captureButton -> {
                    takePhoto()
                    /*cameraCaptureAnim()
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this@CameraActivity, PerfumeInfoActivity::class.java))
                        finish()
                    }, 4000)*/
                }

                R.id.iv_gallery -> {
                    openGallery()
                }

                R.id.iv_close -> {
                    finish()
                }

                R.id.iv_flash -> {
                    if (cameraControls != null) {
                        if (isFlashOn) {
                            isFlashOn = false
                            cameraControls?.cameraControl?.enableTorch(false)
                            binding.ivFlash.setImageResource(R.drawable.ic_ion_flash_off)
                        } else {
                            isFlashOn = true
                            cameraControls?.cameraControl?.enableTorch(true)
                            binding.ivFlash.setImageResource(R.drawable.ic_ion_flash_on)
                        }
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
                            try {
                                Log.d("ProfileDataModel", "ProfileDataModel: ${Gson().toJson(it)}")
                                val data: PerfumeInfoModel? = parseJson(it.data.toString())

                                if (data?.success == true) {
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        val intent = Intent(this, PerfumeInfoActivity::class.java)
                                        intent.putExtra("perfumeInfo", data)
                                        startActivity(intent)
                                        finish()
                                    }, 2000)


                                } else {
                                    showErrorToast("Perfume not found, capture it properly")

                                    binding.ivCameraBg.setImageResource(R.drawable.camera_bg)
                                    binding.ivGif.visibility = View.GONE
                                    binding.captureImage.visibility = View.GONE
                                    startCamera()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()

                                binding.ivCameraBg.setImageResource(R.drawable.camera_bg)
                                binding.ivGif.visibility = View.GONE
                                binding.captureImage.visibility = View.GONE
                                startCamera()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    try {
                        Log.d("ERROR", "initObserver: ${Gson().toJson(it)}")
                        val jsonObject = JSONObject(it.data.toString())
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

    override fun onResume() {
        super.onResume()
        binding.ivFlash.setImageResource(R.drawable.ic_ion_flash_off)
    }


    private fun cameraCaptureAnim() {

        binding.ivCameraBg.setImageResource(R.drawable.camera_searching_bg)
        binding.ivGif.visibility = View.VISIBLE
        Glide.with(this)
            .asGif()
            .load(R.drawable.logo_gif)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivGif)

        /*Glide.with(this)
            .asGif()
            .load(R.drawable.shining_stars)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivGif1)

        Glide.with(this)
            .asGif()
            .load(R.drawable.shining_stars)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivGif2)

        Glide.with(this)
            .asGif()
            .load(R.drawable.shining_stars)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivGif3)

        Glide.with(this)
            .asGif()
            .load(R.drawable.shining_stars)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivGif4)*/


    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraControls = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )


            } catch (exc: Exception) {
                Toast.makeText(this, "Failed to bind camera use cases", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return


        val photoFile = File(
            getExternalFilesDir(null),
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("CameraActivity", "onImageSaved: Photo saved:  ${photoFile.absolutePath}")
                    val file = File(photoFile.absolutePath)
                    val uri = FileProvider.getUriForFile(
                        this@CameraActivity,
                        "${BuildConfig.APPLICATION_ID}.fileProvider",
                        file
                    )
                    Log.d("uri", "onImageSaved: $uri")
                    runOnUiThread {
                        cameraCaptureAnim()
                        cameraProvider.unbindAll()
                    }

                    val bitmap = uriToBitmap(uri, this@CameraActivity)
                    recognizeTextFromBitmap(bitmap, this@CameraActivity)
                    //showToast("Photo saved: ")
                    //startActivity(Intent(this@CameraActivity, PerfumeInfoActivity::class.java))
                    /*binding.ivCapture.visibility = View.VISIBLE

                    Glide.with(this@CameraActivity)
                        .asGif()
                        .load(R.drawable.shining_stars)  // Put your .gif in `res/drawable`
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.ivCapture)

                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.ivCapture.visibility = View.GONE
                        startActivity(Intent(this@CameraActivity, PerfumeInfoActivity::class.java))
                    }, 4000)*/


                }

                override fun onError(exc: ImageCaptureException) {
                    Log.d("CameraActivity", "onImageSaved: Photo saved:  ${exc.message}")
                    //showErrorToast("Photo capture failed: ${exc.message}")

                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
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
                Permissions.check(this, permission, 0, null, object : PermissionHandler() {
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
                //try {
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {

                    startCrop(selectedImageUri)

                    /* cropImage.launch(
                         CropImageContractOptions(
                             uri = selectedImageUri,
                             cropImageOptions = CropImageOptions(
                                 guidelines = CropImageView.Guidelines.ON,
                                 outputCompressFormat = Bitmap.CompressFormat.PNG
                             )
                         )
                     )*/
                }

                /* } catch (ex: Exception) {
                     Toast.makeText(this, "Image size is too large", Toast.LENGTH_SHORT).show()
                     ex.printStackTrace()
                 }*/
            }
        }

    /*private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the cropped image URI.
            val croppedImageUri = result.uriContent
            val croppedImageFilePath = result.getUriFilePath(this) // optional usage
            // Process the cropped image URI as needed.

            imageUri = croppedImageUri
            filePath = croppedImageFilePath

            Log.d("filePath", ":filePath -> ${filePath}")
        } else {
            // An error occurred.
            val exception = result.error
            // Handle the error.
            Log.d("filePath", ":filePath -> $exception")
        }
    }*/
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

    fun recognizeTextFromBitmap(bitmap: Bitmap, context: Context) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val extractedText = visionText.text
                Log.d("ExtractedText", extractedText)
                //Toast.makeText(context, extractedText, Toast.LENGTH_LONG).show()

                viewmodel.askChatGPTForPerfumeName(extractedText) { perfumeName ->
                    if (perfumeName != null) {
                        showToast(perfumeName)
                        Log.d("PerfumeName", "Detected perfume: $perfumeName")
                        val requestMap = hashMapOf<String, Any>(
                            "name" to perfumeName,
                            "isSearch" to true
                        )
                        viewmodel.getPerfumeApi(PERFUME_API, requestMap)
                    } else {
                        showErrorToast("Perfume not found, capture it properly")
                        Log.d("PerfumeName", "No perfume name found")

                        binding.ivCameraBg.setImageResource(R.drawable.camera_bg)
                        binding.ivGif.visibility = View.GONE
                        binding.captureImage.visibility = View.GONE
                        startCamera()
                    }
                }

            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                showErrorToast("Failed: ${e.message}")

                binding.ivCameraBg.setImageResource(R.drawable.camera_bg)
                binding.ivGif.visibility = View.GONE
                binding.captureImage.visibility = View.GONE
                startCamera()
            }
    }

    // Crop result
    val cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val resultUri = UCrop.getOutput(result.data!!)

                resultUri?.let {
                    Log.d("setImageURI", ": $it")
                    runOnUiThread {
                        cameraProvider.unbindAll()
                        cameraCaptureAnim()
                        binding.captureImage.apply {
                            visibility = View.VISIBLE
                            setImageURI(resultUri)
                        }
                    }


                    // imageView.setImageURI(it)
                    val bitmap = uriToBitmap(it, this)
                    recognizeTextFromBitmap(bitmap, this)
                }

                //startActivity(Intent(this@CameraActivity, PerfumeInfoActivity::class.java))
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
                cropError?.printStackTrace()
                Toast.makeText(this, "Crop failed: ${cropError?.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()  // <-- Now start camera after permission is granted
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}