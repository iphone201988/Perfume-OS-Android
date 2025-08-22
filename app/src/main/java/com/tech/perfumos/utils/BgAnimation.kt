package com.tech.perfumos.utils

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.tech.perfumos.R
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class BgAnimation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val image1: ImageView
    private val image2: ImageView

    val marginX = 500f
    val marginY = 2000f

    init {

        LayoutInflater.from(context).inflate(R.layout.bg_animation_layout, this, true)

        image1 = findViewById(R.id.circleImage1)
        image2 = findViewById(R.id.circleImage2)

        post {
            // startCircularAnimation()

            Log.d("image1", "initial image1: ${image1.x} , ${image1.y}")
            Log.d("image1", "initial image2: ${image2.x} , ${image2.y}")
            //  startCircularAnimation1()
            newAnimation()

        }


    }

    // Extension to convert degrees to radians
    private fun Float.toRadians() = Math.toRadians(this.toDouble())


    private fun newAnimation() {
        val center1X = image1.x + image1.width / 2
        val center1Y = image1.y + image1.height / 2
        val center2X = image2.x + image2.width / 2
        val center2Y = image2.y + image2.height / 2

        // Midpoint = center of circle path
        val midX = (center1X + center2X) / 2
        val midY = (center1Y + center2Y) / 2

        // Radius = half the distance between centers
        val radius = hypot(center2X - center1X, center2Y - center1Y) / 2

        // ValueAnimator for 360 degrees
        val animator = ValueAnimator.ofFloat(0f, 360f)
        animator.duration = 16000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener { animation ->
            val angle = (animation.animatedValue as Float).toDouble()
            Log.d("angle", "newAnimation: $angle")
            // Convert angle to radians
            val radians = Math.toRadians(angle)

            // Position on circle
            val xOffset = cos(radians) * radius
            val yOffset = sin(radians) * radius

            // Move centers in circular path
            image1.x = (midX + xOffset - image1.width / 2).toFloat()
            image1.y = (midY + yOffset - image1.height / 2).toFloat()

            image2.x = (midX - xOffset - image2.width / 2).toFloat()
            image2.y = (midY - yOffset - image2.height / 2).toFloat()

            // Optional: rotate images
            image1.rotation = angle.toFloat()
            image2.rotation = -angle.toFloat()


        }
        animator.start()
    }


    // Extension function to square a float
    private fun Float.pow(exponent: Int): Float =
        Math.pow(this.toDouble(), exponent.toDouble()).toFloat()

}