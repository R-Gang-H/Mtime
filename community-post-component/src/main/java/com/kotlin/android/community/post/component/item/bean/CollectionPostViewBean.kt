package com.kotlin.android.community.post.component.item.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.formatCount

/**
 * create by lushan on 2020/9/15
 * description: 帖子收藏列表
 */
data class CollectionPostViewBean(
        var postId: Long = 0L,//帖子id
        var postTitle: String = "",//帖子标题
        var likeCount: Long = 0L,//点赞人数
        var replyCount: Long = 0L,//回复人数
        var isPkPost:Boolean = false//是否是pk帖子
) : ProguardRule {

    fun getLikeCountStr():String = formatCount(likeCount)

    fun getReplyCountStr():String = formatCount(replyCount)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CollectionPostViewBean

        if (postId != other.postId) return false
        if (postTitle != other.postTitle) return false
        if (likeCount != other.likeCount) return false
        if (replyCount != other.replyCount) return false
        if (isPkPost != other.isPkPost) return false

        return true
    }

    override fun hashCode(): Int {
        var result = postId.hashCode()
        result = 31 * result + postTitle.hashCode()
        result = 31 * result + likeCount.hashCode()
        result = 31 * result + replyCount.hashCode()
        result = 31 * result + isPkPost.hashCode()
        return result
    }
}