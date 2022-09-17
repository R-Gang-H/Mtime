package com.kotlin.android.widget.autohintlayout

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/13
 */
class DefaultAutoHintDrawer : IAutoHintDrawer {

    override fun draw(
        drawBounds: Rect, lastDrawX: Float, drawX: Float, drawY: Float,
        fraction: Float, lastHint: String, currHint: String, canvas: Canvas, paint: Paint
    ) {
        val boundsHeight = (drawBounds.bottom - drawBounds.top).toFloat()
        val offsetY = boundsHeight * fraction
        val alpha = paint.alpha
        paint.alpha = (alpha * (1f - fraction)).toInt()
        // draw last hint with curr fraction
        canvas.drawText(getText(lastHint, drawBounds, paint), lastDrawX, drawY - offsetY, paint)

        // reset alpha
        paint.alpha = (alpha * fraction).toInt()
        // draw curr hint with curr fraction
        canvas.drawText(getText(currHint, drawBounds, paint), drawX, boundsHeight + drawY - offsetY, paint)

        // reset alpha
        paint.alpha = alpha
    }

    private fun getText(text: String, drawBounds: Rect, paint: Paint): String {
        if (text.isNotEmpty()) {
            val textWidth = paint.measureText(text)
            if (textWidth > drawBounds.width()) {
                val subIndex = paint.breakText(text, 0, text.length, true, drawBounds.width().toFloat(), null)
                return text.substring(0, subIndex - 3) + "..."
            }
        }
        return text
    }

}