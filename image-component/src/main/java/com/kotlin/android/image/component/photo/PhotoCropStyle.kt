package com.kotlin.android.image.component.photo

import androidx.annotation.DrawableRes

/**
 *
 * Created on 2022/5/13.
 *
 * @author o.s
 */
data class PhotoCropStyle(
    @DrawableRes val icon: Int,
    val title: String,
    var desc: String = "",
)