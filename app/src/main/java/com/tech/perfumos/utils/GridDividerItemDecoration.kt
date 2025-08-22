package com.tech.perfumos.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.tech.perfumos.R


class GridDividerItemDecoration( val context: Context,
    private val spanCount: Int,
    dividerHeightDp: Int,
  ) :
    ItemDecoration()  {

    private val dividerHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dividerHeightDp.toFloat(),
        context.resources.displayMetrics
    ).toInt()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val colorStart = Color.parseColor("#A8D7D9")
    private val colorCenter = Color.parseColor("#E6FEFF")
    private val colorEnd = Color.parseColor("#A8D7D9")
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            if ((i + 1) % spanCount == 0) {
                val left = parent.paddingLeft.toFloat()
                val right = (parent.width - parent.paddingRight).toFloat()

                val overlapMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    3f, // you can change this value
                    parent.context.resources.displayMetrics
                )

                val top = child.bottom - overlapMargin
                val bottom = top + dividerHeight

                val gradient = LinearGradient(
                    left, top, right, top,
                    intArrayOf(ContextCompat.getColor(context, R.color.itemDecorate),
                        ContextCompat.getColor(context, R.color.itemDecorate1),
                        ContextCompat.getColor(context, R.color.itemDecorate2),
                        ContextCompat.getColor(context, R.color.itemDecorate0),
                        ContextCompat.getColor(context, R.color.itemDecorate),
                        ),
                    /*intArrayOf(
                        Color.parseColor("#A8D7D9"),
                        Color.parseColor("#B9E1E3"),
                        Color.parseColor("#BCE4E5"),
                        Color.parseColor("#E6FEFF"),
                        Color.parseColor("#A8D7D9")
                    ),*/
                    floatArrayOf(0f, 0.13f, 0.16f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )

                paint.shader = gradient

                canvas.drawRect(left, top, right, bottom, paint)
            }
        }

        paint.shader = null
    }

//    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//        val childCount = parent.childCount
//
//        for (i in 0 until childCount) {
//            val child = parent.getChildAt(i)
//
//            // Only draw under the last column of the row
//            if ((i + 1) % spanCount == 0) {
//                val left = parent.paddingLeft.toFloat()
//                val right = (parent.width - parent.paddingRight).toFloat()
//                val top = child.bottom.toFloat()
//                val bottom = top + dividerHeight
//
//                // Create gradient shader
////                val gradient = LinearGradient(
////                    left, top, right, top,
////                    intArrayOf(colorStart, colorCenter, colorEnd),
////                    floatArrayOf(0f, 0.5f, 1f),
////                    Shader.TileMode.CLAMP
////                )
//                val gradient = LinearGradient(
//                    left, top, right, top,
//                    intArrayOf(
//                        Color.parseColor("#A8D7D9"),
//                        Color.parseColor("#B9E1E3"),
//                        Color.parseColor("#BCE4E5"),
//                        Color.parseColor("#E6FEFF"),
//                        Color.parseColor("#A8D7D9")
//                    ),
//                    floatArrayOf(0f, 0.13f, 0.16f, 0.5f, 1f),
//                    Shader.TileMode.CLAMP
//                )
//                paint.shader = gradient
//
//                canvas.drawRect(left, top, right, bottom, paint)
//            }
//        }
//
//        paint.shader = null // clear shader
//    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if ((position + 1) % spanCount == 0) {
            outRect.bottom = dividerHeight
        }
    }
}

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int, // in pixels
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}