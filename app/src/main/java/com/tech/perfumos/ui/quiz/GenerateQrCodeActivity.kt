package com.tech.perfumos.ui.quiz

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.data.api.Constants.RECENT_TOP_PERFUME_API
import com.tech.perfumos.databinding.ActivityGenerateQrCodeBinding
import com.tech.perfumos.ui.auth_folder.login_folder.LoginActivity
import com.tech.perfumos.ui.base.BaseActivity
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.ui.dashboad.model.SearchHistoryModel
import com.tech.perfumos.ui.quiz.model.QuizCategoryList
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.Utils
import com.tech.perfumos.utils.Utils.parseJson
import com.tech.perfumos.utils.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class GenerateQrCodeActivity : BaseActivity<ActivityGenerateQrCodeBinding>() {
    private val viewModel: QuizVm by viewModels()
    private var qrBitmap: Bitmap? = null
    private var quizData: QuizCategoryList? = null

    private lateinit var roomId: String
    private lateinit var roomId2: String
    override fun getLayoutResource(): Int {
        return R.layout.activity_generate_qr_code
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        Utils.screenFillView(this)
        clickListener()
        initObserver()
        Glide.with(this).asGif().load(R.drawable.bg_animation)  // Put your .gif in `res/drawable`
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.bgAnim)



        roomId = intent.getStringExtra("roomId").toString()
        roomId2=roomId
        qrBitmap = generateQrCodeTransparentBg(roomId)
        binding.ivQrCode.setImageBitmap(qrBitmap)
        /*quizData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("quizType", QuizCategoryList::class.java)
        } else {
            intent.getSerializableExtra("quizType") as QuizCategoryList?
        }*/
        binding.apply {
            val formatedRoomId = if (roomId.length > 3) {
                roomId.chunked(3).joinToString(" ")
            } else {
                roomId
            }
            tvCode.text = formatedRoomId
        }

    }

    private fun setData() {

    }

    private fun clickListener() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_btn -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.playButton -> {
                    val intent =
                        Intent(this, JoinPlayerActivity::class.java).apply {
                            putExtra("join", "1")
                            putExtra("roomID",roomId2)
                        }
                    startActivity(intent)
//
//                    val hashMap = hashMapOf<String, Any>(
//                        "roomId" to roomId2
//                    )
//                    viewModel.sendInvite(Constants.JOIN_QUIZ_API, hashMap)
                }

                R.id.tvSave -> {
                    /*  val intent = Intent(this, JoinPlayerActivity::class.java).apply {
                          putExtra("join", false)
                          putExtra("quizType", quizData)
                      }
                      startActivity(intent)*/

                    val success = saveQrCodeToGallery(this, qrBitmap!!)
                    if (success) {
                        showToast("QR code saved to gallery!")
                    } else {
                        showErrorToast("Failed to save QR code.")
                    }
                }

                R.id.tvShareQrCode -> {
                    if (qrBitmap != null) {
                        shareQrBitmap(
                            this,
                            "Think you know perfumes?\\nI challenge you to a perfume quiz! \uD83C\uDFC6\\n\\nJoin my quiz room with this PIN: ${roomId}\nOr scan the QR code to jump right in.\\n\\nLet’s see if you can beat my score! \uD83D\uDCAA✨",
                            qrBitmap
                        )
                    } else {
                        showErrorToast("QrCode is now valid")
                    }
                }

                R.id.clTabToCopy -> {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("label", binding.tvCode.text)
                    clipboard.setPrimaryClip(clip)
                    showToast("Text copied to clipboard!")
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
                                            putExtra("join", "1")
                                            putExtra("roomID",roomId2)
                                        }
                                    startActivity(intent)

                                } else {
                                    showErrorToast(jsonObject.getString("message").toString())
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        RECENT_TOP_PERFUME_API -> {
                            try {
                                Log.d("response", "RECENT_TOP_PERFUME_API: ${Gson().toJson(it)}")
                                val data: SearchHistoryModel? = parseJson(it.data.toString())
                                Log.d("response", "RECENT_TOP_PERFUME_API : ${data?.success}")

                                if (data?.data != null) {

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

    private fun generateQrCode(content: String): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            // 400x400 size; adjust size as needed
            barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 400, 400)
        } catch (e: Exception) {
            null
        }
    }

    private fun generateQrCodeTransparentBg(content: String): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            val size = 600 // width and height
            val black = Color.BLACK
            val white = Color.WHITE

            // Generate normal QR bitmap
            val qrBitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, size, size)
            // Make background transparent
            val transparentBitmap =
                Bitmap.createBitmap(qrBitmap.width, qrBitmap.height, Bitmap.Config.ARGB_8888)
            for (x in 0 until qrBitmap.width) {
                for (y in 0 until qrBitmap.height) {
                    val pixel = qrBitmap.getPixel(x, y)
                    if (pixel == white) {
                        transparentBitmap.setPixel(x, y, Color.TRANSPARENT)
                    } else { // black or other
                        transparentBitmap.setPixel(x, y, black)
                    }
                }
            }
            transparentBitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun shareQrBitmap(activity: Activity, message: String, qrBitmap: Bitmap?) {
        if (qrBitmap == null) {
            showErrorToast("QR Code is not valid")
            return
        }
        // Save bitmap to cache
        val cachePath = File(activity.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "qr_image.png")
        FileOutputStream(file).use { out ->
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val qrUri: Uri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.fileProvider",
            file
        )

        // Create share Intent with image and text message
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            //type = "image/*"
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, qrUri)
            putExtra(Intent.EXTRA_TEXT, message)  // Add your message here
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
    }

    fun shareCustom(text: String, qrBitmap: Bitmap?) {
        val apps = listOf(
            "com.whatsapp" to "WhatsApp",
            "com.google.android.apps.dynamite" to "Google Chat",
            "com.instagram.android" to "Instagram"
        )

        val dialog = AlertDialog.Builder(this)
            .setTitle("Share via")
            .setItems(apps.map { it.second }.toTypedArray()) { _, which ->
                val pkg = apps[which].first

                when (pkg) {
                    "com.whatsapp" -> { // WhatsApp
                        // WhatsApp sometimes ignores text with image
                        // Option: send text-only first
                        shareToApp(this, qrBitmap, text, pkg)
                    }

                    "com.instagram.android" -> { // Instagram
                        // Instagram ignores text — copy text to clipboard
                        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("caption", text))
                        showToast("Caption copied. Paste in Instagram.")

                        shareToApp(this, qrBitmap, text, pkg)
                    }

                    else -> {
                        shareToApp(this, qrBitmap, text, pkg)
                    }
                }
            }
            .show()
    }

    fun shareToApp(activity: Activity, qrBitmap: Bitmap?, message: String, appPackage: String) {

        if (qrBitmap == null) {
            showErrorToast("QR Code is not valid")
            return
        }
        // Save bitmap to cache
        val cachePath = File(activity.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "qr_image.png")
        FileOutputStream(file).use { out ->
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val qrUri: Uri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, qrUri)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage(appPackage) // Send directly to that app
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            showErrorToast("App not installed")
        }
    }


    fun saveQrCodeToGallery2(context: Context, bitmap: Bitmap): Boolean {
        val filename = "QRCode_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.png"
        var fos: OutputStream? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above (Scoped Storage)
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/PerfumeOs"
                    ) // Optional subfolder
                }
                val imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            } else {
                // For Android 9 and below
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/YourAppFolder")
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }
                val image = java.io.File(imagesDir, filename)
                fos = java.io.FileOutputStream(image)
            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
    fun saveQrCodeToGallery(context: Context, bitmap: Bitmap): Boolean {
        val filename = "QRCode_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.png"

        // Create a bitmap with white background
        val whiteBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(whiteBitmap)
        canvas.drawColor(Color.WHITE)        // Fill white background
        canvas.drawBitmap(bitmap, 0f, 0f, null)  // Draw QR code on top

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ Scoped Storage
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/PerfumeOs"
                    )
                }
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                imageUri?.let {
                    resolver.openOutputStream(it)?.use { fos ->
                        whiteBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    }
                }
            } else {
                // Android 9 and below
                val imagesDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PerfumeOs")
                if (!imagesDir.exists()) imagesDir.mkdirs()

                val image = File(imagesDir, filename)
                FileOutputStream(image).use { fos ->
                    whiteBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }

                // Make image visible in gallery
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(image.absolutePath),
                    arrayOf("image/png"),
                    null
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}