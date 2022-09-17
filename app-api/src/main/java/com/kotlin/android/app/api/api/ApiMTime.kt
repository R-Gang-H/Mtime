package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.annotation.UploadImageType
import com.kotlin.android.app.data.entity.CommContentList
import com.kotlin.android.app.data.entity.activity.ActivityList
import com.kotlin.android.app.data.entity.bonus.BonusScene
import com.kotlin.android.app.data.entity.bonus.PopupBonusScene
import com.kotlin.android.app.data.entity.comment.*
import com.kotlin.android.app.data.entity.common.*
import com.kotlin.android.app.data.entity.community.album.*
import com.kotlin.android.app.data.entity.community.content.CommentList
import com.kotlin.android.app.data.entity.community.group.*
import com.kotlin.android.app.data.entity.community.home.*
import com.kotlin.android.app.data.entity.community.person.*
import com.kotlin.android.app.data.entity.community.person.AlbumList
import com.kotlin.android.app.data.entity.community.post.PostReleasedList
import com.kotlin.android.app.data.entity.community.praisestate.PraiseState
import com.kotlin.android.app.data.entity.family.FindFamily
import com.kotlin.android.app.data.entity.filmlist.MyCreateFilmList
import com.kotlin.android.app.data.entity.home.*
import com.kotlin.android.app.data.entity.home.tashuo.TaShuoRcmdList
import com.kotlin.android.app.data.entity.home.zhongcao.ZhongCaoRcmdData
import com.kotlin.android.app.data.entity.image.MovieImage
import com.kotlin.android.app.data.entity.image.MoviePhoto
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.live.DirectorUnits
import com.kotlin.android.app.data.entity.member.ExchangeResult
import com.kotlin.android.app.data.entity.member.MemberExchangeList
import com.kotlin.android.app.data.entity.member.MemberHome
import com.kotlin.android.app.data.entity.mine.*
import com.kotlin.android.app.data.entity.monopoly.*
import com.kotlin.android.app.data.entity.movie.LatestComment
import com.kotlin.android.app.data.entity.movie.MovieMore
import com.kotlin.android.app.data.entity.order.BlendOrders
import com.kotlin.android.app.data.entity.review.MovieReviewList
import com.kotlin.android.app.data.entity.review.RatingDetail
import com.kotlin.android.app.data.entity.toplist.*
import com.kotlin.android.app.data.entity.user.Login
import com.kotlin.android.app.data.entity.user.User
import com.kotlin.android.app.data.entity.video.VideoDetail
import com.kotlin.android.app.data.entity.video.VideoPlayList
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * 时光网APP新版API接口服务：
 * 域名统一为：[https://front-gateway.mtime.cn]
 *
 * Created on 2020/6/18.
 *
 * @author o.s
 */
interface ApiMTime : ApiMTimeCommunity, ApiMTimeMonopoly, ApiMTimeSearch, ApiComment, ApiFilmList,
    ApiChatRoom, ApiMessage, ApiSetting, ApiCreator, ApiCommunity, ApiUpload {

    companion object {
        /**
         * IndexController
         */
        const val INDEX_TOP_LIST = "/library/index/app/topList.api" // APP榜单列表

        //        const val INDEX_APP_NAV = "/library/index/app/navi.api" // APP频道切换TAB列表
        const val INDEX_APP_NAV = "/library/index/app/tabs.api" // [2.0]APP频道切换TAB列表
        const val INDEX_FEEDS = "/community/index/app/feeds.api" // APP首页内容Feed流
        const val INDEX_MOVIE_COMMENTS = "/community/index/app/movieComments.api" // APP首页影评列表
        const val INDEX_RECOMMEND_MOVIE =
            "/library/index/dailyRcmdMoviePopup.api" // 今日推荐（每日佳片)-每日弹窗(/dailyRcmdMoviePopup.api)
        const val INDEX_GAME_TOP_LIST = "/library/index/app/gameTopList.api" // 首页-游戏榜单
        const val INDEX_PC_HOT_GROUPS = "/library/index/pc/hotGroups.api" // 热门群组列表
        const val INDEX_LOCATION_LIST = "/library/index/locationList.api" // 获取国家、城市列表
        const val INDEX_TRAILERS = "/library/index/trailerList.api" // 首页预告片列表，APP/PC公用（未确定）
        const val INDEX_TOPLIST_QUERY = "/community/top_list/query.api"// 首页-榜单分页查询

        /**
         * MovieController
         */
        const val MOVIE_RATING = "/library/movie/movierating.api" // 影片评分（新版只支持评总分）
        const val MOVIE_SUMMARY = "/library/movie/summary.api" // 批量获取影片售票状态和在线播放状态
        const val MOVIE_LATEST_COMMENT =
            "/library/movie/currentUser/latestComment.api" // 获取当前用户最新一条长短影评
        const val MOVIE_VIDEO_IDS = "/library/movie/category/videoIds.api" // 获取影片分类视频Id列表
        const val MOVIE_VIDEOS = "/library/movie/category/video.api" // 获取影片分类视频列表
        const val MOVIE_EXTEND_DETAIL = "/library/movie/extendDetail.api" // 获取影片扩展信息
        const val MOVIE_MORE_DETAIL = "/library/movie/moreDetail.api" // 获取影片更多资料
        const val MOVIE_COMMENTS = "/library/movie/comment.api" // 获取影片短评列表
        const val MOVIE_STYLE = "/library/movie/style.api" // 获取影片背景图等Style信息（RPC封装接口）
        const val MOVIE_DETAIL = "/library/movie/detail.api" // 获取影片详情页数据
        const val MOVIE_LONG_COMMENTS = "/library/movie/longCommentList.api" // 获取影片长评列表
        const val MOVIE_MY_SHORT_COMMENTS =
            "/library/movie/myShortComment/list.api" // 获取我的短影评列表（每页只有一条）
        const val MOVIE_MY_COMMENTS = "/library/movie/myComment/lists.api" // 获取我的长影评列表
        const val MOVIE_CREDITS_WITH_TYPE =
            "/library/movie/movieCreditsWithTypes.api" // 获取按职业分类的电影演职员列表（导演+演员）
        const val MOVIE_TIME_NEWS_LIST = "/library/movie/timeNewsList.api" // 获取除时光对话外的文章列表
        const val MOVIE_TIME_TALKS = "/library/movie/timeTalksList.api" // 获取时光对话文章列表
        const val MOVIE_HOT_COMMENTS = "/library/movie/hotComments.api" // 获取热门长短影评列表
        const val MOVIE_DYNAMIC = "/library/movie/dynamic.api" // 获取用户对电影的动态信息
        const val MOVIE_IMAGE_ALL = "/library/movie/imageAll.api" // 获取电影全部图片和分类
        const val MOVIE_COMPANY_LIST = "/library/movie/companyList.api" // 获取电影公司列表
        const val MOVIE_PLOTS = "/library/movie/plots.api" // 获取电影剧情列表
        const val MOVIE_IMAGES = "/library/movie/image.api" // 获取电影图片列表（100张剧照+100张海报）
        const val MOVIE_CREDITS = "/library/movie/credits.api" // 获取电影演职员列表
        const val MOVIE_RECOMMEND_LIST = "/library/movie/movieListRecommend.api" // 获取电影片单列表
        const val MOVIE_VIDEO_DETAIL = "/library/movie/video/detail.api" // 获取视频详情
        const val MOVIE_WANT_TO_SEE = "/library/movie/setWantToSee.api" // 设置电影为想看/取消想看
        const val MOVIE_SET_HAS_SEEN = "/library/movie/setHasSeen.api" // 设置电影为看过
        const val MOVIE_RATING_DETAIL = "/library/movie/movieRatingDetail.api"//获取电影评分详情

        /**
         * PersonController
         */
        const val PERSON_TIME_NEWS_LIST = "/library/person/timeNewsList.api" // 影人的关联新闻列表
        const val PERSON_BIOGRAPHY = "/library/person/biography.api" // 获取影人人物传记列表
        const val PERSON_MOVIES = "/library/person/movies.api" // 获取影人作品列表
        const val PERSON_IMAGE_ALL = "/library/person/imageAll.api" // 获取影人全部图片和分类
        const val PERSON_VIDEOS = "/library/person/videoList.api" // 获取影人关联视频列表
        const val PERSON_IMAGES = "/library/person/image.api" // 获取影人图片列表（100张写真+100张生活照）
        const val PERSON_COMMENTS = "/library/person/comment.api" // 获取影人短评列表（每页20条）
        const val PERSON_DETAIL = "/library/person/detail.api" // 获取影人详情页数据
        const val PERSON_DYNAMIC = "/library/person/dynamic.api" // 获取用户对影人的动态信息

        /**
         * 社区评论api
         */
        const val COMMUNITY_SAVE_COMMENT = "/community/comment.api"//评论api - 保存评论(/comment.api)
        const val COMMUNITY_DELETE_COMMENT =
            "/community/delete_comment.api"//评论api - 删除评论(/delete_comment.api)
        const val COMMUNITY_COMMENT_RELEASED =
            "/community/comments/released.api"//评论api - 分页查询已发布评论(/comments/released.api)
        const val COMMUNITY_COMMENT_UNRELEASED =
            "/community/comments/user_unreleased.api"//评论api - 分页查询用户未发布评论(/comments/user_unreleased.api)

        /**
         * 社区关注api
         */
        const val COMMUNITY_FOLLOW_USER =
            "/community/follow_user.api"//社区交互-关注api - 关注/取消关注用户(/follow_user.api)

        /**
         * 社区-文章api
         */
        const val COMMUNITY_RECOMMEND_ARTICLE = "/community/article/rcmd.api"//文章api - 查询推荐文章
        const val COMMUNITY_ORIGINAL_ARTICLE =
            "/community/article/original.api"//文章api - 分页查询原创文章内容(/article/original.api)
        const val COMMUNITY_CAN_PUBLISH_ARTICLE =
            "/community/article_user/is_allow.api"//文章api - 查询用户能否发文章(/article_user/is_allow.api)
        const val COMMUNITY_REOBJ_ARTICLE =
            "/community/article/reobj_article.api"//文章api - 分页查询一条文章的关联文章列表(/article/reobj_article.api)

        /**
         * 社区交互-点赞api
         */
        const val COMMUNITY_PRAISE_STATE =
            "/community/praise_stat.api"//社区交互-点赞api - 查询点赞点踩状态(/praise_stat.api)
        const val COMMUNITY_PRAISE_UP = "/community/praise_up.api"//社区交互-点赞api - 点赞(/praise_up.api)
        const val COMMUNITY_PRAISE_DOWN =
            "/community/praise_down.api"//社区交互-点赞api - 点踩(/praise_down.api)

        /**
         * 社区交互-收藏api
         */
        const val COMMUNITY_COLLECTION = "/community/collect.api"//社区交互-收藏api - 收藏(/collect.api)
        const val COMMUNITY_COLLECTION_MOVIES =
            "/community/collect_movies.api"//社区交互-收藏api - 分页查询用户收藏电影(/collect_movies.api)
        const val COMMUNITY_COLLECTION_PERSONS =
            "/community/collect_persons.api"//社区交互-收藏api - 分页查询用户收藏影人(/collect_persons.api)
        const val COMMUNITY_COLLECTION_CINEMAS =
            "/community/collect_cinemas.api"//社区交互-收藏api - 分页查询用户收藏影院(/collect_cinemas.api)
        const val COMMUNITY_COLLECTION_ARTICLES =
            "/community/collect_articles.api"//社区交互-收藏api - 分页查询用户收藏文章(/collect_articles.api)
        const val COMMUNITY_COLLECTION_POSTS =
            "/community/collect_posts.api"//社区交互-收藏api - 分页查询用户收藏帖子(/collect_posts.api)

        /**
         * v2收藏 电影 影人 影院
         */
        const val COMMUNITY_COLLECTION_MOVIES_V2 =
            "/community/collect_movies_v2.api"//社区交互-收藏api - 分页查询用户收藏电影(/collect_movies_v2.api)
        const val COMMUNITY_COLLECTION_PERSONS_V2 =
            "/community/collect_persons_v2.api"//社区交互-收藏api - 分页查询用户收藏影人(/collect_persons_v2.api)
        const val COMMUNITY_COLLECTION_CINEMAS_V2 =
            "/community/collect_cinemas_v2.api"//社区交互-收藏api - 分页查询用户收藏影院(/collect_cinemas_v2.api)
        const val COMMUNITY_MY_FILM_LIST =
            "/community/film_list/pageMyFilmList.api"//片单api - 我的片单列表（个人主页专用，缓存1秒）

        /**
         * 社区交互-投票api
         */
        const val COMMUNITY_VOTE = "/community/vote.api" //社区交互-投票api - 投票(/vote.api)

        /**
         * 社区首页api
         */
        const val COMMUNITY_DYNAMIC = "/community/dynamic.api" //社区关注-分页查询动态
        const val COMMUNITY_RCMD = "/community/rcmd/community.api" //社区精选-分页查询社区推荐内容

        /**
         * 社区推荐内容相关API
         */
        const val COMMUNITY_RCMD_TA_SHUO = "/community/rcmd/ta_shuo.api" //内容推荐api-【2.0】TA说推荐列表
        const val COMMUNITY_RCMD_ORIGINAL = "/community/rcmd/original.api" //内容推荐api-【2.0】原创内容推荐列表
        const val COMMUNITY_RCMD_ZHONG_CAO = "/community/rcmd/zhong_cao.api" //内容推荐api-【2.0】种草推荐列表
        const val COMMUNITY_RCMD_APP_INDEX =
            "/community/rcmd/app_index.api" //内容推荐api-【2.0】App首页推荐列表
        const val COMMUNITY_RCMD_SUB_TYPES =
            "/community/rcmd/sub_types.api" //内容推荐api - 【2.0】按推荐类型获取子分类

        /**
         * 社区个人主页
         */
        const val COMMUNITY_USER_HOME = "/community/user_home_page.api" //获取用户主页内容

        const val COMMUNITY_USER_HOME_CONTENT_UNRELEASED =
            "/community/record/user_unreleased.api" //个人主页 内容 未发布
        const val COMMUNITY_USER_HOME_CONTENT_RELEASED =
            "/community/content/user_released.api" //个人主页 内容 已发布
        const val COMMUNITY_USER_HOME_FAMILY =
            "/community/group/myGroupList.api" //个人主页 社区群组api - 我加入的群组 和 我管理的群组
        const val COMMUNITY_USER_HOME_FOLLOW =
            "/community/user_follow_list.api" //个人主页 社区群组api - 关注列表
        const val COMMUNITY_USER_HOME_FAN = "/community/user_fans_list.api" //个人主页 社区群组api - 粉丝列表
        const val COMMUNITY_USER_WANTSEE =
            "/community/attitude/want_see_movie_list.api" //个人主页 社区群组api - 想看列表
        const val COMMUNITY_USER_HASSEEN =
            "/community/attitude/has_seen_movie_list.api" //个人主页 社区群组api - 看过列表

        const val COMMUNITY_USER_HOME_CONTENT =
            "/community/content/user_center.api" //个人主页 内容. 内容类型 必填 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"); VIDEO(5, "视频"), AUDIO(6, "音频"),
        const val COMMUNITY_USER_HOME_COLLECTION =
            "/community/collect_contents.api" //用户收藏 内容. 内容类型 必填 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"); VIDEO(5, "视频"), AUDIO(6, "音频"),

        const val COMMUNITY_TIME_LINE =
            "/community/film_time_line.api"// 分页获取观影时间轴(/film_time_line.api)

        /**
         * 社区家族分类API
         */
        //社区群组api 用来获取群组的所有分类 - 群组分类
        const val COMMUNITY_FAMILY_CLASS = "/community/group/groupCategoryList.api"

        //根据分类获取群组列表
        const val COMMUNITY_FAMILY_LIST_BY_CLASS = "/community/group/groupList.api"

        //查找家族
        const val COMMUNITY_FAMILY_FIND_GROUP = "/community/group/findGroup.api"

        //添加家族分区
        const val COMMUNITY_FAMILY_ADD_SECTION = "/community/group/addSection.api"

        //编辑家族分区
        const val COMMUNITY_FAMILY_EDITOR_SECTION = "/community/group/editSection.api"

        //删除家族分区
        const val COMMUNITY_FAMILY_DELETE_SECTION = "/community/group/deleteSection.api"

        //获取家族分区列表
        const val COMMUNITY_FAMILY_LIST_SECTION = "/community/group/sectionList.api"

        //检查用户是否在组内(/group/checkUserInGroup.api)
        const val COMMUNITY_CHECK_USER_IN_GROUP = "/community/group/checkUserInGroup.api"

        /**
         * 社区群组api
         */
        const val COMMUNITY_MY_FAMILY = "/community/group/myGroupList.api" //我加入的群组 和 我管理的群组 列表
        const val COMMUNITY_HOT_FAMILY = "/community/group/hotGroupList.api" //群组的热度排序列表

        //社区群组api - 群组简介(/group/detail.api)
        const val COMMUNITY_FAMILY_DETAIL = "/community/group/detail.api"

        //设置家族发布权限
        const val COMMUNITY_SET_AUTHORITY = "/community/group/setAuthority.api"

        //用户可创建多少群组
        const val COMMUNITY_GROUP_CREATE_GROUP_COUNT = "/community/group/createGroupCount.api"
        const val COMMUNITY_GROUP_CREATE = "/community/group/createGroup.api" // 创建群组
        const val COMMUNITY_GROUP_EDIT = "/community/group/edit.api" // 修改群组
        const val COMMUNITY_GROUP_ADMIN_LIST = "/community/group/administratorList.api" // 群组管理员列表
        const val COMMUNITY_GROUP_UNSET_ADMIN = "/community/group/unsetAdministrator.api" // 取消管理员
        const val COMMUNITY_GROUP_SET_ADMIN = "/community/group/setAdministrator.api" // 设置管理员
        const val COMMUNITY_GROUP_MEMBER_LIST = "/community/group/memberList.api" // 群组成员列表
        const val COMMUNITY_GROUP_REMOVE_MEMBER_LIST =
            "/community/group/removedMemberList.api" // 群组黑名单列表
        const val COMMUNITY_GROUP_APPLICANT_LIST = "/community/group/applicantList.api"  // 群组申请列表
        const val COMMUNITY_GROUP_REMOVE_MEMBER = "/community/group/removeMember.api" // 群组移除成员
        const val COMMUNITY_GROUP_RESTORE_MEMBER =
            "/community/group/restoreMember.api" // 释放群组黑名单中的成员
        const val COMMUNITY_GROUP_ADD_MEMBER = "/community/group/addMember.api" // 批准用户加入群组
        const val COMMUNITY_GROUP_REFUSE_MEMBER = "/community/group/refuseMember.api" // 拒绝加入群组申请
        const val COMMUNITY_GROUP_ADMIN_AND_ACTIVE_MEMBER_LIST =
            "/community/group/administratorAndActiveMemberList.api" // 群主管理员列表+最近活跃列表
        const val COMMUNITY_GROUP_MEMBER_LIST_BY_NICK_NAME =
            "/community/group/groupMemberListByNickName.api" // 用于在成员列表/申请者列表/黑名单列表中根据用户昵称搜索成员。(名称精确查询)
        const val COMMUNITY_JOIN_GROUP = "/community/group/joinGroup.api" //加入群组
        const val COMMUNITY_OUT_GROUP = "/community/group/outGroup.api" //退出群组

        /**
         * 社区帖子API
         */
        //社区帖子api - 分页查询我的未发布群组帖子记录
        const val COMMUNITY_POST_USER_UNRELEASED = "/community/group_post/user_unreleased.api"

        //社区帖子api - 分页查询已发布群组帖子内容
        const val COMMUNITY_POST_RELEASED = "/community/group_post/released.api"
        const val COMMUNITY_POST_RELEASED_V2 = "/community/group_post/released_v2.api"

        //社区帖子api - 置顶帖子(/post/top.api)
        const val COMMUNITY_POST_TOP = "/community/post/top.api"

        //社区帖子api - 取消置顶帖子(/post/cancel_top.api)
        const val COMMUNITY_POST_CANCEL_TOP = "/community/post/cancel_top.api"

        //社区帖子api - 加精帖子(/post/essence.api)
        const val COMMUNITY_POST_ESSENCE = "/community/post/essence.api"

        //社区帖子api - 取消加精帖子(/post/cancel_essence.api)
        const val COMMUNITY_POST_CANCEL_ESSENCE = "/community/post/cancel_essence.api"

        /**
         * 社区相册api
         */
        const val COMMUNITY_UPDATE_ALBUM =
            "/community/user_album/update"//相册api - 修改相册（/user_album/update）
        const val COMMUNITY_ALBUM_IMAGE_LIST_BY_PAGE =
            "/community/user_image/list_by_page"//相册api - 分页获取相册中的图片（/user_image/list_by_page）
        const val COMMUNITY_ALBUM_LIST_BY_PAGE =
            "/community/user_album/page_albums.api"//相册api - 分页获取相册信息（/user_album/list_by_page）
        const val COMMUNITY_CREATE_ALBUM =
            "/community/user_album/save"//相册api - 创建相册（/user_album/save）
        const val COMMUNITY_SAVE_IMAGE =
            "/community/user_image/save"//相册api - 保存图片（/user_image/save）
        const val COMMUNITY_DELETE_IMAGE =
            "/community/user_image/delete"//相册api - 删除图片（/user_image/delete）
        const val COMMUNITY_DELETE_ALBUM =
            "/community/user_album/delete"//相册api - 删除相册（/user_album/delete）
        const val COMMUNITY_GET_IMAGE_INFO_BY_IDS =
            "/community/user_image/get_by_ids"//相册api - 根据id集合批量获取图片信息（/user_image/get_by_ids）
        const val COMMUNITY_GET_ALBUM_INFO_BY_ID =
            "/community/user_album/get_by_id"//相册api - 根据相册id获取相册信息（/user_album/get_by_id）
        const val COMMUNITY_GET_IMAGE_INFO_BY_ID =
            "/community/user_image/get_by_id"//相册api - 根据id获取图片信息（/user_image/get_by_id）
        const val COMMUNITY_GET_ALBUM_COUNT =
            "/community/user_album/get_album_count"//相册api - 获取相册数量（/user_album/get_album_count）


        /**
         * 榜单api
         */
        const val COMMUNITY_TOPLIST_QUERY = "/community/top_list/query.api"    //  榜单分页查询
        const val COMMUNITY_TOPLIST_DETAIL = "/community/top_list/detail.api"  //  电影/电视剧/影人 - 榜单详情页

        /**
         * 卡片大富翁api
         */
        const val RICHMAN_TOP_USER_LIST = "/richman/topUserList.api" // 游戏榜单详情页 - 排行榜

        /**
         * 社区-回复api
         */
        const val COMMUNITY_SAVE_REPLY = "/community/reply.api"//回复api - 保存回复(/reply.api)
        const val COMMUNITY_REPLY_RELEASED =
            "/community/replies/released.api"//回复api - 分页查询已发布回复(/replies/released.api)
        const val COMMUNITY_REPLY_UNRELEASED =
            "/community/replies/user_unreleased.api"//回复api - 分页查询我的未发布回复(/replies/user_unreleased.api)

        /**
         * VideoController - getPlayUrl
         */
        const val VIDEO_GET_PLAY_URL = "/video/play_url2"//获取视频播放地址

        /**
         * 统一推荐位API
         */
        const val RCMD_REGION_PUBLISH = "/common/rcmd_region_publish/list.api"//统一推荐位API - 获取推荐位数据信息

        const val RCMD_TRAILER_LIST =
            "/common/rcmd_region_publish/listNXM22MovieRecommends.api" //统一推荐位API - 获取每日佳片推荐（视频推荐）

        /**
         * 获取分享信息(/utility/share.api)
         */
        const val COMMON_SHARE = "/common/utility/share.api"

        /**
         * 上传图片,start/load.api里动态获取
         */
        const val COMMON_UPLOAD_IMAGE = "/image/upload"//上传图片

        /**
         * 用户信息
         */
        const val ACCOUNT_STATISTIC_INFO =
            "/user/account/statisticsInfo.api"//AccountController - 用户统计信息(/account/statisticsInfo.api)
        const val ACCOUNT_DETAIL =
            "/user/account/detail.api"//AccountController - 用户详情（/account/detail.api）
        const val AVATAR_EDIT =
            "/user/user/avatar/edit.api"// UserController - 更新头像(/user/avatar/edit.api)
        const val USER_CENTER_BG_EDIT =
            "/user/user/background/edit.api"// UserController - 更新个人主页背景图(/user/user/background/edit.api)

        /**
         * 活动
         */
        const val USER_ACTIVITY_LIST = "/user/activity/list.api" // 用户活动列表

        /**
         * 认证
         */
        const val AUTH_CHECK_PERMISSION =
            "/user/user/auth/mtime/permission.api"//UserController - 查询认证影评人是否符合条件(/user/auth/mtime/permission.api)
        const val GET_AUTH_ROLE =
            "/user/user/auth/mtime/role.api"//UserController - 获取电影认证人角色列表(/user/auth/mtime/role.api)
        const val AUTH_SAVE =
            "/user/user/auth/mtime/save.api"//UserController - 时光媒体人认证(/user/auth/mtime/save.api)

        /**
         * 订单
         */
        const val BLEND_ORDERS =
            "/ticket/order/order/blendOrders.api"//OrderController - 根据token查询用户订单列表（三个月内）(/order/blendOrders.api)
        const val HISTORY_ORDERS =
            "/ticket/order/order/userHistoryOrders.api"//OrderController - 获取三个月之前的数据--默认每页10条(/order/userHistoryOrders.api）


        /**
         * 钱包
         */
        const val WALLET_COUPON_LIST = "/ticket/market/voucher/MyVouchers.api"//钱包-券列表
        const val WALLET_COUPON_ADD = "/ticket/market/voucher/BindVoucher.api"//钱包-添加券
        const val WALLET_GIFT_CARD_LIST = "/ticket/market/card/MTimeCardVaildList.api"//钱包-卡列表
        const val WALLET_GIFT_CARD_ADD = "/ticket/market/card/BindMtimeCard.api"//钱包-添加礼品卡

        /**
         * 首页正在热映、即将上映
         */
        const val HOME_SHOWING_COMING = "/ticket/schedule/top/movies.api"

        /**
         * 会员中心首页
         */
        const val MEMBER_CENTER_HOME = "/user/member/center/home.api"//会员中心
        const val MEMBER_EXCHANGE_LIST = "/user/member/exchange/list.api"//会员中心获取M豆兑换商品列表
        const val MEMBER_EXCHANGE = "/user/member/exchange.api"//时光币兑换奖品


        /**
         * 热门搜索点击
         */
        const val POPLAR_CLICK = "/mtime-search/search/poplarClick"

        /**
         * 彩蛋
         */
        const val POPUP_BONUS_SCENE =
            "/user/member/popupBonusScene.api"//弹出彩蛋, 根据用户ID或业务类型，判断今天、该业务是否还可以弹出彩蛋
        const val BONUS_SCENE = "/user/member/bonusScene.api"//开彩蛋

        /**
         * 扫码登录
         */
        const val QRCODE_LOGIN = "/user/qr_login.api" //扫码登陆 - 进行扫码登陆

        const val J_VERIFY_LOGIN =
            "/user/verify_jpush_login.api" // VerifyLoginController - 极光APP三方一键登录（/verify_jpush_login.api）
        const val LIVE_DIRECTOR_UNITS = "/live/rtc_live_player/floating_layers.api"//直播间导播台

        /**
         * 创作者中心 - 任务中心
         */
        const val CREATOR_TASK_API = "/community/creator_task.api"

        /**
         * 创作者中心 - 权益说明
         */
        const val CREATOR_REWARD_API = "/community/creator_reward.api"

        /**
         * 是否需要弹出彩蛋
         */
        const val GET_POPUP_BONUS_SCENE = "/user/member/popupBonusScene.api"
    }


    /**
     * 统一推荐位API
     * GET ("/common/rcmd_region_publish/list.api")
     * 推荐位CODE，以逗号分隔多个
     */
    @GET(RCMD_REGION_PUBLISH)
    suspend fun getRegionPublishList(
        @Query("codes") version: String,
    ): ApiResponse<RegionPublish>


    /**
     * APP榜单列表
     * GET ("/library/index/app/topList.api")
     */
    @GET(INDEX_TOP_LIST)
    suspend fun getIndexTopList(): ApiResponse<IndexAppTopList>

    /**
     * 首页-榜单分页查询
     * GET ("/community/top_list/query.api")
     */
    @GET(INDEX_TOPLIST_QUERY)
    suspend fun getIndexTopListQuery(): ApiResponse<IndexTopListQuery>

    /**
     * APP频道切换TAB列表
     * GET ("/library/index/app/tabs.api")
     *
     * type	标签类型，必传：1-首页顶部标签
     * cityId 城市Id，必传
     */
    @GET(INDEX_APP_NAV)
    suspend fun getIndexAppNav(
        @Query("type") type: String,
        @Query("cityId") cityId: String,
    ): ApiResponse<HomeTabNavList>

    /**
     * APP首页内容Feed流
     * GET ("/community/index/app/feeds.api")
     *
     * pageIndex	Number  页码
     * pageSize	    Number  每页条数
     */
    @GET(INDEX_FEEDS)
    suspend fun getIndexFeeds(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<Feeds>

    /**
     * APP首页影评列表
     * GET ("/community/index/app/movieComments.api")
     *
     * pageIndex	Number  页码
     * pageSize	    Number  每页条数
     */
    @GET(INDEX_MOVIE_COMMENTS)
    suspend fun getIndexMovieComments(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CommContentList>

    /**
     * 今日推荐（每日佳片)-每日弹窗(/dailyRcmdMoviePopup.api)
     * GET ("/library/index/dailyRcmdMoviePopup.api")
     */
    @GET(INDEX_RECOMMEND_MOVIE)
    suspend fun getIndexRecommendMovie(): ApiResponse<String>

    /**
     * 首页-游戏榜单列表
     * GET ("/library/index/app/gameTopList.api")
     */
    @GET(INDEX_GAME_TOP_LIST)
    suspend fun getIndexGameTopList(): ApiResponse<IndexAppGameTopList>

    /**
     * 热门群组列表
     * GET ("/library/index/pc/hotGroups.api")
     *
     * pageIndex	Number  页码
     * pageSize	    Number  每页条数
     */
    @GET(INDEX_PC_HOT_GROUPS)
    suspend fun getIndexPcHotGroups(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<String>

    /**
     * 获取国家、城市列表
     * GET ("/library/index/locationList.api")
     *
     * locationId	Number  国家，城市id。不传值，默认获取国家
     */
    @GET(INDEX_LOCATION_LIST)
    suspend fun getIndexLocationList(
        @Query("locationId") locationId: Long,
    ): ApiResponse<String>

    /**
     * 首页-正在热映/即将上映
     * GET ("/ticket/schedule/top/movies.api")
     *
     * locationId  城市ID
     */
    @GET(HOME_SHOWING_COMING)
    suspend fun getHomeShowingComingMovies(
        @Query("locationId") locationId: String,
    ): ApiResponse<HomeShowingComingMovies>

    /**
     * 获取每日佳片推荐（视频推荐）
     * type 必填】 1当前日期推荐，2历史推荐
     */
    @GET(RCMD_TRAILER_LIST)
    suspend fun getHomeRcmdTrailers(
        @Query("type") type: Long,
    ): ApiResponse<RcmdTrailerList>

    /**
     * 影片评分（新版只支持评总分）
     * GET ("/library/movie/movierating.api")
     *
     * movieId	Number  电影Id
     * rating	Number  总评分
     */
    @GET(MOVIE_RATING)
    suspend fun getMovieRating(
        @Query("movieId") movieId: Long,
        @Query("rating") rating: Double,
        @Query("subItemRating") subItemRating: String,
    ): ApiResponse<StatusResult>

    /**
     * 批量获取影片售票状态和在线播放状态
     * GET ("/library/movie/summary.api")
     *
     * movieIds	    String  电影Id，多个用 , 分隔
     * locationId	Long    地区Id
     */
    @GET(MOVIE_SUMMARY)
    suspend fun getMovieSummary(
        @Query("movieIds") movieIds: String,
        @Query("locationId") locationId: Long,
    ): ApiResponse<String>

    /**
     * 获取当前用户最新一条长短影评
     * GET ("/library/movie/currentUser/latestComment.api")
     *
     * movieId	Number  电影Id
     */
    @GET(MOVIE_LATEST_COMMENT)
    suspend fun getMovieLatestComment(
        @Query("movieId") movieId: String,
    ): ApiResponse<LatestComment>

    /**
     * 获取影片分类视频Id列表
     * GET ("/library/movie/category/videoIds.api")
     *
     * movieId	Number  电影Id
     * type	    Number  分类Id：-1推荐（所有视频参与分页） 0预告片 1精彩片段 2拍摄花絮 3影人访谈 4电影首映 5MV 7剧集正片
     */
    @GET(MOVIE_VIDEO_IDS)
    suspend fun getMovieVideoIds(
        @Query("movieId") movieId: String,
        @Query("type") type: Long,
    ): ApiResponse<String>

    /**
     * 获取影片分类视频列表
     * GET ("/library/movie/category/video.api")
     *
     * movieId	    Number  电影Id
     * pageIndex	Number  当前页索引
     * type	        Number  视频分类：-1推荐（默认），0预告片，1精彩片段，2拍摄花絮，3影人访谈，4电影首映，5MV，7-剧集正片
     */
    @GET(MOVIE_VIDEOS)
    suspend fun getMovieVideos(
        @Query("movieId") movieId: String,
        @Query("pageIndex") pageIndex: Long,
        @Query("type") type: Long,
    ): ApiResponse<String>

    /**
     * 获取影片扩展信息
     * GET ("/library/movie/extendDetail.api")
     *
     * movieId	Number  电影Id
     */
    @GET(MOVIE_EXTEND_DETAIL)
    suspend fun getMovieExtendDetail(
        @Query("movieId") movieId: String,
    ): ApiResponse<String>

    /**
     * 获取影片更多资料
     * GET ("/library/movie/moreDetail.api")
     *
     * movieId	Number  电影Id
     */
    @GET(MOVIE_MORE_DETAIL)
    suspend fun getMovieMoreDetail(
        @Query("movieId") movieId: Long,
    ): ApiResponse<MovieMore>

    /**
     * 获取影片短评列表
     * GET ("/library/movie/comment.api")
     *
     * movieId	    Number  电影Id
     * pageIndex	Number  当前页索引
     * pageSize	    Number  每页记录数（默认20条）
     * orderType	Number  排序类型：1最热（默认） 2最新
     */
    @GET(MOVIE_COMMENTS)
    suspend fun getMovieComments(
        @Query("movieId") movieId: String,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
        @Query("orderType") orderType: Long,
    ): ApiResponse<MovieReviewList>

    /**
     * 获取影片背景图等Style信息（RPC封装接口）
     * GET ("/library/movie/style.api")
     *
     * movieId	    Number  电影Id
     */
    @GET(MOVIE_STYLE)
    suspend fun getMovieStyle(
        @Query("movieId") movieId: String,
    ): ApiResponse<String>

    /**
     * 获取影片详情页数据
     * GET ("/library/movie/detail.api")
     *
     * movieId	    Number  电影Id
     * locationId	Number  地区Id
     */
    @GET(MOVIE_DETAIL)
    suspend fun getMovieDetail(
        @Query("movieId") movieId: String,
        @Query("locationId") locationId: Long,
    ): ApiResponse<String>

    /**
     * 获取影片长评列表
     * GET ("/library/movie/longCommentList.api")
     *
     * movieId	    Number  电影Id
     * pageIndex	Number  当前页索引
     * pageSize	    Number  每页记录数（默认20条）
     * orderType	Number  排序类型：1最热（默认） 2最新
     */
    @GET(MOVIE_LONG_COMMENTS)
    suspend fun getMovieLongComments(
        @Query("movieId") movieId: String,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
        @Query("orderType") orderType: Long,
    ): ApiResponse<MovieReviewList>

    /**
     * 获取我的短影评列表（每页只有一条）
     * GET ("/library/movie/myShortComment/list.api")
     *
     * movieId	    Number  电影Id
     * pageIndex	Number  当前页索引
     */
    @GET(MOVIE_MY_SHORT_COMMENTS)
    suspend fun getMovieMyShortComments(
        @Query("movieId") movieId: String,
        @Query("pageIndex") pageIndex: Long,
    ): ApiResponse<String>

    /**
     * 获取我的长影评列表
     * GET ("/library/movie/myComment/lists.api")
     *
     * pageIndex	Number  当前页索引
     * pageSize	    Number  每页记录数
     */
    @GET(MOVIE_MY_COMMENTS)
    suspend fun getMovieMyComments(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<MyLongCommentList>

    /**
     * 获取按职业分类的电影演职员列表（导演+演员）
     * GET ("/library/movie/movieCreditsWithTypes.api")
     *
     * movieId	Number  电影Id
     */
    @GET(MOVIE_CREDITS_WITH_TYPE)
    suspend fun getMovieCreditsWithType(
        @Query("movieId") movieId: String,
    ): ApiResponse<String>

    /**
     * 获取除时光对话外的文章列表
     * GET ("/library/movie/timeNewsList.api")
     *
     * movieId	        Number  电影Id
     * pageIndex	    Number  当前页索引
     * pageSize	        Number  每页记录数
     * excludeTimeTalk	Boolean 是否排除时光对话（默认true，pc页传false）
     */
    @GET(MOVIE_TIME_NEWS_LIST)
    suspend fun getMovieTimeNewsList(
        @Query("movieId") movieId: String,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
        @Query("excludeTimeTalk") excludeTimeTalk: Boolean = true,
    ): ApiResponse<String>

    /**
     * 获取时光对话文章列表
     * GET ("/library/movie/timeTalksList.api")
     *
     * movieId	        Number  电影Id
     * pageIndex	    Number  当前页索引
     * pageSize	        Number  每页记录数
     */
    @GET(MOVIE_TIME_TALKS)
    suspend fun getMovieTimeTalks(
        @Query("movieId") movieId: String,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<String>

    /**
     * 获取热门长短影评列表
     * GET ("/library/movie/hotComments.api")
     *
     * movieId	    Number  电影Id
     * minPageSize	Number  短影评每页记录数
     * plusPageSize	Number  长影评每页记录数
     */
    @GET(MOVIE_HOT_COMMENTS)
    suspend fun getMovieHotComments(
        @Query("movieId") movieId: String,
        @Query("minPageSize") minPageSize: Long,
        @Query("plusPageSize") plusPageSize: Long,
    ): ApiResponse<String>

    /**
     * 获取用户对电影的动态信息
     * GET ("/library/movie/dynamic.api")
     *
     * movieId	Number  电影Id
     */
    @GET(MOVIE_DYNAMIC)
    suspend fun getMovieDynamic(
        @Query("movieId") movieId: String,
    ): ApiResponse<String>

    /**
     * 获取电影全部图片和分类
     * GET ("/library/movie/imageAll.api")
     *
     * movieId	Number  电影Id
     */
    @GET(MOVIE_IMAGE_ALL)
    suspend fun getMovieImageAll(
        @Query("movieId") movieId: Long,
    ): ApiResponse<MoviePhoto>

    /**
     * 获取电影公司列表
     * GET ("/library/movie/companyList.api")
     *
     * movieId	Number  电影Id
     */
    @GET(MOVIE_COMPANY_LIST)
    suspend fun getMovieCompanyList(
        @Query("movieId") movieId: String,
    ): ApiResponse<String>

    /**
     * 获取电影剧情列表
     * GET ("/library/movie/plots.api")
     *
     * movieId	Number  电影Id
     */
    @GET(MOVIE_PLOTS)
    suspend fun getMoviePlots(
        @Query("movieId") movieId: String,
    ): ApiResponse<String>

    /**
     * 获取电影图片列表（100张剧照+100张海报）
     * GET ("/library/movie/image.api")
     *
     * movieId	Number  电影Id
     */
    @GET(MOVIE_IMAGES)
    suspend fun getMovieImages(
        @Query("movieId") movieId: Long,
    ): ApiResponse<MovieImage>

    /**
     * 获取电影演职员列表
     * GET ("/library/movie/credits.api")
     *
     * movieId	Number  电影Id
     */
    @GET(MOVIE_CREDITS)
    suspend fun getMovieCredits(
        @Query("movieId") movieId: String,
    ): ApiResponse<String>

    /**
     * 获取电影片单列表
     * GET ("/library/movie/movieListRecommend.api")
     *
     * movieId	    Number  电影Id
     * minPageSize	Number  短影评每页记录数
     * plusPageSize	Number  长影评每页记录数
     */
    @GET(MOVIE_RECOMMEND_LIST)
    suspend fun getMovieRecommendList(
        @Query("movieId") movieId: String,
        @Query("minPageSize") minPageSize: Long,
        @Query("plusPageSize") plusPageSize: Long,
    ): ApiResponse<String>

    /**
     * 获取视频详情
     * GET ("/library/movie/video/detail.api")
     *
     * vId	        Number  视频Id
     * locationId	Number  地区Id
     */
    @GET(MOVIE_VIDEO_DETAIL)
    suspend fun getMovieVideoDetail(
        @Query("vId") vId: Long,
        @Query("locationId") locationId: Long,
    ): ApiResponse<VideoDetail>

    /**
     * 设置电影为想看/取消想看
     * GET ("/library/movie/setWantToSee.api")
     *
     * movieId	Number  电影Id
     * flag	    Number  操作类型：1想看，2取消想看
     * year	    Number  年代（用于生成XXXX年我想看的第XX部电影）
     */
    @GET(MOVIE_WANT_TO_SEE)
    suspend fun getMovieWantToSee(
        @Query("movieId") movieId: Long,
        @Query("flag") flag: Long,
        @Query("year") year: Long,
    ): ApiResponse<WantToSeeResult>

    /**
     * 设置电影为看过
     * GET ("/library/movie/setHasSeen.api")
     *
     * movieId	Number  电影Id
     */
    @GET(MOVIE_SET_HAS_SEEN)
    suspend fun getMovieSetHasSeen(
        @Query("movieId") movieId: String,
    ): ApiResponse<String>

    /**
     * 影人的关联新闻列表
     * GET ("/library/person/timeNewsList.api")
     *
     * personId	    Number  影人ID
     * pageIndex	Number  页码，默认1
     * pageSize	    Number  每页条数，默认10
     */
    @GET(PERSON_TIME_NEWS_LIST)
    suspend fun getPersonTimeNewsList(
        @Query("personId") personId: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<String>

    /**
     * 获取影人人物传记列表
     * GET ("/library/person/biography.api")
     *
     * personId	    Number  影人ID
     */
    @GET(PERSON_BIOGRAPHY)
    suspend fun getPersonBiography(
        @Query("personId") personId: Long,
    ): ApiResponse<String>

    /**
     * 获取影人作品列表
     * GET ("/library/person/movies.api")
     *
     * personId	    Number  影人ID
     * pageIndex	Number  当前页索引
     * pageSize	    Number  每页数量（默认20）
     * orderId	    Number  排序类型（默认0， 1评分高到低，2年代从近到远 ，3年代从远到近，4按评分人数倒序排列，人数相同按评分倒序）
     * type	        Number  作品类型，默认0，返回全部；1返回电影，2返回电视剧，3其他作品
     * year	        Number  年份。默认0，所有年份
     * released	    Number  上映与否。默认0，所有；1只返回已上映数据，2返回未上映作品
     * officeId	    Number  职位ID。以担任职位为搜索条件。不传则返回全部职位的数据
     */
    @GET(PERSON_MOVIES)
    suspend fun getPersonMovies(
        @Query("personId") personId: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
        @Query("orderId") orderId: Long,
        @Query("type") type: Long,
        @Query("year") year: Long,
        @Query("released") released: Long,
        @Query("officeId") officeId: Long,
    ): ApiResponse<String>

    /**
     * 获取影人全部图片和分类
     * GET ("/library/person/imageAll.api")
     *
     * personId	    Number  影人ID
     */
    @GET(PERSON_IMAGE_ALL)
    suspend fun getPersonImageAll(
        @Query("personId") personId: Long,
    ): ApiResponse<String>

    /**
     * 获取影人关联视频列表
     * GET ("/library/person/videoList.api")
     *
     * personId	    Number  影人ID
     */
    @GET(PERSON_VIDEOS)
    suspend fun getPersonVideos(
        @Query("personId") personId: Long,
    ): ApiResponse<String>

    /**
     * 获取影人图片列表（100张写真+100张生活照）
     * GET ("/library/person/image.api")
     *
     * personId	    Number  影人ID
     */
    @GET(PERSON_IMAGES)
    suspend fun getPersonImages(
        @Query("personId") personId: Long,
    ): ApiResponse<String>

    /**
     * 获取影人短评列表（每页20条）
     * GET ("/library/person/comment.api")
     *
     * personId	    Number  影人ID
     * pageIndex	Number  页码，默认1
     * pageSize	    Number  每页条数
     */
    @GET(PERSON_COMMENTS)
    suspend fun getPersonComments(
        @Query("personId") personId: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<String>

    /**
     * 获取影人详情页数据
     * GET ("/library/person/detail.api")
     *
     * personId	    Number  影人ID
     * cityId	    Number  地区Id
     */
    @GET(PERSON_DETAIL)
    suspend fun getPersonDetail(
        @Query("personId") personId: Long,
        @Query("cityId") cityId: Long,
    ): ApiResponse<String>

    /**
     * 获取用户对影人的动态信息
     * GET ("/library/person/dynamic.api")
     *
     * personId	    Number  影人ID
     */
    @GET(PERSON_DYNAMIC)
    suspend fun getPersonDynamic(
        @Query("personId") personId: Long,
    ): ApiResponse<String>

    /**********************************社区内容关注-start******************************/
    /**
     * 社区交互-关注api - 关注/取消关注用户
     * POST (/follow_user.api)
     * action Number  动作 1：关注 2：取消关注
     *  userId Number 用户ID
     */
    @POST(COMMUNITY_FOLLOW_USER)
    suspend fun followUser(
        @Query("action") action: Long,
        @Query("userId") userId: Long,
    ): ApiResponse<CommBizCodeResult>


    /**********************************社区内容关注-end******************************/

    /**********************************社区评论api-start******************************/
    /**
     *   评论api - 分页查询已发布评论(/comments/released.api)
     *   post，提交json串，详情见 http://apidocs.mt-dev.com/community-front-api/index.html#api-%E8%AF%84%E8%AE%BAapi-queryReleasedComment
     *   objType    Number      评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"),
     *                          ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院");
     *  objId       Number      评论主体对象
     *  optId       Number      观点id
     *  sort        Number      排序 USER_CREATE_TIME_DESC(1, "-userCreateTime"), HOT_DESC(2, "-hot,-userCreateTime");
     *  pageIndex  Number
     *  pageSize   Number
     */
    @POST(COMMUNITY_COMMENT_RELEASED)
    suspend fun getCommentListData(@Body body: RequestBody): ApiResponse<CommentList>

    /**
     * 评论api - 分页查询用户未发布评论(/comments/user_unreleased.api)
     * post，提交json串，详情见 http://apidocs.mt-dev.com/community-front-api/index.html#api-%E8%AF%84%E8%AE%BAapi-queryUserUnreleasedComment
     *   objType    Number      评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"),
     *                          ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院");
     *  objId       Number      评论主体对象
     *  optId       Number      观点id
     *  sort        Number      排序 USER_CREATE_TIME_DESC(1, "-userCreateTime"), HOT_DESC(2, "-hot,-userCreateTime");
     *  pageIndex  Number
     *  pageSize   Number
     */
    @POST(COMMUNITY_COMMENT_UNRELEASED)
    suspend fun getUnReleasedCommentListData(@Body body: RequestBody): ApiResponse<CommentList>


    /**
     * 评论api - 保存评论(/comment.api)
     * 详情见：http://apidocs.mt-dev.com/community-front-api/index.html#api-%E8%AF%84%E8%AE%BAapi-saveComment
     * POST (/community/comment.api)
     * commentId    Number   评论id
     * objType      Number   评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"),
     *                          ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院");
     *  objId       Number    评论主体对象id
     *  optId       Number     观点id
     *  images      Array       图片集合
     *      imageId     String      图片id
     *      imageUrl    String      图片Url
     *      imageFormat String      图片格式
     *      imageDes    String      图片描述
     *
     *  body        String          富文本正文
     *
     *
     * @param  {
    "images": [
    {
    "imageFormat": "WBKHR",
    "imageId": "hoOAbax",
    "imageUrl": "1IIx42p",
    "imageDesc": "PJS4SYR"
    }
    ],
    "objId": 3359,
    "commentId": 257,
    "body": "Np",
    "objType": 2535,
    "optId": 5625
    }
     */
    @POST(COMMUNITY_SAVE_COMMENT)
    suspend fun saveComment(@Body body: RequestBody): ApiResponse<SaveCommentResult>

    /**
     * 评论api - 获取评论(/comment.api) 获取评论详情
     * GET (community/comment.api)
     * @param objType       Number       评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"),
     *                                  ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院");
     *  @param commentId    Number      评论id
     */
    @GET(COMMUNITY_SAVE_COMMENT)
    suspend fun getCommentData(
        @Query("objType") objType: Long,
        @Query("commentId") commentId: Long,
    ): ApiResponse<CommentDetail>

    /**
     * 评论api - 删除评论(/delete_comment.api)
     * POST（/community/delete_comment.api）
     *  @param objType       Number       评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"),
     *                                  ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院");
     *  @param commentId    Number      评论id
     */
    @POST(COMMUNITY_DELETE_COMMENT)
    suspend fun deleteComment(
        @Query("objType") objType: Long,
        @Query("commentId") commentId: Long,
    ): ApiResponse<CommBizCodeResult>


    /**********************************社区评论api-end******************************/


    /**
     * 时光轴
     */
    @GET(COMMUNITY_TIME_LINE)
    suspend fun getTimeLineData(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<WantSeeInfo>

    /**********************************社区文章api-start******************************/

    /**
     * 文章api - 查询推荐文章(/article/rcmd.api)
     * @param rcmdId 推荐id
     */
    @GET(COMMUNITY_RECOMMEND_ARTICLE)
    suspend fun getRecommendArticleData(
        @Query("rcmdId") rcmdId: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CommContentList>

    /**
     * 文章api - 分页查询原创文章内容(/article/original.api)
     * @param sort 排序 1.最新 2.最热
     */
    @GET(COMMUNITY_ORIGINAL_ARTICLE)
    suspend fun getOriginArticle(
        @Query("sort") sort: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CommContentList>

    /**
     * 文章api - 查询用户能否发文章(/article_user/is_allow.api)
     */
    @GET(COMMUNITY_CAN_PUBLISH_ARTICLE)
    suspend fun checkCanPublishArticle(): ApiResponse<Boolean>

    /**
     * 文章api - 分页查询一条文章的关联文章列表(/article/reobj_article.api)
     * GET （/community/article/reobj_article.api）
     * @param contentId     Number  内容id
     * @param   recId       Number  记录id(文章记录ID,contentId和recId二选一)
     * @param pageIndex     Number  页面pageIndex
     * @param pageSize      Number  页面size
     *
     */
    @GET(COMMUNITY_REOBJ_ARTICLE)
    suspend fun getReObjArticleList(
        @Query("contentId") contentId: Long? = null,
        @Query("recId") recId: Long? = null,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CommContentList>

    /**********************************社区文章api-end******************************/

    /**********************************社区点赞api-start******************************/

    /**
     * 社区交互-点赞api - 点赞
     * POST (/community/praise_up.api)
     * @param action    Number  动作 1.点赞 2.取消点赞
     * @param objType   Number  点赞主体类型 JOURNAL(1, "日志"),POST(2, "帖子"),FILM_COMMENT(3, "影评"),
     *                                     ARTICLE(4, "文章"),ALBUM(5, "相册"),TOPIC_LIST(6, "榜单"),
     *                                     CINEMA(7, "影院"),MOVIE_TRAILER(8, "预告片"),LIVE(9, "直播"),
     *                                     CARD_USER(10, "卡片用户"),CARD_SUIT(11, "卡片套装"),VIDEO(12, "视频"),
     *                                     AUDIO(13, "音频"),FILM_LIST(14, "片单"),JOURNAL_COMMENT(101, "日志评论"),
     *                                     POST_COMMENT(102, "帖子评论"),FILM_COMMENT_COMMENT(103, "影评评论"),
     *                                     ARTICLE_COMMENT(104, "文章评论"),ALBUM_COMMENT(105, "相册评论"),
     *                                     TOPIC_LIST_COMMENT(106, "榜单评论"),CINEMA_COMMENT(107, "影院评论"),
     *                                     MOVIE_TRAILER_COMMENT(108, "预告片评论"),LIVE_COMMENT(109, "直播评论"),
     *                                     CARD_USER_COMMENT(110, "卡片用户评论"),CARD_SUIT_COMMENT(111, "卡片套装评论"),
     *                                     VIDEO_COMMENT(112, "视频评论"),AUDIO_COMMENT(113, "音频评论"),
     *                                     FILM_LIST_COMMENT(114, "片单评论"),JOURNAL_REPLY(201, "日志回复"),
     *                                     POST_REPLY(202, "帖子回复"),FILM_COMMENT_REPLY(203, "影评回复"),
     *                                     ARTICLE_REPLY(204, "文章回复"),ALBUM_REPLY(205, "相册回复"),
     *                                     TOPIC_LIST_REPLY(206, "榜单回复"),CINEMA_REPLY(207, "影院回复"),
     *                                     MOVIE_TRAILER_REPLY(208, "预告片回复"),LIVE_REPLY(209, "直播回复"),
     *                                     CARD_USER_REPLY(210, "卡片用户回复"),CARD_SUIT_REPLY(211, "卡片套装回复"),
     *                                     VIDEO_REPLY(212, "视频回复"),AUDIO_REPLY(213, "音频回复"),
     *                                     FILM_LIST_REPLY(214, "片单回复"),
     * @param objId     Number   点赞主体
     */
    @POST(COMMUNITY_PRAISE_UP)
    suspend fun praiseUp(
        @Query("action") action: Long,
        @Query("objType") objType: Long,
        @Query("objId") objId: Long,
    ): ApiResponse<CommBizCodeResult>

    /**
     * 社区交互-点赞api - 点踩(/praise_down.api)
     * POST (/community/praise_down.api)
     * @param action    Number  动作 1.点踩 2.取消点踩
     * @param objType   Number  点赞主体类型 JOURNAL(1, "日志"),POST(2, "帖子"),FILM_COMMENT(3, "影评"),
     *                                     ARTICLE(4, "文章"),ALBUM(5, "相册"),TOPIC_LIST(6, "榜单"),
     *                                     CINEMA(7, "影院"),MOVIE_TRAILER(8, "预告片"),LIVE(9, "直播"),
     *                                     CARD_USER(10, "卡片用户"),CARD_SUIT(11, "卡片套装"),VIDEO(12, "视频"),
     *                                     AUDIO(13, "音频"),FILM_LIST(14, "片单"),JOURNAL_COMMENT(101, "日志评论"),
     *                                     POST_COMMENT(102, "帖子评论"),FILM_COMMENT_COMMENT(103, "影评评论"),
     *                                     ARTICLE_COMMENT(104, "文章评论"),ALBUM_COMMENT(105, "相册评论"),
     *                                     TOPIC_LIST_COMMENT(106, "榜单评论"),CINEMA_COMMENT(107, "影院评论"),
     *                                     MOVIE_TRAILER_COMMENT(108, "预告片评论"),LIVE_COMMENT(109, "直播评论"),
     *                                     CARD_USER_COMMENT(110, "卡片用户评论"),CARD_SUIT_COMMENT(111, "卡片套装评论"),
     *                                     VIDEO_COMMENT(112, "视频评论"),AUDIO_COMMENT(113, "音频评论"),
     *                                     FILM_LIST_COMMENT(114, "片单评论"),JOURNAL_REPLY(201, "日志回复"),
     *                                     POST_REPLY(202, "帖子回复"),FILM_COMMENT_REPLY(203, "影评回复"),
     *                                     ARTICLE_REPLY(204, "文章回复"),ALBUM_REPLY(205, "相册回复"),
     *                                     TOPIC_LIST_REPLY(206, "榜单回复"),CINEMA_REPLY(207, "影院回复"),
     *                                     MOVIE_TRAILER_REPLY(208, "预告片回复"),LIVE_REPLY(209, "直播回复"),
     *                                     CARD_USER_REPLY(210, "卡片用户回复"),CARD_SUIT_REPLY(211, "卡片套装回复"),
     *                                     VIDEO_REPLY(212, "视频回复"),AUDIO_REPLY(213, "音频回复"),
     *                                     FILM_LIST_REPLY(214, "片单回复"),
     * @param objId     Number  点踩主体
     */
    @POST(COMMUNITY_PRAISE_DOWN)
    suspend fun praiseDown(
        @Query("action") action: Long,
        @Query("objType") objType: Long,
        @Query("objId") objId: Long,
    ): ApiResponse<CommBizCodeResult>

    /**
     * 社区交互-点赞api - 查询点赞点踩状态(/praise_stat.api)
     * GET(/community/praise_stat.api)
     * @param   objType     Number  点赞主体类型 JOURNAL(1, "日志"),POST(2, "帖子"),FILM_COMMENT(3, "影评"),ARTICLE(4, "文章"),
     *                           ALBUM(5, "相册"),TOPIC_LIST(6, "榜单"),JOURNAL_COMMENT(101, "日志评论"),POST_COMMENT(102, "帖子评论"),
     *                              FILM_COMMENT_COMMENT(103, "影评评论"),ARTICLE_COMMENT(104, "文章评论"),ALBUM_COMMENT(105, "相册评论"),
     *                          TOPIC_LIST_COMMENT(106, "榜单评论"),CINEMA_COMMENT(107, "影院评论"),JOURNAL_REPLY(201, "日志回复"),POST_REPLY(202, "帖子回复"),
     *                          FILM_COMMENT_REPLY(203, "影评回复"),ARTICLE_REPLY(204, "文章回复"),ALBUM_REPLY(205, "相册回复"),TOPIC_LIST_REPLY(206, "榜单回复"),CINEMA_REPLY(207, "影院回复");
     * @param   objId       Number      点赞主体id
     *
     */
    @GET(COMMUNITY_PRAISE_STATE)
    suspend fun getPraiseState(
        @Query("objType") objType: Long,
        @Query("objId") objId: Long,
    ): ApiResponse<PraiseState>

    /**********************************社区点赞api-end******************************/

    /**********************************社区收藏api-start******************************/

    /**
     * 社区交互-收藏api - 分页查询用户收藏电影(/collect_movies_v2.api)
     * GET （community/collect_movies_v2.api）
     * @param pageIndex     Number      分页index
     * @param pageSize      Number      分页size
     */
    @GET(COMMUNITY_COLLECTION_MOVIES_V2)
    suspend fun getCollectionMovie(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CollectionMovie>

    /**
     * 社区交互-收藏api - 分页查询用户收藏影人(/collect_persons_v2.api)
     * GET （/community/collect_persons_v2.api）
     * @param pageIndex     Number      分页index
     * @param pageSize      Number      分页size
     */
    @GET(COMMUNITY_COLLECTION_PERSONS_V2)
    suspend fun getCollectionPerson(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CollectionPerson>

    /**
     * 社区交互-收藏api - 分页查询用户收藏影院(/collect_cinemas_v2.api)
     * GET （/community/collect_cinemas_v2.api）
     * @param pageIndex     Number      分页index
     * @param pageSize      Number      分页size
     */
    @GET(COMMUNITY_COLLECTION_CINEMAS_V2)
    suspend fun getCollectionCinema(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CollectionCinema>

    /**
     * 片单api - 我的片单列表（个人主页专用，缓存1秒）（/film_list/pageMyFilmList.api）
     * GET （/film_list/pageMyFilmList.api）
     * @param nextStamp     String      分页index
     * @param pageSize      Number      分页size
     */
    @GET(COMMUNITY_MY_FILM_LIST)
    suspend fun getMyFilmList(
        @Query("userId") userId: Long,
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<MyCreateFilmList>

    /**
     *  社区交互-收藏api - 收藏
     *  POST (/collect.api)
     *  @param action  Number 动作 1.收藏 2.取消收藏
     *  @param objType Number  收藏主体类型 MOVIE(1, "电影"),PERSON(2, "影人"),CINEMA(3, "影院"),POST(4, "帖子"),ARTICLE(5, "文章"),JOURNAL(6, "日志"),FILM_COMMENT(7, "影评"),VIDEO(8, "视频"),AUDIO(9, "音频"),FILM_LIST(10, "片单"),
     * @param objId Number 收藏主体
     */
    @POST(COMMUNITY_COLLECTION)
    suspend fun collection(
        @Query("action") action: Long,
        @Query("objType") objType: Long,
        @Query("objId") objId: Long,
    ): ApiResponse<CollectionResult>

    /**
     * 社区交互-收藏api - 分页查询用户收藏电影(/collect_movies.api)
     * GET （community/collect_movies.api）
     * @param pageIndex     Number      分页index
     * @param pageSize      Number      分页size
     */
    @GET(COMMUNITY_COLLECTION_MOVIES)
    suspend fun getMovieCollection(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CollectionMovie>


    /**
     * 社区交互-收藏api - 分页查询用户收藏影人(/collect_persons.api)
     * GET （/community/collect_persons.api）
     * @param pageIndex     Number      分页index
     * @param pageSize      Number      分页size
     */
    @GET(COMMUNITY_COLLECTION_PERSONS)
    suspend fun getPersonCollection(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CollectionPerson>

    /**
     * 社区交互-收藏api - 分页查询用户收藏影院(/collect_cinemas.api)
     * GET （/community/collect_cinemas.api）
     * @param pageIndex     Number      分页index
     * @param pageSize      Number      分页size
     */
    @GET(COMMUNITY_COLLECTION_CINEMAS)
    suspend fun getCinemaCollection(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CollectionCinema>

    /**
     * 社区交互-收藏api - 分页查询用户收藏文章(/collect_articles.api)
     * GET （/community/collect_articles.api）
     * @param pageIndex     Number      分页index
     * @param pageSize      Number      分页size
     */
    @GET(COMMUNITY_COLLECTION_ARTICLES)
    suspend fun getArticleCollection(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CollectionArticle>

    /**
     * 社区交互-收藏api - 分页查询用户收藏帖子(/collect_posts.api)
     * GET （/community/collect_posts.api）
     * @param pageIndex     Number      分页index
     * @param pageSize      Number      分页size
     */
    @GET(COMMUNITY_COLLECTION_POSTS)
    suspend fun getPostCollection(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CollectionPost>


    /**********************************社区收藏api-end******************************/

    /***
     * 社区交互-投票api - 投票(/vote.api)
     * POST
     *
     * @param objType Number 投票主体类型 POST(1, "帖子")
     * @param objId    Number 投票主体对象ID
     * @param voteIds Array 用户投票的选项ID列表
     */
    @POST(COMMUNITY_VOTE)
    suspend fun communityVote(@Body body: RequestBody): ApiResponse<CommBizCodeResult>

    /**
     * 社区首页api - 社区关注-分页查询动态(/dynamic.api)
     */
    @GET(COMMUNITY_DYNAMIC)
    suspend fun getCommunityHomeFollowDynamic(
        @Query("pageStamp") pageStamp: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
        @Query("locationId") locationId: String,
    ): ApiResponse<CommunityFollowList>

    /**
     * 社区首页api - 社区精选-分页查询社区推荐内容(/rcmd/community.api)
     */
    @GET(COMMUNITY_RCMD)
    suspend fun getCommunityHomeRcmdSelection(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<RcmdSelectionList>

    /**
     * 社区-用户主页api - 获取用户主页内容(/user_home_page.api)
     */
    @GET(COMMUNITY_USER_HOME)
    suspend fun getUserHomeInfo(@Query("userId") userId: Long): ApiResponse<UserHomeInfo>

    /**
     * 未发布 社区个人主页内容4\、影评3\ 日志\1 帖子\2 api - 社区个人主页 内容-分页查询(/rcmd/community.api)
     * @Query("pageIndex") pageIndex: Long,
     * @Query("pageSize") pageSize: Long,
     * @Query("type") type: Long
     */
    @POST(COMMUNITY_USER_HOME_CONTENT_UNRELEASED)
    suspend fun getCommunityPersonContentUnreleased(@Body body: RequestBody): ApiResponse<ContentList>


    /**
     * 已发布 社区个人主页内容4\、影评3\ 日志\1 帖子\2 api - 社区个人主页 内容-分页查询(/rcmd/community.api)
     *
     * @Query("pageIndex") pageIndex: Long,
     * @Query("pageSize") pageSize: Long,
     * @Query("type") type: Long,
     * @Query("userId") userId: Long
     */
    @POST(COMMUNITY_USER_HOME_CONTENT_RELEASED)
    suspend fun getCommunityPersonContentReleased(@Body body: RequestBody): ApiResponse<ContentList>


    @POST(COMMUNITY_USER_HOME_CONTENT)
    suspend fun getCommunityPersonContent(@Body body: RequestBody): ApiResponse<ContentList>

    /**
     * 社区交互-收藏api - 分页查询用户收藏内容(/collect_contents.api)
     */
    @GET(COMMUNITY_USER_HOME_COLLECTION)
    suspend fun getPersonCollectContent(
        @Query("type") type: Long,
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<CollectionContentList>

    /**
     * 社区群组api - 我加入的群组 和 我管理的群组 列表(/group/myGroupList.api)
     */
    @GET(COMMUNITY_USER_HOME_FAMILY)
    suspend fun getCommunityPersonFamily(
        @Query("userId") userId: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<PersonFamilyList>

    /**
     * 社区交互-关注api - 分页查询用户的关注列表(/user_follow.api)
     */
    @GET(COMMUNITY_USER_HOME_FOLLOW)
    suspend fun getCommunityPersonFollow(
        @Query("userId") userId: Long,
        @Query("pageSize") pageSize: Long,
        @Query("nextStamp") nextStamp: String?,
    ): ApiResponse<PersonMyFriendList>

    /**
     * 社区交互-关注api - 分页查询用户的粉丝列表(/user_fans.api)
     */
    @GET(COMMUNITY_USER_HOME_FAN)
    suspend fun getCommunityPersonFan(
        @Query("userId") userId: Long,
        @Query("pageSize") pageSize: Long,
        @Query("nextStamp") nextStamp: String?,
    ): ApiResponse<PersonMyFriendList>


    /**
     * 社区交互-想过api - 获取想看影片列表(/library/movie/wantSeeMovieList.api)
     */
    @GET(COMMUNITY_USER_WANTSEE)
    suspend fun getCommunityWantSee(
        @Query("uid") userId: Long,
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<WantSeeInfo>

    /**
     * 社区交互-看过api - 获取看过影片列表(/attitude/has_seen_movie_list.api)
     */
    @GET(COMMUNITY_USER_HASSEEN)
    suspend fun getCommunityHasSeen(
        @Query("uid") userId: Long,
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<WantSeeInfo>

    /**
     * 社区群组分类api 用来获取群组的所有分类(/group/groupCategoryList.api)
     */
    @GET(COMMUNITY_FAMILY_CLASS)
    suspend fun getCommunityFamilyClass(): ApiResponse<GroupCategoryList>


    /**
     * 社区群组api - 根据分类获取群组列表(/group/groupList.api)
     */
    @GET(COMMUNITY_FAMILY_LIST_BY_CLASS)
    suspend fun getCommunityFamilyListByClass(
        @Query("categoryId") categoryId: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<GroupListByClass>

    /**
     * 社区家族api - 【2.0】找家族(/group/findGroup.api)
     */
    @GET(COMMUNITY_FAMILY_FIND_GROUP)
    suspend fun getCommunityFamilyFindGroup(): ApiResponse<FindFamily>

    /**
     * 社区家族api - 2.0 添加家族分区（/group/addSection.api）
     */
    @POST(COMMUNITY_FAMILY_ADD_SECTION)
    suspend fun addCommunityFamilySection(@Body body: RequestBody): ApiResponse<CommonResult>

    /**
     * 社区家族api - 2.0 删除家族分区（/group/deleteSection.api）
     */
    @POST(COMMUNITY_FAMILY_DELETE_SECTION)
    suspend fun delCommunityFamilySection(@Body body: RequestBody): ApiResponse<CommonResult>

    /**
     * 社区家族api - 2.0 编辑家族分区（/group/editSection.api）
     */
    @POST(COMMUNITY_FAMILY_EDITOR_SECTION)
    suspend fun editorCommunityFamilySection(@Body body: RequestBody): ApiResponse<CommonResult>

    /**
     * 社区家族api - 【2.0】获取家族分区列表（/group/sectionList.api）
     */
    @GET(COMMUNITY_FAMILY_LIST_SECTION)
    suspend fun getCommunityFamilySectionList(@Query("groupId") groupId: Long): ApiResponse<GroupSectionList>

    @GET(COMMUNITY_CHECK_USER_IN_GROUP)
    suspend fun checkUserInGroup(@Query("groupId") groupId: Long): ApiResponse<CommonResult>

    /**
     * 我加入的群组 和 我管理的群组 列表
     */
    @GET(COMMUNITY_MY_FAMILY)
    suspend fun getCommunityMyFamily(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<MyGroupList>

    /**
     * 群组的热度排序列表
     */
    @GET(COMMUNITY_HOT_FAMILY)
    suspend fun getCommunityHotFamily(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<HotGroupList>

    /**
     * 家族详情
     */
    @GET(COMMUNITY_FAMILY_DETAIL)
    suspend fun getCommunityFamilyDetail(
        @Query("groupId") groupId: Long,
    ): ApiResponse<Group>

    /**
     * 用户可创建多少群组
     * GET ("/community/group/createGroupCount.api")
     */
    @GET(COMMUNITY_GROUP_CREATE_GROUP_COUNT)
    suspend fun getCommunityCreateGroupCount(): ApiResponse<GroupCreateGroupCount>

    /**
     * 创建群组
     * POST ("/community/group/createGroup.api")
     * form表单的post提交，参数用@Field，不用@Query
     *
     * groupImg	            String  群组封面
     * groupName	        String  群组名称
     * groupDes	            String  群组简介
     * primaryCategoryId	Number  群组一级分类ID
     * joinPermission	    Number  加入权限 （1-自由加入 2-需要审核）
     */
    @FormUrlEncoded
    @POST(COMMUNITY_GROUP_CREATE)
    suspend fun postCommunityCreateGroup(
        @Field("groupImg") groupImg: String,
        @Field("groupName") groupName: String,
        @Field("groupDes") groupDes: String,
        @Field("primaryCategoryId") primaryCategoryId: Long,
        @Field("joinPermission") joinPermission: Long,
    ): ApiResponse<CommonResult>

    /**
     * 修改群组
     * POST ("/community/group/edit.api")
     * form表单的post提交，参数用@Field，不用@Query
     *
     * groupId              Number  群组id
     * groupImg	            String  群组图片ID（上传图片API返回的fileID）
     * groupName	        String  群组名称
     * groupDes	            String  群组简介
     * joinPermission	    Number  加入权限 （1-自由加入 2-需要审核）
     */
    @FormUrlEncoded
    @POST(COMMUNITY_GROUP_EDIT)
    suspend fun postCommunityEditGroup(
        @Field("groupId") groupId: Long,
        @Field("groupImg") groupImg: String,
        @Field("groupName") groupName: String,
        @Field("groupDes") groupDes: String,
        @Field("joinPermission") joinPermission: Long,
    ): ApiResponse<CommonResult>

    /**
     * 设置家族发布权限
     * @param groupAuthority 权限选项值，必传：1-成员加入发帖及评论，2-所有人任意发帖评论，3-管理员任意发帖评论
     * @param groupId 家族id
     */
    @POST(COMMUNITY_SET_AUTHORITY)
    suspend fun postCommunitySetAuthority(@Body body: RequestBody): ApiResponse<CommonResult>


    /**
     * 群组管理员列表
     * GET ("/community/group/administratorList.api")
     *
     * groupId	Number  群组id
     */
    @GET(COMMUNITY_GROUP_ADMIN_LIST)
    suspend fun getCommunityGroupAdminList(
        @Query("groupId") groupId: Long,
    ): ApiResponse<GroupUserList>

    /**
     * 取消管理员
     * POST ("/community/group/unsetAdministrator.api")
     *
     * groupId	Number  群组id
     * userIds	String  被取消管理员的用户ID 多个","隔开
     */
    @POST(COMMUNITY_GROUP_UNSET_ADMIN)
    suspend fun postCommunityGroupUnsetAdmin(
        @Query("groupId") groupId: Long,
        @Query("userIds") userIds: String,
    ): ApiResponse<CommonResult>

    /**
     * 设置管理员
     * POST ("/community/group/setAdministrator.api")
     *
     * groupId	Number  群组id
     * userIds	String  被设置管理员的用户ID 多个","隔开
     */
    @POST(COMMUNITY_GROUP_SET_ADMIN)
    suspend fun postCommunityGroupSetAdmin(
        @Query("groupId") groupId: Long,
        @Query("userIds") userIds: String,
    ): ApiResponse<CommonResult>

    /**
     * 群组成员列表
     * GET ("/community/group/memberList.api")
     *
     * groupId	    Number  群组id
     * pageIndex	Number
     * pageSize	    Number
     */
    @GET(COMMUNITY_GROUP_MEMBER_LIST)
    suspend fun getCommunityGroupMemberList(
        @Query("groupId") groupId: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<GroupUserList>

    /**
     * 群组黑名单列表
     * GET ("/community/group/removedMemberList.api")
     *
     * groupId	    Number  群组id
     * pageIndex	Number
     * pageSize	    Number
     */
    @GET(COMMUNITY_GROUP_REMOVE_MEMBER_LIST)
    suspend fun getCommunityGroupRemoveMemberList(
        @Query("groupId") groupId: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<GroupUserList>

    /**
     * 群组申请列表
     * GET ("/community/group/applicantList.api")
     *
     * groupId	    Number  群组id
     * pageIndex	Number
     * pageSize	    Number
     */
    @GET(COMMUNITY_GROUP_APPLICANT_LIST)
    suspend fun getCommunityGroupApplicantList(
        @Query("groupId") groupId: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<GroupUserList>

    /**
     * 群组移除成员
     * POST ("/community/group/removeMember.api")
     *
     * groupId	Number  群组id
     * userIds	String  被设置管理员的用户ID 多个","隔开
     */
    @POST(COMMUNITY_GROUP_REMOVE_MEMBER)
    suspend fun postCommunityGroupRemoveMember(
        @Query("groupId") groupId: Long,
        @Query("userIds") userIds: String,
    ): ApiResponse<CommonResult>

    /**
     * 释放群组黑名单中的成员
     * POST ("/community/group/restoreMember.api")
     *
     * groupId	Number  群组id
     * userIds	String  被设置管理员的用户ID 多个","隔开
     */
    @POST(COMMUNITY_GROUP_RESTORE_MEMBER)
    suspend fun postCommunityGroupRestoreMember(
        @Query("groupId") groupId: Long,
        @Query("userIds") userIds: String,
    ): ApiResponse<CommonResult>

    /**
     * 批准用户加入群组
     * POST ("/community/group/addMember.api")
     *
     * groupId	Number  群组id
     * userIds	String  被设置管理员的用户ID 多个","隔开
     */
    @POST(COMMUNITY_GROUP_ADD_MEMBER)
    suspend fun postCommunityGroupAddMember(
        @Query("groupId") groupId: Long,
        @Query("userIds") userIds: String,
    ): ApiResponse<CommonResult>

    /**
     * 拒绝加入群组申请
     * POST ("/community/group/refuseMember.api")
     *
     * groupId	Number  群组id
     * userIds	String  被设置管理员的用户ID 多个","隔开
     */
    @POST(COMMUNITY_GROUP_REFUSE_MEMBER)
    suspend fun postCommunityGroupRefuseMember(
        @Query("groupId") groupId: Long,
        @Query("userIds") userIds: String,
    ): ApiResponse<CommonResult>

    /**
     * 群主管理员列表+最近活跃列表
     * GET ("/community/group/administratorAndActiveMemberList.api")
     *
     * groupId	    Number  群组id
     */
    @GET(COMMUNITY_GROUP_ADMIN_AND_ACTIVE_MEMBER_LIST)
    suspend fun getCommunityGroupAdminAndActiveMemberList(
        @Query("groupId") groupId: Long,
    ): ApiResponse<GroupAdminActiveMemberList>

    /**
     * 用于在成员列表/申请者列表/黑名单列表中根据用户昵称搜索成员。(名称精确查询)
     * GET ("/community/group/groupMemberListByNickName.api")
     *
     * groupId	    Number  群组id
     * nickName	    String
     * type	        Number  类型 -1:申请者 1：群主 2：管理员 3：普通成员 4：黑名单（1、2分别是群主和管理员，暂时没有昵称搜索功能）
     */
    @GET(COMMUNITY_GROUP_MEMBER_LIST_BY_NICK_NAME)
    suspend fun getCommunityGroupMemberListByNickName(
        @Query("groupId") groupId: Long,
        @Query("nickName") nickName: String,
        @Query("type") type: Long,
    ): ApiResponse<GroupUserList>

    /**
     * 加入群组
     *
     * status 1：加入群组成功（即加入了成员列表中） 2 加入群组失败 3 该用户此前已经加入成功了此群组 4 黑名单中的用户，即用户被移除到黑名单。5 审核中（正在加入中）（即加入了申请列表中，需要管理员审核才能进入成员列表中）
     */
    @POST(COMMUNITY_JOIN_GROUP)
    suspend fun joinGroup(@Query("groupId") groupId: Long): ApiResponse<CommonResult>

    /**
     * 退出群组
     */
    @POST(COMMUNITY_OUT_GROUP)
    suspend fun outGroup(@Query("groupId") groupId: Long): ApiResponse<CommonResult>

    /**
     * 我的未发布群组帖子记录
     */
    @POST(COMMUNITY_POST_USER_UNRELEASED)
    suspend fun getCommunityPostUserUnreleased(@Body body: RequestBody): ApiResponse<CommContentList>

    /**
     * 获取群组下已发布的帖子
     */
    @POST(COMMUNITY_POST_RELEASED)
    suspend fun getCommunityPostReleased(@Body body: RequestBody): ApiResponse<PostReleasedList>

    /**
     * 获取群组下已发布的帖子
     */
    @POST(COMMUNITY_POST_RELEASED_V2)
    suspend fun getCommunityPostReleasedV2(@Body body: RequestBody): ApiResponse<PostReleasedList>

    /**
     * 社区帖子api - 置顶帖子(/post/top.api)
     * POST(/community/post/top.api)
     * @param contentId     Number  内容id
     */
    @POST(COMMUNITY_POST_TOP)
    suspend fun postTop(@Query("contentId") contentId: Long): ApiResponse<CommBizCodeResult>

    /**
     *  社区帖子api - 取消置顶帖子(/post/cancel_top.api)
     * POST(/community/post/cancel_top.api)
     * @param contentId     Number  内容id
     */
    @POST(COMMUNITY_POST_CANCEL_TOP)
    suspend fun postCancelTop(@Query("contentId") contentId: Long): ApiResponse<CommBizCodeResult>

    /**
     * 社区帖子api - 加精帖子(/post/essence.api)
     * POST(/community/post/essence.api)
     * @param contentId     Number  内容id
     */
    @POST(COMMUNITY_POST_ESSENCE)
    suspend fun postEssence(@Query("contentId") contentId: Long): ApiResponse<CommBizCodeResult>

    /**
     * 社区帖子api - 取消加精帖子(/post/cancel_essence.api)
     * POST(/community/post/cancel_essence.api)
     * @param contentId     Number  内容id
     */
    @POST(COMMUNITY_POST_CANCEL_ESSENCE)
    suspend fun postCancelEssence(@Query("contentId") contentId: Long): ApiResponse<CommBizCodeResult>

    /**
     * 榜单分页查询
     * GET ("/community/top_list/query.api")
     *
     * type         Number  类型 1电影 2电视剧 3影人
     * pageIndex	Number  页码
     * pageSize	    Number  每页条数
     */
    @GET(COMMUNITY_TOPLIST_QUERY)
    suspend fun getTopListQuery(
        @Query("type") type: Long,
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<IndexTopListQuery>

    /**
     * 电影/影人榜单详情
     * GET ("/community/top_list/detail.api")
     *
     * id   Number  榜单ID
     */
    @GET(COMMUNITY_TOPLIST_DETAIL)
    suspend fun getTopListDetail(
        @Query("id") type: Long,
    ): ApiResponse<TopListInfo>

    /**
     * 游戏榜单详情页 - 排行榜
     * POST ("/richman/topUserList.api")
     *
     * rankType   Number  排行榜分类：1昨日道具狂人，2昨日衰人，3昨日交易达人，4昨日收藏大玩家，5金币大富翁，6套装组合狂
     */
    @POST(RICHMAN_TOP_USER_LIST)
    suspend fun getRichmanTopUserList(
        @Query("rankType") type: Long,
    ): ApiResponse<GameTopList>

    /**********************************社区回复api-start******************************/

    /**
     * 回复api - 保存回复(/reply.api)
     * POST (/community/reply.api)
     * replyId      Number          回复id
     * objType      Number          评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"),
     *                              ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院");
     * commentId    Number          评论id
     * reReplyId    Number          被回复回复Id
     * images       Array           图片集合
     *      imageId     String      图片ID 必填
     *      imageUrl    String      图片URL
     *      imageFormat String      图片格式 例：gif,jpg,png
     *      imageDesc   String      图片描述
     * body         String          富文本正文
     *
     */
    @POST(COMMUNITY_SAVE_REPLY)
    suspend fun saveReply(@Body body: RequestBody): ApiResponse<SaveReplyResult>

    /**
     * 回复api - 分页查询已发布回复(/replies/released.api)
     * POST (/community/replies/released.api)
     * objType      Number      评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"),
     *                          FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院");
     * commentId    Number      评论id
     * pageIndex    Number
     * pageSize     Number
     */
    @POST(COMMUNITY_REPLY_RELEASED)
    suspend fun getReleasedReplyList(@Body body: RequestBody): ApiResponse<ReplyList>

    /**
     * 回复api - 分页查询我的未发布回复(/replies/user_unreleased.api)
     * POST (/community/replies/released.api)
     * objType      Number      评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"),
     *                          FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院");
     * commentId    Number      评论id
     * pageIndex    Number
     * pageSize     Number
     */
    @POST(COMMUNITY_REPLY_UNRELEASED)
    suspend fun getUnReleasedReplyList(@Body body: RequestBody): ApiResponse<ReplyList>


    /**********************************社区回复api-end******************************/

    /**********************************videoController-start******************************/
    /**
     * 获取视频播放地址
     * GET (/video/play_url)
     * video_id     Number      视频id
     * source       Number      视频类型 1-电影预告片 2-自媒体  3-媒资
     * scheme       String      地址协议前缀
     */
    @GET(VIDEO_GET_PLAY_URL)
    suspend fun getPlayUrl(
        @Query("video_id") videoId: Long,
        @Query("source") source: Long,
        @Query("scheme") scheme: String,
    ): ApiResponse<VideoPlayList>
    /**********************************videoController-end******************************/

    /**
     * 获取分享信息(/utility/share.api)
     *
     * type 1	文章详情页 2	片单详情页 3	视频详情页 4	家族详情页 5	帖子详情页 6	长影评页面 7 短影评页面 8	日志详情页 9	相册详情页 10	卡片大富翁 11	影人详情页 12	影片资料页 13片单活动
     * relateId 分享对象的ID，用于获取该对象的相关内容
     * secondRelateId 分享对象ID2（用于需要多个ID才能获取到分享内容的情况）
     */
    @GET(COMMON_SHARE)
    suspend fun getShareInfo(
        @Query("type") type: Long,
        @Query("relateId") relateId: Long? = null,
        @Query("secondRelateId") secondRelateId: Long? = null,
    ): ApiResponse<CommonShare>
    /***************相册api start******************/

    /**
     * 相册api - 修改相册（/user_album/update）
     * POST（/community/user_album/update）
     * @param id    Number      相册id
     * @param name  String      相册名
     * @param description   String  相册描述
     * {
     *   "name": "P",
     *   "description": "vB",
     *  "id": 5080,
     *  "userId": 6434
     *  }
     */
    @POST(COMMUNITY_UPDATE_ALBUM)
    suspend fun upateAlbum(@Body body: RequestBody): ApiResponse<AlbumUpdate>

    /**
     * 相册api - 分页获取相册中的图片（/user_image/list_by_page）
     * POST(/community/user_image/list_by_page)
     * @param userId    Number  用户Id（当前登陆的用户Id、好友的用户id 等...），必填
     * @param albumId   Number  相册id，必填
     * @param   page    Object
     *          @param  pageIndex   Number
     *          @param  pageSize    Number
     * @param   ShieldStatus    Number      屏蔽该用户所有图片 0公开,1屏蔽,2查询所有，必填
     * @param   status  Number  状态：1正常 3已删除 4待审核，不传值：查询所有数据
     *  {
     *       "albumId": 5245,
     *       "ShieldStatus": 497,
     *      "page": {
     *     "pageIndex": 7861,
     *    "pageSize": 180
     *   },
     *  "userId": 605,
     *   "status": [
     *   5348
     *   ]
     *   }
     */
    @POST(COMMUNITY_ALBUM_IMAGE_LIST_BY_PAGE)
    suspend fun getImageListInAlbum(@Body body: RequestBody): ApiResponse<ImageListInAlbum>

    /**
     * 相册api - 分页获取相册信息（/community/user_album/page_albums.api）
     * GET(/community/user_album/page_albums.api)
     * @param userId    Number  用户Id（当前登陆的用户Id、好友的用户id 等...），必填
     *          @param  nextStamp   String 下一页标识，默认或第一页可为null
     *          @param  pageSize    Number
     */
    @GET(COMMUNITY_ALBUM_LIST_BY_PAGE)
    suspend fun getAlbumList(
        @Query("userId") userId: Long,
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long
    ): ApiResponse<AlbumList>

    /**
     * 相册api - 创建相册（/user_album/save）
     * POST(/community/user_album/save)
     * @param name  String  相册名 必填
     * @param description   String  相册描述 可选
     * @param userId    Number  用户id
     *{
    "name": "LXe",
    "description": "mjGuLurEVc",
    "userId": 6414
    }
     */
    @POST(COMMUNITY_CREATE_ALBUM)
    suspend fun createAlbum(@Body body: RequestBody): ApiResponse<AlbumCreate>

    /**
     * 相册api - 保存图片（/user_image/save）
     * POST(/community/user_image/save)
     * @param albumId   Number  相册id
     * @param   saveImages  Array   图片集合
     *          @param fileId   String  图片id
     *          @param  fileSize    Number  文件大小
     *          @param  width       Number  图片宽度
     *          @param  height      Number  图片高度
     *          @param  userId      Number
     *{
    "albumId": 2524,
    "saveImages": [
    {
    "fileSize": 540,
    "width": 8410,
    "userId": 14,
    "fileId": "eXaNjyJKWG",
    "height": 9344
    }
    ]
    }
     */
    @POST(COMMUNITY_SAVE_IMAGE)
    suspend fun saveAlbumImage(@Body body: RequestBody): ApiResponse<SaveImageListInAlbum>

    /**
     * 相册api - 删除图片（/user_image/delete）
     * POST(/community/user_image/delete)
     * @param   albumId     Number      相册id
     * @param   imageId     Number      图片id
     * @param   userId      Number      用户id
     *
     * {
    "imageId": 9163,
    "albumId": 9584,
    "userId": 392
    }

     */
    @POST(COMMUNITY_DELETE_IMAGE)
    suspend fun deleteAlbumImage(@Body body: RequestBody): ApiResponse<AlbumUpdate>


    /**
     * 相册api - 删除相册（/user_album/delete）
     * POST(/community/user_album/delete)
     * @param   id      Number  相册id
     * @param   userId  Number  用户id
     * {
    "id": 7998
    }
     */
    @POST(COMMUNITY_DELETE_ALBUM)
    suspend fun deleteAlbum(@Body body: RequestBody): ApiResponse<AlbumUpdate>

    /**
     * 相册api - 根据相册id获取相册信息（/user_album/get_by_id）
     * GET(/community/user_album/get_by_id)
     *  albumId   Number   相册id
     *  shieldStatus    Number 屏蔽该用户所有图片 0公开,1屏蔽；可选参数，不传值查询所有
     *  status          Array   状态：1已审核 3已删除 4待审核；可选参数，不传值查询所有
     * {
    "albumId": 9519,
    "shieldStatus": 2143,
    "status": [
    9257
    ]
    }
     */
    @POST(COMMUNITY_GET_ALBUM_INFO_BY_ID)
    suspend fun getAlbumDetail(@Body body: RequestBody): ApiResponse<AlbumInfo>


    /***************相册api end******************/

    /**
     * 上传图片
     * POST(/image/upload)
     * @param   url     String  http://front-gateway.mtime.cn/common/startup/load.api?isUpgrade=true接口返回上传图片路径 VariateExt.imgUploadUrl
     * @param  file Object  文件对象
     * @param   imageType   Number  图片文件分类枚举：200起 参考枚举对象 ImageFileType
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
     *            post中不写路径用@url添加上传图片全路径
     */
    @Multipart
    @POST("")
    suspend fun imageUpload(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @Query("imageType") @UploadImageType imageType: Long,
    ): ApiResponse<PhotoInfo>


    /**
     *  AccountController - 用户统计信息(/account/statisticsInfo.api)
     *  GET(/user/account/statisticsInfo.api)
     *  @param locationId    Number  地域ID。默认290北京
     */
    @GET(ACCOUNT_STATISTIC_INFO)
    suspend fun getAccountStatisticInfo(@Query("locationId") locationId: Long): ApiResponse<AccountStatisticsInfo>


    /**
     * AccountController - 用户详情（/account/detail.api）
     * GET(/user/account/detail.api)
     * @param   param   Number  默认0；当前端传入param=1：代表【跳过缓存获取用户信息】，用于【用户刚刚绑定手机号后，通过这个参数让后端跳过缓存，重新获取用户信息，及时得到刚绑定的手机号】。为确保性能，其他情况请勿传入param参数
     * @param   locationId  Number  选填  地域ID,默认290北京(地区热映数据需要此字段)
     */
    @GET(ACCOUNT_DETAIL)
    suspend fun getAccountDetail(
        @Query("param") param: Long,
        @Query("locationId") locationId: Long? = null,
    ): ApiResponse<User>

    /**
     * 用户活动列表
     * @param nextStamp String  下一页标识，默认或第一页可为null
     * @param pageSize    Number  每页页面数，默认10
     */
    @GET(USER_ACTIVITY_LIST)
    suspend fun getUserActivityList(
        @Query("nextStamp") nextStamp: String? = null,
        @Query("pageSize") pageSize: Long? = null,
    ): ApiResponse<ActivityList>

    /**
     * UserController - 更新头像(/user/avatar/edit.api)
     * POST(/user/user/avatar/edit.api)
     *
     * fileName	String  图片上传到时光网后的得到的文件ID 必须参数
     */
    @POST(AVATAR_EDIT)
    @FormUrlEncoded
    suspend fun postAvatarEdit(
        @Field("fileName") fileName: String,
    ): ApiResponse<AvatarEdit>

    /**
     * UserController - 更新背景(/user/user/background/edit.api)
     * POST(user/user/background/edit.api)
     *
     * fileName	String  图片上传到时光网后的得到的文件ID 必须参数
     */
    @POST(USER_CENTER_BG_EDIT)
    suspend fun postUserCenterEdit(
        @Body param: RequestBody
    ): ApiResponse<UserCenterBgEdit>


    /**
     * UserController - 查询认证影评人是否符合条件(/user/auth/mtime/permission.api)
     * POST(/user/user/auth/mtime/permission.api)
     */
    @POST(AUTH_CHECK_PERMISSION)
    suspend fun checkAuthPermission(): ApiResponse<CheckAuthPermission>

    /**
     * UserController - 获取电影认证人角色列表(/user/auth/mtime/role.api)
     * GET（/user/user/auth/mtime/role.api）
     */
    @GET(GET_AUTH_ROLE)
    suspend fun getAuthRoleList(): ApiResponse<AuthRoleList>

    /**
     * UserController - 时光媒体人认证(/user/auth/mtime/save.api)
     * POST(/user/user/auth/mtime/save.api)
     * @param authtype  Number  1.个人 2.影评人 3.电影人    4.机构
     * @param   name    String  真实姓名
     * @param   mobile  String  手机号
     * @param   email   String  邮箱
     * @param   idcard  String  身份证
     * @param   idcarddata  Object  身份证附件（图片）
     * @param   workcarddata    Object  工作证明附件（图片）
     * @param   authletterdata  Object  机构附件（图片）
     * @param   businessData    Object  营业执照（图片）
     * @param   filmcommentlistStr  String   长影评ID集合，以英文逗号分隔
     * @param   tags    String  作品，多个使用|分隔
     * @param   authrolelistStr    String    认证角色集合，以英文逗号分隔
     */

    @Multipart
    @POST(AUTH_SAVE)
    suspend fun saveAuth(
        @Query("authtype") authtype: Long,
        @Query("name") name: String,
        @Query("mobile") mobile: String,
        @Query("email") email: String,
        @Query("idcard") idcard: String,
        @Query("filmcommentlistStr") filmcommentlistStr: String,
        @Query("tags") tags: String,
        @Query("authrolelistStr") authrolelistStr: String,
        @Part idcarddata: MultipartBody.Part?,
        @Part workcarddata: MultipartBody.Part?,
        @Part authletterdata: MultipartBody.Part?,
        @Part businessData: MultipartBody.Part?,
    ): ApiResponse<CheckAuthPermission>

    /**
     * OrderController - 根据token查询用户订单列表（三个月内）(/order/blendOrders)
     * GET(/ticket/order/order/blendOrders.api)
     */
    @GET(BLEND_ORDERS)
    suspend fun getOrderInThreeMonth(): ApiResponse<BlendOrders>

    /**
     * OrderController - 获取三个月之前的数据--默认每页10条(/order/userHistoryOrders.api）
     * POST(/ticket/order/order/userHistoryOrders.api)
     *  @param pageIndex    Number  页码
     *  @param  platformId  Number  第三方平台(0:时光，1第三方)
     *  @param  orderType   String  1表示在线选座订单、2表示电子券订单，默认为1
     *      {
    "orderType": "CxI",
    "pageIndex": 2016,
    "platformId": 4644
    }
     *
     */
    @FormUrlEncoded
    @POST(HISTORY_ORDERS)
    suspend fun getOrdersOutThreeMonth(
        @Field("orderType") orderType: String,
        @Field("pageIndex") pageIndex: Long,
        @Field("platformId") platformId: Long,
    ): ApiResponse<BlendOrders>

    /************************************************* 钱包 api start**************************************************/
    /**
     * 个人-钱包API： 个人账户优惠券列表(/ticket/market/voucher/MyVouchers.api)
     * GET （/ticket/market/voucher/MyVouchers.api）
     * @param pageIndex     Number      分页index
     * @param pageSize      Number      分页size
     */
    @GET(WALLET_COUPON_LIST)
    suspend fun getCoupons(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
        @Query("type") type: Long,
    ): ApiResponse<CouponList>

    /**
     * 个人-钱包API： 个人账户礼品卡列表(/ticket/market/card/MTimeCardVaildList.api)
     * GET （/ticket/market/card/GetAccountMembershipCardList.api）
     * @param pageIndex     Number      分页index
     * @param pageSize      Number      分页size
     */
    @GET(WALLET_GIFT_CARD_LIST)
    suspend fun getGiftCards(
        @Query("pageIndex") pageIndex: Long,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<GiftCardList>

    /**
     * voucherCode	String 兑换码
     * vcode	    String 验证码
     * vcodeId	    String 验证码Id
     */
    @POST(WALLET_COUPON_ADD)
    suspend fun addCoupon(@Body requestBody: RequestBody): ApiResponse<AddCoupoBean>

    /**
     *
     * cardNum	String 电影卡卡号
     * password	String 号密码
     * vcode	String 图形验证码，默认为空字符串
     * vcodeId	String 验证码Id，默认为空字符串
     * orderId	String 订单id
     */
    @POST(WALLET_GIFT_CARD_ADD)
    suspend fun addGiftCard(@Body requestBody: RequestBody): ApiResponse<AddGiftCardBean>

    /**
     * 会员中心主页
     * GET(/user/member/center/home.api)
     */
    @GET(MEMBER_CENTER_HOME)
    suspend fun getMemberCenterHome(): ApiResponse<MemberHome>

    /**
     * 会员中心获取抽奖配置列表
     * GET(/user/member/exchange/list.api)
     */
    @GET(MEMBER_EXCHANGE_LIST)
    suspend fun getMemberLuckList(): ApiResponse<MemberExchangeList>


    /**
     *时光币兑换奖品
     * POST(user/member/exchange.api)
     * @param configId  Number  兑换奖品id
     * @param mtimebQuantity    Number  兑换奖品M豆需要的数量
     */
    @POST(MEMBER_EXCHANGE)
    suspend fun exchangeGoods(
        @Query("configId") configId: Long,
        @Query("mtimebQuantity") mtimebQuantity: Long,
    ): ApiResponse<ExchangeResult>

    /**
     * 下载恩建
     */
    @Streaming//用来下载大文件
    @GET()
    suspend fun downloadFile(@Url fileUrl: String): ResponseBody

    /**
     * 热门点击搜索
     * POST（/mtime-search/search/poplarClick）
     * @param pType  Number  影片 1 、  影人 2、  文章 3
     * @param   sType   Number  影片 0 、 电视剧 1 、 文章、影人 -1
     * @param   keyword String  影片id 、影人id、文章标题
     */
    @FormUrlEncoded
    @POST(POPLAR_CLICK)
    suspend fun poplarClick(
        @Field("pType") pType: Int,
        @Field("sType") sType: Int,
        @Field("keyword") keyword: String,
    ): ApiResponse<Any>

    /**
     * 弹出彩蛋, 根据用户ID或业务类型，判断今天、该业务是否还可以弹出彩蛋
     * GET(/user/member/popupBonusScene.api)
     * @param action    Number  1,用户当日第一次分享成功时;
     *                          2,用户当日第一次成功发布长短影评;
     *                          3,用户当日第一次成功发布帖子;
     *                          4,用户当日第一次加入新家族时;
     *                          5,用户当日第一次成功创建家族时;
     *                          6,用户当日第一次成功合成卡片大富翁套装时;
     *                          7,用户当日第一次购票且支付成功时
     */
    @GET(POPUP_BONUS_SCENE)
    suspend fun popupBonusScene(@Query("action") action: Long): ApiResponse<PopupBonusScene>


    /**
     * 打开彩蛋
     * POST(/user/member/bonusScene.api)
     * @param action    Number  1,用户当日第一次分享成功时;
     *                          2,用户当日第一次成功发布长短影评;
     *                          3,用户当日第一次成功发布帖子;
     *                          4,用户当日第一次加入新家族时;
     *                          5,用户当日第一次成功创建家族时;
     *                          6,用户当日第一次成功合成卡片大富翁套装时;
     *                          7,用户当日第一次购票且支付成功时
     */
    @POST(BONUS_SCENE)
    suspend fun bondunsScene(@Query("action") action: Long): ApiResponse<BonusScene>

    /**
     * 扫码登陆 - 进行扫码登陆
     */
    @POST(QRCODE_LOGIN)
    suspend fun qrcodeLogin(@Query("uuid") uuid: String): ApiResponse<CommBizCodeResult>

    /**
     * VerifyLoginController - 极光APP三方一键登录（/verify_jpush_login.api）
     * POST [J_VERIFY_LOGIN]
     *
     * loginToken	String  认证SDK获取到的loginToken
     */
    @POST(J_VERIFY_LOGIN)
    suspend fun postJVerifyLogin(@Query("loginToken") loginToken: String): ApiResponse<Login>

    /**
     * 获取电影评分详情(/movie/movieRatingDetail.api)
     */
    @GET(MOVIE_RATING_DETAIL)
    suspend fun getMovieRatingDetail(@Query("movieId") movieId: Long): ApiResponse<RatingDetail>


    /**
     * 导播台获取
     * GET(/live/rtc_live_player/floating_layers.api)
     * @param   liveId  Long    直播id
     */
    @GET(LIVE_DIRECTOR_UNITS)
    suspend fun getDirectorUnits(@Query("liveId") liveId: Long): ApiResponse<DirectorUnits>

    /**
     * 创作者中心 - 任务中心
     * userId	Number【选填】用户id（此id可以不传，不传的话则返回当前登录用户的数据）
     * type	Number【必填】调用类型 0:APP,1:pc 默认APP调用
     */
    @GET(CREATOR_TASK_API)
    suspend fun getCreatorTaskInfo(): ApiResponse<CreatorTaskEntity>

    /**
     * 创作者中心 - 权益说明
     */
    @GET(CREATOR_REWARD_API)
    suspend fun getCreatorReward(): ApiResponse<RewardEntity>

    /**
     * 内容推荐api - 【2.0】TA说推荐列表
     * @param nextStamp 分页标识，第一页可不传
     * @param pageSize 每页记录数，默认10
     */
    @GET(COMMUNITY_RCMD_TA_SHUO)
    suspend fun getCommunityRcmdTaShuoList(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<TaShuoRcmdList>

    /**
     * 内容推荐api - 【2.0】原创内容推荐列表
     * @param rcmdTagsFilter 推荐标签，逗号分隔，必传：1-电影，2-电视，3-音乐，4-人物，5-产业，6-全球拾趣，7-时光对话，8-时光策划，9-时光快讯，10-超级英雄，11-吐槽大会，12-时光大赏，13-精选，101-华语，102-欧美，103-日韩，104-其他
     * @param nextStamp 分页标识，第一页可不传
     * @param pageSize 每页记录数，默认10
     */
    @GET(COMMUNITY_RCMD_ORIGINAL)
    suspend fun getCommunityRcmdOriginalList(
        @Query("rcmdTagsFilter") rcmdTagsFilter: String,
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<OriginalRcmdContentList>

    /**
     * 内容推荐api - 【2.0】种草推荐列表
     * @param subTypeId 子分类Id，必传
     * @param nextStamp 分页标识，第一页可不传
     * @param pageSize 每页记录数，默认10
     */
    @GET(COMMUNITY_RCMD_ZHONG_CAO)
    suspend fun getCommunityRcmdZhongCaolList(
        @Query("subTypeId") subTypeId: Long,
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<ZhongCaoRcmdData>

    /**
     * 内容推荐api - 【2.0】App首页推荐列表
     * @param nextStamp 分页标识，第一页可不传
     * @param pageSize 每页记录数，默认10
     */
    @GET(COMMUNITY_RCMD_APP_INDEX)
    suspend fun getCommunityRcmdAppIndexlList(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long,
    ): ApiResponse<HomeRcmdContentList>

    /**
     * 内容推荐api - 【2.0】按推荐类型获取子分类
     * @param rcmdType 推荐类型：7种草内容推荐，9找家族推荐，10片单推荐
     */
    @GET(COMMUNITY_RCMD_SUB_TYPES)
    suspend fun getCommunityRcmdSubtypes(
        @Query("rcmdType") rcmdType: Long
    ): ApiResponse<CommSubTypeList>

    @GET(GET_POPUP_BONUS_SCENE)
    suspend fun checkPopupBonus(
        @Query("action") action: Long,
    ): ApiResponse<PopupBonusScene>
}
