package com.kotlin.android.ugc.detail.component.bean

import android.graphics.drawable.Drawable
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.user.UserManager

/**
 * create by lushan on 2020/8/12
 * description: ugc详情相册标题
 */
data class UgcAlbumTitleViewBean(var album: String = "", var albumId: Long = 0L, var userId: Long = 0L) :
    ProguardRule {
    //    是否是自己的相册
    fun isMyAlbum(): Boolean = userId == UserManager.instance.userId && userId != 0L

    fun getEditDrawable():Drawable?{
        return if (isMyAlbum()) getDrawable(R.drawable.ic_edit) else null
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UgcAlbumTitleViewBean

        if (album != other.album) return false
        if (albumId != other.albumId) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = album.hashCode()
        result = 31 * result + albumId.hashCode()
        result = 31 * result + userId.hashCode()
        return result
    }

}
