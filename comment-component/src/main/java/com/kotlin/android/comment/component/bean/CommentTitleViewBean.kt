package com.kotlin.android.comment.component.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.formatCount


/**
 * Created by lushan on 2020/8/7
 * 评论列表标题
 */
data class CommentTitleViewBean(var totalCount: Long = 0L,//评论总数
                                var isNew: Boolean = false//是否是最新
) : ProguardRule {
    fun getTotalCountStr():String{
        return if (totalCount<=0)"" else formatCount(totalCount)
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommentTitleViewBean

        if (totalCount != other.totalCount) return false
        if (isNew != other.isNew) return false

        return true
    }

    override fun hashCode(): Int {
        var result = totalCount.hashCode()
        result = 31 * result + isNew.hashCode()
        return result
    }
}