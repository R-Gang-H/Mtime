package com.kotlin.android.film.widget.seat

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorInt
import com.kotlin.android.film.R
import com.kotlin.android.mtime.ktx.getColor

/**
 * 最为图中线
 *
 * Created on 2022/2/15.
 *
 * @author o.s
 */
internal class SeatCenterLine(
    private val seatChart: SeatChart, // 座位图
    private val orientation: Orientation,
    @ColorInt var divideLineColor: Int = getColor(R.color.color_99979797), // 座位图中线颜色
) {
    private val matrixValues: MatrixValues = seatChart.matrixValues // 矩阵值对象
    private var startX: Float = 0F
    private var startY: Float = 0F
    private var endX: Float = 0F
    private var endY: Float = 0F

    private val paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = divideLineColor
            strokeWidth = 1.5F
        }
    }

    private val path by lazy {
        Path()
    }

    private val lineGap by lazy {
        FloatArray(4)
    }

    fun draw(canvas: Canvas) {
        measure()

        canvas.drawPath(path, paint)
    }

    private fun measure() {
        val gap: Float
        if (Orientation.VERTICAL == orientation) { // 垂直分割线
            gap = 10 * matrixValues.sy

            this.startX = seatChart.centerWidth * matrixValues.sx + matrixValues.dx
            this.startY = matrixValues.dy
            this.endX = startX
            this.endY = startY + seatChart.height * matrixValues.sy
        } else { // 水平分割线
            gap = 10 * matrixValues.sx

            this.startX = matrixValues.dx
            this.startY = seatChart.centerHeight * matrixValues.sy + matrixValues.dy
            this.endX = startX + seatChart.width * matrixValues.sx
            this.endY = startY
        }
        for (i in 0..3) {
            lineGap[i] = gap
        }
        paint.pathEffect = DashPathEffect(lineGap, 0F)

        path.reset()
        path.moveTo(startX, startY)
        path.lineTo(endX, endY)
    }
}