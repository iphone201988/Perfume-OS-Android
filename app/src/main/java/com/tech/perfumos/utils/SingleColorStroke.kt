package com.tech.perfumos.utils


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import com.tech.perfumos.R
//
//
//class SingleColorStroke @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null
//) : ShapeableImageView(context, attrs) {
//
//    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        style = Paint.Style.STROKE
//    }
//    private val rect = RectF()
//
//    private var strokeColor: Int = ContextCompat.getColor(context, android.R.color.black)
//    private var strokeWidthPx: Float = dpToPx(5f)
//
//    init {
//        context.theme.obtainStyledAttributes(
//            attrs,
//            R.styleable.GradientStrokeCircleView,
//            0, 0
//        ).apply {
//            try {
//                strokeColor = getColor(
//                    R.styleable.GradientStrokeCircleView_strokeColor,
//                    strokeColor
//                )
//                strokeWidthPx = getDimension(
//                    R.styleable.GradientStrokeCircleView_strokeWidth,
//                    strokeWidthPx
//                )
//            } finally {
//                recycle()
//            }
//        }
//
//        strokePaint.color = strokeColor
//        strokePaint.strokeWidth = strokeWidthPx
//    }
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        updateRect()
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        canvas.drawOval(rect, strokePaint)
//    }
//
//    private fun updateRect() {
//        val strokeWidth = strokePaint.strokeWidth
//        rect.set(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth)
//        invalidate()
//    }
//
//    private fun dpToPx(dp: Float): Float {
//        return TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            dp,
//            resources.displayMetrics
//        )
//    }
//
//    // --- Dynamic Setters ---
//    fun setStrokeColor(color: Int) {
//        strokeColor = color
//        strokePaint.color = strokeColor
//        invalidate()
//    }
//
//    fun setStrokeWidthDp(dp: Float) {
//        strokeWidthPx = dpToPx(dp)
//        strokePaint.strokeWidth = strokeWidthPx
//        updateRect()
//    }
//
//    fun setStrokeWidthPx(px: Float) {
//        strokeWidthPx = px
//        strokePaint.strokeWidth = strokeWidthPx
//        updateRect()
//    }
//}

import android.graphics.*

class SingleColorStroke @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ShapeableImageView(context, attrs) {

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val clipPath = Path()
    private var strokeColor: Int = Color.BLACK
    private var strokeWidthPx: Float = dpToPx(5f)
    private val rect = RectF()

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GradientStrokeCircleView,
            0, 0
        ).apply {
            try {
                strokeColor = getColor(
                    R.styleable.GradientStrokeCircleView_strokeColor,
                    strokeColor
                )
                strokeWidthPx = getDimension(
                    R.styleable.GradientStrokeCircleView_strokeWidth,
                    strokeWidthPx
                )
            } finally {
                recycle()
            }
        }

        strokePaint.color = strokeColor
        strokePaint.strokeWidth = strokeWidthPx
        scaleType = ScaleType.CENTER_CROP
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val inset = strokeWidthPx / 2f
        rect.set(inset, inset, w - inset, h - inset)

        // Create circular clip path
        clipPath.reset()
        clipPath.addOval(rect, Path.Direction.CW)
        clipPath.close()
    }

    override fun onDraw(canvas: Canvas) {
        // Clip to circle before drawing image
        val save = canvas.save()
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
        canvas.restoreToCount(save)

        // Draw stroke
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
    fun setStrokeColor(color: Int) {
        strokeColor = color
        strokePaint.color = strokeColor
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
