package com.tech.perfumos.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageDecoder
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.ui.dashboad.fragment_folder.chat_folder.ChatModel
import com.tech.perfumos.ui.quiz.model.SubmitQuizModel
import com.tech.perfumos.utils.blur_folder.BlurView
import com.tech.perfumos.utils.event.SingleActionEvent
import java.io.File
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.days

object Utils {
    var routeToHomeDashboardActivity: Int =
        0 // welcome =0, login =1,OnboardingActivity =2,ArticleContent=3
    val updateTheTutorial = SingleActionEvent<Int>()
    val lastApiCall = SingleActionEvent<Int>()
    val targetViews = mutableListOf<View>()
    lateinit var bottomAppBar: BottomAppBar
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var fabCamera: FloatingActionButton
    var isLogin: Boolean = false

    var navGraphHeight: Float = 0.0f
    var navGraphWidth: Float = 0.0f

    fun View.preventMultipleClick() {
        isEnabled = false
        postDelayed({
            try {
                isEnabled = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 510)
    }

    fun createRoundedBackground(
        context: Context, color: Int, cornerRadiusDp: Float
    ): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.setColor(color)
        drawable.cornerRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, cornerRadiusDp, context.resources.displayMetrics
        )
        return drawable
    }

    var route: Int = 1
    var isCompleted: Boolean = false

    @BindingAdapter("imageGifImage")
    @JvmStatic
    fun imageGifImage(view: ImageView, colorResId: Boolean?) {
//        Glide.with(view.context)
//            .asGif()
//            .load(R.raw.down_arrow)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .into(view)
    }

    @BindingAdapter("drawableTintCompat")
    @JvmStatic
    fun drawableTintCompat(textView: TextView, color: Int) {
        textView.compoundDrawableTintList = ColorStateList.valueOf(color)
    }


//    @BindingAdapter("drawableTintCompatColor")
//    @JvmStatic
//    fun drawableTintCompatColor(textView: TextView, value: SubmitQuizModel) {
//        if (value.status == "fail") {
//            textView.compoundDrawableTintList =
//                ContextCompat.getColorStateList(textView.context, R.color.trophy_color)
//        } else {
//            textView.compoundDrawableTintList =
//                ContextCompat.getColorStateList(textView.context, R.color.trophy_color)
//        }
//    }

    @JvmStatic
    @BindingAdapter("bindPoints")
    fun bindPoints(textView: TextView, bean: SubmitQuizModel?) {
        bean ?: return

        if (bean.status == "fail") {
            // ❌ Fail → show negative points
            textView.text = "${bean.pointsEarned}"
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.red_color))
        } else {
            // ✅ Pass → show positive points
            textView.text = "${bean.pointsEarned}"
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.Pass_color))
        }
    }

    @JvmStatic
    @BindingAdapter("drawableTintCompatColor")
    fun drawableTintCompatColor(textView: TextView, bean: SubmitQuizModel?) {
        bean ?: return

        if (bean.status == "fail") {
            // ❌ Fail case
            textView.text = "Fail"
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.red_color))
            textView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_cross_white, 0, 0, 0
            )
            textView.compoundDrawableTintList =
                ContextCompat.getColorStateList(textView.context, R.color.red_color)
        } else {
            // ✅ Pass case
            textView.text = "Pass"
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.Pass_color))
            textView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_tick, 0, 0, 0
            )
            textView.compoundDrawableTintList =
                ContextCompat.getColorStateList(textView.context, R.color.Pass_color)
        }
    }

    @BindingAdapter("backgroundColorLayout")
    @JvmStatic
    fun backgroundColorLayout(view: LinearLayout, @ColorRes colorResId: Int?) {
        val context = view.context
        val color = try {
            if (colorResId != null && colorResId != 0) {
                ContextCompat.getColor(context, colorResId)
            } else {
                ContextCompat.getColor(context, R.color.trophy_color)
            }
        } catch (_: Exception) {
            ContextCompat.getColor(context, R.color.trophy_color)
        }

        val bgDrawable = createRoundedBackground(
            context, color, 5f
        )
        view.background = bgDrawable
    }

    @BindingAdapter("trophyTextColor")
    @JvmStatic
    fun trophyTextColor(view: TextView, pos: Int) {
        val context = view.context
        val color = if (pos < 3) {
            ContextCompat.getColor(context, R.color.bottom_bar_color)
        } else {
            ContextCompat.getColor(context, R.color.trophy_color3)
        }
        view.setTextColor(color)
    }

    @BindingAdapter("trophyLayout")
    @JvmStatic
    fun trophyLayout(view: LinearLayout, pos: Int) {
        val context = view.context
        val color = when (pos) {
            0 -> {
                ContextCompat.getColor(context, R.color.trophy_color1)

            }

            1 -> {
                ContextCompat.getColor(context, R.color.trophy_color2)

            }

            2 -> {
                ContextCompat.getColor(context, R.color.trophy_color3)

            }

            else -> {
                ContextCompat.getColor(context, R.color.trophy_color)
            }
        }


        val bgDrawable = createRoundedBackground(
            context, color, 5f
        )
        view.background = bgDrawable
    }

    @BindingAdapter("bgTrophyTint")
    @JvmStatic
    fun bgTrophyTint(view: ImageView, @ColorRes colorResId: Int?) {
        val context = view.context
        val color = try {
            if (colorResId != null && colorResId != 0) {
                ContextCompat.getColor(context, colorResId)
            } else {
                ContextCompat.getColor(context, R.color.trophy_color)
            }
        } catch (_: Exception) {
            ContextCompat.getColor(context, R.color.trophy_color)
        }
        view.imageTintList = ColorStateList.valueOf(color)
    }

    @BindingAdapter("bgTrophyAlpha")
    @JvmStatic
    fun bgTrophyAlpha(view: ImageView, pos: Int) {
        val alpha = when (pos) {
            0 -> {
                1f

            }

            1 -> {
                1f

            }

            2 -> {
                1f

            }

            else -> {
                0.2f
            }
        }
        view.setAlpha(alpha)
    }

    @BindingAdapter("textColorSet")
    @JvmStatic
    fun textColorSet(view: TextView, @ColorRes color: Int) {
        val context = view.context
        view.setTextColor(ContextCompat.getColor(context, color))
    }

    fun screenFillView(context: Activity) {
//        context.window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
        context.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        context.window.statusBarColor = android.graphics.Color.TRANSPARENT

        /* // Draw behind status bar
         WindowCompat.setDecorFitsSystemWindows(context.window, false)

         // Optional: make status bar transparent
         context.window.statusBarColor = android.graphics.Color.TRANSPARENT

         // Optional: Light or dark icons
         WindowInsetsControllerCompat(
             context.window,
             context.window.decorView
         ).isAppearanceLightStatusBars = true
    */

    }

    @BindingAdapter("constraintLayoutLeftCC")
    @JvmStatic
    fun constraintLayoutLeftCC(view: ConstraintLayout, value: ChatModel) {
        if (value.type == 2) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }


    @BindingAdapter("blurEffect")
    @JvmStatic
    fun blurEffect(view: LinearLayout, value: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val radius = 16f
            val blurEffect = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
            view.setRenderEffect(blurEffect)
        }
    }

    @BindingAdapter("blurView")
    @JvmStatic
    fun blurView(view: BlurView, value: Boolean) {/*       val radius = 20f

               val decorView = (view.context as Activity).window.decorView
               val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
               val windowBackground = decorView.background
               view.setupWith(rootView)
                   .setFrameClearDrawable(windowBackground)
                   .setBlurRadius(radius)*/
        val radius = 15f

        // Use context to get the Activity safely
        val activity = view.context as? Activity ?: run {
            // Try to unwrap if it's a ContextWrapper
            var context = view.context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    break
                }
                context = context.baseContext
            }
            if (context is Activity) context else return
        }

        val decorView = activity.window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView.background

        view.setupWith(rootView).setFrameClearDrawable(windowBackground).setBlurRadius(radius)

    }

    @BindingAdapter("constraintLayoutRightCC")
    @JvmStatic
    fun constraintLayoutRightCC(view: ConstraintLayout, value: ChatModel) {
        if (value.type == 1) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    @BindingAdapter("imageDrawableSet")
    @JvmStatic
    fun imageDrawableSet(view: ImageView, value: Int) {
        view.setImageResource(value)
    }


    @BindingAdapter("textColorSet")
    @JvmStatic
    fun textColorSet(view: TextView, colorHex: String) {
        if (!colorHex.isNullOrBlank()) {
            try {
                view.setTextColor(Color.parseColor(colorHex))
            } catch (e: IllegalArgumentException) {
                e.printStackTrace() // Invalid color format
            }
        } else {
            // Optionally set a default color if input is empty/null
            view.setTextColor(Color.parseColor("#2C2D3D"))
        }
    }

    @BindingAdapter("backgroundSet")
    @JvmStatic
    fun backgroundSet(view: ConstraintLayout, selected: Boolean = false) {
        val context = view.context
        if (selected) {
            view.setBackgroundResource(R.drawable.bg_selected_45)/*     view.backgroundTintList =
                     ContextCompat.getColorStateList(context, R.color.trophy_color1)*/
        } else {
            view.setBackgroundResource(R.drawable.artical_bg_45)
            view.backgroundTintList = null
        }/*
        if (drawableResId != 0) {
            view.setBackgroundResource(drawableResId)
        } else {
            // Set a default drawable if none provided
            view.setBackgroundResource(R.drawable.artical_bg_45)
        }*/
    }


    @BindingAdapter("animationSplash")
    @JvmStatic
    fun animationSplash(view: ShapeableImageView, value: Boolean?) {

        if (view.visibility == View.GONE) {
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate().alpha(1f).setDuration(1800).start()
        }
    }

    @BindingAdapter("loadImage")
    @JvmStatic
    fun loadImage(view: ImageView, url: String?) {
        Log.i("dfksjdkfljdflkjdkf", "loadImage: " + url)
        try {
            if (url != null) {
                val imageUrl = if (url.contains("http")) {
                    url
                } else {
                    "${Constants.BASE_URL_IMAGE}$url"
                }
                Glide.with(view.context).load(imageUrl).error(R.drawable.earn_badge_img).into(view)
            } else {
                Glide.with(view.context).load(R.drawable.earn_badge_img).into(view)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /*@BindingAdapter(value = ["loadImage", "isGrayscale"], requireAll = false)
    @JvmStatic
    fun loadImage(view: ImageView, url: String?, isGrayscale: Boolean?) {
        try {
            val imageUrl = if (url?.contains("http") == true) url else "${Constants.BASE_URL_IMAGE}$url"

            Glide.with(view.context)
                .load(if (url != null) imageUrl else R.drawable.earn_badge_img)
                .into(view)

            if (isGrayscale == false) {
                // Apply grayscale color filter
                *//*val matrix = ColorMatrix()
                matrix.setSaturation(0f)
                val filter = ColorMatrixColorFilter(matrix)
                view.colorFilter = filter*//*

                val matrix = ColorMatrix().apply {
                    // Remove saturation for grayscale
                    setSaturation(0f)
                    // Optional: increase contrast
                    val contrast = 1.2f
                    val translate = (-0.5f * contrast + 0.5f) * 255f
                    val contrastMatrix = floatArrayOf(
                        contrast, 0f, 0f, 0f, translate,
                        0f, contrast, 0f, 0f, translate,
                        0f, 0f, contrast, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    )
                    postConcat(ColorMatrix(contrastMatrix))
                }
                view.colorFilter = ColorMatrixColorFilter(matrix)
            } else {
                // Clear any color filter to show normal image
                view.clearColorFilter()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }*/

    @BindingAdapter("loadPlaceHolder")
    @JvmStatic
    fun loadPlaceHolder(view: ImageView, url: String?) {
        try {
            if (url != null) {
                val imageUrl = if (url.contains("http")) {
                    url
                } else {
                    "${Constants.BASE_URL_IMAGE}$url"
                }
                Glide.with(view.context).load(imageUrl).into(view)
            } else {

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @BindingAdapter("loadRankPlaceHolder")
    @JvmStatic
    fun loadRankPlaceHolder(view: ImageView, url: String?) {
        try {
            if (url != null) {
                val imageUrl = if (url.contains("http")) {
                    url
                } else {
                    "${Constants.BASE_URL_IMAGE}$url"
                }
                Glide.with(view.context).load(imageUrl).into(view)
            } else {
                view.setImageResource(R.drawable.earn_badge_img)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    @BindingAdapter(value = ["authorImage", "authorName"], requireAll = false)
    @JvmStatic
    fun loadAuthorImage(view: ImageView, authorImage: String?, authorName: String?) {
        val context = view.context

        if (authorImage.isNullOrEmpty()) {
            // Show initials if no image
            val initialsBitmap = generateInitialsBitmap(authorName ?: "")
            view.setImageBitmap(initialsBitmap)
        } else {
            val imageUrl = if (authorImage.contains("http")) {
                authorImage
            } else {
                "${Constants.BASE_URL_IMAGE}$authorImage"
            }

            Glide.with(context).load(imageUrl).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        val initialsBitmap = generateInitialsBitmap(authorName ?: "")
                        view.setImageBitmap(initialsBitmap)
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                }).into(view)
        }
    }

    @BindingAdapter("gifImageLoad")
    @JvmStatic
    fun gifImageLoad(view: ImageView, value: Boolean?) {

    }

    @JvmStatic
    @BindingAdapter("setSpanCount")
    fun setSpanCount(recyclerView: RecyclerView, spanCount: Int) {
        val currentManager = recyclerView.layoutManager
        if (currentManager is GridLayoutManager) {
            currentManager.spanCount = spanCount
            recyclerView.layoutManager = currentManager
        } else {
            recyclerView.layoutManager = GridLayoutManager(recyclerView.context, spanCount)
        }
    }

    @BindingAdapter("drawableImage")
    @JvmStatic
    fun drawableImage(image: ShapeableImageView, imgUrl: Int?) {
        image.setImageResource(imgUrl!!)
    }

    @BindingAdapter("setRating")
    @JvmStatic
    fun setRating(rateBar: RatingBar, rating: Double?) {
        if (rating != null) {
            rateBar.rating = rating.toFloat()
        }
    }

    @BindingAdapter("textGradient")
    @JvmStatic
    fun textGradient(text: TextView, textValue: String?) {
        val context: Context = text.context
        val shader = LinearGradient(
            0f, 0f, 0f, text.textSize, intArrayOf(
                ContextCompat.getColor(context, R.color.splash_gradient_first),
                ContextCompat.getColor(context, R.color.splash_gradient_second)
            ), null, Shader.TileMode.CLAMP
        )
        text.paint.shader = shader
        text.text = textValue
    }

    @BindingAdapter("textGradient1")
    @JvmStatic
    fun textGradient1(text: TextView, textValue: String?) {

        /*val shader: Shader = LinearGradient(
            0f,
            0f,
            text.width.toFloat(),
            text.textSize,
            intArrayOf(
                "#4896FF".toColorInt(),
                "#437CC8".toColorInt(),
                "#002C66".toColorInt(),
            ),
            null,
            Shader.TileMode.CLAMP
        )*/
        val shader = LinearGradient(
            0f, 0f, text.width.toFloat(), text.height.toFloat(), intArrayOf(
                "#4896FF".toColorInt(), "#437CC8".toColorInt(), "#002C66".toColorInt()
            ), null, Shader.TileMode.CLAMP
        )


        text.paint.shader = shader
        text.text = textValue
    }


    @BindingAdapter("seekbarThumbColor")
    @JvmStatic
    fun seekbarThumbColor(view: CustomGradientSeekBar, colorResId: Int) {
        // view.setLabelText(colorResId)
        /* val context = view.context
         val color = try {
             if (colorResId != null && colorResId != 0) {
                 ContextCompat.getColor(context, colorResId)
             } else {
                 ContextCompat.getColor(context, R.color.thumb1)
             }
         } catch (_: Exception) {
             ContextCompat.getColor(context, R.color.thumb1)
         }
         view.thumbColorStart = color
         view.thumbColorEnd = color*/
    }

    @BindingAdapter("labelText")
    @JvmStatic
    fun bindLabelText(view: CustomGradientSeekBar, text: String?) {
        if (text != null) {
            val showText = text.replaceFirstChar { it.uppercase() }
            view.setLabelText(showText)
        }
    }

    @BindingAdapter("labelTextColor")
    @JvmStatic
    fun bindLabelTextColor(view: CustomGradientSeekBar, color: String?) {
        color?.let { view.setLabelTextColor(Color.parseColor(it)) }
    }

    @BindingAdapter("progressGradientStartColor")
    @JvmStatic
    fun bindProgressStartColor(view: CustomGradientSeekBar, color: String?) {
        color?.let { view.setProgressStartColor(Color.parseColor(it)) }
    }

    @BindingAdapter("progressGradientEndColor")
    @JvmStatic
    fun bindProgressEndColor(view: CustomGradientSeekBar, color: String?) {
        color?.let { view.setProgressEndColor(Color.parseColor(it)) }
    }

    @BindingAdapter("seekbarThumbColor")
    @JvmStatic
    fun bindThumbColor(view: CustomGradientSeekBar, color: String?) {
        // val color = ContextCompat.getColor(view.context, color)
        color?.let { view.setThumbColor(Color.parseColor(it)) }
    }


    fun navigateWithSlideAnimations(navController: NavController, destinationId: Int) {
        val navOptions =
            NavOptions.Builder().setEnterAnim(R.anim.slide_in_right) // Define enter animation
                .setExitAnim(R.anim.slide_out_left) // Define exit animation
                .setPopEnterAnim(R.anim.slide_in_left) // Define pop enter animation
                .setPopExitAnim(R.anim.slide_out_right) // Define pop exit animation
                .build()

        navController.navigate(destinationId, null, navOptions)
    }

    fun goActivity(context: Context, activity: Activity) {
        val intent = Intent(context, activity::class.java)
        startActivity(context, intent, null)
    }

    fun createGradientStrokeDrawable(context: Context): Drawable {
        // Step 1: Inner solid (background)
        val solidDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#B2FFFFFF")) // Semi-transparent white fill
            cornerRadius = context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp)
        }

        // Step 2: Outer stroke with gradient
        val gradientStroke = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                Color.parseColor("#004096"), Color.parseColor("#15C8C9")
            )
        ).apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.TRANSPARENT) // Make background transparent
            cornerRadius = context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp)
            setStroke(5, Color.BLACK) // temp stroke to define stroke bounds (won’t be shown)
        }

        // Step 3: Combine into LayerDrawable
        val layers = arrayOf<Drawable>(gradientStroke, solidDrawable)
        val layerDrawable = LayerDrawable(layers)

        // Padding to show stroke (inset inner drawable)
        val strokeWidthPx = 5 // You can also convert dp to px if needed
        layerDrawable.setLayerInset(1, strokeWidthPx, strokeWidthPx, strokeWidthPx, strokeWidthPx)

        return layerDrawable
    }

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getDeviceToken(): String {
        var fcmToken: String = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            fcmToken = token
        }
        Log.d("fmcToken", "getDeviceToken: ${fcmToken}")
        return fcmToken
    }

    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", ".jpg", storageDir
        )
    }


    inline fun <reified T> parseJson(json: String): T? {
        return try {
            val gson = Gson()
            gson.fromJson(json, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun toggleTheme(isDarkMode: Boolean) {
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun uriToBitmap(uri: Uri, context: Context): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    fun generateInitialsBitmap(name: String): Bitmap {


        val initials =
            name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                .uppercase()

        Log.d("initials", "generateInitialsBitmap: name $name, initials $initials")

        val size = 200
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.parseColor("#5D5DFF") // Change to your preferred background color
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        // Draw Circle Background
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        // Draw Initials Text
        paint.apply {
            color = Color.WHITE
            textSize = size / 2f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }

        val textBounds = Rect()
        paint.getTextBounds(initials, 0, initials.length, textBounds)
        val xPos = size / 2f
        val yPos = (size / 2f - textBounds.exactCenterY())

        canvas.drawText(initials, xPos, yPos, paint)

        return bitmap
    }

    fun formatReviewCount(count: Int?): String {

        return if (count != null) {
            when {
                count == 0 -> "0 reviews"
                count in 1 until 100 -> "$count reviews"
                count in 100 until 1000 -> {
                    val rounded = (count / 100) * 100
                    if (count == rounded) "$rounded reviews" else "${rounded}+ reviews"
                }

                else -> {
                    val roundedK = (count / 1000)
                    val lowerBound = roundedK * 1000
                    if (count == lowerBound) "${roundedK}k reviews" else "${roundedK}k+ reviews"
                }
            }
        } else {
            "0 reviews"
        }
    }

    fun TextView.makeExpandable(
        originalText: String, isExpanded: Boolean, onToggle: (Boolean) -> Unit
    ) {
        val maxLinesCollapsed = 3
        val moreText = " more..."
        val lessText = " less..."

        fun setCollapsed() {
            maxLines = maxLinesCollapsed
            post {
                // Only ellipsize if text is longer than 2 lines
                if (layout != null && layout.lineCount > maxLinesCollapsed) {
                    val end = layout.getLineEnd(maxLinesCollapsed - 1)
                    val truncated = originalText.substring(0, end).trimEnd()
                    val displayText = "$truncated$moreText"
                    val spannable = SpannableString(displayText)
                    spannable.setSpan(
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                onToggle(true)
                            }
                        },
                        spannable.length - moreText.length,
                        spannable.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    text = spannable
                    movementMethod = LinkMovementMethod.getInstance()
                } else {
                    text = originalText
                }
            }
        }

        fun setExpanded() {
            maxLines = Integer.MAX_VALUE
            val displayText = "$originalText$lessText"
            val spannable = SpannableString(displayText)
            spannable.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        onToggle(false)
                    }
                },
                spannable.length - lessText.length,
                spannable.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text = spannable
            movementMethod = LinkMovementMethod.getInstance()
        }

        if (isExpanded) setExpanded() else setCollapsed()
    }

    fun getTimeAgo(dateString: String): String {
        // Parse the date string (ISO-8601 with milliseconds and timezone)
        try {

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val createdAtDate: Date? = try {
                sdf.parse(dateString)
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }

            val now = Date()
            val diffMillis = now.time - (createdAtDate?.time ?: 0)

            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
            val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
            val days = TimeUnit.MILLISECONDS.toDays(diffMillis)
            val weeks = days / 7
            val months = days / 30
            val years = days / 365

            return when {
                minutes < 1 -> "Now"
                minutes < 60 -> "${minutes}min"
                hours < 24 -> "${hours} hours"
                days < 7 -> "${days} Days"
                days < 30 -> "${weeks} weeks"
                months < 12 -> "${months} months"
                else -> "${years} Years"
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
    }


}