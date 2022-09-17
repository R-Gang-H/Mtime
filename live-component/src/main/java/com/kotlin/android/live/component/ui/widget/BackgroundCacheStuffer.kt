package com.kotlin.android.live.component.ui.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import com.kotlin.android.ktx.ext.dimension.dp
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer

/**
 * create by lushan on 2021/3/16
 * description:弹幕背景
 */
class BackgroundCacheStuffer : SpannedCacheStuffer() {
    private val paint = Paint()
    override fun measure(danmaku: BaseDanmaku?, paint: TextPaint?, fromWorkerThread: Boolean) {
        danmaku?.padding = 5.dp // 在背景绘制模式下增加padding
        super.measure(danmaku, paint, fromWorkerThread)
    }

    //绘制背景
    override fun drawBackground(danmaku: BaseDanmaku?, canvas: Canvas?, left: Float, top: Float) {
        super.drawBackground(danmaku, canvas, left, top)
    }

    //    绘制描边
    override fun drawStroke(danmaku: BaseDanmaku?, lineText: String?, canvas: Canvas?, left: Float, top: Float, paint: Paint?) {
        super.drawStroke(danmaku, lineText, canvas, left, top, paint)
    }


}