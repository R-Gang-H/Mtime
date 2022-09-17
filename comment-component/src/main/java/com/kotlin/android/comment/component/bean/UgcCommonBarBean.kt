package com.kotlin.android.comment.component.bean

import com.kotlin.android.app.data.entity.common.MovieSubItemRating
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/8/25
 * description:
 *     1.标题上关注用户
2.图集还是图文类型
3.底部是否可以评论
4.点赞、点踩、回复数
 */

data class UgcCommonBarBean(var isAlbumType: Boolean,
                            var createUser: CreateUser,
                            var canComment: Boolean,//是否都可以评论
                            var commentSupport: CommentSupport,//点赞相关
                            var isShortReview:Boolean = false,//是否是短影评
                            var commentPmsn:Long? = 1L,//1允许评论，2禁止评论
                            var editBtn: CommunityContent.BtnShow? = null//编辑内容副本 如果是null不显示编辑按钮
) : ProguardRule {

    //    创建人
    data class CreateUser(
            val authType: Long = 0L,//1.个人 2.影评人 3.电影人 4.机构
            val avatarUrl: String = "",//昵称
            val followed: Boolean = false,//当前用户是否关注该用户
            val nikeName: String = "",//用户昵称
            val userId: Long = 0L,//用户id
            val createTime: String = "",//用户创建时间
            val score:String = "",//影评人发布评分
            val fcSubItemRatings: List<MovieSubItemRating> = listOf(), //影评发表者分项评分列表
            val createTimeStamp:Long = 0L,//发布时间戳
            val contentStatus:Long = -1L//文章状态
    ) : ProguardRule

    //    底部点赞、点踩、回复数
    data class CommentSupport(
            var commentCount: Long = 0L,//评论数
            val praiseDownCount: Long = 0L,//点踩数
            val praiseUpCount: Long = 0L,//点赞数
            val userCollected: Boolean = false,//用户是否收藏
            val userPraised: Long = 0L,//当前用户点赞 1.点赞、2.点踩 null 无
            val userVotedId:Long = 0L,//pk帖子用 pk帖子是否已经投过票 0未投票
            val voteStat:CommunityContent.VoteStat? = null,//pk帖子信息，投票帖子 对应参与人数信息
            val vote: CommunityContent.Vote? = null//pk帖子选项信息
    ) : ProguardRule
}