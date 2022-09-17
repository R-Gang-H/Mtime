package com.kotlin.android.image.coil.ext

import android.widget.ImageView
import com.kotlin.android.image.ImageCallback
import com.kotlin.android.image.R

/**
 * 兼容Java代码的图片库，
 *
 * Created on 2022/3/7.
 *
 * @author o.s
 */
object CoilCompat {

    fun loadGifImage(
        imageView: ImageView?,
        data: Any?,
        width: Int = 0,
        height: Int = 0,
        useProxy: Boolean = true,
        allowHardware: Boolean = true,
        circleCrop: Boolean = false,
        defaultImgRes: Int? = null,
        errorImgRes: Int? = null,
        cornerRadius: Float = 0F,
    ) {
        imageView?.loadImage(
            data = data,
            width = width,
            height = height,
            useProxy = useProxy,
            allowHardware = allowHardware,
            defaultImgRes = defaultImgRes ?: R.drawable.default_image,
            errorImgRes = errorImgRes,
            circleCrop = circleCrop,
            roundedRadius = cornerRadius,
        )
    }

    fun loadGifImageCallback(
        data: Any?,
        width: Int = 0,
        height: Int = 0,
        useProxy: Boolean = true,
        allowHardware: Boolean = true,
        imageCallback: ImageCallback
    ) {
        loadImage(
            data = data,
            width = width,
            height = height,
            useProxy = useProxy,
            allowHardware = allowHardware,
            callback = imageCallback
        )
    }

    fun loadBlurImage(
        imageView: ImageView?,
        data: String?,
        width: Int = 0,
        height: Int = 0,
        useProxy: Boolean = true,
        defaultImgRes: Int? = null,
        blurRadius: Float = 0F,
        blurSampling: Float = 0F,
    ) {
        imageView?.loadImage(
            data = data,
            width = width,
            height = height,
            useProxy = useProxy,
            defaultImgRes = defaultImgRes ?: R.drawable.default_image,
            blurRadius = blurRadius,
            blurSampling = blurSampling,
        )
    }
}