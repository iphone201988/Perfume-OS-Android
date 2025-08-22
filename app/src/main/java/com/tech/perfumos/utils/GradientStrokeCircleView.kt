package com.tech.perfumos.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import com.tech.perfumos.R

class GradientStrokeCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ShapeableImageView(context, attrs) {

    private val strokeWidthDp = 5f
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val rect = RectF()

    init {
        val strokeWidthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            strokeWidthDp,
            resources.displayMetrics
        )
        strokePaint.strokeWidth = strokeWidthPx
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val strokeWidth = strokePaint.strokeWidth
        rect.set(strokeWidth, strokeWidth, w - strokeWidth, h - strokeWidth)

        // Set gradient shader for stroke
        strokePaint.shader = SweepGradient(
            w / 2f, h / 2f,
            intArrayOf(
                ContextCompat.getColor(context, R.color.gradient1),
                ContextCompat.getColor(context, R.color.gradient2)
            ),
            null
        )

    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawOval(rect, strokePaint)
    }
}
