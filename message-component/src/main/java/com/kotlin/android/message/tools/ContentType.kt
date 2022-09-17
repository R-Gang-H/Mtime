package com.kotlin.android.message.tools

/**
 * Created by zhaoninglongfei on 2022/4/24
 * 消息中心 - 点赞/评论 内容类型及转换
 */
//内容类型
enum class ContentType(val type: Long) {
    JOURNAL(1L),//JOURNAL(1, "日志")
    POST(2L),//POST(2, "帖子")
    FILM_COMMENT(3L),//FILM_COMMENT(3, "影评")
    ARTICLE(4L),//ARTICLE(4, "文章")
    ALBUM(5L),//ALBUM(5, "相册")
    TOPIC_LIST(6L),//TOPIC_LIST(6, "榜单")
    CINEMA(7L),//CINEMA(7, "影院")
    MOVIE_TRAILER(8L),//MOVIE_TRAILER(8, "预告片")
    LIVE(9L),//LIVE(9, "直播")
    CARD_USER(10L),//CARD_USER(10, "卡片用户")
    CARD_SUIT(11L),//CARD_SUIT(11, "卡片套装")
    VIDEO(12L),//VIDEO(12, "视频")
    AUDIO(13L),//AUDIO(13, "音频")
    FILM_LIST(14L),//FILM_LIST(14, "片单")
}

fun Long?.toContentType(): ContentType? {
    return when (this) {
        1L -> ContentType.JOURNAL
        2L -> ContentType.POST
        3L -> ContentType.FILM_COMMENT
        4L -> ContentType.ARTICLE
        5L -> ContentType.ALBUM
        6L -> ContentType.TOPIC_LIST
        7L -> ContentType.CINEMA
        8L -> ContentType.MOVIE_TRAILER
        9L -> ContentType.LIVE
        10L -> ContentType.CARD_USER
        11L -> ContentType.CARD_SUIT
        12L -> ContentType.VIDEO
        13L -> ContentType.AUDIO
        14L -> ContentType.FILM_LIST
        else -> null
    }
}