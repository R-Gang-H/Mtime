package com.kotlin.android.comment.component.bean

import android.text.TextUtils
import com.kotlin.android.app.data.entity.community.content.CommentList
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.formatCount

/**
 * Created by lushan on 2020/8/6
 * 评论回复viewBean  评论列表恢复及评论详情恢复
 */
data class ReplyViewBean(var userId: Long = 0L,
                         var replyId: Long = 0L,
                         var userName: String = "",//回复用户昵称
                         var userPic: String = "",//回复用户头像
                         var replyContent: String = "",//用户回复内容
                         var publishDate: String = "",//回复评论时间
                         var praiseCount: Long = 0,//点赞数
                         var picUrl: String = "",//回复的图片
                         var isLike: Boolean = false,//是否自己点击支持
                         var userAuthType: Long = 0L//认证类型
) : ProguardRule {

    companion object {
        fun obtain(reply: CommentList.Reply): ReplyViewBean {
            return ReplyViewBean().apply {
                replyId = reply.replyId
                userName = reply.createUser?.nikeName.orEmpty() //回复用户昵称
                userPic = reply.createUser?.avatarUrl.orEmpty() //回复用户头像
                replyContent = reply.body.orEmpty() //用户回复内容
                publishDate = reply.userCreateTime?.show.orEmpty() //回复评论时间
//                praiseCount = //点赞数
                picUrl = reply.images?.firstOrNull()?.imageUrl.orEmpty() //回复的图片
//                isLike = //是否自己点击支持
//                userAuthType = //认证类型
            }
        }
    }

    fun getPraiseCountStr(): String {
        return if (praiseCount <= 0L) "" else formatCount(praiseCount)
    }

    fun isShowScanImg(): Boolean {
        return TextUtils.isEmpty(picUrl).not()
    }

    //是不是机构认证用户
    fun isInstitutionAuthUser(): Boolean {
        return userAuthType == 4L
    }

    //是不是认证用户
    fun isAuthUser(): Boolean {
        return userAuthType > 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReplyViewBean

        if (userId != other.userId) return false
        if (replyId != other.replyId) return false
        if (userName != other.userName) return false
        if (userPic != other.userPic) return false
        if (replyContent != other.replyContent) return false
        if (publishDate != other.publishDate) return false
        if (praiseCount != other.praiseCount) return false
        if (picUrl != other.picUrl) return false
        if (isLike != other.isLike) return false
        if (userAuthType != other.userAuthType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + replyId.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + userPic.hashCode()
        result = 31 * result + replyContent.hashCode()
        result = 31 * result + publishDate.hashCode()
        result = 31 * result + praiseCount.hashCode()
        result = 31 * result + picUrl.hashCode()
        result = 31 * result + isLike.hashCode()
        result = 31 * result + userAuthType.hashCode()
        return result
    }

}