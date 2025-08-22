package com.tech.perfumos.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.res.ResourcesCompat
import com.tech.perfumos.R

class CustomGradientSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.seekBarStyle
) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    private var startColor: Int = Color.BLUE
    private var endColor: Int = Color.CYAN
    private var cornerRadius: Float = dpToPx(30f)
    private var trackHeight: Float = dpToPx(20f)

    private var seekEnable: Boolean = true
    private var backgroundColor: Int = Color.WHITE
    private var thumbColorStart: Int = Color.BLUE
    private var thumbColorEnd: Int = Color.CYAN
    private var thumbSize: Float = dpToPx(32f)

    private var thumbStroke: Float = dpToPx(1.5f)
    private var thumbStrokeColor: Int = Color.CYAN

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var thumbDrawable: Drawable? = null

    private var labelText: String = ""
    private var progressShadow: Boolean = false
    private var showBackground: Boolean = true
    private var labelTextColor: Int = Color.BLACK
    private var labelTextSize: Float = dpToPx(14f)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomGradientSeekBar, 0, 0).apply {
            try {
                startColor = getColor(
                    R.styleable.CustomGradientSeekBar_progressGradientStartColor,
                    startColor
                )
                endColor =
                    getColor(R.styleable.CustomGradientSeekBar_progressGradientEndColor, endColor)
                cornerRadius =
                    getDimension(R.styleable.CustomGradientSeekBar_cornerRadius, cornerRadius)
                trackHeight =
                    getDimension(R.styleable.CustomGradientSeekBar_trackHeight, trackHeight)
                backgroundColor =
                    getColor(R.styleable.CustomGradientSeekBar_seekBackgroundColor, backgroundColor)
                thumbColorStart =
                    getColor(R.styleable.CustomGradientSeekBar_thumbColorStart, thumbColorStart)
                thumbColorEnd =
                    getColor(R.styleable.CustomGradientSeekBar_thumbColorEnd, thumbColorEnd)
                thumbSize = getDimension(R.styleable.CustomGradientSeekBar_thumbSize, thumbSize)

                thumbStroke =
                    getDimension(R.styleable.CustomGradientSeekBar_thumbStrokeWidth, thumbStroke)
                thumbStrokeColor =
                    getColor(R.styleable.CustomGradientSeekBar_thumbStrokeColor, thumbStrokeColor)

                labelText = getString(R.styleable.CustomGradientSeekBar_labelText) ?: ""
                progressShadow = getBoolean(R.styleable.CustomGradientSeekBar_progressShadow, false)
                showBackground = getBoolean(R.styleable.CustomGradientSeekBar_show_background, true)
                seekEnable = getBoolean(R.styleable.CustomGradientSeekBar_seekEnable, true)
                labelTextColor =
                    getColor(R.styleable.CustomGradientSeekBar_labelTextColor, Color.BLACK)
                labelTextSize =
                    getDimension(R.styleable.CustomGradientSeekBar_labelTextSize, labelTextSize)

                labelPaint.color = labelTextColor
                labelPaint.textSize = labelTextSize
                labelPaint.textAlign = Paint.Align.LEFT
                isEnabled = seekEnable

            } finally {
                recycle()
            }
        }

        thumb = ColorDrawable(Color.TRANSPARENT) // prevent default thumb
        thumbDrawable = createThumbDrawable()
        Log.d("seekEnable", ": seekEnable -> $seekEnable")


    }

    override fun onDraw(canvas: Canvas) {
        val centerY = height / 2f
        val availableWidth = width - paddingLeft - paddingRight
        val progressRatio = progress.toFloat() / max
        val progressX = paddingLeft + availableWidth * progressRatio

        // Draw background track
        if(showBackground){

        val trackRect = RectF(
            paddingLeft.toFloat(),
            centerY - trackHeight / 2,
            width - paddingRight.toFloat(),
            centerY + trackHeight / 2
        )
        paint.shader = null
        //paint.color = Color.WHITE
        paint.color = backgroundColor
        canvas.drawRoundRect(trackRect, cornerRadius, cornerRadius, paint)
        }

        if (progressShadow) {
            val linearShadow = LinearGradient(
                progressX, 0f, progressX + dpToPx(12f), 0f,
                //Color.parseColor("#44000000"), Color.TRANSPARENT,
                Color.parseColor("#33000000"), Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            paint.shader = linearShadow
            canvas.drawRoundRect(
                progressX - dpToPx(23f),
                centerY - trackHeight / 2,
                progressX + dpToPx(24f),
                centerY + trackHeight / 2,
                0f,
                0f,
                paint
            )
        }


        // Draw gradient progress
        val shader = LinearGradient(
            paddingLeft.toFloat(), 0f, progressX, 0f,
            startColor, endColor, Shader.TileMode.CLAMP
        )
        paint.shader = shader
        val progressRect = RectF(
            paddingLeft.toFloat(),
            centerY - trackHeight / 2,
            progressX,
            centerY + trackHeight / 2
        )
        canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, paint)

        paint.shader = null

        val typeface = ResourcesCompat.getFont(context, R.font.alice_regular)
        labelPaint.typeface = typeface


        if (labelText.isNotEmpty()) {
            val fontMetrics = labelPaint.fontMetrics
            val textHeight = fontMetrics.descent - fontMetrics.ascent
            val textBaseline = height / 2f + textHeight / 2f - fontMetrics.descent

            // Draw text at start (left-aligned, just after padding)
            val paddingLeft = paddingLeft + 30f
            canvas.drawText(labelText, paddingLeft, textBaseline, labelPaint)
        }

        thumbDrawable?.let { thumb ->
            val drawableSize = thumb.intrinsicWidth
            val thumbX = progressX - drawableSize +10
            val thumbY = centerY - drawableSize / 2
            thumb.setBounds(
                thumbX.toInt(),
                thumbY.toInt(),
                (thumbX + drawableSize).toInt(),
                (thumbY + drawableSize).toInt()
            )
            canvas.save()
            thumb.draw(canvas)
            canvas.restore()
        }
    }


    private fun createThumbDrawable(): Drawable {
        val padding = dpToPx(6f).toInt() // padding around the circle to allow shadow
        val canvasSize = thumbSize.toInt() + padding * 2

        val bitmap = Bitmap.createBitmap(canvasSize, canvasSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val centerX = canvasSize / 2f
        val centerY = canvasSize / 2f
        val radius = thumbSize / 2f

        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = thumbColorStart
            style = Paint.Style.FILL
            setShadowLayer(dpToPx(4f), 0f, dpToPx(2f), Color.parseColor("#33000000"))
        }


        // Required for shadow to render
        setLayerType(LAYER_TYPE_SOFTWARE, shadowPaint)

        // Card-like base (background + shadow)
        canvas.drawCircle(centerX, centerY, radius, shadowPaint)

        // Gradient fill
        val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, thumbSize, thumbSize,
                thumbColorStart, thumbColorEnd, Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, radius * 0.9f, gradientPaint)

        if (thumbStrokeColor != Color.CYAN) {
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = thumbStrokeColor
                style = Paint.Style.STROKE
                strokeWidth = thumbStroke
            }
            canvas.drawCircle(centerX, centerY, radius * 0.9f, strokePaint)
        }
        // White border


        val drawable = BitmapDrawable(resources, bitmap)
        drawable.setBounds(0, 0, canvasSize, canvasSize)
        drawable.paint.isAntiAlias = true
        return drawable
    }


    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    fun showHideProgressShadow(show:Boolean){
        progressShadow = show
        invalidate()
    }

    fun setLabelText(value: String?) {
        labelText = value ?: ""
        invalidate()
    }

    fun setLabelTextColor(@ColorInt value: Int) {
        labelTextColor = value
        labelPaint.color = value
        invalidate()
    }

    fun setProgressStartColor(@ColorInt value: Int) {
        startColor = value
        invalidate()
    }

    fun setProgressEndColor(@ColorInt value: Int) {
        endColor = value
        invalidate()
    }

    fun setThumbColor(@ColorInt value: Int) {
        thumbColorStart = value
        thumbColorEnd = value
        thumbDrawable = createThumbDrawable()
        invalidate()
    }
}