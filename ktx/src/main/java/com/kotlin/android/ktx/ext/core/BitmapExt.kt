package com.kotlin.android.ktx.ext.core

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.util.Base64
import androidx.annotation.FloatRange
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.io.safeClose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

/**
 * Bitmap相关扩展：
 *
 * Created on 2020/6/23.
 *
 * @author o.s
 */

const val SHARE_BITMAP_LIMIT: Long = 10 * 1024 * 1024// 单位(b)，即图片限制大小为：10M (微信分享)
const val SHARE_BITMAP_LIMIT_WB: Long = 2 * 1024 * 1024// 单位(b)，即图片限制大小为：2M (微博分享)
const val SHARE_THUMB_BITMAP_LIMIT: Long = 32 * 1024// 单位(b)，即缩略图限制大小为：32k (微信分享)
const val SHARE_MINI_PROGRAM_BITMAP_LIMIT: Long = 128 * 1024 // 单位(b)，小程序消息封面图片，小于128k (微信分享)

fun Bitmap?.getSize(): Int = this?.allocationByteCount ?: 0

/**
 * Bitmap to ByteArray
 * [isRecycle] 是否回收原图
 */
fun Bitmap?.toByteArray(isRecycle: Boolean): ByteArray? {
    if (this == null) {
        return null
    }
    val buffer = ByteBuffer.allocate(byteCount)
    copyPixelsToBuffer(buffer)
    if (isRecycle) {
        recycle()
    }
    return buffer.array()
}

fun Bitmap.toByteArray(): ByteArray? {
    if (isRecycled) {
        return null
    }
    val bos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 85, bos)
    val result = bos.toByteArray()
    bos.safeClose()
    return result
}

/**
 * 获取裁剪缩略图的字节数组
 * 根据图形目标 [dst] 矩形宽高比，以原图中心点为中心，修正原图裁剪区域的宽高比，
 * [dst] 裁剪图形的目标矩形。如：Rect(0, 0, 80, 80)
 * [isRecycle] 是否回收原图
 */
fun Bitmap?.toByteArrayWithClipThumbShape(
        dst: Rect = Rect(0, 0, 80, 80),
        isRecycle: Boolean = true
): ByteArray? {
    return clipThumbShape(dst, isRecycle)?.run {
        toByteArray(isRecycle)
    }
}

/**
 * 获取裁剪缩略图的字节数组
 * 根据图形目标 [dst] 矩形宽高比，以原图中心点为中心，修正原图裁剪区域的宽高比，
 * [dst] 裁剪图形的目标矩形。如：Rect(0, 0, 80, 80)
 * [isRecycle] 是否回收原图
 */
fun Bitmap?.toShareThumbBitmapWithClip(
        dst: Rect = Rect(0, 0, 80, 80),
        isRecycle: Boolean = true
): Bitmap? {
    return clipThumbShape(dst, isRecycle)
}

/**
 * 【异步协程】获取裁剪缩略图的字节数组
 * 根据图形目标 [dst] 矩形宽高比，以原图中心点为中心，修正原图裁剪区域的宽高比，
 * [dst] 裁剪图形的目标矩形。如：Rect(0, 0, 80, 80)
 * [isRecycle] 是否回收原图
 */
suspend fun Bitmap?.asyncToByteArrayForClipThumbShape(
        dst: Rect = Rect(0, 0, 80, 80),
        isRecycle: Boolean = true
): ByteArray? {
    val array = withContext(Dispatchers.IO) {
        clipThumbShape(dst, isRecycle)?.run {
            toByteArray(isRecycle)
        }
    }
    return withContext(Dispatchers.Main) {
        array
    }
}

/**
 * 裁剪缩略图
 * 根据图形目标 [dst] 矩形宽高比，以原图中心点为中心，修正原图裁剪区域的宽高比，
 * [dst] 裁剪图形的目标矩形。如：Rect(0, 0, 80, 80)
 * [isRecycle] 是否回收原图
 */
fun Bitmap?.clipThumbShape(
        dst: Rect = Rect(0, 0, 80, 80),
        isRecycle: Boolean = true
): Bitmap? {
    if (this == null) {
        return null
    }
    val dstRadio = dst.width() / dst.height().toFloat()
    val srcRadio = width / height.toFloat()
    val src = Rect(0, 0, width, height)
    if (srcRadio < dstRadio) {
        val srcH = width / dstRadio
        val top = (height - srcH) / 2
        src.set(0, top.toInt(), width, (top + srcH).toInt())
    } else if (srcRadio > dstRadio) {
        val srcW = height * dstRadio
        val left = (width - srcW) / 2
        src.set(left.toInt(), 0, (left + srcW).toInt(), height)
    }

    val bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawBitmap(this, src, dst, null)

    if (isRecycle) {
        recycle()
    }
    return bitmap
}

/**
 * 【异步协程】裁剪圆形图片
 * [scale] 裁剪比例
 * [src] 裁剪图形所在矩形
 * [isRecycle] 是否回收原图
 */
suspend fun Bitmap?.asyncClipCircleShape(
        @FloatRange(from = 0.0, to = 1.0) scale: Float = 1F,
        src: Rect? = null,
        isRecycle: Boolean = true
): Bitmap? {
    val localBitmap = withContext(Dispatchers.IO) {
        clipCircleShape(scale, src, isRecycle)
    }
    return withContext(Dispatchers.Main) {
        localBitmap
    }
}

/**
 * 裁剪圆形图片
 * [scale] 裁剪比例
 * [src] 裁剪图形所在矩形
 * [isRecycle] 是否回收原图
 */
fun Bitmap?.clipCircleShape(
        @FloatRange(from = 0.0, to = 1.0) scale: Float = 1F,
        src: Rect? = null,
        isRecycle: Boolean = true
): Bitmap? {
    if (this == null) {
        return null
    }
    val w = src?.width()?.toFloat() ?: width * scale
    val h = src?.height()?.toFloat() ?: height * scale
    val radius = if (w <= h) {
        w
    } else {
        h
    } / 2
    val sideLength = (radius * 2).toInt()

    val bitmap = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val left = (width - sideLength) / 2
    val top = (height - sideLength) / 2
    val srcRect = Rect(left, top, left + sideLength, top + sideLength)
    val dst = Rect(0, 0, sideLength, sideLength)

    val option = BitmapShapeOption()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val path = Path()
    path.addCircle(radius, radius, radius, Path.Direction.CW)
    canvas.clipPath(path, option)
    canvas.drawBitmap(this, srcRect, dst, paint)
//    canvas.drawShapeOption(path, paint, option)

    if (isRecycle) {
        recycle()
    }
    return bitmap
}

/**
 * 裁剪椭圆形图片
 * [scale] 裁剪比例
 * [src] 裁剪图形所在矩形
 * [isRecycle] 是否回收原图
 */
fun Bitmap?.clipOvalShape(
        @FloatRange(from = 0.0, to = 1.0) scale: Float = 1F,
        src: Rect? = null,
        isRecycle: Boolean = true
): Bitmap? {
    if (this == null) {
        return null
    }
    val w = src?.width()?.toFloat() ?: width * scale
    val h = src?.height()?.toFloat() ?: height * scale
    val wide = w.toInt()
    val high = h.toInt()

    val bitmap = Bitmap.createBitmap(wide, high, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val left = (width - wide) / 2
    val top = (height - high) / 2
    val srcRect = Rect(left, top, left + wide, top + high)
    val dst = Rect(0, 0, wide, high)

    val option = BitmapShapeOption()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val path = Path()
    path.addOval(RectF(dst), Path.Direction.CW)
    canvas.clipPath(path, option)
    canvas.drawBitmap(this, srcRect, dst, paint)
//    canvas.drawShapeOption(path, paint, option)

    if (isRecycle) {
        recycle()
    }
    return bitmap
}

/**
 * 裁剪方形图片
 * [scale] 裁剪比例
 * [src] 裁剪图形所在矩形
 * [isRecycle] 是否回收原图
 */
fun Bitmap?.clipSquareShape(
        @FloatRange(from = 0.0, to = 1.0) scale: Float = 1F,
        src: Rect? = null,
        isRecycle: Boolean = true
): Bitmap? {
    if (this == null) {
        return null
    }
    val w = src?.width()?.toFloat() ?: width * scale
    val h = src?.height()?.toFloat() ?: height * scale
    val radius = if (w <= h) {
        w
    } else {
        h
    } / 2
    val sideLength = (radius * 2).toInt()

    val bitmap = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val left = (width - sideLength) / 2
    val top = (height - sideLength) / 2
    val srcRect = src ?: Rect(left, top, left + sideLength, top + sideLength)
    val dst = Rect(0, 0, sideLength, sideLength)
    canvas.drawBitmap(this, srcRect, dst, Paint(Paint.ANTI_ALIAS_FLAG))

    if (isRecycle) {
        recycle()
    }
    return bitmap
}

/**
 * 裁剪矩形图片
 * [src] 裁剪图形所在矩形
 * [isCenter] 是否是处于源图片中央
 * [isRecycle] 是否回收原图
 */
fun Bitmap?.clipRectShape(
        src: Rect? = null,
        isCenter: Boolean = true,
        isRecycle: Boolean = true
): Bitmap? {
    if (this == null) {
        return null
    }
    val w = src?.width()?.toFloat() ?: width.toFloat()
    val h = src?.height()?.toFloat() ?: height.toFloat()
    val radius = if (w <= h) {
        w
    } else {
        h
    } / 2
    val sideLength = (radius * 2).toInt()

    val bitmap = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val left = (width - sideLength) / 2
    val top = (height - sideLength) / 2
    val centerRect = Rect(left, top, left + sideLength, top + sideLength)
    val srcRect = if (isCenter) {
        centerRect
    } else {
        src ?: centerRect
    }
    val dst = Rect(0, 0, sideLength, sideLength)
    canvas.drawBitmap(this, srcRect, dst, Paint(Paint.ANTI_ALIAS_FLAG))

    if (isRecycle) {
        recycle()
    }
    return bitmap
}

/**
 * 裁剪矩形图片
 * [cornerRadius] 裁剪的圆角半径(dp)  top-left, top-right, bottom-right, bottom-left
 * [direction] 裁剪的圆角方位
 * [scale] 裁剪比例
 * [src] 裁剪图形所在矩形
 * [isRecycle] 是否回收原图
 */
fun Bitmap?.clipRoundRectShape(
        cornerRadius: Int,
        direction: Int = Direction.ALL,
        @FloatRange(from = 0.0, to = 1.0) scale: Float = 1.0F,
        src: Rect? = null,
        isRecycle: Boolean = true
): Bitmap? {
    if (this == null) {
        return null
    }
    val w = src?.width()?.toFloat() ?: width * scale
    val h = src?.height()?.toFloat() ?: height * scale
    val radius = if (w <= h) {
        w
    } else {
        h
    } / 2
    val sideLength = (radius * 2).toInt()

    val bitmap = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val left = (width - sideLength) / 2
    val top = (height - sideLength) / 2
    val srcRect = src ?: Rect(left, top, left + sideLength, top + sideLength)
    val dst = Rect(0, 0, sideLength, sideLength)

    val path = Path()
    val r = cornerRadius.dpF
    val radii = floatArrayOf(0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F)
    if (direction == Direction.ALL) {
        radii[0] = r
        radii[1] = r
        radii[2] = r
        radii[3] = r
        radii[4] = r
        radii[5] = r
        radii[6] = r
        radii[7] = r
    }
    if (direction and Direction.LEFT_TOP == Direction.LEFT_TOP) {
        radii[0] = r
        radii[1] = r
    }
    if (direction and Direction.RIGHT_TOP == Direction.RIGHT_TOP) {
        radii[2] = r
        radii[3] = r
    }
    if (direction and Direction.RIGHT_BOTTOM == Direction.RIGHT_BOTTOM) {
        radii[4] = r
        radii[5] = r
    }
    if (direction and Direction.LEFT_BOTTOM == Direction.LEFT_BOTTOM) {
        radii[6] = r
        radii[7] = r
    }
    path.addRoundRect(RectF(srcRect), radii, Path.Direction.CW)
    canvas.clipPath(path)

    canvas.drawBitmap(this, srcRect, dst, Paint(Paint.ANTI_ALIAS_FLAG))

    if (isRecycle) {
        recycle()
    }
    return bitmap
}

/**
 * 裁剪路径图形
 * [path] 裁剪路径
 * [option] 额外选项
 */
fun Canvas?.clipPath(path: Path, option: BitmapShapeOption) {
    this?.apply {
        if (option.hasInverseEvenOdd) {
            path.fillType = Path.FillType.INVERSE_EVEN_ODD
        }
        clipPath(path)
        path.fillType = Path.FillType.EVEN_ODD
    }
}

/**
 * 根据形状选项，进行额外绘制
 * [path] 绘制路径
 * [paint] 画笔
 * [option] 形状选项
 *
 */
fun Canvas?.drawShapeOption(path: Path, paint: Paint, option: BitmapShapeOption) {
    this?.apply {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = option.strokeWidth
        paint.color = option.strokeColor
        drawPath(path, paint)
    }
}

/**
 * 形状选项
 */
data class BitmapShapeOption(
        var strokeWidth: Float = 1F,
        var strokeColor: Int = Color.TRANSPARENT,
        var hasInverseEvenOdd: Boolean = false
)

/**
 * 获取符合大小限制的分享图片
 * [limit] 字节大小限制(单位kb)
 * [isRecycle] 是否回收原图，前提是如果生成了新图片
 */
fun Bitmap?.toShareBitmapWithLimit(
        limit: Long = SHARE_BITMAP_LIMIT,
        isRecycle: Boolean = true
): Bitmap? {
    if (this == null || isRecycled) {
        return null
    }
    val size = getSize() // b shr 10 // kb （除以 2^10 = 1024）
    val ratio = size / limit.toFloat()
    if (ratio <= 1) {
        return this
    }
    val dstWidth = width / sqrt(ratio)
    val dstHeight = height / sqrt(ratio)
    return scaleImage(dstWidth.toInt(), dstHeight.toInt(), isRecycle)
}

/**
 * 缩放图片
 * [dstWidth] 目标图片宽度
 * [dstHeight] 目标图片高度
 * [isRecycle] 是否回收原图
 */
fun Bitmap.scaleImage(
        dstWidth: Int,
        dstHeight: Int,
        isRecycle: Boolean
): Bitmap {
    val matrix = Matrix()
    matrix.postScale(dstWidth / width.toFloat(), dstHeight / height.toFloat())
    val bitmap = Bitmap.createBitmap(this, 0, 0, dstWidth, dstHeight, matrix, true)
    if (isRecycle) {
        recycle()
    }
    return bitmap
}


/**
 * 保存图片到内存卡
 * @param path  "/sdcard/image"
 * @return 是否保存成功
 */
fun Bitmap.savePic(context: Context, path: String): Boolean {
    val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    val outfile = File(path)
//    文件不存在，需要创建一个新文件
    if (outfile.isDirectory.not()) {
        try {
            outfile.mkdirs()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    val fname = "${outfile}/${sdf.format(Date())}.png"
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(fname)
        if (null != fos) {
            this.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.flush()
            fos.close()
        }
        /** 扫描图片,非常重要，会将图片加入media database，如果不加入，getImages()将获取不到此图  */
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(File(fname))
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    return true

}

/**
 * 压缩分享图片，返回压缩完成的 ByteArray
 */
fun Bitmap?.compressByteArrayWithShareLimit(
    limit: Long = SHARE_THUMB_BITMAP_LIMIT,
    isRecycle: Boolean = true
): ByteArray? {
    return this?.run {
        if (!isRecycled) {
            val bos = ByteArrayOutputStream()
            var quality = 100
            compress(Bitmap.CompressFormat.JPEG, quality, bos)
            while (bos.toByteArray().size > limit && quality != 2) {
                bos.reset()
                quality -= when {
                    quality > 20 -> {
                        10
                    }
                    quality > 10 -> {
                        5
                    }
                    else -> {
                        2
                    }
                }
                compress(Bitmap.CompressFormat.JPEG, quality, bos)
            }
            if (bos.toByteArray().size > limit) {
                val thumbImg = Bitmap.createScaledBitmap(this, width / 2, height / 2, true)
                if (isRecycle) {
                    recycle()
                }
                bos.safeClose()
                return thumbImg.compressByteArrayWithShareLimit(limit, true)
            }
            if (isRecycle) {
                recycle()
            }
            var result = bos.toByteArray()
            bos.safeClose()
            if (result.size > limit) {
                toShareBitmapWithLimit(limit, false)?.run {
                    result = toByteArray()
                }
            }
            result
        } else {
            null
        }
    }
}

/**
 * base64转bitmap
 */
fun webData2Bitmap(data:String):Bitmap{
    val imageBytes = Base64.decode(data, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
}