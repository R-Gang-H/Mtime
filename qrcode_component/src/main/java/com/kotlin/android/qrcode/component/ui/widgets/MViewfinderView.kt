package com.kotlin.android.qrcode.component.ui.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.kotlin.android.qrcode.component.journeyapps.barcodescanner.ViewfinderView

/**
 * create by lushan on 2020/12/21
 * description:
 */
class MViewfinderView(var ctx: Context,var attrs: AttributeSet): ViewfinderView(ctx,attrs) {


    /**
     * 需要实现新的ui
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }
}