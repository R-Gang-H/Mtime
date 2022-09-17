package com.kotlin.android.app.data.entity.comment

import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/1
 * description:评论详情
 */
data class CommentDetail(
        var body: String? = "",//富文本正文
        var commentId: Long = 0L,//评论id
        var createUser: CreateUser? = null,//评论创建人
        var images: List<CommContent.Image>? = mutableListOf(),//评论中图片
        var interactive: Interactive? = null,//交互数据，点赞数
        var objId: Long = 0L,//评论主体id
        var objType: Long = 0,//评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院");
        var optId: Long = 0,//观点id
//        var replies: List<CommentList.Reply>? = mutableListOf(),
        var replyCount: Long = 0L,//回复数
        var userCreateTime: CommContent.UserCreateTime?
) : ProguardRule {

    fun getFirstCommentPic(): String {
        return if (images?.isNotEmpty() == true) {
            images?.get(0)?.imageUrl.orEmpty()
        } else {
            ""
        }
    }

    data class CreateUser(
            var authType: Long = 0L,//用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
            var avatarUrl: String? = "",//头像
            var gender: Long = 0L,//性别1男 2女 3保密
            var nikeName: String? = "",//用户昵称
            var userId: Long = 0L//用户id
    ) : ProguardRule

    data class Interactive(
            var praiseDownCount: Long = 0L,//点踩数
            var praiseUpCount: Long = 0L,//点赞数
            var userPraised: Long? = 0L//当前用户点赞：1.点赞 2.点踩 null:无(未操作或当前用户未登录)
    )


}


