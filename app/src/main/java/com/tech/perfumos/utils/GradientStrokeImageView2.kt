package com.tech.perfumos.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import com.tech.perfumos.R

class  GradientStrokeImageView2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ShapeableImageView(context, attrs) {

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val clipPath = Path()
    private var strokeWidthPx: Float = dpToPx(5f)
    private val rect = RectF()

    private var gradientColors: IntArray = intArrayOf(Color.RED, Color.BLUE)
    private var gradientPositions: FloatArray? = null
    private var gradient: Shader? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GradientStrokeCircleView2,
            0, 0
        ).apply {
            try {
                strokeWidthPx = getDimension(
                    R.styleable.GradientStrokeCircleView2_strokeWidth2,
                    strokeWidthPx
                )

                val startColor = getColor(
                    R.styleable.GradientStrokeCircleView2_strokeGradientStartColor,
                    ContextCompat.getColor(context, R.color.gradient2)
                )
                val endColor = getColor(
                    R.styleable.GradientStrokeCircleView2_strokeGradientEndColor,
                    ContextCompat.getColor(context, R.color.gradient1)
                )
                //gradientColors = intArrayOf(startColor, endColor)
                gradientColors = intArrayOf(
                    Color.parseColor("#006AFA"),
                    Color.parseColor("#006AFA"),
                    Color.parseColor("#67E9E9"),
                    Color.parseColor("#67E9E9"),
                   /* Color.parseColor("#feda75"),
                    Color.parseColor("#fa7e1e"),
                    Color.parseColor("#d62976"),
                    Color.parseColor("#962fbf"),
                    Color.parseColor("#4f5bd5"),
                    Color.parseColor("#feda75")*/ // repeat first for smooth loop
                )
            } finally {
                recycle()
            }
        }

        strokePaint.strokeWidth = strokeWidthPx
        scaleType = ScaleType.CENTER_CROP
    }

    /*override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val inset = strokeWidthPx / 2f
        rect.set(inset, inset, w - inset, h - inset)

        clipPath.reset()
        clipPath.addOval(rect, Path.Direction.CW)
        clipPath.close()

        // Create gradient shader for the stroke
        gradient = SweepGradient(
            w / 2f, h / 2f,
            gradientColors,
            gradientPositions
        )
        strokePaint.shader = gradient
    }*/

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val inset = strokeWidthPx / 2f
        rect.set(inset, inset, w - inset, h - inset)

        clipPath.reset()
        clipPath.addOval(rect, Path.Direction.CW)
        clipPath.close()

        // Use LinearGradient for top-to-bottom effect
        gradient = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            gradientColors,
            gradientPositions,
            Shader.TileMode.CLAMP
        )
        strokePaint.shader = gradient
    }

    override fun onDraw(canvas: Canvas) {
        val save = canvas.save()
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
        canvas.restoreToCount(save)

        canvas.drawOval(rect, strokePaint)
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }

    // --- Dynamic Setters ---
    fun setGradientColors(colors: IntArray, positions: FloatArray? = null) {
        gradientColors = colors
        gradientPositions = positions
        gradient = SweepGradient(width / 2f, height / 2f, gradientColors, gradientPositions)
        strokePaint.shader = gradient
        invalidate()
    }

    fun setStrokeWidthDp(dp: Float) {
        strokeWidthPx = dpToPx(dp)
        strokePaint.strokeWidth = strokeWidthPx
        requestLayout()
    }

    fun setStrokeWidthPx(px: Float) {
        strokeWidthPx = px
        strokePaint.strokeWidth = strokeWidthPx
        requestLayout()
    }
}
