package com.kotlin.android.image.bindadapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 创建者: zl
 * 创建时间: 2020/6/18 10:20 AM
 * 描述:自定义图片加载注解
 */

/***
 * 布局里设置width height传dp
 */
@BindingAdapter(value = ["load_image", "load_image_width", "load_image_height", "load_image_circle_crop", "load_image_default_img","load_image_roundedRadius"], requireAll = false)
fun loadImage(
        imageView: ImageView,
        url: String?,
        width: Int = 0,
        height: Int = 0,
        circleCrop: Boolean = false,
        defaultImg: Drawable? = null,
        roundedRadius : Int? = null
) {
//    LogUtils.e("width = $width  height = $height 转换后宽 = ${imageView.context.dp2px(width)}  转换后高 = ${imageView.context.dp2px(height)}")
    /**
     * 加载图片使用Coil，Glide先不要用，因为新的图片加载组件里，Glide功能不完整(与旧工程里的Glide有冲突)
     * 默认使用loadGifImage函数，既能加载gif，也能加载普通图片
     */
    imageView.loadImage(
            data = url,
            width = width.dp,
            height = height.dp,
            defaultImg = defaultImg,
            circleCrop = circleCrop,
            roundedRadius = roundedRadius?.toFloat()?:0f,
    )
}


