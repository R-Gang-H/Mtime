package com.kotlin.android.app.data.entity.community.person

import com.kotlin.android.app.data.ProguardRule

/**
 *
 * @author wangwei
 * @date 2020/9/21
 * 个人主页实体类
 *
 * userId 用户ID

nikeName 用户昵称

avatarUrl 头像

gender 性别 1.男2.女3.保密

registDuration	String 注册时长

info	String 简介

authType 用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");

followCount 用户关注数

fansCount 用户粉丝数

followed	Boolean 当前用户是否关注该用户 true:已关注 false:未关注 null:用户本人

wantSeeCount 想看

hasSeenCount 看过

journalCount 日志数

postCount 帖子数

filmCommentCount 影评数

showArticle	Boolean 是否显示文章标签

articleCount 文章数

groupCount 加入群组数

albumCount 相册数
userLevel 用户等级
 */
data class UserHomeInfo(
    val albumCount: Long = 0,
    val articleCount: Long = 0,
    val authType: Long = 0,
    val avatarUrl: String? = "",
    val fansCount: Long = 0,
    val filmCommentCount: Long = 0,
    val followCount: Long = 0,
    val followed: Boolean? = false,//当前用户是否关注该用户 true:已关注 false:未关注 null:用户本人
    val gender: Int = 0,
    val groupCount: Long = 0,
    val hasSeenCount: Long = 0,
    val info: String? = "",
    val journalCount: Long = 0,
    val nikeName: String? = "",
    val postCount: Long = 0,
    val registDuration: String? = "",
    val showArticle: Boolean = false,
    val userId: Long = 0,
    val wantSeeCount: Long = 0,
    val userLevel: Long = 0,

    val status: Long = 0,
    val authRole: String? = "",//认证角色，"电影人"认证类型 比如“认证 导演”“认证 演员”“认证 编剧”
    val imId: String? = "",//IM即时通讯Id
    val backgroundAppUrl: String? = "",//APP 背景图
    val videoCount: Long = 0,//视频数
    val filmListCount: Long = 0,//片单数
    val audioCount: Long = 0,//音频数
    val showAudio: Boolean = false,//是否显示音频标签
    val creator: Boolean = false,//是否是创作者标签
    var creatorInfo: CreatorInfo?,//创作者相关
) : ProguardRule

data class CreatorInfo(
    var commentCount: Long?= 0,//总评论量
    var contentCount: Int?= 0,
    var creatorAppLogoUrl: String?,//等级icon
    var creatorLevelId: String?,//等级id
    var creatorLevelName: String?,//等级名称
    var creatorPcLogoUrl: String?,
    var medalCount: Int?= 0,//总完成勋章数
    var ongoingMedalInfos: List<OngoingMedalInfo>?,//已完成勋章集合，最新top5
    var sevenDayBrowseCount: Int?= 0,
    var praiseCount: Long?= 0,//总点赞量
    var collectCount: Long? = 0,//总收藏量
    var viewsCount: Long?= 0//总阅读量
)

data class OngoingMedalInfo(
    var appLogoUrl: String?,
    var medalId: Int?= 0,
    var medalName: String?,
    var pcLogoUrl: String?,
    var typeName: String?
)

