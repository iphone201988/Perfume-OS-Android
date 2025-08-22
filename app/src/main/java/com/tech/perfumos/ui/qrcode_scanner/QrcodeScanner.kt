package com.tech.perfumos.ui.qrcode_scanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants

import com.tech.perfumos.databinding.ActivityQrcodeScannerBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.base.permission.PermissionHandler
import com.tech.perfumos.ui.base.permission.Permissions
import com.tech.perfumos.ui.quiz.JoinPlayerActivity
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.showErrorToast

import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean


@AndroidEntryPoint
class QrcodeScanner : BaseActivity<ActivityQrcodeScannerBinding>() {
    val viewmodel: QrcodeScannerVm by viewModels()
    private lateinit var cameraExecutor: ExecutorService
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private var cameraControls: Camera? = null
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private var microQRCodeType = 0
    private var isScanning = true
    private var codeScan: String? = null

    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override fun getLayoutResource(): Int {
        return R.layout.activity_qrcode_scanner
    }

    override fun getViewModel(): BaseViewModel {
        return viewmodel
    }

    override fun onCreateView() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        checkPermissions()
        clickListener()
        initObserver();
    }

    private fun initObserver() {
        viewmodel.commonObserver.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading..")
                    isScanning = false
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        Constants.JOIN_QUIZ_API -> {
                            try {
                                Log.d("response", "RECENT_TOP_PERFUME_API: ${Gson().toJson(it)}")
                                val jsonObject = JSONObject(it.data.toString())
                                Log.d("ERROR", "initObserver: $jsonObject")

                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    showToast(jsonObject.getString("message").toString())

                                    val intent =
                                        Intent(this, JoinPlayerActivity::class.java).apply {
                                            putExtra("join", "2")
                                            putExtra("roomID", codeScan)
                                        }
                                    startActivity(intent)

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
                    try {
                        isScanning = true;
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

    private fun clickListener() {
        viewmodel.onClick.observe(this) {
            when (it?.id) {


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

    var isFlashOn = false
    private fun checkPermissions() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Please Grant Permission.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** start scan code ***/
    private fun startCamera() {
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            cameraProviderFuture.addListener({
                cameraProvider = cameraProviderFuture.get()
                preview = Preview.Builder().build()
                binding.viewFinder.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

                val builder = ImageAnalysis.Builder().setTargetResolution(Size(1080, 1920))
                imageAnalysis = builder.build()

                imageAnalysis.setAnalyzer(
                    cameraExecutor,
                    QrCodeAnalyzer(microQRCodeType) { barcode ->
                        if (!isScanning) return@QrCodeAnalyzer // Return early if scanning is stopped

                        Log.d("BarcodeLog", "$barcode:")
                        codeScan = barcode.toString().trim()
                        val hashMap = hashMapOf<String, Any>(
                            "roomId" to codeScan.toString()
                        )
                        viewmodel.sendInvite(Constants.JOIN_QUIZ_API, hashMap)

                    })

                try {
                    cameraProvider.unbindAll()
                    cameraControls =
                        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(this))
        } catch (e: Exception) {
            e.printStackTrace()
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
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/*"
        galleryIntent.launch(i)
    }

    private var galleryIntent: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {

                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {


                    scanQrCodeFromUri(this, selectedImageUri, 1) { result ->
                        if (result.isNotEmpty()) {

                            Log.d("BarcodeLog", "$result:")
                            codeScan = result.toString().trim()
                            val hashMap = hashMapOf<String, Any>(
                                "roomId" to codeScan.toString()
                            )
                            viewmodel.sendInvite(Constants.JOIN_QUIZ_API, hashMap)
                            // Use the scanned data here
                        } else {
                            Log.d("QRScan", "No QR code detected")
                            showErrorToast("No QR code detected")
                        }
                    }
                }

            }
        }

    private fun getOptions(type: Int): com.google.mlkit.vision.barcode.BarcodeScannerOptions {
        return when (type) {
            1 -> {
                // Micro QR Code
                com.google.mlkit.vision.barcode.BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE,
                        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_PDF417,
                        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_AZTEC,
                        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_DATA_MATRIX
                    ).build()
            }

            else -> {
                // Default
                com.google.mlkit.vision.barcode.BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE,
                        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_AZTEC,
                        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_PDF417,
                        com.google.mlkit.vision.barcode.common.Barcode.FORMAT_DATA_MATRIX
                    ).build()
            }
        }
    }

    fun scanQrCodeFromUri(
        context: Context,
        uri: Uri,
        barcodeType: Int,
        callback: (String) -> Unit
    ) {
        try {
            // Convert URI to Bitmap
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                // Prepare InputImage
                val image = InputImage.fromBitmap(bitmap, 0)

                // Set scanner options (like your QrCodeAnalyzer)
                val scanner = BarcodeScanning.getClient(getOptions(barcodeType))

                // Process the image
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            callback(barcode.rawValue ?: "")
                        }
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        callback("")
                    }
            } else {
                callback("")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback("")
        }
    }

}