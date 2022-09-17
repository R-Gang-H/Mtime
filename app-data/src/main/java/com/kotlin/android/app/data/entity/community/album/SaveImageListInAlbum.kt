package com.kotlin.android.app.data.entity.community.album

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/30
 * description:保存相册图片
 */
data class SaveImageListInAlbum(
    var albumId: Int = 0,
    var items: MutableList<ImageListInAlbum.Image>? = mutableListOf<ImageListInAlbum.Image>()
): ProguardRule
