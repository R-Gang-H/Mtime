package com.kotlin.android.app.data.constant

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/9/2
 *
 * 通用的常量类
 */
object CommConstant {
    //造物社H5地址
    const val URL_ZAO_WU_SHE_H5 = "https://vip.mtime.cn/nature/society"

    /**影片按钮状态值*/
    const val MOVIE_BTN_STATE_PRESELL = 1L //预售
    const val MOVIE_BTN_STATE_TICKET = 2L //购票
    const val MOVIE_BTN_STATE_WANT_SEE = 3L //想看
    const val MOVIE_BTN_STATE_WANT_SEEN = 4L //已想看

    /**想看接口请求flag值*/
    const val MOVIE_WANT_SEE_FLAG = 1L
    const val MOVIE_CANCEL_WANT_SEE_FLAG = 2L

    /**推荐位code*/
    const val RCMD_REGION_HOME_BANNER = "NX_M20_Advert_HeadImg" //首页轮播图数据
    const val RCMD_REGION_SPLASH_AD = "APP_M20_Full_Screen" //启动页广告
    const val RCMD_REGION_TICKET_BANNER = "Ticket_M22_Banner"   // 购票首页banner推荐位
    const val RCMD_REGION_NEW_VIDEO = "NX_M20_New_Video"        // 购票-待映页-预告片推荐位
    const val RCMD_REGION_FIND_MOVIE_BANNER = "APP_M20_Mtime_Lookfor"
    const val RCMD_REGION_TA_SHUO_BANNER = "NX_M22_User_Recommend" //TA说banner
    const val RCMD_REGION_ZHONG_CAO_BANNER = "NX_M22_Peripheral_Recommend" //种草banner
    const val RCMD_REGION_MTIME_ORIGINAL_BANNER = "APP_M20_Mtime_choice" //时光原创精选

    /**点赞/点踩操作*/
    const val PRAISE_ACTION_SUPPORT = 1L //点赞/点踩
    const val PRAISE_ACTION_CANCEL = 2L //取消点赞/点踩

    /**点赞点踩的主体类型*/
    const val PRAISE_OBJ_TYPE_JOURNAL = 1L //日志
    const val PRAISE_OBJ_TYPE_POST = 2L //帖子
    const val PRAISE_OBJ_TYPE_FILM_COMMENT = 3L //影评
    const val PRAISE_OBJ_TYPE_ARTICLE = 4L //文章
    const val PRAISE_OBJ_TYPE_ALBUM = 5L //相册
    const val PRAISE_OBJ_TYPE_TOPIC_LIST = 6L //榜单
    const val PRAISE_OBJ_TYPE_CINEMA = 7L //CINEMA(7, "影院")
    const val PRAISE_OBJ_TYPE_MOVIE_TRAILER = 8L //预告片
    const val PRAISE_OBJ_TYPE_LIVE = 9L//直播
    const val PRAISE_OBJ_TYPE_CARD_USER = 10L//CARD_USER(10, "卡片用户")
    const val PRAISE_OBJ_TYPE_CARD_SUIT = 11L//CARD_SUIT(11, "卡片套装")
    const val PRAISE_OBJ_TYPE_VIDEO = 12L//VIDEO(12, "视频")
    const val PRAISE_OBJ_TYPE_AUDIO = 13L//AUDIO(13, "音频")
    const val PRAISE_OBJ_TYPE_FILM_LIST = 14L//FILM_LIST(14, "片单")
    const val PRAISE_OBJ_TYPE_JOURNAL_COMMENT = 101L //日志评论
    const val PRAISE_OBJ_TYPE_POST_COMMENT = 102L //帖子评论
    const val PRAISE_OBJ_TYPE_FILM_COMMENT_COMMENT = 103L //影评评论
    const val PRAISE_OBJ_TYPE_ARTICLE_COMMENT = 104L //文章评论
    const val PRAISE_OBJ_TYPE_ALBUM_COMMENT = 105L //相册评论
    const val PRAISE_OBJ_TYPE_TOPIC_LIST_COMMENT = 106L //榜单评论
    const val PRAISE_OBJ_TYPE_CINEMA_COMMENT = 107L //影院评论
    const val PRAISE_OBJ_TYPE_MOVIE_TRAILER_COMMENT = 108L //预告片评论
    const val PRAISE_OBJ_TYPE_LIVE_COMMENT = 109L//直播评论
    const val PRAISE_OBJ_TYPE_CARD_USER_COMMENT = 110L//CARD_USER_COMMENT(110, "卡片用户评论")
    const val PRAISE_OBJ_TYPE_CARD_SUIT_COMMENT = 111L//CARD_SUIT_COMMENT(111, "卡片套装评论")
    const val PRAISE_OBJ_TYPE_VIDEO_COMMENT = 112L//视频评论
    const val PRAISE_OBJ_TYPE_AUDIO_COMMENT = 113L//音频评论
    const val PRAISE_OBJ_TYPE_JOURNAL_REPLY = 201L //日志回复
    const val PRAISE_OBJ_TYPE_POST_REPLY = 202L //帖子回复
    const val PRAISE_OBJ_TYPE_FILM_COMMENT_REPLY = 203L //影评回复
    const val PRAISE_OBJ_TYPE_ARTICLE_REPLY = 204L //文章回复
    const val PRAISE_OBJ_TYPE_ALBUM_REPLY = 205L //相册回复
    const val PRAISE_OBJ_TYPE_TOPIC_LIST_REPLY = 206L //榜单回复
    const val PRAISE_OBJ_TYPE_CINEMA_REPLY = 207L //影院回复
    const val PRAISE_OBJ_TYPE_MOVIE_TRAILER_REPLY = 208L //预告片回复
    const val PRAISE_OBJ_TYPE_LIVE_REPLY = 209L//直播回复
    const val PRAISE_OBJ_TYPE_CARD_USER_REPLY = 210L//卡片用户回复
    const val PRAISE_OBJ_TYPE_CARD_SUIT_REPLY = 211L//卡片套装回复
    const val PRAISE_OBJ_TYPE_VIDEO_REPLY = 212L//VIDEO_REPLY(212, "视频回复")
    const val PRAISE_OBJ_TYPE_AUDIO_REPLY = 213L//AUDIO_REPLY(213, "音频回复")
    const val PRAISE_OBJ_TYPE_FILM_LIST_REPLY = 214L//FILM_LIST_REPLY(214, "片单回复")

    /**收藏操作*/
    const val COLLECTION_ACTION_SUPPORT = 1L //收藏
    const val COLLECTION_ACTION_CANCEL = 2L //取消收藏

    /**收藏的主体类型*/
    const val COLLECTION_OBJ_TYPE_FILM = 1L //电影
    const val COLLECTION_OBJ_TYPE_PERSON = 2L //影人
    const val COLLECTION_OBJ_TYPE_CINEMA = 3L //影院
    const val COLLECTION_OBJ_TYPE_POST = 4L //帖子
    const val COLLECTION_OBJ_TYPE_ARTICLE = 5L //文章
    const val COLLECTION_OBJ_TYPE_JOURNAL = 6L//日志
    const val COLLECTION_OBJ_TYPE_FILM_COMMENT = 7L//影评
    const val COLLECTION_OBJ_TYPE_VIDEO = 8L//视频
    const val COLLECTION_OBJ_TYPE_AUDIO = 9L//音频
    const val COLLECTION_OBJ_TYPE_FILM_LIST = 10L//片单

    /**加入家族接口结果类型
     * 1：加入群组成功（即加入了成员列表中）
     * 2 加入群组失败
     * 3 该用户此前已经加入成功了此群组
     * 4 黑名单中的用户，即用户被移除到黑名单。
     * 5 审核中（正在加入中）（即加入了申请列表中，需要管理员审核才能进入成员列表中）
     * */
    const val JOIN_FAMILY_RESULT_STATUS_SUCCEED = 1L
    const val JOIN_FAMILY_RESULT_STATUS_FAILURE = 2L
    const val JOIN_FAMILY_RESULT_STATUS_JOINED = 3L
    const val JOIN_FAMILY_RESULT_STATUS_BLACKLIST = 4L
    const val JOIN_FAMILY_RESULT_STATUS_JOINING = 5L

    /**
     * 获取分享type
     * 1	文章详情页
     * 2	片单详情页
     * 3	预告片详情页
     * 4	家族详情页
     * 5	帖子详情页
     * 6	长影评页面
     * 7    短影评页面
     * 8	日志详情页
     * 9	相册详情页
     * 10	卡片大富翁
     * 11	影人详情页
     * 12	影片资料页
     * 13   片单
     * 14   视频详情页
     * 15   音频详情页
     */
    const val SHARE_TYPE_ARTICLE = 1L
    const val SHARE_TYPE_SUBJECT = 2L
    const val SHARE_TYPE_MOVIE_TRAILER = 3L
    const val SHARE_TYPE_FAMILY = 4L
    const val SHARE_TYPE_POST = 5L
    const val SHARE_TYPE_LONG_REVIEW = 6L
    const val SHARE_TYPE_SHORT_REVIEW = 7L
    const val SHARE_TYPE_JOURNAL = 8L
    const val SHARE_TYPE_ALBUM = 9L
    const val SHARE_TYPE_CARD_CONNOPOLY = 10L
    const val SHARE_TYPE_PERSON = 11L
    const val SHARE_TYPE_FILM = 12L
    const val SHARE_TYPE_FILM_LIST = 13L
    const val SHARE_TYPE_VIDEO = 14L
    const val SHARE_TYPE_AUDIO = 15L


    /**
     * 获取主体类型对应分享类型
     * @param objType ugc对应主体类型
     * @param isShortReview 只有是影评的时候会传递该参数
     */
    fun getShareTypeByContentType(objType: Long, isShortReview: Boolean = false): Long {
        return when (objType) {
            PRAISE_OBJ_TYPE_JOURNAL -> SHARE_TYPE_JOURNAL//分享日志
            PRAISE_OBJ_TYPE_POST -> SHARE_TYPE_POST//分享帖子
            PRAISE_OBJ_TYPE_FILM_COMMENT -> if (isShortReview) SHARE_TYPE_SHORT_REVIEW else SHARE_TYPE_LONG_REVIEW//分享影评
            PRAISE_OBJ_TYPE_ARTICLE -> SHARE_TYPE_ARTICLE//分享文章
            PRAISE_OBJ_TYPE_ALBUM -> SHARE_TYPE_ALBUM//相册分享
            PRAISE_OBJ_TYPE_TOPIC_LIST -> SHARE_TYPE_SUBJECT//片单分享
            PRAISE_OBJ_TYPE_MOVIE_TRAILER -> SHARE_TYPE_MOVIE_TRAILER//预告片分享
            else -> 0L
        }
    }

    /**
     * 获取点赞类型值
     * @param objType 大主题类型  1.日志 2.帖子 3.影评 4.文章 5.相册 6.榜单 8预告片
     * @param praiseAction 0,返回大主题  1.返回大主题对应评论 2.返回大主题对应回复
     */
    fun getPraiseUpType(objType: Long, praiseAction: Long): Long {
        return when (praiseAction) {
            COMMENT_PRIASE_ACTION_NONE -> objType
            COMMENT_PRAISE_ACTION_COMMENT -> getPraiseCommentType(objType)
            COMMENT_PRAISE_ACTION_REPLY -> getPraiseReplyType(objType)
            else -> 0L
        }
    }

    /**
     * 获取大主题对应评论类型
     * @param objType 大主题类型
     */
    private fun getPraiseCommentType(objType: Long): Long {
        return when (objType) {
            PRAISE_OBJ_TYPE_JOURNAL -> PRAISE_OBJ_TYPE_JOURNAL_COMMENT
            PRAISE_OBJ_TYPE_POST -> PRAISE_OBJ_TYPE_POST_COMMENT
            PRAISE_OBJ_TYPE_FILM_COMMENT -> PRAISE_OBJ_TYPE_FILM_COMMENT_COMMENT
            PRAISE_OBJ_TYPE_ARTICLE -> PRAISE_OBJ_TYPE_ARTICLE_COMMENT
            PRAISE_OBJ_TYPE_ALBUM -> PRAISE_OBJ_TYPE_ALBUM_COMMENT
            PRAISE_OBJ_TYPE_TOPIC_LIST -> PRAISE_OBJ_TYPE_TOPIC_LIST_COMMENT
            PRAISE_OBJ_TYPE_MOVIE_TRAILER -> PRAISE_OBJ_TYPE_MOVIE_TRAILER_COMMENT
            PRAISE_OBJ_TYPE_LIVE -> PRAISE_OBJ_TYPE_LIVE_COMMENT
            PRAISE_OBJ_TYPE_CARD_USER -> PRAISE_OBJ_TYPE_CARD_USER_COMMENT
            PRAISE_OBJ_TYPE_CARD_SUIT -> PRAISE_OBJ_TYPE_CARD_SUIT_COMMENT
            PRAISE_OBJ_TYPE_VIDEO -> PRAISE_OBJ_TYPE_VIDEO_COMMENT
            PRAISE_OBJ_TYPE_AUDIO -> PRAISE_OBJ_TYPE_AUDIO_COMMENT
            else -> 0L
        }
    }

    /**
     * 获取大主题对应评论回复类型
     */
    private fun getPraiseReplyType(objType: Long): Long {
        return when (objType) {
            PRAISE_OBJ_TYPE_JOURNAL -> PRAISE_OBJ_TYPE_JOURNAL_REPLY
            PRAISE_OBJ_TYPE_POST -> PRAISE_OBJ_TYPE_POST_REPLY
            PRAISE_OBJ_TYPE_FILM_COMMENT -> PRAISE_OBJ_TYPE_FILM_COMMENT_REPLY
            PRAISE_OBJ_TYPE_ARTICLE -> PRAISE_OBJ_TYPE_ARTICLE_REPLY
            PRAISE_OBJ_TYPE_ALBUM -> PRAISE_OBJ_TYPE_ALBUM_REPLY
            PRAISE_OBJ_TYPE_TOPIC_LIST -> PRAISE_OBJ_TYPE_TOPIC_LIST_REPLY
            PRAISE_OBJ_TYPE_MOVIE_TRAILER -> PRAISE_OBJ_TYPE_MOVIE_TRAILER_REPLY
            PRAISE_OBJ_TYPE_LIVE -> PRAISE_OBJ_TYPE_LIVE_REPLY
            PRAISE_OBJ_TYPE_CARD_USER -> PRAISE_OBJ_TYPE_CARD_USER_REPLY
            PRAISE_OBJ_TYPE_CARD_SUIT -> PRAISE_OBJ_TYPE_CARD_SUIT_REPLY
            PRAISE_OBJ_TYPE_VIDEO -> PRAISE_OBJ_TYPE_VIDEO_REPLY
            PRAISE_OBJ_TYPE_AUDIO -> PRAISE_OBJ_TYPE_AUDIO_REPLY
            else -> 0L
        }
    }


    const val COMMENT_PRIASE_ACTION_NONE = 0L //返回大主题
    const val COMMENT_PRAISE_ACTION_COMMENT = 1L//返回大主题对应评论
    const val COMMENT_PRAISE_ACTION_REPLY = 2L//返回大主题对应回复

    //    图片上传业务类型imageFileType
    const val IMAGE_UPLOAD_USER_UPLOAD = 1L//用户上传照片
    const val IMAGE_UPLOAD_PERSON_OR_MOVIE_BY_USER = 16L// 用户上传电影、影人图片
    const val IMAGE_UPLOAD_VIP_HEAD_PIC = 13L//会员头像
    const val IMAGE_UPLOAD_VIP_DEFAULT_HEAD_PIC = 0L//会员默认头像
    const val IMAGE_UPLOAD_GROUP_HEAD_PIC = 7L//群组头像
    const val IMAGE_UPLOAD_GROUP_ACTIVITY_HEAD_PIC = 27L//群组活动头像
    const val IMAGE_UPLOAD_COMMON = 14L//通用上传
    const val IMAGE_UPLOAD_CARD_GAME = 26L//卡片游戏图片
    const val IMAGE_UPLOAD_CARD_GAME_ANSWER = 25L//问答游戏图片
    const val IMAGE_UPLOAD_WEIBO = 70L//微博图片
    const val IMAGE_UPLOAD_SELF_MEDIA = 200L//自媒体图片


    const val IMAGE_UPLOAD_USER_CENTER_BG = 201L//个人主页背景图片

    /**
     * 个人中心-想看、看过Tab标签
     */
    const val MINE_WANT_SEE = 0L //想看
    const val MINE_HAS_SEE = 1L //看过

    const val COLLECTION_TYPE_MOVIE = 1L//影片
    const val COLLECTION_TYPE_CINEMA = 2L//影院
    const val COLLECTION_TYPE_PERSON = 3L//影人
    const val COLLECTION_TYPE_ARTICLE = 4L//文章
    const val COLLECTION_TYPE_POST = 5L//帖子


    /**
     * 文章和影评版权说明
     */
    const val TYPE_CONTENT_ORIGINAL = 1L//原创
    const val TYPE_CONTENT_SPOILER = 2L//剧透
    const val TYPE_CONTENT_COPYRIGHT = 3L//版权声明
    const val TYPE_CONTENT_DISCLAIMER = 4L//免责声明

    const val TYPE_TICKET_HOME_MOVIE_HOT = 1 //票务正在热映页
    const val TYPE_TICKET_HOME_MOVIE_INCOMING = 2 //票务即将上映页
    const val TYPE_TICKET_HOME_MOVIE_CINEMA = 3 //票务影院列表页

    /**
     * 会员等级
     * 0入门影迷，1中级影迷，2高级影迷，3资深影迷，4殿堂影迷
     */
    const val MEMBER_LEVEL_PRIMARY = 0L
    const val MEMBER_LEVEL_MIDDLE = 1L
    const val MEMBER_LEVEL_HIGHT = 2L
    const val MEMBER_LEVEL_SENIOR = 3L
    const val MEMBER_LEVEL_HALL = 4L

    /**
     * 认证类型
     * 1个人，2影评人，3电影人，4机构
     */
    const val AUTH_TYPE_PERSONAL = 1L
    const val AUTH_TYPE_MOVIE_CRITIC = 2L
    const val AUTH_TYPE_FILM_MAKER = 3L
    const val AUTH_TYPE_ORGAN = 4L

    /**
     * 关注操作
     */
    const val FOLLOW_ACTION_SUPPORT = 1L
    const val FOLLOW_ACTION_CANCEL = 2L

    /**
     * 我的内容 tab 类型
     */
    const val KEY_CONTENT_TYPE = "content_type"
    const val MINE_CONTENT_TAB_DRAFTS = 1L //草稿箱
    const val MINE_CONTENT_TAB_ALL = 2L //全部
    const val MINE_CONTENT_TAB_WAIT_REVIEW = 3L //审核
    const val MINE_CONTENT_TAB_REVIEW_FAIL = 4L //审核失败
    const val MINE_CONTENT_TAB_RELEASED = 5L //已发布

    /**
     * 检查是否为发布状态的内容类型
     */
    const val CHECK_RELEASED_CONTENT_TYPE_JOURNAL = 1L  // 日志
    const val CHECK_RELEASED_CONTENT_TYPE_POST = 2L     // 帖子
    const val CHECK_RELEASED_CONTENT_TYPE_COMMENT = 3L   // 影评
    const val CHECK_RELEASED_CONTENT_TYPE_ARTICLE = 4L  // 文章
    const val CHECK_RELEASED_CONTENT_TYPE_VIDEO = 5L    // 视频
    const val CHECK_RELEASED_CONTENT_TYPE_AUDIO = 6L    // 音频

    //创作者内容状态
    const val CONTENT_STATE_DRAFT = 0L//草稿
    const val CONTENT_STATE_WAIT_HANDLE = 11L//待处理
    const val CONTENT_STATE_WAIT_REVIEW = 12L//待审核
    const val CONTENT_STATE_REVIEW_FAILED = 13L//审核失败
    const val CONTENT_STATE_RELEASED = 20L//已发布
    const val CONTENT_STATE_RELEASED_WAIT_HANDLE = 21L//已发布编辑待处理
    const val CONTENT_STATE_RELEASED_WAIT_REVIEW = 22L//已发布编辑待审核
    const val CONTENT_STATE_RELEASED_REVIEW_FAILED = 23L//已发布编辑审核失败
    const val CONTENT_STATE_EDIT_WAIT_HANDLE = 51L//副本编辑待处理
    const val CONTENT_STATE_EDIT_WAIT_REVIEW = 52L//副本编辑待审核
    const val CONTENT_STATE_EDIT_REVIEW_FAILED = 53L//副本编辑审核失败

    // 用户认证类型：null代表没有认证， 1"个人", 2"影评人", 3"电影人", 4"机构", -1“审核中”;
    const val USER_AUTH_TYPE_REVIEW_NULL = -1000L // 没有认证
    const val USER_AUTH_TYPE_REVIEW_AUDIT = -1L   // 审核中
    const val USER_AUTH_TYPE_REVIEW_PERSON = 2L   // 影评人认证
    const val USER_AUTH_TYPE_MOVIE_PERSON = 3L    // 电影人
    const val USER_AUTH_TYPE_ORGANIZATION = 4L    // 机构认证

}
