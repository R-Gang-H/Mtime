package com.kotlin.android.mtime.ktx.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.kotlin.android.ktx.ext.log.e


/**
 * 截取scrollview的屏幕
 * @param scrollView
 * @return
 */
fun NestedScrollView.getBitmapByView(): Bitmap? {
    "getBitmapByView获取bitmap".e()
    var h = 0
    var bitmap: Bitmap? = null
    // 获取scrollview实际高度
    var i = 0
    while (i < this.childCount) {
        h = this.getChildAt(i).height
        i++
    }
    // 创建对应大小的bitmap
    bitmap = Bitmap.createBitmap(
        this.width, h,
        Bitmap.Config.RGB_565
    )
    val canvas = Canvas(bitmap)
    //设置背景色防止不可见地方黑屏
    canvas.drawColor(Color.parseColor("#ffffff"))
    this.draw(canvas)
    return bitmap
}

fun View?.loadBitmapFromViewBySystem(): Bitmap? {
    this ?: return null
    this.isDrawingCacheEnabled = true
    this.buildDrawingCache()
    return this.drawingCache

}

