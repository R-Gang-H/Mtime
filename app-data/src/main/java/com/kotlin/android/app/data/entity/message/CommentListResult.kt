package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/3/18
 * 评论/回复 列表返回
 */
data class CommentListResult(
    var nextStamp: String? = null,
    var pageSize: Long? = 0L,
    var items: List<Comment>? = null,
    var hasNext: Boolean? = null//是否有下一页
) : ProguardRule {

    data class Comment(
        var type: Long? = null,//消息类型： 1收到对内容的评论；2收到对评论的回复；3收到针对回复的回复
        var messageId: String? = null,
        var commentDate: Long? = null,//收到评论或回复的时间 Unix时间戳 ，单位毫秒
        var user: CommentUser? = null,//评论用户信息
        var contentInfo: ContentInfo? = null,//母内容
        var commentInfo: CommentInfo? = null,
        var replyInfo: ReplyInfo? = null,//回复(type=2,3时有值)
        var replyToReplyInfo: ReplyToReplyInfo? = null,//回复所针对的回复主体，replyInfo针对这条回复进行的回复。(type=3时有值)
        var unRead: Boolean? = null,//是否未读 true：未读
    ) : ProguardRule {

        data class CommentUser(
            var userId: Long? = null,// 用户ID
            var nickName: String? = null,//用户昵称
            var avatarUrl: String? = null,//头像
            var gender: Long? = 3L,// 性别 1:男2:女3:保密
            var authType: Long? = null,//用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
            var authRole: String? = null,//认证角色 用户认证类型为"电影人"，才有的角色字段，其余为空
        ) : ProguardRule

        //内容
        data class ContentInfo(
            //被评论的内容主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), ALBUM(5, "相册"),
            // TOPIC_LIST(6, "榜单"), CINEMA(7, "影院"); MOVIE_TRAILER(8, "预告片"); LIVE(9, "直播"), CARD_USER(10, "卡片用户"),
            // CARD_SUIT(11, "卡片套装"), VIDEO(12, "视频"), AUDIO(13, "音频"), FILM_LIST(14, "片单");
            var contentType: Long = 1,
            var contentId: Long = 0,//内容ID
            var title: String? = "",//标题
            var content: String? = "",//内容
            var imgList: ArrayList<String>? = null,//图片列表（如果只有一张图，就是单图。图可能是影片海报或者影人海报，不要关心）
            var imgTag: String? = "",//展示在图片上的文字（可能影片名、影人名，展示于图片上方，也可能为空）
            var hasVideo: Boolean? = false,//是否有视频
            var commentCount: Long? = null,//评论数
            var praiseCount: Long? = null,//点赞数
        ) : ProguardRule

        //评论的内容
        data class CommentInfo(
            var commentId: Long = 0,
            var commentContent: String? = null,
            var commentImg : String? = null
        ) : ProguardRule

        //回复的内容
        data class ReplyInfo(
            var replyId: Long = 0,
            var replyContent: String? = null,
            var replyImg : String? = null
        ) : ProguardRule

        //回复所针对的回复主体
        data class ReplyToReplyInfo(
            var replyId: Long = 0,
            var replyContent: String? = null,
            var replyImg : String? = null
        ) : ProguardRule
    }
}