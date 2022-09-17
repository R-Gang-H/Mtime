package com.kotlin.android.ktx.ext.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

/**
 * 剧中的ImageSpan
 *
 * Created on 2020/10/27.
 *
 * @author o.s
 */
class CenterImageSpan(drawable: Drawable) : ImageSpan(drawable) {

    override fun getSize(
            paint: Paint,
            text: CharSequence?,
            start: Int,
            end: Int,
            fm: Paint.FontMetricsInt?
    ): Int {
        val rect = drawable.bounds
        fm?.apply {
            val fmPaint = paint.fontMetricsInt
            val fontHeight = fmPaint.bottom - fmPaint.top
            val drHeight = rect.bottom - rect.top
            val t = drHeight / 2 - fontHeight / 4
            val b = drHeight / 2 + fontHeight / 4
            ascent = -b
            top = -b
            bottom = t
            descent = t
        }
        return rect.right
    }

    override fun draw(
            canvas: Canvas,
            text: CharSequence?,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
    ) {
        canvas.save()
        val fmPaint = paint.fontMetricsInt
        val fontHeight = fmPaint.bottom - fmPaint.top
        val rect = drawable.bounds
        val drawableHeight = rect.bottom - rect.top
        val transY = (fontHeight - drawableHeight) / 2
//        val transY: Int = (bottom - top - drawable.bounds.bottom) / 2 + top
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }

}