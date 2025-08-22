package com.tech.perfumos.utils.charts

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.Log
import com.github.mikephil.charting.charts.RadarChart

import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.renderer.RadarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import androidx.core.graphics.toColorInt

class CircularWebRadarChartRenderer(
    chart: RadarChart,
    animator: com.github.mikephil.charting.animation.ChartAnimator,
    viewPortHandler: ViewPortHandler
) : RadarChartRenderer(chart, animator, viewPortHandler) {

    // Draw all web lines as circles
    override fun drawWeb(c: Canvas?) {
        if (c == null) return

        val yAxis = mChart.yAxis
        val center = mChart.centerOffsets
        val factor = mChart.factor
        val labelCount = yAxis.labelCount

        mWebPaint.color = mChart.webColor
        mWebPaint.strokeWidth = mChart.webLineWidth

        // Draw concentric circles for each web line
        for (i in 1..labelCount) {
            val r = (i / labelCount.toFloat()) * (yAxis.axisMaximum - yAxis.axisMinimum) * factor
            c.drawCircle(center.x, center.y, r, mWebPaint)
        }

        // Draw radial lines from center to each label (optional)
        val sliceAngle = mChart.sliceAngle
        val entryCount = mChart.data.maxEntryCountSet?.entryCount ?: return
        Log.d("entryCount", "drawWeb: $entryCount")
        for (j in 0 until entryCount) {
            val angle = j * sliceAngle + mChart.rotationAngle
            val px = center.x + (yAxis.axisMaximum - yAxis.axisMinimum) * factor * Math.cos(
                Math.toRadians(angle.toDouble())
            ).toFloat()
            val py = center.y + (yAxis.axisMaximum - yAxis.axisMinimum) * factor * Math.sin(
                Math.toRadians(angle.toDouble())
            ).toFloat()
            c.drawLine(center.x, center.y, px, py, mWebPaint)
        }
    }

    // Draw the data set as a smooth closed path (circular/spline)
    override fun drawDataSet(c: Canvas?, dataSet: IRadarDataSet?, index: Int) {
        if (c == null || dataSet == null) return

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        val sliceAngle = mChart.sliceAngle
        val factor = mChart.factor
        val entryCount = dataSet.entryCount
        val center = mChart.centerOffsets

        if (entryCount == 0) return
        Log.d("entryCount", "drawDataSet: $entryCount")
        val points = mutableListOf<Pair<Float, Float>>()
        for (j in 0 until entryCount) {
            val e = dataSet.getEntryForIndex(j) as RadarEntry
            val angle = (j * sliceAngle * phaseX + mChart.rotationAngle) % 360
            val r = (e.value - mChart.yChartMin) * factor * phaseY
            val px = center.x + r * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val py = center.y + r * Math.sin(Math.toRadians(angle.toDouble())).toFloat()
            points.add(px to py)
        }
//        /*val path = Path()
//        if (points.isNotEmpty()) {
//            // Use a Catmull-Rom spline for smoothness
//            path.moveTo(points[0].first, points[0].second)
//            val n = points.size
//            for (i in 0 until n) {
//                val p0 = points[(i - 1 + n) % n]
//                val p1 = points[i % n]
//                val p2 = points[(i + 1) % n]
//                val p3 = points[(i + 2) % n]
//
//                val c1x = p1.first + (p2.first - p0.first) / 6f
//                val c1y = p1.second + (p2.second - p0.second) / 6f
//                val c2x = p2.first - (p3.first - p1.first) / 6f
//                val c2y = p2.second - (p3.second - p1.second) / 6f
//
//                path.cubicTo(c1x, c1y, c2x, c2y, p2.first, p2.second)
//            }
//            path.close()
//        }*/

        val path = Path()
        for (j in 0 until entryCount) {
            val e = dataSet.getEntryForIndex(j) as RadarEntry
            val angle = (j * sliceAngle * phaseX + mChart.rotationAngle) % 360
            val r = (e.value - mChart.yChartMin) * factor * phaseY
            val px = center.x + r * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val py = center.y + r * Math.sin(Math.toRadians(angle.toDouble())).toFloat()
            if (j == 0) {
                path.moveTo(px, py)
            } else {
                path.lineTo(px, py)
            }
        }
        path.close()

        val paint = mRenderPaint
        paint.style = Paint.Style.STROKE
        paint.color = dataSet.color
        paint.strokeWidth = 0f
        c.drawPath(path, paint)

        if (dataSet.isDrawFilledEnabled) {
            val bounds = RectF()
            path.computeBounds(bounds, true)
            val shader = LinearGradient(
                bounds.left, bounds.top, bounds.left, bounds.bottom,
                "#EE004096".toColorInt(),
                "#ED15C8C9".toColorInt(),
                Shader.TileMode.CLAMP
            )
            val fillPaint = Paint().apply {
                style = Paint.Style.FILL
                isAntiAlias = true
                this.shader = shader
            }
            c.drawPath(path, fillPaint)
        }
    }
}