package com.tech.perfumos.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View





class GradientStrokeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    View(context, attrs, defStyle) {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderWidth = 4f   // 4dp (assuming x2 density)
    private val cornerRadius = 30f // 10dp (assuming x4 density)
    private val shadowRadius = 10f // Shadow blur radius
    private val shadowDx = 0f      // Horizontal shadow offset
    private val shadowDy = 6f      // Vertical shadow offset
    private val shadowColor = Color.parseColor("#67E9E9") // Red shadow with 50% opacity

    init {
        // Solid base fill
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = Color.parseColor("#7f7f7f") // Pure white fill

        // Blur effect layer
        blurPaint.style = Paint.Style.FILL

        // Border gradient
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth

        // Shadow paint
        shadowPaint.style = Paint.Style.STROKE
        shadowPaint.strokeWidth = borderWidth // Match border width for shadow
        shadowPaint.color = Color.TRANSPARENT // Transparent to avoid fill interference
        shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)

        // Disable hardware acceleration for shadow rendering
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        // Add padding to prevent shadow clipping
        setPadding(
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            (shadowRadius + shadowDy).toInt()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Ensure minimum size accounts for shadow and border
        val minWidth = (2 * (borderWidth + shadowRadius) + suggestedMinimumWidth).toInt()
        val minHeight = (2 * (borderWidth + shadowRadius + shadowDy) + suggestedMinimumHeight).toInt()
        setMeasuredDimension(
            resolveSize(minWidth, widthMeasureSpec),
            resolveSize(minHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val halfBorder = borderWidth / 2
        // Adjust RectF to account for padding and shadow
        val rectF = RectF(
            paddingLeft + halfBorder,
            paddingTop + halfBorder,
            width - paddingRight - halfBorder,
            height - paddingBottom - halfBorder
        )

        // Draw shadow along the border path
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, shadowPaint)

        // Fill base with pure white
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint)

        // Blur effect layer with soft gradient
        blurPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.left, rectF.bottom,
            Color.parseColor("#1AFFFFFF"),  // White with ~10% opacity at top
            Color.parseColor("#1ACCCCCC"),  // Light gray with ~10% opacity at bottom
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, blurPaint)

        // Border gradient
        /*borderPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.right, rectF.top,
            Color.parseColor("#006AFA"),
            Color.parseColor("#67E9E9"),
            Shader.TileMode.CLAMP
        )*/

        borderPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.left, rectF.bottom,
            Color.parseColor("#006AFA"),
            Color.parseColor("#67E9E9"),
            Shader.TileMode.CLAMP
        )

        // Draw the border
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
    }
}

class GradientStrokeView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    View(context, attrs, defStyle) {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderWidth = 6f   // 4dp (assuming x2 density)
    private val cornerRadius = 35f // 10dp (assuming x4 density)
    private val shadowRadius = 10f // Shadow blur radius
    private val shadowDx = 0f      // Horizontal shadow offset
    private val shadowDy = 6f      // Vertical shadow offset
    private val shadowColor = Color.parseColor("#67E9E9") // Red shadow with 50% opacity

    init {
        // Solid base fill
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = Color.parseColor("#99FFFFFF") // Pure white fill
//        fillPaint.color = Color.parseColor("#59FFFFFF") // Pure white fill

        // Blur effect layer
        blurPaint.style = Paint.Style.FILL

        // Border gradient
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth

        // Shadow paint
        shadowPaint.style = Paint.Style.STROKE
        shadowPaint.strokeWidth = borderWidth // Match border width for shadow
        shadowPaint.color = Color.TRANSPARENT // Transparent to avoid fill interference
        shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)

        // Disable hardware acceleration for shadow rendering
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        // Add padding to prevent shadow clipping
        setPadding(
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            (shadowRadius + shadowDy).toInt()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Ensure minimum size accounts for shadow and border
        val minWidth = (2 * (borderWidth + shadowRadius) + suggestedMinimumWidth).toInt()
        val minHeight = (2 * (borderWidth + shadowRadius + shadowDy) + suggestedMinimumHeight).toInt()
        setMeasuredDimension(
            resolveSize(minWidth, widthMeasureSpec),
            resolveSize(minHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val halfBorder = borderWidth / 2
        // Adjust RectF to account for padding and shadow
        val rectF = RectF(
            paddingLeft + halfBorder,
            paddingTop + halfBorder,
            width - paddingRight - halfBorder,
            height - paddingBottom - halfBorder
        )

        // Draw shadow along the border path
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, shadowPaint)

        // Fill base with pure white
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint)

        // Blur effect layer with soft gradient
        blurPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.left, rectF.bottom,
            Color.parseColor("#1AFFFFFF"),  // White with ~10% opacity at top
            Color.parseColor("#1ACCCCCC"),  // Light gray with ~10% opacity at bottom
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, blurPaint)

        // Border gradient
        borderPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.right, rectF.top,
            Color.parseColor("#006AFA"),
            Color.parseColor("#67E9E9"),
            Shader.TileMode.CLAMP
        )

        // Draw the border
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
    }
}


/*class GradientStrokeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blueOverlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderWidth = 8f   // 4dp (assuming x2 density)
    private val cornerRadius = 40f // 10dp (assuming x4 density)
    private val shadowRadius = 16f // Shadow blur radius
    private val shadowDx = 0f      // Horizontal shadow offset
    private val shadowDy = 8f      // Vertical shadow offset
    private val shadowColor = Color.parseColor("#80FF0000") // Red shadow with 50% opacity

    init {
        // Solid base fill
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = Color.parseColor("#B3FFFFFF") // Solid white fill

        // Blue gradient overlay
        blueOverlayPaint.style = Paint.Style.FILL

        // Border gradient
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth

        // Shadow paint
        shadowPaint.style = Paint.Style.STROKE
        shadowPaint.strokeWidth = borderWidth // Match border width for shadow
        shadowPaint.color = Color.TRANSPARENT // Transparent to avoid fill interference
        shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)

        // Disable hardware acceleration for shadow rendering
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        // Add padding to prevent shadow clipping
        setPadding(
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            (shadowRadius + shadowDy).toInt()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Ensure minimum size accounts for shadow and border
        val minWidth = (2 * (borderWidth + shadowRadius) + suggestedMinimumWidth).toInt()
        val minHeight = (2 * (borderWidth + shadowRadius + shadowDy) + suggestedMinimumHeight).toInt()
        setMeasuredDimension(
            resolveSize(minWidth, widthMeasureSpec),
            resolveSize(minHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val halfBorder = borderWidth / 2
        // Adjust RectF to account for padding and shadow
        val rectF = RectF(
            paddingLeft + halfBorder,
            paddingTop + halfBorder,
            width - paddingRight - halfBorder,
            height - paddingBottom - halfBorder
        )

        // Draw shadow along the border path
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, shadowPaint)

        // Fill base with solid white
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint)

        // Blue gradient overlay effect with very low opacity for blur-like effect
        blueOverlayPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.left, rectF.bottom,
            Color.parseColor("#1A006AFA"),  // Light blue top, ~10% opacity
            Color.parseColor("#1A4AD3FA"),  // Deeper blue bottom, ~10% opacity
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, blueOverlayPaint)

        // Border gradient
        borderPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.right, rectF.top,
            Color.parseColor("#006AFA"),
            Color.parseColor("#67E9E9"),
            Shader.TileMode.CLAMP
        )

        // Draw the border
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
    }
}*/
/*
class GradientStrokeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blueOverlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderWidth = 8f   // 4dp (assuming x2 density)
    private val cornerRadius = 40f // 10dp (assuming x4 density)
    private val shadowRadius = 16f // Shadow blur radius
    private val shadowDx = 0f      // Horizontal shadow offset
    private val shadowDy = 8f      // Vertical shadow offset
    private val shadowColor = Color.parseColor("#80FF0000") // Red shadow with 50% opacity

    init {
        // Solid base fill
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = Color.parseColor("#B3FFFFFF") // Semi-transparent white fill

        // Blue gradient overlay
        blueOverlayPaint.style = Paint.Style.FILL

        // Border gradient
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth

        // Shadow paint
        shadowPaint.style = Paint.Style.STROKE
        shadowPaint.strokeWidth = borderWidth // Match border width for shadow
        shadowPaint.color = Color.TRANSPARENT // Transparent to avoid fill interference
        shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)

        // Disable hardware acceleration for shadow rendering
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        // Add padding to prevent shadow clipping
        setPadding(
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            (shadowRadius + shadowDy).toInt()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Ensure minimum size accounts for shadow and border
        val minWidth = (2 * (borderWidth + shadowRadius) + suggestedMinimumWidth).toInt()
        val minHeight = (2 * (borderWidth + shadowRadius + shadowDy) + suggestedMinimumHeight).toInt()
        setMeasuredDimension(
            resolveSize(minWidth, widthMeasureSpec),
            resolveSize(minHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val halfBorder = borderWidth / 2
        // Adjust RectF to account for padding and shadow
        val rectF = RectF(
            paddingLeft + halfBorder,
            paddingTop + halfBorder,
            width - paddingRight - halfBorder,
            height - paddingBottom - halfBorder
        )

        // Draw shadow along the border path
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, shadowPaint)

        // Fill base with semi-transparent white
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint)

        // Blue gradient overlay effect with softer, more diffused colors
        blueOverlayPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.left, rectF.bottom,
            Color.parseColor("#80006AFA"),  // Light blue top, increased opacity
            Color.parseColor("#804AD3FA"),  // Deeper blue bottom, increased opacity
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, blueOverlayPaint)

        // Border gradient
        borderPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.right, rectF.top,
            Color.parseColor("#006AFA"),
            Color.parseColor("#67E9E9"),
            Shader.TileMode.CLAMP
        )

        // Draw the border
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
    }
}*/
//class GradientStrokeView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyle: Int = 0
//) :
//    View(context, attrs, defStyle) {
//
//    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val blueOverlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val borderWidth = 8f   // 4dp (assuming x2 density)
//    private val cornerRadius = 40f // 10dp (assuming x4 density)
//    private val shadowRadius = 16f // Shadow blur radius
//    private val shadowDx = 0f      // Horizontal shadow offset
//    private val shadowDy = 8f      // Vertical shadow offset
//    private val shadowColor = Color.parseColor("#80FF0000") // Red shadow with 50% opacity
//
//    init {
//        // Solid base fill
//        fillPaint.style = Paint.Style.FILL
//        fillPaint.color = Color.parseColor("#B3FFFFFF") // Updated to requested fill color
//
//        // Blue gradient overlay
//        blueOverlayPaint.style = Paint.Style.FILL
//
//        // Border gradient
//        borderPaint.style = Paint.Style.STROKE
//        borderPaint.strokeWidth = borderWidth
//
//        // Shadow paint
//        shadowPaint.style = Paint.Style.STROKE
//        shadowPaint.strokeWidth = borderWidth // Match border width for shadow
//        shadowPaint.color = Color.TRANSPARENT // Transparent to avoid fill interference
//        shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)
//
//        // Disable hardware acceleration for shadow rendering
//        setLayerType(LAYER_TYPE_SOFTWARE, null)
//
//        // Add padding to prevent shadow clipping
//        setPadding(
//            shadowRadius.toInt(),
//            shadowRadius.toInt(),
//            shadowRadius.toInt(),
//            (shadowRadius + shadowDy).toInt()
//        )
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        // Ensure minimum size accounts for shadow and border
//        val minWidth = (2 * (borderWidth + shadowRadius) + suggestedMinimumWidth).toInt()
//        val minHeight = (2 * (borderWidth + shadowRadius + shadowDy) + suggestedMinimumHeight).toInt()
//        setMeasuredDimension(
//            resolveSize(minWidth, widthMeasureSpec),
//            resolveSize(minHeight, heightMeasureSpec)
//        )
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        val halfBorder = borderWidth / 2
//        // Adjust RectF to account for padding and shadow
//        val rectF = RectF(
//            paddingLeft + halfBorder,
//            paddingTop + halfBorder,
//            width - paddingRight - halfBorder,
//            height - paddingBottom - halfBorder
//        )
//
//        // Draw shadow along the border path
//        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, shadowPaint)
//
//        // Fill base with semi-transparent white
//        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint)
//
//        // Blue gradient overlay effect
//        blueOverlayPaint.shader = LinearGradient(
//            rectF.left, rectF.top, rectF.left, rectF.bottom,
//            Color.parseColor("#33006AFA"),  // Light blue top
//            Color.parseColor("#334AD3FA"),  // Deeper blue bottom
//            Shader.TileMode.CLAMP
//        )
//        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, blueOverlayPaint)
//
//        // Border gradient
//        borderPaint.shader = LinearGradient(
//            rectF.left, rectF.top, rectF.right, rectF.top,
//            Color.parseColor("#006AFA"),
//            Color.parseColor("#67E9E9"),
//            Shader.TileMode.CLAMP
//        )
//
//        // Draw the border
//        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
//    }
//}

/*
class GradientStrokeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :

    View(context, attrs, defStyle) {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blueOverlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderWidth = 8f   // 4dp (assuming x2 density)
    private val cornerRadius = 40f // 10dp (assuming x4 density)
    private val shadowRadius = 16f // Shadow blur radius
    private val shadowDx = 0f      // Horizontal shadow offset
    private val shadowDy = 8f      // Vertical shadow offset
    private val shadowColor = Color.parseColor("#80FF0000") // Red shadow with 50% opacity

    init {
        // Solid base fill
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = Color.parseColor("#B3FFFFFF") // Updated to requested fill color

        // Blue gradient overlay
        blueOverlayPaint.style = Paint.Style.FILL

        // Border gradient
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth

        // Shadow paint
        shadowPaint.style = Paint.Style.STROKE
        shadowPaint.strokeWidth = borderWidth // Match border width for shadow
        shadowPaint.color = Color.TRANSPARENT // Transparent to avoid fill interference
        shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)

        // Disable hardware acceleration for shadow rendering
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        // Add padding to prevent shadow clipping
        setPadding(
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            (shadowRadius + shadowDy).toInt()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Ensure minimum size accounts for shadow and border
        val minWidth = (2 * (borderWidth + shadowRadius) + suggestedMinimumWidth).toInt()
        val minHeight = (2 * (borderWidth + shadowRadius + shadowDy) + suggestedMinimumHeight).toInt()
        setMeasuredDimension(
            resolveSize(minWidth, widthMeasureSpec),
            resolveSize(minHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val halfBorder = borderWidth / 2
        // Adjust RectF to account for padding and shadow
        val rectF = RectF(
            paddingLeft + halfBorder,
            paddingTop + halfBorder,
            width - paddingRight - halfBorder,
            height - paddingBottom - halfBorder
        )

        // Draw shadow along the border path
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, shadowPaint)

        // Fill base with semi-transparent white
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint)

        // Blue gradient overlay effect
        blueOverlayPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.left, rectF.bottom,
            Color.parseColor("#33006AFA"),  // Light blue top
            Color.parseColor("#334AD3FA"),  // Deeper blue bottom
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, blueOverlayPaint)

        // Border gradient
        borderPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.right, rectF.top,
            Color.parseColor("#006AFA"),
            Color.parseColor("#67E9E9"),
            Shader.TileMode.CLAMP
        )

        // Draw the border
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
    }
}*/

/*class GradientStrokeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    View(context, attrs, defStyle) {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blueOverlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderWidth = 8f   // 4dp (assuming x2 density)
    private val cornerRadius = 40f // 10dp (assuming x4 density)
    private val shadowRadius = 16f // Shadow blur radius
    private val shadowDx = 0f      // Horizontal shadow offset
    private val shadowDy = 8f      // Vertical shadow offset
    private val shadowColor = Color.parseColor("#80FF0000") // Red shadow with 50% opacity

    init {
        // Solid base fill
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = Color.parseColor("#59FFFFFF")

        // Blue gradient overlay
        blueOverlayPaint.style = Paint.Style.FILL

        // Border gradient
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth

        // Shadow paint
        shadowPaint.style = Paint.Style.FILL
        shadowPaint.color = Color.WHITE // Background for shadow to contrast
        shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)

        // Disable hardware acceleration for shadow rendering
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        // Add padding to prevent shadow clipping
        setPadding(
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            shadowRadius.toInt(),
            (shadowRadius + shadowDy).toInt()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Ensure minimum size accounts for shadow and border
        val minWidth = (2 * (borderWidth + shadowRadius) + suggestedMinimumWidth).toInt()
        val minHeight = (2 * (borderWidth + shadowRadius + shadowDy) + suggestedMinimumHeight).toInt()
        setMeasuredDimension(
            resolveSize(minWidth, widthMeasureSpec),
            resolveSize(minHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val halfBorder = borderWidth / 2
        // Adjust RectF to account for padding and shadow
        val rectF = RectF(
            paddingLeft + halfBorder,
            paddingTop + halfBorder,
            width - paddingRight - halfBorder,
            height - paddingBottom - halfBorder
        )

        // Draw shadow first
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, shadowPaint)

        // Fill base white with transparency
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint)

        // Blue gradient overlay effect
        blueOverlayPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.left, rectF.bottom,
            Color.parseColor("#33006AFA"),  // Light blue top
            Color.parseColor("#334AD3FA"),  // Deeper blue bottom
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, blueOverlayPaint)

        // Border gradient
        borderPaint.shader = LinearGradient(
            rectF.left, rectF.top, rectF.right, rectF.top,
            Color.parseColor("#006AFA"),
            Color.parseColor("#67E9E9"),
            Shader.TileMode.CLAMP
        )

        // Draw the border
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
    }
}*/
//class GradientStrokeView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyle: Int = 0
//) : View(context, attrs, defStyle) {
//
//    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val blueOverlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val borderWidth = 8f   // 4dp (assuming x2 density)
//    private val cornerRadius = 40f // 10dp (assuming x4 density)
//
//    init {
//        // Solid base fill
//        fillPaint.style = Paint.Style.FILL
//        fillPaint.color = Color.parseColor("#59FFFFFF")
//
//        // Blue gradient overlay (we’ll set shader dynamically)
//        blueOverlayPaint.style = Paint.Style.FILL
//
//        // Border gradient
//        borderPaint.style = Paint.Style.STROKE
//        borderPaint.strokeWidth = borderWidth
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        val halfBorder = borderWidth / 2
//        val rectF = RectF(
//            halfBorder,
//            halfBorder,
//            width.toFloat() - halfBorder,
//            height.toFloat() - halfBorder
//        )
//
//        // Fill base white with transparency
//        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint)
//
//        // Blue gradient overlay effect
//        blueOverlayPaint.shader = LinearGradient(
//            0f, 0f, 0f, height.toFloat(), // vertical gradient
//            Color.parseColor("#33006AFA"),  // light blue top
//            Color.parseColor("#334AD3FA"),  // deeper blue bottom
//            Shader.TileMode.CLAMP
//        )
//        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, blueOverlayPaint)
//
//        // Border gradient (recreate shader on resize)
//        borderPaint.shader = LinearGradient(
//            0f, 0f, width.toFloat(), 0f,
//            Color.parseColor("#006AFA"),
//            Color.parseColor("#67E9E9"),
//            Shader.TileMode.CLAMP
//        )
//
//        // Draw the border
//        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
//    }
//}

//
//class GradientStrokeView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyle: Int = 0
//) : View(context, attrs, defStyle) {
//
//    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val borderWidth = 8f   // 4dp (assuming x2 density)
//    private val cornerRadius = 40f // 10dp (assuming x4 density)
//
//    init {
//        // Set up the fill paint
//        fillPaint.style = Paint.Style.FILL
//        fillPaint.color = Color.parseColor("#59FFFFFF")
//
//        // Set up the border paint with a linear gradient
//        borderPaint.style = Paint.Style.STROKE
//        borderPaint.strokeWidth = borderWidth
//        borderPaint.shader = LinearGradient(
//            0f, 0f, width.toFloat(), 0f,
//            Color.parseColor("#006AFA"),
//            Color.parseColor("#67E9E9"),
//            Shader.TileMode.CLAMP
//        )
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        val halfBorder = borderWidth / 2
//        val rectF = RectF(
//            halfBorder,
//            halfBorder,
//            width.toFloat() - halfBorder,
//            height.toFloat() - halfBorder
//        )
//
//        // Re-apply gradient with current width in case view was resized
//        borderPaint.shader = LinearGradient(
//            0f, 0f, width.toFloat(), 0f,
//            Color.parseColor("#006AFA"),
//            Color.parseColor("#67E9E9"),
//            Shader.TileMode.CLAMP
//        )
//
//        // Draw the filled rounded rectangle
//        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint)
//
//        // Draw the border over it
//        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
//    }
//}
