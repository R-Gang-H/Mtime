package com.kotlin.android.film.widget.seat

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorInt
import com.kotlin.android.film.R
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.spF
import com.kotlin.android.mtime.ktx.getColor

/**
 *
 * Created on 2022/2/15.
 *
 * @author o.s
 */
internal class SeatScreen(
    private val seatChart: SeatChart, // 座位图
    private val seatHeader: SeatHeader, // 座位图图标
    val height: Float = 18.dpF,
    var screenWidthScale: Float = 0.5F,
    var minScreenWidth: Float = 180.dpF,
    var titleTextSize: Float = 11.spF,
    @ColorInt var titleColor: Int = getColor(R.color.color_666c7b), // 标题文字颜色
    @ColorInt var screenBackgroundColor: Int = getColor(R.color.color_c7c9cd), // 座位图中屏幕背景颜色
) {
    private val matrixValues: MatrixValues = seatChart.matrixValues // 矩阵值对象
    private var top: Float = 0F
    private var bottom: Float = 0F
    private var startX: Float = 0F
    private var startY: Float = 0F
    private var _halfTextWidth: Float = 0F

    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textSize = titleTextSize // 不跟随变化
        }
    }

    private val path by lazy {
        Path()
    }

    var title: String = ""
        set(value) {
            field = value
            _halfTextWidth = paint.measureText(value) / 2
        }

    fun init(seatManager: SeatManager<*, *>,) {
        title = seatManager.getScreenName()

        top = seatHeader.height
        bottom = top + height

        startY = paint.getBaseline(top = top, bottom = bottom)
    }

    fun draw(canvas: Canvas) {
        measure()

        paint.color = screenBackgroundColor
        canvas.drawPath(path, paint)

        paint.color = titleColor
        canvas.drawText(title, startX, startY, paint) // y不变
    }

    private fun measure() {
        val centerX = seatChart.centerWidth * matrixValues.sx + matrixValues.dx
        startX = centerX - _halfTextWidth

        var screenWidth = seatChart.width * screenWidthScale * matrixValues.sx
        if (screenWidth < minScreenWidth) {
            screenWidth = minScreenWidth
        }
        val halfScreenWidth = screenWidth / 2
        val left = centerX - halfScreenWidth
        val right = centerX + halfScreenWidth

        path.reset()
        path.moveTo(centerX, top)
        path.lineTo(left, top)
        path.lineTo(left + 10, bottom - 20) // y不变
        path.quadTo(left + 10, bottom - 7, left + 30, bottom)
        path.lineTo(right - 30, bottom) // y不变
        path.quadTo(right - 10, bottom - 7, right - 10, bottom - 20) // y不变
        path.lineTo(right, top)
    }

}