package com.kotlin.android.film.widget.seat

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.ColorInt
import com.kotlin.android.film.R
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.getColor

/**
 * 座位图索引绘制
 *
 * Created on 2022/2/9.
 *
 * @author o.s
 */
internal class SeatIndexes(
    private val seatIcon: SeatIcon, // 座位icon
    private val seatChart: SeatChart, // 座位图
    var width: Float = 14.dpF,
    var edge: Float = 5.dpF,
    var verticalSpacing: Float = 4.dpF,
    @ColorInt private var overviewBackgroundColor: Int = getColor(R.color.color_7e000000),
    private var indexTextSize: Float = 10.dpF,
) {
    private val matrixValues: MatrixValues = seatChart.matrixValues // 矩阵值对象
    private val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            textSize = indexTextSize

            val textHeight = measureText("1")
            offsetY = textHeight / 3
        }
    }
    private val rectF by lazy { RectF() }

    private var offsetY: Float = 0F // y 偏移量
    private var halfWidth: Float = 0F // 一半宽度
    private var vSpacing: Float = 0F // 缩放后的间隔
    private var itemHeight: Float = 0F // 缩放后的高度
    private var itemVHeight: Float = 0F // item包含间隔的缩放高度
    private var indexes: ArrayList<String>? = null

    init {
        rectF.left = edge
        rectF.right = edge + width

        halfWidth = width / 2
    }

    fun init(
        indexes: ArrayList<String>
    ) {
        this.indexes = indexes
    }

    fun draw(canvas: Canvas) {
        measure()

        paint.color = overviewBackgroundColor
        canvas.drawRoundRect(rectF, halfWidth, halfWidth, paint)

        paint.color = Color.WHITE
        var top = -itemVHeight + matrixValues.dy
        var bottom: Float
        var baseline: Float
        indexes?.forEach {
            top += itemVHeight
            bottom = top + itemHeight
            baseline = paint.getBaseline(top = top, bottom = bottom)

            canvas.drawText(it, rectF.left + halfWidth, baseline, paint)
        }
    }

    private fun measure() {
        vSpacing = verticalSpacing * matrixValues.sy
        itemHeight = seatIcon.height * matrixValues.sy
        itemVHeight = itemHeight + vSpacing

        rectF.top = matrixValues.dy - offsetY
        rectF.bottom = matrixValues.dy + seatChart.height * matrixValues.sy + offsetY
    }

}