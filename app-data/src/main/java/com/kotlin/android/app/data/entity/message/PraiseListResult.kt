package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/3/18
 * 点赞列表返回
 */
data class PraiseListResult(
    var nextStamp: String? = null,
    var pageSize: Long? = 0L,
    var items: List<Praise>? = null,
    var hasNext: Boolean? = null//是否有下一页
) : ProguardRule {

    data class Praise(
        var messageId: String? = null,
        //点赞主体类型 JOURNAL(1, "日志"),POST(2, "帖子"),FILM_COMMENT(3, "影评"),ARTICLE(4, "文章"),ALBUM(5, "相册"),TOPIC_LIST(6, "榜单"),
        // MOVIE_TRAILER(8, "预告片"),JOURNAL_COMMENT(101, "日志评论"),POST_COMMENT(102, "帖子评论"),FILM_COMMENT_COMMENT(103, "影评评论"),
        // ARTICLE_COMMENT(104, "文章评论"),ALBUM_COMMENT(105, "相册评论"),TOPIC_LIST_COMMENT(106, "榜单评论"),CINEMA_COMMENT(107, "影院评论"),
        // MOVIE_TRAILER_COMMENT(108, "预告片评论"),JOURNAL_REPLY(201, "日志回复"),
        // POST_REPLY(202, "帖子回复"),FILM_COMMENT_REPLY(203, "影评回复"),ARTICLE_REPLY(204, "文章回复"),ALBUM_REPLY(205, "相册回复"),
        // TOPIC_LIST_REPLY(206, "榜单回复"),CINEMA_REPLY(207, "影院回复"),MOVIE_TRAILER_REPLY(208, "预告片回复");
        var relatedType: Long? = 0,
        var relatedId: Long? = 0,//点赞主体ID
        var praiseLevel: Long? = 0,//1针对内容主体点赞；2针对评论点赞；3针对回复点赞
        var praiseUserTotal: Long? = 0,//点赞人数
        var users: List<PraiseUser>? = null,//点赞人
        var praiseDate: Long? = 0,  //点赞时间 Unix时间戳 ，单位秒
        var contentInfo: ContentInfo? = null,//内容（praiseLevel = 1，2，3时都必须）
        var commentInfo: CommentInfo? = null,//评论（praiseLevel = 2，3时必须）
        var replyInfo: ReplyInfo? = null,//回复（praiseLevel = 3时必须）
        var unRead: Boolean? = null,//是否未读 true：未读

    ) : ProguardRule {
        data class PraiseUser(
            var followed: Boolean? = null,//当前用户是否关注该用户 true:已关注 false:未关注 null:用户本人
            var userSign: String? = null,//个人签名
            var userId: Long? = null,// 用户ID
            var nickName: String? = null,// 用户昵称
            var avatarUrl: String? = null,//头像
            var gender: Long? = 3L,// 性别 1:男2:女3:保密
            var authType: Long? = null,//用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
            var authRole: String? = null//认证角色 用户认证类型为"电影人"，才有的角色字段，其余为空
        ) : ProguardRule

        //内容（praiseLevel = 1，2，3时都必须）
        data class ContentInfo(
            //内容类型 OURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), ALBUM(5, "相册"),
            // TOPIC_LIST(6, "榜单"), CINEMA(7, "影院"); MOVIE_TRAILER(8, "预告片"); LIVE(9, "直播"),
            // CARD_USER(10, "卡片用户"), CARD_SUIT(11, "卡片套装"), VIDEO(12, "视频"), AUDIO(13, "音频"), FILM_LIST(14, "片单")
            var contentType: Long = 1,
            var contentId: Long = 0,
            var title: String? = "",
            var content: String? = "",
            var imgList: ArrayList<String>? = null,
            var imgTag: String? = "",
            var hasVideo: Boolean? = false,
            var commentCount: Long? = null,//评论数
            var praiseCount: Long? = null,//点赞数
        ) : ProguardRule

        //评论的内容
        data class CommentInfo(
            var commentId: Long = 0,
            var commentContent: String? = null,
            var commentImg: String? = null,
            var praiseCount : Int = 0
        ) : ProguardRule

        //回复的内容
        data class ReplyInfo(
            var replyId: Long = 0,
            var replyContent: String? = null,
            var replyImg: String? = null,
            var praiseCount : Int = 0
        ) : ProguardRule
    }
}