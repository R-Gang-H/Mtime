package com.kotlin.android.comment.component.bean

import com.kotlin.android.app.data.constant.CommConstant.PRAISE_OBJ_TYPE_CARD_USER
import com.kotlin.android.app.data.constant.CommConstant.PRAISE_OBJ_TYPE_LIVE
import com.kotlin.android.app.data.entity.community.content.CommentList
import com.kotlin.android.app.data.entity.community.content.CommentList.Item.Companion.USER_PRAISE_UP
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.user.UserManager

/**
 * Created by lushan on 2020/8/6
 * 评论列表
 */
data class CommentViewBean(
        var commentId: Long = 0L,//评论id
        var userId: Long = 0L,//评论用户id
        var userName: String = "",//用户名称
        var userPic: String = "",//用户头像
        var publishDate: String = "",//发布时间
        var commentContent: String = "",//评论内容
        var likeCount: Long = 0L,//点赞数
        var userPraised: Long? = null,//当前用户赞：1:点赞 2点踩 null:无(未操作或当前用户未登录)
        var praiseDownCount: Long = 0L,//点踩数
        var commentPic: String = "",//评论图片
        var replyList: MutableList<ReplyViewBean> = mutableListOf(), //评论回复列表
        var replyCount: Long = 0L,//评论回复条数
        var type: Long = 0L,//评论类型
        var objId: Long = 0L,//评论主体
        var userAuthType:Long = 0L,//认证类型
        var isShowTriangle: Boolean = false//评论详情页回复是否显示三角形
) : ProguardRule {

    fun getLikeCountStr():String{
        return if (likeCount<=0) "" else formatCount(likeCount)
    }

    fun getReplyCountStr():String{
        return if (replyCount<=0) "" else formatCount(replyCount)
    }

    companion object{
        fun covertReplyViewBean(bean:CommentViewBean):ReplyViewBean{
            return ReplyViewBean(bean.userId,0L,bean.userName,bean.userPic)
        }

        fun obtain(item: CommentList.Item): CommentViewBean {
            return CommentViewBean().apply {
                type = item.objType
                objId = item.objId //评论主体
                commentId = item.commentId
                userId = item.createUser?.userId ?: 0L //评论用户id
                userName = item.createUser?.nikeName.orEmpty() //用户名称
                userPic = item.createUser?.avatarUrl.orEmpty() //用户头像
                publishDate = formatPublishTime(item.userCreateTime?.stamp) //发布时间
                commentContent = item.body.orEmpty() //评论内容
                likeCount = item.interactive?.praiseUpCount ?: 0L //点赞数
                userPraised = item.interactive?.userPraised //当前用户赞：1:点赞 2点踩 null:无(未操作或当前用户未登录)

                replyCount = item.replyCount //评论回复条数
                commentPic = item.images?.firstOrNull()?.imageUrl.orEmpty() //评论图片
                userAuthType = item.createUser?.authType ?: 0L //认证类型

//                praiseDownCount = 0L//点踩数
                val replies = ArrayList<ReplyViewBean>()
                item.replies?.forEach {
                    replies.add(ReplyViewBean.obtain(it))
                }
                replyList = replies //评论回复列表
//                isShowTriangle = false//评论详情页回复是否显示三角形
            }
        }
    }


    //是不是机构认证用户
    fun isInstitutionAuthUser(): Boolean {
        return userAuthType == CommConstant.AUTH_TYPE_ORGAN
    }

    //是不是认证用户
    fun isAuthUser(): Boolean {
        return userAuthType > CommConstant.AUTH_TYPE_PERSONAL
    }

//    点赞
    fun isLike(): Boolean = userPraised == USER_PRAISE_UP

    /**
     * 是否是自己的评论
     */
    fun isMyComment(): Boolean {
        return (objId == UserManager.instance.userId && type == PRAISE_OBJ_TYPE_CARD_USER)
                || (userId == UserManager.instance.userId && type!=PRAISE_OBJ_TYPE_LIVE)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommentViewBean

        if (commentId != other.commentId) return false
        if (userId != other.userId) return false
        if (userName != other.userName) return false
        if (userPic != other.userPic) return false
        if (publishDate != other.publishDate) return false
        if (commentContent != other.commentContent) return false
        if (likeCount != other.likeCount) return false
        if (userPraised != other.userPraised) return false
        if (praiseDownCount != other.praiseDownCount) return false
        if (commentPic != other.commentPic) return false
        if (replyList != other.replyList) return false
        if (replyCount != other.replyCount) return false
        if (type != other.type) return false
        if (objId != other.objId) return false
        if (userAuthType != other.userAuthType) return false
        if (isShowTriangle != other.isShowTriangle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = commentId.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + userPic.hashCode()
        result = 31 * result + publishDate.hashCode()
        result = 31 * result + commentContent.hashCode()
        result = 31 * result + likeCount.hashCode()
        result = 31 * result + (userPraised?.hashCode() ?: 0)
        result = 31 * result + praiseDownCount.hashCode()
        result = 31 * result + commentPic.hashCode()
        result = 31 * result + replyList.hashCode()
        result = 31 * result + replyCount.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + objId.hashCode()
        result = 31 * result + userAuthType.hashCode()
        result = 31 * result + isShowTriangle.hashCode()
        return result
    }


}