package com.kotlin.android.app.data.entity.community.content

import com.kotlin.android.app.data.entity.comment.CommentDetail
import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/8/25
 * description: 评论返回实体
 */

data class CommentList(
        val pageIndex: Long = 0L,//分页index
        val pageSize: Long = 0L,//分页size
        val hasNext: Boolean = true,//是否有下页
        val items: List<Item>? = mutableListOf(),
        val totalCount: Int = 0//评论总条数
) : ProguardRule {
    data class Item(
            val body: String?,
            val bodyWords:String?,//兼容prd上的老数据，去掉html标签
            val commentId: Long,//评论id
            val createUser: CommentDetail.CreateUser?,//发表评论人
            val images: List<CommunityContent.Cover>? = mutableListOf(),//评论中图片
            val interactive: Interactive?,//用户点赞、点踩交互数据
            val objId: Long,//评论主体对象
            val objType: Long,// 评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院");
            val optId: Long?,//观点id
            val replies: List<Reply>? = mutableListOf(),//平路回复
            val replyCount: Long,//评论回复数
            val userCreateTime: CommunityContent.UserCreateTime?,

            /**
             * 自定义评论发布状态:true表示发布的
             */
            var releasedState: Boolean = true
    ) : ProguardRule {
        companion object{
            const val USER_PRAISE_UP = 1L//点赞
            const val USER_PRAISE_DOWN = 2L//点踩
        }
    }




    data class Interactive(
            val praiseDownCount: Long,//点踩数
            val praiseUpCount: Long,//点赞数
            val userPraised: Long?//用户赞：1:点赞 2点踩 null:无
    )

    data class Reply(
            val body: String?,
            val createUser: CommentDetail.CreateUser?,//创建用户
            val images: List<CommunityContent.Cover>? = mutableListOf(),
            val reReplyId: Long,//被回复的回复id
            val reUser: CommentDetail.CreateUser?,//被回复用户
            val replyId: Long,//回复id
            val userCreateTime: CommunityContent.UserCreateTime?
    )


}

