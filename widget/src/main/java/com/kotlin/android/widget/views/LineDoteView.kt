package com.kotlin.android.widget.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.widget.R

/**
 * create by lushan on 2020/11/5
 * description: 虚线圆点
 */
class LineDoteView @JvmOverloads constructor(
    var ctx: Context,
    var attributes: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(ctx, attributes, defStyleAttr) {

    private var paint = Paint()

    init {
        paint.setColor(getColor(R.color.color_e0dfde))
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
    }

    private var bigR = 7.5f.dpF//大圆点半径
    private var minR = 2.dpF//小圆点半径

    private val margin = 7.5f.dpF

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        绘制两个半圆
        canvas?.drawCircle(0f, height / 2f, bigR, paint)
        canvas?.drawCircle(width.toFloat(), height / 2f, bigR, paint)
        var startPosition = bigR + margin
        while (startPosition < width - bigR - margin) {
            canvas?.drawCircle(startPosition, height / 2f, minR, paint)
            startPosition += minR + margin
        }
    }

}