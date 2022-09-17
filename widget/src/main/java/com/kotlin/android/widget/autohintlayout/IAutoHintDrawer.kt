package com.kotlin.android.widget.autohintlayout

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/13
 */
interface IAutoHintDrawer {
    fun draw(
        drawBounds: Rect,
        lastDrawX: Float,
        drawX: Float,
        drawY: Float,
        fraction: Float,
        lastHint: String,
        currHint: String,
        canvas: Canvas,
        paint: Paint
    )
}