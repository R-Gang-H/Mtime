package com.kotlin.android.qrcode.component.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.ArrayMap
import androidx.annotation.ColorInt
import com.google.zxing.*
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.qrcode.component.R


/**
 * create by lushan on 2020/11/17
 * description: 二维码生成和识别工具类， 可以参考：https://github.com/journeyapps/zxing-android-embedded
 */

/**
 * 生成二维码，带有白边儿
 */
fun String.createQrCodeBitmap(width: Int, height: Int, hints: Map<EncodeHintType, *>? = null, @ColorInt blackColor: Int = getColor(R.color.color_000000), @ColorInt whiteColor: Int = getColor(R.color.color_ffffff)): Bitmap? {
    if (TextUtils.isEmpty(this)) {
        return null
    }

    return try {
        var barcodeEncoder = ColorBarcodeEncode(blackColor, whiteColor)
        barcodeEncoder.encodeBitmap(this, BarcodeFormat.QR_CODE, width, height, hints)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getHitTypeMap(): Map<EncodeHintType, *>? {
    var arrayMap = ArrayMap<EncodeHintType, Any>()
    arrayMap[EncodeHintType.CHARACTER_SET] = "UTF-8"
    arrayMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
    return arrayMap
}

/**
 * 生成去掉白边儿的二维码
 */
fun String.createQrCodeBitmapWithoutWhiteBar(width: Int, height: Int, hints: Map<EncodeHintType, *>? = null, @ColorInt blackColor: Int = getColor(R.color.color_000000), @ColorInt whiteColor: Int = getColor(R.color.color_ffffff)): Bitmap? {
    if (TextUtils.isEmpty(this)) {
        return null
    }
    var barcodeEncoder = ColorBarcodeEncode(blackColor, whiteColor)
    var bitMatrix = barcodeEncoder.encode(this, BarcodeFormat.QR_CODE, width, height, hints)
    bitMatrix = deleteWhite(bitMatrix)
    return barcodeEncoder.createBitmap(bitMatrix)
}

private fun deleteWhite(bitMatrix: BitMatrix?): BitMatrix? {
    if (bitMatrix == null) {
        return null
    }
    var enclosingRectangle = bitMatrix.enclosingRectangle
    var resWidth = enclosingRectangle[2] + 1
    var resHeight = enclosingRectangle[3] + 1
    var resMatrix = BitMatrix(resWidth, resHeight)
    resMatrix.clear()
    (0 until resWidth).forEach { i ->
        (0 until resHeight).forEach { j ->
            if (bitMatrix.get(i + enclosingRectangle[0], j + enclosingRectangle[1])) {
                resMatrix.set(i, j)
            }
        }
    }
    return resMatrix
}

/**
 * 生成二维码
 * @param width 二维码宽度
 * @param height 二维码高度
 * @param hints  配置项
 * @param   hasWhite    是否有白边儿 true有白边
 * @param   blackColor 二维码黑色对应颜色
 * @param   whiteColor 二维码白色对应颜色
 */
fun String.createQrCode(width: Int = 55.dp, height: Int = 55.dp, hints: Map<EncodeHintType, *>? = null, hasWhite: Boolean = true, @ColorInt blackColor: Int = getColor(R.color.color_000000), @ColorInt whiteColor: Int = getColor(R.color.color_ffffff)): Bitmap? {
    return if (hasWhite) {
        this.createQrCodeBitmap(width, height, hints, blackColor, whiteColor)
    } else {
        this.createQrCodeBitmapWithoutWhiteBar(width, height, hints, blackColor, whiteColor)
    }

}

/**
 * 将本地图片文件转换成可解码二维码的 Bitmap。为了避免图片太大，这里对图片进行了压缩。
 *
 * @param picturePath 本地图片文件路径
 * @return
 */
private fun getDecodeAbleBitmap(picturePath: String): Bitmap? {
    return try {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(picturePath, options)
        var sampleSize = options.outHeight / 400
        if (sampleSize <= 0) {
            sampleSize = 1
        }
        options.inSampleSize = sampleSize
        options.inJustDecodeBounds = false
        BitmapFactory.decodeFile(picturePath, options)
    } catch (e: java.lang.Exception) {
        null
    }
}


/**
 * 从文件中获取图片解析二维码，同步解析，比较好使，需在子线程中调用 可以直接使用doAsync
 *
 *   doAsync {
 *      val recogniseQrCodeFromFile = recogniseQrCodeFromFile(photos[0].path)
 *       runOnUiThread {
 *          showToast(recogniseQrCodeFromFile)
 *       }
 *   }
 */
fun recogniseQrCodeFromFile(picturePath: String): String? {
    var bitmap = getDecodeAbleBitmap(picturePath)
    bitmap ?: return "无法识别"
    return syncDecodeQRCode(bitmap)
}


/**
 * 同步解析bitmap二维码。该方法是耗时操作，请在子线程中调用。 可以直接使用doAsync
 *
 * @param bitmap 要解析的二维码图片
 * @return 返回二维码图片里的内容 或 null
 */
fun syncDecodeQRCode(bitmap: Bitmap): String? {
    val HINTS = ArrayMap<DecodeHintType, Any>()
    HINTS[DecodeHintType.TRY_HARDER] = BarcodeFormat.QR_CODE
    HINTS[DecodeHintType.POSSIBLE_FORMATS] = getAllFormatList()
    HINTS[DecodeHintType.CHARACTER_SET] = "UTF-8"
    var result: Result? = null
    var source: RGBLuminanceSource? = null
    return try {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        source = RGBLuminanceSource(width, height, pixels)
        result = MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(source)), HINTS)
        result.getText()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        if (source != null) {
            try {
                result = MultiFormatReader().decode(BinaryBitmap(GlobalHistogramBinarizer(source)), HINTS)
                return result.getText()
            } catch (e2: Throwable) {
                e2.printStackTrace()
            }
        }
        "无法识别"
    }
}


private fun getAllFormatList(): MutableList<BarcodeFormat> {
    return mutableListOf<BarcodeFormat>().apply {
        add(BarcodeFormat.AZTEC)
        add(BarcodeFormat.CODABAR)
        add(BarcodeFormat.CODE_39)
        add(BarcodeFormat.CODE_93)
        add(BarcodeFormat.CODE_128)
        add(BarcodeFormat.DATA_MATRIX)
        add(BarcodeFormat.EAN_8)
        add(BarcodeFormat.EAN_13)
        add(BarcodeFormat.ITF)
        add(BarcodeFormat.MAXICODE)
        add(BarcodeFormat.PDF_417)
        add(BarcodeFormat.QR_CODE)
        add(BarcodeFormat.RSS_14)
        add(BarcodeFormat.RSS_EXPANDED)
        add(BarcodeFormat.UPC_A)
        add(BarcodeFormat.UPC_E)
        add(BarcodeFormat.UPC_EAN_EXTENSION)
    }
}
fun Activity.scanResult(){
    IntentIntegrator(this).initiateScan()
}




