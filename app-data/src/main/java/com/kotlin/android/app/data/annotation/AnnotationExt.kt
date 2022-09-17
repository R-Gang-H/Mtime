package com.kotlin.android.app.data.annotation

import androidx.annotation.IntDef
import androidx.annotation.LongDef
import com.kotlin.android.app.data.constant.CommConstant

/**
 * 类型约束: 枚举替代方案
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */

/**
 * 用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
 */
@LongDef(AUTH_TYPE_PERSONAL, AUTH_TYPE_FILM_CRITIC, AUTH_TYPE_FILM_MAKER, AUTH_TYPE_INSTITUTION)
@Retention(AnnotationRetention.SOURCE)
annotation class AuthType

/**
 *  性别 1:男2:女3:保密
 */
@LongDef(MALE, FEMALE, UNKNOWN)
@Retention(AnnotationRetention.SOURCE)
annotation class Gender

/**
 * 标签 ORIGINAL(1, "原创"), SPOILER(2, "剧透"), COPYRIGHT(3, "版权声明"), DISCLAIMER(4, "免责声明");
 */
@LongDef(RECORD_TAG_ORIGINAL, RECORD_TAG_SPOILER, RECORD_TAG_COPYRIGHT, RECORD_TAG_DISCLAIMER)
@Retention(AnnotationRetention.SOURCE)
annotation class RecordTags

/**
 * 内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), VIDEO(5, "视频"), AUDIO(6, "音频");
 */
@LongDef(CONTENT_TYPE_JOURNAL, CONTENT_TYPE_POST, CONTENT_TYPE_FILM_COMMENT, CONTENT_TYPE_ARTICLE, CONTENT_TYPE_VIDEO, CONTENT_TYPE_AUDIO)
@Retention(AnnotationRetention.SOURCE)
annotation class ContentType

const val CONTENT_TYPE_JOURNAL           = 1L // 日志
const val CONTENT_TYPE_POST              = 2L // 帖子
const val CONTENT_TYPE_FILM_COMMENT      = 3L // 影评(长/短)(创作中心长影评)
const val CONTENT_TYPE_ARTICLE           = 4L // 文章
const val CONTENT_TYPE_VIDEO             = 5L // 视频
const val CONTENT_TYPE_AUDIO             = 6L // 音频

/**
 * 发布类型：内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"PC)
 */
@LongDef(PUBLISH_JOURNAL, PUBLISH_POST, PUBLISH_FILM_COMMENT, PUBLISH_ARTICLE, PUBLISH_LONG_FILM_COMMENT)
@Retention(AnnotationRetention.SOURCE)
@Deprecated(message = "过时", replaceWith = ReplaceWith("com.kotlin.android.app.data.annotation.ContentType"))
annotation class PublishType

const val PUBLISH_JOURNAL               = 1L // JOURNAL(1, "日志")
const val PUBLISH_POST                  = 2L // POST(2, "帖子")
const val PUBLISH_FILM_COMMENT          = 3L // FILM_COMMENT(3, "影评")
const val PUBLISH_ARTICLE               = 4L // 文章
const val PUBLISH_LONG_FILM_COMMENT     = 5L // 长影评

/**
 * 影评类型 LONG_COMMENT(1, "长影评"), SHORT_COMMENT(2, "短影评");
 */
@LongDef(LONG_COMMENT, SHORT_COMMENT)
@Retention(AnnotationRetention.SOURCE)
annotation class CommentType

/**
 * 评论权限 ALLOW_ALL(1, "允许任何人"), FORBID_ALL(2, "禁止所有人");
 */
@LongDef(ALLOW_ALL, FORBID_ALL)
@Retention(AnnotationRetention.SOURCE)
annotation class CommentPermission

/**
 * 关联对象类型 必填 MOVIE(1, "电影"), PERSON(2, "影人"), ARTICLE(3, "文章")
 */
@LongDef(RELATION_TYPE_MOVIE, RELATION_TYPE_PERSON, RELATION_TYPE_ARTICLE)
@Retention(AnnotationRetention.SOURCE)
annotation class RelationType

/**
 * 视频来源 必填 MOVIE_VIDEO(1, "电影预告片"), SELF_MEDIA_VIDEO(2, "自媒体"), MEDIA_VIDEO(3, "媒资")
 */
@LongDef(VIDEO_SOURCE_MOVIE_VIDEO, VIDEO_SOURCE_SELF_MEDIA_VIDEO, VIDEO_SOURCE_MEDIA_VIDEO)
@Retention(AnnotationRetention.SOURCE)
annotation class VideoSource

/**
 * 当前用户是否加入此群组 0:未加入1:已加入成功2:加入中（待审核）3:黑名单
 */
@LongDef(USER_JOIN_STATE_NONE, USER_JOIN_STATE_SUCCESS, USER_JOIN_STATE_PENDING, USER_JOIN_STATE_BLACKLIST)
@Retention(AnnotationRetention.SOURCE)
annotation class UserJoinState

/**
 * 当前用户群组权限 APPLICANT(-1, "申请者"), OWNER(1, "群主"), ADMINISTRATOR(2, "管理员"), MEMBER(3, "普通成员"), BLACKLIST(4, "黑名单");
 */
@LongDef(GROUP_ROLE_APPLICANT, GROUP_ROLE_OWNER, GROUP_ROLE_ADMINISTRATOR, GROUP_ROLE_MEMBER, GROUP_ROLE_BLACKLIST)
@Retention(AnnotationRetention.SOURCE)
annotation class GroupRole

/**
 * 群组的发帖和评论的权限 JOIN_POST_JOIN_COMMENT(1, "加入发帖加入评论"), FREE_POST_FREE_COMMENT(2, "自由发帖自由评论"), ADMIN_POST_FREE_COMMENT(3, "管理员发帖自由评论")
 */
@LongDef(JOIN_POST_JOIN_COMMENT, FREE_POST_FREE_COMMENT, ADMIN_POST_FREE_COMMENT)
@Retention(AnnotationRetention.SOURCE)
annotation class GroupAuth

/**
 * 当前用户帖子评论权限 1:可评论2:不可评论
 */
@LongDef(POST_PERMISSION_COMMENT, POST_PERMISSION_NOT_COMMENT)
@Retention(AnnotationRetention.SOURCE)
annotation class PostPermission

/**
 * 按钮显示:1:预购2:购票3:想看4:已想看 null:不显示按钮
 */
@LongDef(SHOW_TYPE_PRE_ORDER, SHOW_TYPE_BUY_TICKET, SHOW_TYPE_WANT_TO_SEE, SHOW_TYPE_TO_SEE, SHOW_TYPE_NOT_SHOW)
@Retention(AnnotationRetention.SOURCE)
annotation class ShowType

/**
 * imageType   Number  图片文件分类枚举：200起 参考枚举对象 ImageFileType
 *                                                  用户上传电影、影人图片：16
 *                                                  用户上传照片：1
 *                                                  会员头像：13
 *                                                  会员默认头像：0
 *                                                  群组头像：7
 *                                                  群组活动图片：27
 *                                                  通用上传：14
 *                                                  卡片游戏图片：26
 *                                                  问答游戏图片：25
 *                                                  微博图片：70
 *                                                  自媒体图片：200
 */
@LongDef(
        CommConstant.IMAGE_UPLOAD_VIP_DEFAULT_HEAD_PIC, // 会员默认头像：0
        CommConstant.IMAGE_UPLOAD_USER_UPLOAD, // 用户上传照片：1
        CommConstant.IMAGE_UPLOAD_GROUP_HEAD_PIC, // 群组头像：7
        CommConstant.IMAGE_UPLOAD_VIP_HEAD_PIC, // 会员头像：13
        CommConstant.IMAGE_UPLOAD_COMMON, // 通用上传：14
        CommConstant.IMAGE_UPLOAD_PERSON_OR_MOVIE_BY_USER, // 用户上传电影、影人图片：16
        CommConstant.IMAGE_UPLOAD_CARD_GAME_ANSWER, // 问答游戏图片：25
        CommConstant.IMAGE_UPLOAD_CARD_GAME, // 卡片游戏图片：26
        CommConstant.IMAGE_UPLOAD_GROUP_ACTIVITY_HEAD_PIC, // 群组活动图片：27
        CommConstant.IMAGE_UPLOAD_WEIBO, // 微博图片：70
        CommConstant.IMAGE_UPLOAD_SELF_MEDIA // 自媒体图片：200
)
@Retention(AnnotationRetention.SOURCE)
annotation class UploadImageType

/**
 * 搜索类型：0影片、1影院、2影人、3全部、4商品、5文章、6 用户、7 影评、8 帖子、9 日志、10 家族  默认全部
 */
@LongDef(SEARCH_MOVIE, SEARCH_CINEMA, SEARCH_PERSON, SEARCH_ALL, SEARCH_GOODS, SEARCH_ARTICLE)
@Retention(AnnotationRetention.SOURCE)
annotation class SearchType

@IntDef(CARD_MONOPOLY_STYLE_SELF, CARD_MONOPOLY_STYLE_FRIEND, CARD_MONOPOLY_STYLE_OTHER)
@Retention(AnnotationRetention.SOURCE)
annotation class CardMonopolyStyle

@IntDef(CARD_MONOPOLY_UNKNOWN, CARD_MONOPOLY_MY_POCKET, CARD_MONOPOLY_MY_COFFER)
@Retention(AnnotationRetention.SOURCE)
annotation class CardMonopolyMainTab

const val SEARCH_MOVIE                  = 0L // 搜索影片
const val SEARCH_CINEMA                 = 1L // 搜索影院
const val SEARCH_PERSON                 = 2L // 搜索影人
const val SEARCH_ALL                    = 3L // 搜索全部，默认
const val SEARCH_GOODS                  = 4L // 搜索商品
const val SEARCH_ARTICLE                = 5L // 搜索文章
const val SEARCH_USER                   = 6L // 搜索用户
const val SEARCH_FILM_COMMENT           = 7L // 搜索影评
const val SEARCH_POST                   = 8L // 搜索帖子
const val SEARCH_LOG                    = 9L // 搜索日志
const val SEARCH_FAMILY                 = 10L// 搜索家族
const val SEARCH_FILM_LIST              = 11L// 片单
const val SEARCH_VIDEO                  = 12L// 视频
const val SEARCH_AUDIO                  = 13L// 播客

const val AUTH_TYPE_PERSONAL            = 1L // 个人
const val AUTH_TYPE_FILM_CRITIC         = 2L // 影评人
const val AUTH_TYPE_FILM_MAKER          = 3L // 电影人
const val AUTH_TYPE_INSTITUTION         = 4L // 机构

const val MALE                          = 1L // 男
const val FEMALE                        = 2L // 女
const val UNKNOWN                       = 3L // 保密

const val RECORD_TAG_ORIGINAL           = 1L // 原创
const val RECORD_TAG_SPOILER            = 2L // 剧透
const val RECORD_TAG_COPYRIGHT          = 3L // 版权声明
const val RECORD_TAG_DISCLAIMER         = 4L // 免责声明

const val LONG_COMMENT                  = 1L // 长影评
const val SHORT_COMMENT                 = 2L // 短影评

const val ALLOW_ALL                     = 1L // 允许任何人
const val FORBID_ALL                    = 2L // 禁止所有人

const val RELATION_TYPE_MOVIE           = 1L // 电影
const val RELATION_TYPE_PERSON          = 2L // 影人
const val RELATION_TYPE_ARTICLE         = 3L // 文章

const val VIDEO_SOURCE_MOVIE_VIDEO      = 1L // 电影预告片
const val VIDEO_SOURCE_SELF_MEDIA_VIDEO = 2L // 自媒体
const val VIDEO_SOURCE_MEDIA_VIDEO      = 3L // 媒资

const val USER_JOIN_STATE_NONE          = 0L // 未加入
const val USER_JOIN_STATE_SUCCESS       = 1L // 已加入成功
const val USER_JOIN_STATE_PENDING       = 2L // 加入中（待审核）
const val USER_JOIN_STATE_BLACKLIST     = 3L // 黑名单

const val GROUP_ROLE_APPLICANT          = -1L // 申请者
const val GROUP_ROLE_OWNER              = 1L // 群主
const val GROUP_ROLE_ADMINISTRATOR      = 2L // 管理员
const val GROUP_ROLE_MEMBER             = 3L // 普通成员
const val GROUP_ROLE_BLACKLIST          = 4L // 黑名单

const val JOIN_POST_JOIN_COMMENT        = 1L // 加入发帖加入评论
const val FREE_POST_FREE_COMMENT        = 2L // 自由发帖自由评论
const val ADMIN_POST_FREE_COMMENT       = 3L // 管理员发帖自由评论

const val POST_PERMISSION_COMMENT       = 1L // 可评论
const val POST_PERMISSION_NOT_COMMENT   = 2L // 不可评论

const val SHOW_TYPE_PRE_ORDER           = 1L // 预购
const val SHOW_TYPE_BUY_TICKET          = 2L // 购票
const val SHOW_TYPE_WANT_TO_SEE         = 3L // 想看
const val SHOW_TYPE_TO_SEE              = 4L // 已想看
const val SHOW_TYPE_NOT_SHOW            = 0L // null:不显示按钮

const val CARD_MONOPOLY_STYLE_SELF      = 0 // 我的主页
const val CARD_MONOPOLY_STYLE_FRIEND    = 1 // 好友主页
const val CARD_MONOPOLY_STYLE_OTHER     = 2 // 陌生人主页
const val CARD_MONOPOLY_UNKNOWN         = 0 // 未知的
const val CARD_MONOPOLY_MY_POCKET       = 1 // 我的口袋
const val CARD_MONOPOLY_MY_COFFER       = 2 // 我的保险箱