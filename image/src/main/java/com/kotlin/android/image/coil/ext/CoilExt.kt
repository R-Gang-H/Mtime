package com.kotlin.android.image.coil.ext

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.repeatCount
import coil.request.videoFrameMillis
import coil.target.ImageViewTarget
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import com.kotlin.android.core.CoreApp
import com.kotlin.android.image.ImageCallback
import com.kotlin.android.image.R
import com.kotlin.android.image.getProxyUrl
import com.kotlin.android.ktx.ext.core.Direction

/**
 * 创建者: zl
 * 创建时间: 2020/8/10 3:40 PM
 * 描述:Coil图片加载框架扩展
 * Coil默认提供了四种变换：模糊变换、圆形变换、灰度变换和圆角变换：
 * BlurTransformation CircleCropTransformation GrayscaleTransformation RoundedCornersTransformation
 */

/**
 * 简单介绍
 * https://github.com/coil-kt/coil/
 * 完全使用kotlin编写，使用了kotlin的协程，图片网络请求方式默认为Okhttp,相比较于我们常用的Picasso，Glide或者Fresco，它有以下几个特点：
 * 足够快速，它在内存、图片存储、图片的采样、Bitmap重用、暂停\取消下载等细节方面都有很大的优化(相比于上面讲的三大框架)；
 * 足够轻量，只有大概1500个核心方法，当然也是相对于PGF而言的；
 * 足够新，也足够现代！使用了最新的Kotlin协程所编写，充分发挥了CPU的性能，同时也使用了OKHttp、Okio、LifeCycle等比较新式的Android库。
 * */
/**
 * 既可以加载Gif，同时兼容加载普通图片
 *
 * [roundedRadius] 圆角半径，默认为0，即：没有圆角
 * [direction] 指定圆角方位，默认 [Direction.ALL]
 */
fun ImageView.loadImage(
    data: Any?,
    width: Int = 0,
    height: Int = 0,
    useProxy: Boolean = true,
    isLoadSvg: Boolean = false,
    isLoadVideo: Boolean = false,
    defaultImg: Drawable? = null,
    errorImg: Drawable? = null,
    @DrawableRes defaultImgRes: Int = R.drawable.default_image,
    @DrawableRes errorImgRes: Int? = null,
    allowHardware: Boolean = true,
    circleCrop: Boolean = false,
    roundedRadius: Float = 0f, //圆角半径，此值为0时，可以指定单独某个角的值
    direction: Int = Direction.ALL,
//    roundedRadiusTopLeft: Float = 0f,
//    roundedRadiusTopRight: Float = 0f,
//    roundedRadiusBottomLeft: Float = 0f,
//    roundedRadiusBottomRight: Float = 0f,
    blurRadius: Float = 0f,
    blurSampling: Float = 0f,
    callback: ImageCallback? = null,
    onStart: ((placeholder: Drawable?) -> Unit)? = null,
    onError: ((error: Drawable?) -> Unit)? = null,
    onSuccess: ((result: Drawable) -> Unit)? = null
) {
    loadImage(
        data = data,
        width = width,
        height = height,
        useProxy = useProxy,
        isLoadSvg = isLoadSvg,
        isLoadVideo = isLoadVideo,
        defaultImg = defaultImg,
        errorImg = errorImg,
        defaultImgRes = defaultImgRes,
        errorImgRes = errorImgRes,
        allowHardware = allowHardware,
        circleCrop = circleCrop,
        roundedRadius = roundedRadius,
        direction = direction,
//        roundedRadiusTopLeft = roundedRadiusTopLeft,
//        roundedRadiusTopRight = roundedRadiusTopRight,
//        roundedRadiusBottomLeft = roundedRadiusBottomLeft,
//        roundedRadiusBottomRight = roundedRadiusBottomRight,
        blurRadius = blurRadius,
        blurSampling = blurSampling,
        imageView = this,
        callback = callback,
        onStart = onStart,
        onError = onError,
        onSuccess = onSuccess
    )
}

/**
 * [roundedRadius] 圆角半径，默认为0，即：没有圆角
 * [direction] 指定圆角方位，默认 [Direction.ALL]
 * 如果用到callback，必须传ImageView！！单独使用target(onSuccess)，获取的drawable只有中间一条，图片不完整
 */
fun loadImage(
    data: Any?,
    width: Int = 0,
    height: Int = 0,
    useProxy: Boolean = true,
    isLoadSvg: Boolean = false,
    isLoadVideo: Boolean = false,
    defaultImg: Drawable? = null,
    errorImg: Drawable? = null,
    @DrawableRes defaultImgRes: Int = R.drawable.default_image,
    @DrawableRes errorImgRes: Int? = null,
    allowHardware: Boolean = true,
    circleCrop: Boolean = false,
    roundedRadius: Float = 0F, //圆角半径，此值为0时，可以指定单独某个角的值
    direction: Int = Direction.ALL,
//    roundedRadiusTopLeft: Float = 0f,
//    roundedRadiusTopRight: Float = 0f,
//    roundedRadiusBottomLeft: Float = 0f,
//    roundedRadiusBottomRight: Float = 0f,
    blurRadius: Float = 0f,
    blurSampling: Float = 0f,
    imageView: ImageView? = null,
    callback: ImageCallback? = null,
    onStart: ((placeholder: Drawable?) -> Unit)? = null,
    onError: ((error: Drawable?) -> Unit)? = null,
    onSuccess: ((result: Drawable) -> Unit)? = null
) {
    loadIAny(
        data = if (data is String) {
            getProxyUrl(
                imgUrl = data,
                width = width,
                height = height,
                useProxy = useProxy
            )
        } else {
            data
        },
        imageLoader = when {
            isLoadSvg -> {
                svgImageLoader
            }
            isLoadVideo -> {
                videoLoader
            }
            else -> {
                gifImageLoader
            }
        },
        context = imageView?.context ?: CoreApp.instance.applicationContext
    ) {
        // 占位及错误图
        if (null == defaultImg) {
            placeholder(defaultImgRes)
        } else {
            placeholder(defaultImg)
        }
        // 如果没有错误图，则使用占位图
        if (null == errorImg) {
            if (null == errorImgRes) {
                if (null == defaultImg) {
                    error(defaultImgRes)
                } else {
                    error(defaultImg)
                }
            } else {
                error(errorImgRes)
            }
        } else {
            error(errorImg)
        }

        if (isLoadVideo) {
            videoFrameMillis(1L)
        }

        //如果不关闭硬件加速，部分通过canvas获取bitmap的无法获取成功
        allowHardware(allowHardware)

        // 转换
        val transformations: MutableList<Transformation> = ArrayList()
        // 转换-圆形
        if (circleCrop) {
            transformations.add(CircleCropTransformation())
        }
        // 转换-圆角
        if (roundedRadius > 0f) {
            if (direction == Direction.ALL) {
                transformations.add(RoundedCornersTransformation(roundedRadius))
            } else {
                var topLeft = 0F
                var topRight = 0F
                var bottomLeft = 0F
                var bottomRight = 0F
                if (direction and Direction.LEFT_TOP == Direction.LEFT_TOP) {
                    topLeft = roundedRadius
                }
                if (direction and Direction.RIGHT_TOP == Direction.RIGHT_TOP) {
                    topRight = roundedRadius
                }
                if (direction and Direction.RIGHT_BOTTOM == Direction.RIGHT_BOTTOM) {
                    bottomLeft = roundedRadius
                }
                if (direction and Direction.LEFT_BOTTOM == Direction.LEFT_BOTTOM) {
                    bottomRight = roundedRadius
                }

                transformations.add(
                    RoundedCornersTransformation(
                        topLeft = topLeft,
                        topRight = topRight,
                        bottomLeft = bottomLeft,
                        bottomRight = bottomRight
                    )
                )
            }
//        } else if ((roundedRadiusTopLeft
//                    + roundedRadiusTopRight
//                    + roundedRadiusBottomLeft
//                    + roundedRadiusBottomRight) > 0f
//        ) {
//            transformations.add(
//                RoundedCornersTransformation(
//                    topLeft = roundedRadiusTopLeft,
//                    topRight = roundedRadiusTopRight,
//                    bottomLeft = roundedRadiusBottomLeft,
//                    bottomRight = roundedRadiusBottomRight
//                )
//            )
        }
        // 转换-模糊
        if (blurRadius > 0f && blurSampling > 0f) {
            transformations.add(
                BlurTransformation(
                    context = imageView?.context ?: CoreApp.instance.applicationContext,
                    radius = blurRadius,
                    sampling = blurSampling
                )
            )
        }
        if (transformations.isNotEmpty()) {
            transformations(transformations)
        }

        imageView?.apply {
            target(object : ImageViewTarget(this) {
                override fun onError(error: Drawable?) {
                    super.onError(error)
                    callback?.onError(error)
                    onError?.invoke(error)
                }

                override fun onStart(placeholder: Drawable?) {
                    super.onStart(placeholder)
                    callback?.onStart(placeholder)
                    onStart?.invoke(placeholder)
                }

                override fun onSuccess(result: Drawable) {
                    super.onSuccess(result)
                    callback?.onSuccess(result)
                    onSuccess?.invoke(result)
                }
            })
        } ?: target(
            onStart = {
                callback?.onStart(it)
                onStart?.invoke(it)
            },
            onError = {
                callback?.onError(it)
                onError?.invoke(it)
            },
            onSuccess = {
                callback?.onSuccess(it)
                onSuccess?.invoke(it)
            }
        )
    }
}

private fun loadIAny(
    data: Any?,
    imageLoader: ImageLoader = gifImageLoader,
    context: Context = CoreApp.instance.applicationContext,
    singlePlay:Boolean = false,
    builder: ImageRequest.Builder.() -> Unit = {}

): Disposable {
    val request = ImageRequest.Builder(context)
        .data(data)
        .apply(builder)
    if (singlePlay) {//gif只播放一次，底部导航栏有可能用到
        request.repeatCount(1)
    }
    return imageLoader.enqueue(request.build())
}

/**
 * 设置ImageLoader，用于加载Gif
 */
private val gifImageLoader = ImageLoader.Builder(CoreApp.instance.applicationContext)
    .componentRegistry {
//        add(MyGifDecoder())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            add(ImageDecoderDecoder(context = CoreApp.instance.applicationContext)) // add(MyGifDecoder())
        } else {
            add(GifDecoder())
        }
    }
    .build()

private val svgImageLoader = ImageLoader.Builder(CoreApp.instance.applicationContext)
    .componentRegistry {
        add(SvgDecoder(CoreApp.instance.applicationContext))
    }
    .build()

private val videoLoader = ImageLoader.Builder(CoreApp.instance.applicationContext)
    .componentRegistry {
        add(VideoFrameFileFetcher(CoreApp.instance.applicationContext))
        add(VideoFrameUriFetcher(CoreApp.instance.applicationContext))
    }
    .build()
