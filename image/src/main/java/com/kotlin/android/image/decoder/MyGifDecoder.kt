//package com.kotlin.android.image.decoder
//
//import android.graphics.Bitmap
//import android.os.Build
//import coil.annotation.InternalCoilApi
//import coil.bitmap.BitmapPool
//import coil.decode.*
//import coil.size.Size
//import com.kotlin.android.core.CoreApp
//import com.kotlin.android.image.R
//import okio.BufferedSource
//import okio.buffer
//import okio.sink
//import pl.droidsonroids.gif.GifDrawable
//import java.io.File
//
///**
// * Coil 加载GIF解码器
// *
// * 解决 Coil ImageDecoderDecoder 加载问题：Coil 提供的 ImageDecoderDecoder 内部使用了系统的 AnimatedImageDrawable（有bug）
// *
// * Created on 2021/2/24.
// *
// * @author o.s
// */
//class MyGifDecoder : Decoder {
//
//    /**
//     * Return true if this decoder supports decoding [source].
//     *
//     * Implementations **must not** consume the source, as this can cause subsequent calls to [handles] and [decode] to fail.
//     *
//     * Prefer using [BufferedSource.peek], [BufferedSource.rangeEquals], or other non-destructive methods to check
//     * for the presence of header bytes or other markers. Implementations can also rely on [mimeType],
//     * however it is not guaranteed to be accurate (e.g. a file that ends with .png, but is encoded as a .jpg).
//     *
//     * @param source The [BufferedSource] to read from.
//     * @param mimeType An optional MIME type for the [source].
//     */
//    override fun handles(source: BufferedSource, mimeType: String?): Boolean {
//        return DecodeUtils.isGif(source) ||
//                DecodeUtils.isAnimatedWebP(source) ||
//                (Build.VERSION.SDK_INT >= 30 && DecodeUtils.isAnimatedHeif(source))
//    }
//
//    /**
//     * Decode [source] as a [Drawable].
//     *
//     * NOTE: Implementations are responsible for closing [source] when finished with it.
//     *
//     * @param pool A [BitmapPool] which can be used to request [Bitmap] instances.
//     * @param source The [BufferedSource] to read from.
//     * @param size The requested dimensions for the image.
//     * @param options A set of configuration options for this request.
//     */
//
//    @InternalCoilApi
//    override suspend fun decode(
//            pool: BitmapPool,
//            source: BufferedSource,
//            size: Size,
//            options: Options
//    ): DecodeResult = withInterruptibleSource(source) { interruptibleSource ->
//        try {
//            val bufferedSource = interruptibleSource.buffer()
//
//            gifDrawable(bufferedSource)
//        } catch (e: Throwable) {
//            e.printStackTrace()
//            DecodeResult(
//                    CoreApp.instance.getDrawable(R.drawable.default_image)!!,
//                    isSampled = false
//            )
//        }
//    }
//
//    private fun gifDrawable(bufferedSource: BufferedSource): DecodeResult {
//        var tempFile: File ?= null
//        try {
//            tempFile = File.createTempFile("tmp", ".gif", null)
//            bufferedSource.use { tempFile.sink().use(it::readAll) }
//            val drawable = GifDrawable(tempFile)
//
//            return DecodeResult(
//                    drawable = drawable,
//                    isSampled = false
//            )
//        } finally {
//            tempFile?.delete()
//        }
//
//    }
//
////    @Deprecated("不好使，播放时长不准确，性能低")
////    private fun movieGif(
////            bufferedSource: BufferedSource,
////            pool: BitmapPool,
////            source: BufferedSource,
////            size: Size,
////            options: Options
////    ): DecodeResult {
//////        val tempFile = File.createTempFile("tmp", ".gif", null)
//////        bufferedSource.use { tempFile.sink().use(it::readAll) }
//////        val movie = bufferedSource.use { Movie.decodeFile(tempFile.path) }
////
////        val movie = bufferedSource.use { Movie.decodeStream(it.inputStream()) }
////
////        val drawable = MovieGifDrawable(
////                movie = movie,
////                pool = pool,
////                config = when {
////                    movie.isOpaque && options.allowRgb565 -> Bitmap.Config.RGB_565
////                    Build.VERSION.SDK_INT >= 26 && options.config == Bitmap.Config.HARDWARE -> Bitmap.Config.ARGB_8888
////                    else -> options.config
////                },
////                scale = options.scale
////        )
////
////        return DecodeResult(
////                drawable = drawable,
////                isSampled = false
////        )
////    }
////
////    @Deprecated("系统提供的 AnimatedImageDrawable 播放有 bug 速度极快")
////    private fun animImageDrawable(
////            bufferedSource: BufferedSource,
////            pool: BitmapPool,
////            source: BufferedSource,
////            size: Size,
////            options: Options
////    ): DecodeResult {
//////        val buffer: ByteBuffer = ByteBuffer.wrap(bufferedSource.readByteArray())
//////        val decoderSource = ImageDecoder.createSource(buffer)
////
////        val tempFile = File.createTempFile("tmp", ".gif", null)
////        bufferedSource.use { tempFile.sink().use(it::readAll) }
//////        val decoderSource = ImageDecoder.createSource(CoreApp.instance.assets, "tmp8487056605038674843.gif")
////        val decoderSource = ImageDecoder.createSource(tempFile)
////
////        val drawable = ImageDecoder.decodeDrawable(decoderSource)
////
////        "decoderSource=$decoderSource, drawable=$drawable".e()
////        return DecodeResult(
////                drawable = drawable,
////                isSampled = false
////        )
////    }
//
//}