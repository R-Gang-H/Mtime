package com.kotlin.android.qrcode.component.utils

import android.graphics.Bitmap
import androidx.annotation.ColorInt
import com.google.zxing.common.BitMatrix
import com.kotlin.android.qrcode.component.journeyapps.barcodescanner.BarcodeEncoder

/**
 * create by lushan on 2020/11/17
 * description:
 */
class ColorBarcodeEncode(@ColorInt var blackColor: Int, @ColorInt var whiteColor: Int) : BarcodeEncoder() {
    override fun createBitmap(matrix: BitMatrix): Bitmap? {
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (matrix[x, y]) blackColor else whiteColor
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
}