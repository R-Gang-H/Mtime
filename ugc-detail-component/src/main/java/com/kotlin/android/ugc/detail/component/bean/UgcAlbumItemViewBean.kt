package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by lushan on 2020/8/6
 * UGC相册item
 */
data class UgcAlbumItemViewBean(var imageId: Long = 0L,//图片id
                                var imagePic: String = "",//图片url
                                var imagePath: String = ""//本地图片地址
) : ProguardRule {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UgcAlbumItemViewBean

        if (imageId != other.imageId) return false
        if (imagePic != other.imagePic) return false
        if (imagePath != other.imagePath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imageId.hashCode()
        result = 31 * result + imagePic.hashCode()
        result = 31 * result + imagePath.hashCode()
        return result
    }
}