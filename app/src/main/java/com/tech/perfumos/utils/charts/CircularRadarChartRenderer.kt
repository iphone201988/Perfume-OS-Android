package com.tech.perfumos.utils.charts


import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.github.mikephil.charting.renderer.RadarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler


class CircularRadarChartRenderer(
    chart: RadarChart,
    animator: com.github.mikephil.charting.animation.ChartAnimator,
    viewPortHandler: ViewPortHandler
) : RadarChartRenderer(
    chart,
    animator,
    viewPortHandler
) /*RadarChartRenderer(chart, animator, viewPortHandler)*/ {

    override fun drawDataSet(c: Canvas?, dataSet: IRadarDataSet?, index: Int) {
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        val sliceangle = mChart.sliceAngle
        val factor = mChart.factor


        val entryCount = dataSet?.entryCount

        val center = mChart.centerOffsets

        val path = Path()

        // Calculate points
        val points = mutableListOf<Pair<Float, Float>>()
        for (j in 0 until entryCount!!) {
            val e = dataSet.getEntryForIndex(j) as RadarEntry
            val angle = (j * sliceangle * phaseX + mChart.rotationAngle) % 360
            val r = (e.value - mChart.yChartMin) * factor * phaseY
            val px = center.x + r * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val py = center.y + r * Math.sin(Math.toRadians(angle.toDouble())).toFloat()
            points.add(px to py)
        }


        // Draw smooth curve (simple Catmull-Rom spline for closed path)
        if (points.isNotEmpty()) {
            path.moveTo(points[0].first, points[0].second)
            val n = points.size
            for (i in 0 until n) {
                val p0 = points[(i - 1 + n) % n]
                val p1 = points[i % n]
                val p2 = points[(i + 1) % n]
                val p3 = points[(i + 2) % n]

                val c1x = p1.first + (p2.first - p0.first) / 6f
                val c1y = p1.second + (p2.second - p0.second) / 6f
                val c2x = p2.first - (p3.first - p1.first) / 6f
                val c2y = p2.second - (p3.second - p1.second) / 6f

                path.cubicTo(c1x, c1y, c2x, c2y, p2.first, p2.second)
            }
            path.close()
        }

        // Draw the path
        val paint = mRenderPaint
        paint.style = Paint.Style.STROKE
        paint.color = dataSet.color
        paint.strokeWidth = dataSet.lineWidth
        c?.drawPath(path, paint)

        // Optionally fill
        if (dataSet.isDrawFilledEnabled) {
            paint.style = Paint.Style.FILL
            paint.alpha = dataSet.fillAlpha
            c?.drawPath(path, paint)
        }
    }
}