package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/9
 * description: 身份认证-作品类型
 */
data class AuthMovieTypeBean(
        var id: Long = 0L,//类型id
        var tag: String = "",//作品类型名称
        var isSelected: Boolean = false,//是否是选中的
        var canClick: Boolean = true//是否可以点击
) : ProguardRule {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthMovieTypeBean

        if (id != other.id) return false
        if (tag != other.tag) return false
        if (isSelected != other.isSelected) return false
        if (canClick != other.canClick) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + tag.hashCode()
        result = 31 * result + isSelected.hashCode()
        result = 31 * result + canClick.hashCode()
        return result
    }
}