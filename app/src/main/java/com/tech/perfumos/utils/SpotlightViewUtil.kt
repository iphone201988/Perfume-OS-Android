package com.tech.perfumos.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.bumptech.glide.util.Util
import com.google.android.material.card.MaterialCardView
import com.tech.perfumos.R
import com.tech.perfumos.data.local.SharedPrefManager
import com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.HomeFragment
import com.tech.perfumos.utils.Utils.preventMultipleClick

object SpotlightViewUtil {
    val bufferHeight: Int = 10
    val bufferWidth: Int = 25
    lateinit var sharedPrefManager: SharedPrefManager
    fun applySpotlightEffect(
        sharedPref: SharedPrefManager,
        context: Context,
        targetViews: List<View>,
        textList: List<SpotlightViewUtilModel>,
        overlayAlpha: Float,
        animate: Boolean = false,
        callback: ((View) -> Unit)? = null
    ) {
        sharedPrefManager = sharedPref
        //sharedPref. setOnboardingComplete("1")
        if(sharedPref.getOnboardingComplete() !=null && sharedPref.getOnboardingComplete().isNullOrEmpty()){
            Utils.route = sharedPrefManager.getOnboardingComplete()!!.toInt()

        }
        val userData = sharedPrefManager.getCurrentUser()
        if(userData != null){
            userData.tutorialProgess = 1
            sharedPrefManager.saveUser(userData)
        }

        //sharedPref.setOnboardingCompleteBool(false)
        if (targetViews.isEmpty()) {
            throw IllegalArgumentException("targetViews list cannot be empty")
        }
        val rootView = targetViews.first().rootView.findViewById<ViewGroup>(android.R.id.content)
        if (rootView.width > 0 && rootView.height > 0) {
            applySpotlight(
                rootView,
                context,
                targetViews,
                overlayAlpha,
                animate,
                callback,
                textList
            )


        } else {
            // Wait for layout pass
            rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    if (rootView.width > 0 && rootView.height > 0) {
                        applySpotlight(
                            rootView,
                            context,
                            targetViews,
                            overlayAlpha,
                            animate,
                            callback,
                            textList
                        )
                    }
                }
            })
        }
    }


    private fun applySpotlight(
        rootView: ViewGroup,
        context: Context,
        targetViews: List<View>,
        overlayAlpha: Float,

        animate: Boolean,
        callback: ((View) -> Unit)?,
        textList: List<SpotlightViewUtilModel>,
    ) {
        val overlayLayout = LayoutInflater.from(context)
            .inflate(R.layout.spotlight_overlay, rootView, false)


        functionForAnim(
            overlayLayout,
            rootView,
            context,
            overlayAlpha,
            targetViews,
            textList
        )
        if (animate) {
            overlayLayout.alpha = 0f
        }

        rootView.addView(overlayLayout)
        overlayLayout.isClickable = true

        if (animate) {
            overlayLayout.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null)
                .start()
        }
        callback?.invoke(overlayLayout)
    }

    private fun updateSpotlight(
        indices: List<Int>,
        rootView: ViewGroup,
        overlayAlpha: Float,
        targetViews: List<View>,
        context: Context,
        overlayLayout: View,
        firstText: String,
        secondText: String,
    ) {
        val overlayImage = overlayLayout.findViewById<ImageView>(R.id.spotlight_image)
        val tvHeader = overlayLayout.findViewById<TextView>(R.id.firstText)
        val tvBody = overlayLayout.findViewById<TextView>(R.id.secondText)

        /**/

        Log.d("firstText", "updateSpotlight: $firstText")
        if(secondText.isEmpty()){
            tvBody.visibility = View.GONE
        }else{
            tvBody.visibility = View.VISIBLE

        }


        tvHeader.text = firstText
        tvBody.text = secondText

        val overlayBitmap = Bitmap.createBitmap(
            rootView.width,
            rootView.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(overlayBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawColor(Color.argb((overlayAlpha * 255).toInt(), 0, 0, 0))
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        // Highlight views at the specified indices
        for (index in indices) {
            val targetView = targetViews.getOrNull(index)
            if (targetView != null) {
                val location = IntArray(2)
                targetView.getLocationOnScreen(location)
                val left = location[0].toFloat()
                val top = location[1].toFloat()
                val right = left + targetView.width
                val bottom = top + targetView.height
                val spotlightRect = RectF(left, top, right, bottom)
                val cornerRadiusPx = getCornerRadiusInPixels(context, targetView)
                if (cornerRadiusPx > 0) {
                    canvas.drawRoundRect(spotlightRect, cornerRadiusPx, cornerRadiusPx, paint)
                } else {
                    canvas.drawRect(spotlightRect, paint)
                }
            }
        }

        paint.xfermode = null
        overlayImage.setImageBitmap(overlayBitmap)
    }

    private fun functionForAnim(
        overlayLayout: View,
        rootView: ViewGroup,
        context: Context,
        overlayAlpha: Float,
        targetViews: List<View>,
        textList: List<SpotlightViewUtilModel>,
    ) {
        overlayLayout.findViewById<ImageView>(R.id.gifUp).visibility = View.GONE
        val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
        val backBottom = overlayLayout.findViewById<ImageView>(R.id.backBottom)
        Log.d("route", "functionForAnim: ${Utils.route} , ${Utils.isCompleted}")
        Utils.route = sharedPrefManager.getOnboardingComplete()?.toInt()!!
        Log.i("fff32f", "functionForAnim: "+Utils.route)
        /*Utils.route = 3*/
        backBottom.setOnClickListener {
            spotlightEffectFullRemove(overlayLayout, false)
           // Utils.route = 1
            if(Utils.isCompleted){
                Utils.updateTheTutorial.postValue(2)
            }else{
                Utils.updateTheTutorial.postValue(4)
            }
            Log.d("route", "functionForAnim: ${Utils.route} , ${Utils.isCompleted}")
        }
      /*  updateSpotlight(
            listOf(0, 1),
            rootView,
            overlayAlpha,
            targetViews,
            context,
            overlayLayout,
            textList[0].first,
            textList[0].second,
        )*/
        val countValueTextView = overlayLayout.findViewById<TextView>(R.id.countValue)
        if (Utils.route == 3){
            Utils.updateTheTutorial.postValue(3)
        }  else{
            Utils.updateTheTutorial.postValue(4)
        }
        countValueTextView.apply { text = "${Utils.route}/9" }
        sharedPrefManager.setOnboardingComplete("${Utils.route}")


        routeNavigation(
            overlayLayout, rootView,
            overlayAlpha,
            targetViews,
            context,
            false,
            textList
        )
        mainLayout.layoutParams.apply {
            width = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._200sdp)
        }
        mainLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if(Utils.route !=1){
                    return
                }
                mainLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mainLayout.apply {
                    fadeOutViewGif(overlayLayout.findViewById<ImageView>(R.id.gifDown))
                    val navFloatingHeight =
                        targetViews[0].height + (targetViews[1].height / 2) + bufferHeight

                    animateMainLayoutPosition(
                        mainLayout, rootView.width * 0.5f - (mainLayout.width * 0.5f),
                        rootView.height.toFloat() - (mainLayout.height + navFloatingHeight)
                    ) {
                        x = rootView.width * 0.5f - (mainLayout.width * 0.5f)
                        y = rootView.height.toFloat() - (mainLayout.height + navFloatingHeight)
                        visibility = View.VISIBLE
                        elevation = 10f
                        bringToFront()
                        requestLayout()
                        invalidate()
                    }

                }

            }
        })

        val countValue = overlayLayout.findViewById<TextView>(R.id.countValue)
        overlayLayout.findViewById<ImageView>(R.id.back_next_icon).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                if (Utils.route <= 1) {
                    return@setOnClickListener
                }

                Handler(Looper.getMainLooper()).post {
                    it.preventMultipleClick()
                    Utils.route--
                    if (Utils.route == 3){
                        Utils.updateTheTutorial.postValue(3)
                    }  else{
                        Utils.updateTheTutorial.postValue(4)
                    }
                    countValue.apply { text = "${Utils.route}/9" }
                    sharedPrefManager.setOnboardingComplete("${Utils.route}")


                    routeNavigation(
                        overlayLayout, rootView,
                        overlayAlpha,
                        targetViews,
                        context,
                        false,
                        textList
                    )
                }
            }
        }
        overlayLayout.findViewById<ImageView>(R.id.next_icon).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                if (Utils.route >= 9) {
                    return@setOnClickListener
                }
                Handler(Looper.getMainLooper()).post {
                    it.preventMultipleClick()
                    Utils.route++

                    if (Utils.route == 3){
                        Utils.updateTheTutorial.postValue(3)
                    }  else{
                        Utils.updateTheTutorial.postValue(4)
                    }
                    Log.d("Route", "functionForAnim: ${Utils.route}")
                    countValue.apply { text = "${Utils.route}/9" }
                    sharedPrefManager.setOnboardingComplete("${Utils.route}")
                    routeNavigation(
                        overlayLayout, rootView,
                        overlayAlpha,
                        targetViews,
                        context,
                        true,
                        textList
                    )
                }


            }
        }

    }

    private fun removeSpotlightEffect3(
        overlayView: View?,
        context: Context?,
        rootView: ViewGroup?,
        overlayAlpha: Float = 0.7f, // Default alpha, adjust as needed
        animate: Boolean = false,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        if (overlayView == null || rootView == null) {
            onAnimationEnd?.invoke()
            return
        }

        // Find the ImageView containing the overlay
        val overlayImage = overlayView.findViewById<ImageView>(R.id.spotlight_image)
        if (overlayImage == null) {
            onAnimationEnd?.invoke()
            return
        }

        // Check lifecycle safety
        val isValidContext = when (context) {
            is Activity -> !context.isFinishing && !context.isDestroyed
            is Fragment -> !context.isDetached && context.activity != null
            else -> true // Fallback for non-Activity/Fragment context
        }

        if (!isValidContext) {
            overlayImage.setImageBitmap(null) // Clear bitmap as a fallback
            onAnimationEnd?.invoke()
            return
        }

        // Create a new Bitmap with solid semi-transparent black color
        val overlayBitmap = Bitmap.createBitmap(
            rootView.width,
            rootView.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(overlayBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawColor(Color.argb((overlayAlpha * 255).toInt(), 0, 0, 0))

        if (animate) {
            overlayImage.animate()
                .alpha(0f)
                .setDuration(300) // 300ms fade-out
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        overlayImage.setImageBitmap(overlayBitmap) // Set new solid black Bitmap
                        overlayImage.alpha = 1f // Reset alpha for visibility
                        onAnimationEnd?.invoke()
                    }
                })
                .start()
        } else {
            overlayImage.setImageBitmap(overlayBitmap) // Set new solid black Bitmap immediately
            onAnimationEnd?.invoke()
        }
    }

    fun removeSpotlightEffect2(
        overlayView: View?,
        animate: Boolean = false,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        if (overlayView?.parent !is ViewGroup) return

        if (animate) {
            overlayView.animate()
                .alpha(0f)
                .setDuration(300) // 300ms fade-out
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (overlayView.parent is ViewGroup) {
                            (overlayView.parent as ViewGroup).removeView(overlayView)
                        }
                        onAnimationEnd?.invoke()
                    }
                })
                .start()
        } else {
            if (overlayView.parent is ViewGroup) {
                (overlayView.parent as ViewGroup).removeView(overlayView)
            }
            onAnimationEnd?.invoke()
        }
    }

    private fun spotlightEffectFullRemove(
        overlayView: View?,
        animate: Boolean = false,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        if (overlayView?.parent !is ViewGroup) return

        if (animate) {
            overlayView.animate()
                .alpha(0f)
                .setDuration(300) // 300ms fade-out
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (overlayView.parent is ViewGroup) {
                            (overlayView.parent as ViewGroup).removeView(overlayView)
                        }
                        onAnimationEnd?.invoke()
                    }
                })
                .start()
        } else {
            if (overlayView.parent is ViewGroup) {
                (overlayView.parent as ViewGroup).removeView(overlayView)
            }
            onAnimationEnd?.invoke()
        }
    }

    private fun fadeOutAndHide(view: ConstraintLayout, duration: Long = 300L, onEnd: (() -> Unit)? = null) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {


                onEnd?.invoke()
            }
            .start()
    }

    fun flipFromBottomToTop(view: View, duration: Long = 600L, onEnd: (() -> Unit)? = null) {
        view.pivotX = view.width / 2f
        view.pivotY = view.height.toFloat()

        view.rotationX = 90f
        view.visibility = View.VISIBLE
        view.alpha = 0f

        view.animate()
            .rotationX(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                onEnd?.invoke()
            }
            .start()
    }

    private fun routeNavigation(
        overlayLayout: View,
        rootView: ViewGroup,
        overlayAlpha: Float,
        targetViews: List<View>,
        context: Context,
        isClickNext: Boolean,
        textList: List<SpotlightViewUtilModel>,
    ) {
        val backNextIcon = overlayLayout.findViewById<ImageView>(R.id.back_next_icon)
        val firstText = overlayLayout.findViewById<TextView>(R.id.firstText)
        val secondText = overlayLayout.findViewById<TextView>(R.id.secondText)
        if (Utils.route == 1) {
            backNextIcon.setImageResource(R.drawable.back_next_icon_grey)
            secondText.setTextColor(Color.BLACK)
            secondText.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            backNextIcon.setImageResource(R.drawable.back_next_icon)
            secondText.setTextColor(ContextCompat.getColor(context, R.color.text_color_tuitorial))
        }
        val gifDown2 = overlayLayout.findViewById<ImageView>(R.id.gifDown2)
        val gifDown = overlayLayout.findViewById<ImageView>(R.id.gifDown)
        val gifUp = overlayLayout.findViewById<ImageView>(R.id.gifUp)
        if (Utils.route == 5 || Utils.route == 6 || Utils.route == 7 || Utils.route == 8) {
            gifDown2.visibility = View.VISIBLE
//            gifDown.visibility=View.GONE
//            gifUp.visibility=View.GONE
        } else {
            gifDown2.visibility = View.GONE
//            gifDown.visibility=View.VISIBLE
//            gifUp.visibility=View.VISIBLE
        }


        val backBtn = overlayLayout.findViewById<ImageView>(R.id.backBottom)

        val mainViewParent = overlayLayout.findViewById<ConstraintLayout>(R.id.mainViewParent)
        val endTutorial = overlayLayout.findViewById<TextView>(R.id.endTutorial)
        val congratulationsCC = overlayLayout.findViewById<ConstraintLayout>(R.id.congratulationsCC)
        val textBack = overlayLayout.findViewById<TextView>(R.id.textBack)
        val homeBack = overlayLayout.findViewById<TextView>(R.id.homeBack)

        val nextIcon = overlayLayout.findViewById<ImageView>(R.id.next_icon)
        val textNext = overlayLayout.findViewById<TextView>(R.id.textNext)
        val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)

        if (Utils.route == 9) {
            nextIcon.setImageResource(R.drawable.back_next_icon_grey)
            nextIcon.rotation = 180f
            endTutorial.visibility = View.VISIBLE
        } else {
            nextIcon.setImageResource(R.drawable.next_icon)
            nextIcon.rotation = 0f
            endTutorial.visibility = View.GONE
        }
        if(Utils.route == 9){
            backBtn.visibility = View.INVISIBLE
        }else{
            backBtn.visibility = View.VISIBLE
        }

        endTutorial.setOnClickListener {
//            val overlayImage = overlayLayout.findViewById<ImageView>(R.id.spotlight_image)
//            overlayImage.setImageBitmap(null)
            removeSpotlightEffect3(overlayLayout, context, rootView, 0.7f, false) {
            }
            congratulationsCC.visibility = View.VISIBLE
            endTutorial.visibility = View.GONE
            backNextIcon.visibility = View.GONE
            textBack.visibility = View.GONE
            nextIcon.visibility = View.GONE
            textNext.visibility = View.GONE
            mainLayout.visibility = View.GONE

            val userData = sharedPrefManager.getCurrentUser()
            if(userData != null){
                userData.tutorialProgess = 1
                sharedPrefManager.saveUser(userData)
                sharedPrefManager.setOnboardingCompleteBool(true)
            }
            Utils.lastApiCall.postValue(0)
        }
        homeBack.setOnClickListener {
            Utils.isCompleted=true
            /*if(Utils.isCompleted){
                Utils.updateTheTutorial.postValue(2)
            }*/
            Utils.updateTheTutorial.postValue(1)
            backBtn.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                fadeOutAndHide(congratulationsCC) {
                    congratulationsCC.visibility = View.GONE
                    val gifDown = overlayLayout.findViewById<ImageView>(R.id.gifDown)
                    val gifUp = overlayLayout.findViewById<ImageView>(R.id.gifUp)
                    val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
                    gifUp.visibility = View.GONE
                    gifDown.visibility = View.VISIBLE
                    mainLayout.post {

                        targetViews[4].post {
                            val location = IntArray(2)
                            targetViews[4].getLocationOnScreen(location)
                            val y = location[1]
                            val x = location[0]
                            animateMainLayoutPosition(
                                mainLayout,
                                (rootView.width - mainLayout.width) - bufferHeight.toFloat(),
                                y.toFloat() - (targetViews[4].height + bufferHeight)
                            ) {
                                mainLayout.x =
                                    (rootView.width - mainLayout.width) - bufferHeight.toFloat()
                                mainLayout.y = y.toFloat() - (targetViews[4].height + bufferHeight)
                                mainLayout.visibility = View.VISIBLE
                                mainLayout.elevation = 10f
                                mainLayout.bringToFront()
                                mainLayout.requestLayout()
                                mainLayout.invalidate()

                                updateSpotlight(
                                    listOf(4),
                                    rootView,
                                    overlayAlpha,
                                    targetViews,
                                    context,
                                    overlayLayout,
                                    textList[9].first, textList[9].second,
                                )
                            }
                            fadeOutViewGifTransition(
                                gifDown,
                                (mainLayout.width * 0.5f) - (gifDown.width * 0.5f)
                            ) {
                                gifDown.x = (mainLayout.width * 0.5f) - (gifDown.width * 0.5f)
                            }
                        }
                    }
                }
            }, 100);

        }
        if (Utils.route == 4) {
//            val overlayImage = overlayLayout.findViewById<ImageView>(R.id.spotlight_image)
//
//            overlayImage.setImageBitmap(null)
            mainViewParent.visibility = View.VISIBLE
            HomeFragment.scrollList.postValue(1)
            val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
            mainLayout.layoutParams.apply {
                width = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp)
            }
        } else if (Utils.route == 1) {
            HomeFragment.scrollList.postValue(2)

            mainViewParent.visibility = View.VISIBLE
            val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
            mainLayout.layoutParams.apply {
                width = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._200sdp)
            }
        } else {
            HomeFragment.scrollList.postValue(2)

            mainViewParent.visibility = View.VISIBLE
            val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
            mainLayout.layoutParams.apply {
                width = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp)
            }
        }


        val spotlightItems = when (Utils.route) {
            1, 5, 6, 7, 8 -> listOf(0, 1)
            2 -> listOf(2)
            3 -> listOf(3, 4, 5)
            4 -> listOf(6)
            9 -> listOf(7)
            else -> emptyList()
        }
        val spotlightAction = {
            if (Utils.route == 4) {
                mainViewParent.visibility = View.VISIBLE
            }
            updateSpotlight(
                spotlightItems,
                rootView,
                overlayAlpha,
                targetViews,
                context,
                overlayLayout,
                textList[Utils.route - 1].first,
                textList[Utils.route - 1].second,
            )
        }

        val delay = when (Utils.route) {
            3 -> if (isClickNext) 0L else 500L
            4 -> 500L
            else -> 0L
        }

        if (delay > 0) {
            Handler(Looper.getMainLooper()).postDelayed(spotlightAction, delay)
        } else {
            spotlightAction()
        }
        if (Utils.route == 1) {
            val gifDown = overlayLayout.findViewById<ImageView>(R.id.gifDown)
            val gifUp = overlayLayout.findViewById<ImageView>(R.id.gifUp)
            val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
            val animationGifTop = overlayLayout.findViewById<ImageView>(R.id.animationGifTop)
            gifUp.visibility = View.GONE
            gifDown.visibility = View.GONE
            mainLayout.visibility = View.VISIBLE

            animateGifFromTargetBack(animationGifTop, gifDown) {
                gifUp.visibility = View.GONE
                gifDown.visibility = View.VISIBLE

            }
            fadeOutViewGif(gifDown, 500L)
            mainLayout.post {
                val navFloatingHeight =
                    targetViews[0].height + (targetViews[1].height / 2) + bufferHeight
                animateMainLayoutPosition(
                    mainLayout, rootView.width * 0.5f - (mainLayout.width * 0.5f),
                    rootView.height.toFloat() - (mainLayout.height + navFloatingHeight)
                ) {
                    animationGifTop.visibility = View.GONE
                    gifDown.visibility = View.VISIBLE
                    mainLayout.post {
                        mainLayout.x = rootView.width * 0.5f - (mainLayout.width * 0.5f)
                        mainLayout.y =
                            rootView.height.toFloat() - (mainLayout.height + navFloatingHeight)

                        mainLayout.elevation = 10f
                        mainLayout.bringToFront()
                        mainLayout.requestLayout()
                        mainLayout.invalidate()
                    }

                }

            }
        } else if (Utils.route == 2) {
            val gifDown = overlayLayout.findViewById<ImageView>(R.id.gifDown)
            val gifUp = overlayLayout.findViewById<ImageView>(R.id.gifUp)
            val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)

            val animationGif = overlayLayout.findViewById<ImageView>(R.id.animationGif)
            gifUp.visibility = View.INVISIBLE
            gifDown.visibility = View.INVISIBLE
            mainLayout.visibility = View.VISIBLE
//            gifUp.animate().alpha(1f).setDuration(300).start()
            animateGifToTarget(animationGif, gifUp) {
                gifUp.visibility = View.VISIBLE
                gifDown.visibility = View.GONE
                fadeOutViewGif(gifUp, 20L)
            }


            targetViews[2].doOnPreDraw {
                val location = IntArray(2)
                it.getLocationOnScreen(location)
                val y = location[1]


                mainLayout.post {
                    animateMainLayoutPosition(
                        mainLayout,
                        rootView.width * 0.5f - (mainLayout.width * 0.5f),
                        y.toFloat() + targetViews[2].height + bufferHeight
                    ) {
                        //                    mainLayout.animate()
//                        .x(rootView.width * 0.5f - (mainLayout.width * 0.5f))
//                        .y( y.toFloat() + targetViews[2].height + bufferHeight)
//                        .setDuration(300)
//                        .setInterpolator(DecelerateInterpolator())
//                        .start()
                        mainLayout.x = rootView.width * 0.5f - (mainLayout.width * 0.5f)
                        mainLayout.y = y.toFloat() + targetViews[2].height + bufferHeight

                        mainLayout.elevation = 10f
                        mainLayout.bringToFront()
                        mainLayout.requestLayout()
                        mainLayout.invalidate()
                    }

                }
            }
        } else if (Utils.route == 3) {
            Handler(Looper.getMainLooper()).postDelayed({
                val gifDown = overlayLayout.findViewById<ImageView>(R.id.gifDown)
                val gifUp = overlayLayout.findViewById<ImageView>(R.id.gifUp)
                val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
                gifDown.visibility = View.INVISIBLE
                mainLayout.visibility = View.VISIBLE
                val animationGifTop = overlayLayout.findViewById<ImageView>(R.id.animationGifTop)
                if (isClickNext) {
                    gifUp.visibility = View.GONE
                    gifDown.visibility = View.INVISIBLE
                    animateGifFromTargetBack(animationGifTop, gifDown) {
                        gifUp.visibility = View.GONE
                        gifDown.visibility = View.VISIBLE
                        fadeOutViewGif(gifDown, 20L)

                    }
                } else {
                    gifUp.visibility = View.GONE
                    gifDown.visibility = View.VISIBLE
                }

                fadeOutViewGif(gifDown)
                targetViews[3].post {
                    val location = IntArray(2)
                    targetViews[3].getLocationOnScreen(location)
                    val y = location[1]

                    mainLayout.post {
                        animateMainLayoutPosition(
                            mainLayout, rootView.width * 0.5f - (mainLayout.width * 0.5f),
                            y.toFloat() - (bufferHeight + targetViews[3].height),
                            700L
                        ) {
                            mainLayout.x = rootView.width * 0.5f - (mainLayout.width * 0.5f)
                            mainLayout.y = y.toFloat() - (bufferHeight + targetViews[3].height)
                            mainLayout.elevation = 10f
                            mainLayout.bringToFront()
                            mainLayout.requestLayout()
                            mainLayout.invalidate()
                        }

                    }
                }
            }, 500)
        } else if (Utils.route == 4) {
            removeSpotlightEffect3(overlayLayout, context, rootView, 0.7f, false) {
            }
            Handler(Looper.getMainLooper()).postDelayed({
                val mainViewParent =
                    overlayLayout.findViewById<ConstraintLayout>(R.id.mainViewParent)
                val gifDown = overlayLayout.findViewById<ImageView>(R.id.gifDown)
                val gifUp = overlayLayout.findViewById<ImageView>(R.id.gifUp)
                val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
                mainViewParent.visibility = View.VISIBLE
                gifUp.visibility = View.GONE
                gifDown.visibility = View.VISIBLE
                mainLayout.visibility = View.VISIBLE
                targetViews[6].post {
                    val location = IntArray(2)
                    targetViews[6].getLocationOnScreen(location)
                    val y = location[1]
                    mainLayout.post {


                        fadeOutViewGif(gifDown) {
                            gifDown.x = (mainLayout.width * 0.5f) - (gifDown.width * 0.5f)
                        }
                        animateMainLayoutPosition(
                            mainLayout, rootView.width * 0.5f - (mainLayout.width * 0.5f),
                            y.toFloat() - (bufferHeight + mainLayout.height)
                        ) {
                            mainLayout.x = rootView.width * 0.5f - (mainLayout.width * 0.5f)
                            mainLayout.y = y.toFloat() - (bufferHeight + mainLayout.height)
                            mainLayout.elevation = 10f
                            mainLayout.bringToFront()
                            mainLayout.requestLayout()
                            mainLayout.invalidate()
                        }


                    }
                }

            }, 500)
        }
        else if (Utils.route == 5) {
            val gifDown = overlayLayout.findViewById<ImageView>(R.id.gifDown)
            val gifUp = overlayLayout.findViewById<ImageView>(R.id.gifUp)
            val gifDown2 = overlayLayout.findViewById<ImageView>(R.id.gifDown2)
            val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
            gifUp.visibility = View.GONE
            gifDown.visibility = View.INVISIBLE
            mainLayout.visibility = View.VISIBLE
            val navFloatingHeight = targetViews[0].height + bufferHeight
            val navFloatingWidth = (Utils.navGraphWidth / 2) - bufferWidth
            mainLayout.post {
                gifDown2.post {
                    Utils.targetViews[0]?.let {
                        val location = IntArray(2)
                        it.getLocationOnScreen(location)
                        val x = location[0]
                        val y = location[1]
                        fadeOutViewGifTransitionBoth(
                            gifDown2,
                            ((x + (Utils.targetViews[0].width / 2)) - (gifDown2.width / 2)).toFloat(),
                            rootView.height - (navFloatingHeight + gifDown2.height).toFloat()
                        ) {
                            gifDown2.y =
                                rootView.height - (navFloatingHeight + gifDown2.height).toFloat()
                            gifDown2.x =
                                ((x + (Utils.targetViews[0].width / 2)) - (gifDown2.width / 2)).toFloat()
                        }
                    }

                }

                gifDown.x = navFloatingWidth.toFloat()
                Log.d("gifDown_x", "routeNavigation: 5 ${gifDown.x}")

                animateMainLayoutPosition(
                    mainLayout, 0f,
                    rootView.height.toFloat() - (mainLayout.height + navFloatingHeight/*+gifDown.height*/)
                ) {
                    mainLayout.x = 0f
                    mainLayout.y =
                        rootView.height.toFloat() - (mainLayout.height + navFloatingHeight/*+gifDown.height*/)
                    mainLayout.elevation = 10f
                    mainLayout.bringToFront()
                    mainLayout.requestLayout()
                    mainLayout.invalidate()
                }
            }
        } else if (Utils.route == 6) {
            //val gifDown = overlayLayout.findViewById<ImageView>(R.id.gifDown)
            val gifUp = overlayLayout.findViewById<ImageView>(R.id.gifUp)
            val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
            gifUp.visibility = View.GONE
            gifDown.visibility = View.INVISIBLE
            mainLayout.visibility = View.VISIBLE

            gifDown2.post {
                val navFloatingHeight = targetViews[0].height + bufferHeight
                Utils.targetViews[1].let {
                    val location = IntArray(2)
                    it.getLocationOnScreen(location)
                    val x = location[0]
                    val y = location[1]
                    Log.d("gifDown_x", "routeNavigation: $x")
                    fadeOutViewGifTransitionBoth(
                        gifDown2,
                        ((x + (Utils.targetViews[1].width / 2)) - (gifDown2.width / 2)).toFloat(),
                        rootView.height - (navFloatingHeight + gifDown2.height).toFloat()
                    ) {
                        gifDown2.x =
                            ((x + (Utils.targetViews[1].width / 2)) - (gifDown2.width / 2)).toFloat()
                        gifDown2.y =
                            rootView.height - (navFloatingHeight + gifDown2.height).toFloat()

                    }
                }
            }
            mainLayout.post {
                val navFloatingWidth = (Utils.navGraphWidth) - bufferWidth
                gifDown.x = (mainLayout.width - gifDown.width.toFloat()) / 2 /*navFloatingWidth*/
                val navFloatingHeight = targetViews[0].height + bufferHeight

                animateMainLayoutPosition(
                    mainLayout, mainLayout.width * 0.15f,
                    rootView.height.toFloat() - (mainLayout.height + navFloatingHeight)
                ) {
                    mainLayout.x = mainLayout.width * 0.15f
                    mainLayout.y =
                        rootView.height.toFloat() - (mainLayout.height + navFloatingHeight)
                    mainLayout.elevation = 10f
                    mainLayout.bringToFront()
                    mainLayout.requestLayout()
                    mainLayout.invalidate()



                }
            }

        } else if (Utils.route == 7) {
            val gifDown = overlayLayout.findViewById<ImageView>(R.id.gifDown)
            val gifUp = overlayLayout.findViewById<ImageView>(R.id.gifUp)
            val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
            gifUp.visibility = View.GONE
            gifDown.visibility = View.INVISIBLE
            mainLayout.visibility = View.VISIBLE
            mainLayout.post {
//                gifDown2.post{
//                    val navFloatingHeight = targetViews[0].height + bufferHeight
//                    gifDown2.x=(((rootView.width/5)*3) ).toFloat()
//                    gifDown2.y=rootView.height-(navFloatingHeight+gifDown2.height).toFloat();
//                }
                gifDown2.post {
                    val navFloatingHeight = targetViews[0].height + bufferHeight
                    Utils.targetViews[3].let {
                        val location = IntArray(2)
                        it.getLocationOnScreen(location)
                        val x = location[0]
                        val y = location[1]

                        fadeOutViewGifTransitionBoth(
                            gifDown2,
                            ((x + (Utils.targetViews[3].width / 2)) - (gifDown2.width / 2)).toFloat(),
                            rootView.height - (navFloatingHeight + gifDown2.height).toFloat()
                        ) {
                            gifDown2.x =
                                ((x + (Utils.targetViews[3].width / 2)) - (gifDown2.width / 2)).toFloat()
                            gifDown2.y =
                                rootView.height - (navFloatingHeight + gifDown2.height).toFloat()
                        }
                    }

                }

//                    gifDown.x = (mainLayout.width * 0.5f) - (gifDown.width * 0.5f)
                gifDown.x = (mainLayout.width - gifDown.width.toFloat()) / 2

                val navFloatingHeight = targetViews[0].height + bufferHeight

//                gifDown.x = mainLayout.width * 0.5f - (gifDown.width * 0.5f)
//                mainLayout.x = rootView.width.toFloat() - mainLayout.width.toFloat() - 100f
//                mainLayout.y = rootView.height.toFloat() * 0.72f

                animateMainLayoutPosition(
                    mainLayout, Utils.fabCamera.x,
                    rootView.height.toFloat() - (mainLayout.height + navFloatingHeight)
                ) {
                    mainLayout.elevation = 10f
                    mainLayout.bringToFront()
                    mainLayout.requestLayout()
                    mainLayout.invalidate()
                    mainLayout.y =
                        rootView.height.toFloat() - (mainLayout.height + navFloatingHeight)
                    mainLayout.x =
                        Utils.fabCamera.x/*(rootView.width.toFloat() - mainLayout.width.toFloat() )*/
                }

            }
        } else if (Utils.route == 8) {
            val gifDown = overlayLayout.findViewById<ImageView>(R.id.gifDown)
            val gifUp = overlayLayout.findViewById<ImageView>(R.id.gifUp)
            val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
            gifUp.visibility = View.GONE
            gifDown.visibility = View.INVISIBLE
            mainLayout.visibility = View.VISIBLE
            mainLayout.post {

                gifDown2.post {
                    val navFloatingHeight = targetViews[0].height + bufferHeight
                    Utils.targetViews[4]?.let {
                        val location = IntArray(2)
                        it.getLocationOnScreen(location)
                        val x = location[0]
                        val y = location[1]



                        fadeOutViewGifTransitionBoth(
                            gifDown2,
                            ((x + (Utils.targetViews[4].width / 2)) - (gifDown2.width / 2)).toFloat(),
                            rootView.height - (navFloatingHeight + gifDown2.height).toFloat()
                        ) {
                            gifDown2.x =
                                ((x + (Utils.targetViews[4].width / 2)) - (gifDown2.width / 2)).toFloat()
                            gifDown2.y =
                                rootView.height - (navFloatingHeight + gifDown2.height).toFloat()
                        }

                    }


                }
                gifDown.x = (mainLayout.width * 0.6f)

                val navFloatingHeight = targetViews[0].height + bufferHeight

                animateMainLayoutPosition(
                    mainLayout, rootView.width.toFloat() - mainLayout.width.toFloat(),
                    rootView.height.toFloat() - (mainLayout.height + navFloatingHeight)
                ) {
                    mainLayout.y =
                        rootView.height.toFloat() - (mainLayout.height + navFloatingHeight)
                    mainLayout.x = rootView.width.toFloat() - mainLayout.width.toFloat()
                    mainLayout.elevation = 10f
                    mainLayout.bringToFront()
                    mainLayout.requestLayout()
                    mainLayout.invalidate()
                }


            }
        } else if (Utils.route == 9) {
            val gifDown = overlayLayout.findViewById<ImageView>(R.id.gifDown)
            val gifUp = overlayLayout.findViewById<ImageView>(R.id.gifUp)
            val mainLayout = overlayLayout.findViewById<LinearLayout>(R.id.mainLayout)
            gifUp.visibility = View.VISIBLE
            gifDown.visibility = View.GONE
            mainLayout.visibility = View.VISIBLE
            mainLayout.post {
                targetViews[7].post {
                    val location = IntArray(2)
                    targetViews[7].getLocationOnScreen(location)
                    val y = location[1]
                    val x = location[0]
                    fadeOutViewGifTransition(gifUp, mainLayout.width.toFloat() * 0.4f) {
                        gifUp.x = mainLayout.width.toFloat() * 0.4f
                    }
                    animateMainLayoutPosition(
                        mainLayout, rootView.width.toFloat() - mainLayout.width.toFloat(),
                        y.toFloat() + targetViews[7].height + bufferHeight
                    ) {
                        mainLayout.x = rootView.width.toFloat() - mainLayout.width.toFloat()
                        mainLayout.y = y.toFloat() + targetViews[7].height + bufferHeight
                        mainLayout.elevation = 10f
                        mainLayout.bringToFront()
                        mainLayout.requestLayout()
                        mainLayout.invalidate()
                    }
                }

            }
        }


    }

    private fun getCornerRadiusInPixels(context: Context, view: View): Float {
        // Check for MaterialCardView
        if (view is MaterialCardView) {
            return view.radius
        }

        // Check for CardView
        if (view is CardView) {
            return view.radius
        }


        if (view is com.google.android.material.floatingactionbutton.FloatingActionButton) {
            return view.width.coerceAtMost(view.height) / 2f
        }

        if (view.background is GradientDrawable) {
            val gradientDrawable = view.background as GradientDrawable
            val radii = gradientDrawable.cornerRadii
            // Return the first radius (assume uniform corners) or cornerRadius
            return radii?.getOrNull(0) ?: gradientDrawable.cornerRadius
        }

        if (view is com.google.android.material.bottomappbar.BottomAppBar ||
            view is com.google.android.material.bottomnavigation.BottomNavigationView
        ) {
            // Assume a mild radius to soften the spotlight edge (customize if needed)
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                12f,
                context.resources.displayMetrics
            )
        }
        return 0f
    }


    fun removeSpotlightEffect(
        overlayView: View?,
        animate: Boolean = false,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        if (overlayView?.parent !is ViewGroup) return

        if (animate) {
            overlayView.animate()
                .alpha(0f)
                .setDuration(300) // 300ms fade-out
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (overlayView.parent is ViewGroup) {
                            (overlayView.parent as ViewGroup).removeView(overlayView)
                        }
                        onAnimationEnd?.invoke()
                    }
                })
                .start()
        } else {
            if (overlayView.parent is ViewGroup) {
                (overlayView.parent as ViewGroup).removeView(overlayView)
            }
            onAnimationEnd?.invoke()
        }
    }

    fun animateMainLayoutPosition(
        layout: LinearLayout,
        targetX: Float,
        targetY: Float,
        duration: Long = 500L,
        onAnimationEnd: (() -> Unit)
    ) {
        Handler(Looper.getMainLooper()).post {

            layout.animate()
                .x(targetX)
                .y(targetY)
                .setDuration(duration)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    onAnimationEnd.invoke()
                }
                .start()

        }

    }

    fun fadeOutViewGif(
        view: ImageView,
        duration: Long = 500L,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        Handler(Looper.getMainLooper()).post {
            view.animate()
                .alpha(1f)
                .setDuration(duration)
                .withEndAction {
                    onAnimationEnd?.invoke()
                }
                .setInterpolator(AccelerateInterpolator())
                .start()
        }

    }

    private fun fadeOutViewGifTransition(
        view: ImageView,
        targetX: Float,
        duration: Long = 500L,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        Handler(Looper.getMainLooper()).post {
            view.animate()
                .x(targetX)
                .setDuration(duration)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    onAnimationEnd?.invoke()
                }
                .start()
        }
    }

    private fun fadeOutViewGifTransitionBoth(
        view: ImageView,
        targetX: Float,
        targetY: Float,
        duration: Long = 500L,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        Handler(Looper.getMainLooper()).post {
            view.animate()
                .x(targetX)
                .y(targetY)
                .setDuration(duration)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    onAnimationEnd?.invoke()
                }
                .start()
        }
    }


    private fun animateGifFromTargetBack(
        animationGif: View,
        targetView: View,
        duration: Long = 300L,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        if (animationGif.visibility == View.VISIBLE) return

        animationGif.visibility = View.VISIBLE

        animationGif.post {
            // Set pivot to bottom center for natural flip
            animationGif.pivotX = animationGif.width / 2f
            animationGif.pivotY = animationGif.height.toFloat()

            // Set camera distance for better 3D effect
            val scale = animationGif.context.resources.displayMetrics.density
            animationGif.cameraDistance = 8000 * scale

            val targetLocation = IntArray(2)
            targetView.getLocationOnScreen(targetLocation)
            val targetX = targetLocation[0].toFloat()
            val targetY = targetLocation[1].toFloat()

            val currentLocation = IntArray(2)
            animationGif.getLocationOnScreen(currentLocation)
            val currentX = currentLocation[0].toFloat()
            val currentY = currentLocation[1].toFloat()

            val deltaX = targetX - currentX
            val deltaY = targetY - currentY

            val translateX =
                ObjectAnimator.ofFloat(animationGif, "translationX", 0f, deltaX).apply {
                    interpolator = LinearInterpolator()
                }
            val translateY =
                ObjectAnimator.ofFloat(animationGif, "translationY", 0f, deltaY).apply {
                    interpolator = LinearInterpolator()
                }

            // Flip from 90° (folded down) to 0° (flat) — bottom to top flip
            val flip = ObjectAnimator.ofFloat(animationGif, "rotationX", 90f, 0f).apply {
                interpolator = DecelerateInterpolator()
            }

            AnimatorSet().apply {
                playTogether(translateX, translateY, flip)
                this.duration = duration
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animationGif.visibility = View.GONE
                        animationGif.translationX = 0f
                        animationGif.translationY = 0f
                        animationGif.rotationX = 0f
                        onAnimationEnd?.invoke()
                    }
                })
                start()
            }
        }
    }


    private fun animateGifToTarget(
        animationGif: View,
        targetView: View,
        duration: Long = 480L,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        // Avoid double triggering
        if (animationGif.visibility == View.VISIBLE) return

        animationGif.visibility = View.VISIBLE

        animationGif.post {
            // Get screen positions
            val targetLocation = IntArray(2)
            targetView.getLocationOnScreen(targetLocation)
            val targetX = targetLocation[0].toFloat()
            val targetY = targetLocation[1].toFloat()

            val currentLocation = IntArray(2)
            animationGif.getLocationOnScreen(currentLocation)
            val currentX = currentLocation[0].toFloat()
            val currentY = currentLocation[1].toFloat()

            val deltaX = targetX - currentX
            val deltaY = targetY - currentY

            // 👇 Move in a straight line with linear interpolator
            val translateX =
                ObjectAnimator.ofFloat(animationGif, "translationX", 0f, deltaX).apply {
                    interpolator = LinearInterpolator()
                }
            val translateY =
                ObjectAnimator.ofFloat(animationGif, "translationY", 0f, deltaY).apply {
                    interpolator = LinearInterpolator()
                }

            // 👇 Flip upward (like a calendar page)
            val flip = ObjectAnimator.ofFloat(animationGif, "rotationX", 0f, -90f).apply {
                interpolator = DecelerateInterpolator()
            }

            // 👇 Combine flip + movement
            AnimatorSet().apply {
                playTogether(translateX, translateY, flip)
                this.duration = duration
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animationGif.visibility = View.GONE
                        animationGif.translationX = 0f
                        animationGif.translationY = 0f
                        animationGif.rotationX = 0f
                        onAnimationEnd?.invoke()
                    }
                })
                start()
            }
        }
    }


}
