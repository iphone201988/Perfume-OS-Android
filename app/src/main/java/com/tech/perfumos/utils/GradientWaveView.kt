package com.tech.perfumos.utils
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class GradientWaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var shader: LinearGradient? = null
    private val shaderMatrix = Matrix()
    private var animator: ValueAnimator? = null
    private var offset = 0f
    private val colorStart = Color.parseColor("#006AFA")
    private val colorEnd = Color.parseColor("#67E9E9")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    private fun startAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 10000L // Slower = smoother
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                offset = it.animatedFraction
                invalidate()
            }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Extend the gradient beyond bounds to allow smooth translation
        shader = LinearGradient(
            -w.toFloat(), -h.toFloat(), 2 * w.toFloat(), 2 * h.toFloat(), // Big gradient bounds
            intArrayOf(colorStart, colorEnd, colorStart),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        shader?.let {
            val dx = width * offset
            val dy = height * offset

            shaderMatrix.setTranslate(-dx, -dy)
            it.setLocalMatrix(shaderMatrix)

            paint.shader = it
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }
}
