package com.kotlin.android.app.router.path

import com.kotlin.android.router.annotation.LoginPath
import com.kotlin.android.router.annotation.RouteGroup
import com.kotlin.android.router.annotation.RoutePath

/**
 * 创建者: zl
 * 创建时间: 2020/6/3 4:24 PM
 * 描述: 用于组件开发中，ARouter单Activity跳转的统一路径注册
 * 在这里注册添加路由路径，需要写清楚注释，标明功能界面
 * 页面path路径至少两级,第一级目录代表group
 */
@RoutePath
class RouterActivityPath {
    /**
     * 宿主组件
     */
    @RouteGroup
    object Main {
        /**组件根路径*/
        const val MAIN = "/main"

        /**引导页面*/
        const val PAGER_SPLASH = "$MAIN/SplashActivity"

        /**主界面*/
        const val PAGER_MAIN = "$MAIN/MainTabActivity"

        /**城市切换页*/
        const val PAGER_CITY_CHANGE = "$MAIN/CityChangeActivity"
        const val PAGER_LOCAL_SELECT = "$MAIN/LocationSelectActivity"

        /**图片详情页面*/
        const val PAGE_PHOTO_DETAIL = "$MAIN/PhotoDetailActivity"

        /**影人详情页*/
        const val PAGE_ACTORVIEW = "$MAIN/ActorViewActivity"

        /**影院详情*/
        const val PAGE_CINEMAVIEW = "$MAIN/CinemaViewActivity"

        /**订单详情*/
        const val PAGE_ORDER_DETAIL = "$MAIN/OrderDetailActivity"

        /**订单支确认页*/
        @LoginPath
        const val PAGER_ORDER_CONFIRM_ACTIVITY = "$MAIN/OrderConfirmActivity"

        /**订单支付页*/
        @LoginPath
        const val PAGER_ORDER_PAY_ACTIVITY = "$MAIN/OrderPayActivity"
    }

//    @RouteGroup
//    object MainKt {
//
//        /**组件根路径*/
//        const val MAIN_KT = "/mainKt"
//
//        /**订单支确认页*/
//        @LoginPath
//        const val PAGER_ORDER_CONFIRM_ACTIVITY = "$MAIN_KT/OrderConfirmActivity"
//
//        /**订单支付页*/
//        @LoginPath
//        const val PAGER_ORDER_PAY_ACTIVITY = "$MAIN_KT/OrderPayActivity"
//
//    }

    /**
     * 启动页
     */
    @RouteGroup
    object Splash {
        /**组件根路径*/
        const val SPLASH = "/splash"

        /**启动界面*/
        const val PAGER_SPLASH_ACTIVITY = "$SPLASH/splash"
    }

    /**
     * 首页组件
     * */
    @RouteGroup
    object Home {
        /**组件根路径*/
        const val HOME = "/home"

        /**首页主界面*/
        const val PAGER_HOME_MAIN_ACTIVITY = "$HOME/main"

        /**榜单页*/
        const val PAGER_TOPLIST_ACTIVITY = "$HOME/TopListActivity"

        /**电影榜单详情页*/
        const val PAGER_TOPLIST_DETAIL_ACTIVITY = "$HOME/ToplistDetailActivity"

        /**游戏榜单详情页*/
        const val PAGER_TOPLIST_GAME_DETAIL_ACTIVITY = "$HOME/ToplistGameDetailActivity"

        /**游戏榜单列表页*/
        const val PAGER_TOPLIST_GAME_ACTIVITY = "$HOME/ToplistGameActivity"

        /**游戏榜单列表页*/
        const val PAGER_FIND_MOVIE_ACTIVITY = "$HOME/FindMovieActivity"
    }

    @RouteGroup
    object Film {
        /**组件根路径*/
        const val FILM = "/film"

        /**座位图主界面*/
        const val PAGER_SEAT = "$FILM/SeatActivity"

    }

    /**
     * 社区组件
     */
    @RouteGroup
    object Community {
        /**组件根路径*/
        const val COMMUNITY = "/community"

        /**社区个人主页*/
        const val PAGER_PERSON = "$COMMUNITY/CommunityPersonActivity"

        /***
         * 我的好友页面
         */
        @LoginPath
        const val PAGER_FRIEND = "$COMMUNITY/CommunityMyFriendActivity"

        /**
         * 想看、看过页面
         */
        @LoginPath
        const val PAGE_WANT_SEE = "$COMMUNITY/MyWantSeeActivity"
        /***
         * 我的好友页面
         */
        @LoginPath
        const val PAGER_TIME_LINE = "$COMMUNITY/CommunityTimeLineActivity"

        /**社区个人收藏*/
        @LoginPath
        const val PAGER_PERSON_COLLECTION = "$COMMUNITY/CommunityPersonCollectionActivity"
    }

    /**
     * 社区家族组件
     */
    @RouteGroup
    object CommunityFamily {
        /**组件根路径*/
        const val COMMUNITY_FAMILY = "/CommunityFamily"

        /**家族详情页面*/
        const val PAGER_FAMILY_DETAIL = "$COMMUNITY_FAMILY/FamilyDetail"

        /**家族分类列表页面*/
        const val PAGER_FAMILY_CLASS_LIST = "$COMMUNITY_FAMILY/FamilyClassList"

        /**创建|管理家族页*/
        @LoginPath
        const val PAGER_FAMILY_CREATE = "$COMMUNITY_FAMILY/FamilyCreateActivity"

        /**编辑家族名称/简介页*/
        const val PAGER_FAMILY_EDIT_INFO = "$COMMUNITY_FAMILY/FamilyEditInfoActivity"

        /**家族加入权限页*/
        const val PAGER_FAMILY_PERMISSION = "$COMMUNITY_FAMILY/FamilyPermissionActivity"

        /**家族分区管理页*/
        const val PAGER_FAMILY_SECTION_MANAGER = "$COMMUNITY_FAMILY/FamilySectionManagerActivity"

        /**家族群组分类页*/
        const val PAGER_FAMILY_CATEGORY = "$COMMUNITY_FAMILY/FamilyCategoryActivity"

        /**家族管理员页*/
        @LoginPath
        const val PAGER_FAMILY_ADMIN = "$COMMUNITY_FAMILY/FamilyAdminActivity"

        /**添加家族管理员页*/
        @LoginPath
        const val PAGER_FAMILY_ADD_ADMIN = "$COMMUNITY_FAMILY/FamilyAddAdminActivity"

        /**家族成员列表页*/
        const val PAGER_FAMILY_MEMBER = "$COMMUNITY_FAMILY/FamilyMemberActivity"

        /**找家族页面*/
        const val PAGER_FAMILY_FIND = "$COMMUNITY_FAMILY/FindFamilyActivity"

        /**家族成员管理页*/
        @LoginPath
        const val PAGER_FAMILY_MEMBER_MANAGE = "$COMMUNITY_FAMILY/FamilyMemberManageActivity"
    }

    /**
     * 卡片大富翁组件
     */
    @RouteGroup
    object CardMonopoly {
        /**组件根路径*/
        const val CARD_MONOPOLY = "/game"

        /**卡片大富翁主页面*/
        @LoginPath
        const val PAGER_CARD_MAIN = "$CARD_MONOPOLY/CardMain"

        /**卡片大富翁机器人*/
        const val PAGER_CARD_ROBOT = "$CARD_MONOPOLY/CardRobot"

        /**卡片套装*/
        const val PAGER_SUIT = "$CARD_MONOPOLY/Suit"

        /**卡片套装选择*/
        const val PAGER_SUIT_SELECTED = "$CARD_MONOPOLY/SuitSelected"

        /**卡片套装详情*/
        const val PAGER_SUIT_DETAIL = "$CARD_MONOPOLY/SuitDetail"

        /**我的道具卡*/
        const val PAGER_PROP = "$CARD_MONOPOLY/Prop"

        /**卡片商店*/
        const val PAGER_STORE = "$CARD_MONOPOLY/Store"

        /**许愿树*/
        const val PAGER_WISHING = "$CARD_MONOPOLY/Wishing"

        /**拍卖行*/
        const val PAGER_AUCTION = "$CARD_MONOPOLY/Auction"

        /**交易记录*/
        const val PAGER_DEAL_RECORDS = "$CARD_MONOPOLY/DealRecords"

        /**卡片大富翁我的卡友*/
        const val PAGER_CARD_FRIEND = "$CARD_MONOPOLY/CardFriend"

        /**卡片大富翁限量套装排行*/
        const val PAGER_SUIT_RANK = "$CARD_MONOPOLY/SuitRank"

        /**卡片大富翁游戏介绍*/
        const val PAGER_GAME_GUIDE = "$CARD_MONOPOLY/GameSuggest"

        /**卡片大富翁查看大图*/
        const val PAGER_IMAGE_DETAIL = "$CARD_MONOPOLY/ImgDetail"

        /**卡片大富翁留言板*/
        const val PAGER_MESSAGE_BOARD = "$CARD_MONOPOLY/MessageBoard"

        /**卡片大富翁评论列表*/
        const val PAGER_SUIT_COMMENT = "$CARD_MONOPOLY/Comment"
    }

    /**
     * 旧的用户组件
     */
    @RouteGroup
    object AppUser {
        /**组件根路径*/
        const val APP_USER = "/appUser"

        /**登录界面*/
        const val PAGER_LOGIN = "$APP_USER/Login"

        /**个人资料页*/
        @LoginPath
        const val PAGE_PROFILE = "$APP_USER/ProfileActivity"

        /**关于我们*/
        const val PAGE_ABOUT = "$APP_USER/AboutActivity"

        /**设置*/
        const val PAGE_SETTING = "$APP_USER/SettingActivity"

        /**扫一扫*/
        const val PAGE_CAPTURE = "$APP_USER/CaptureActivity"

        /**抽奖页面*/
        const val PAGE_COMMON_WEBACTIVITY = "$APP_USER/CommonWebActivity"
    }

    /**
     * User组件
     */
    @RouteGroup
    object User {
        /**组件根路径*/
        const val USER = "/user"

//        /**登录界面*/
//        const val PAGER_LOGIN = "$USER/Login"
//
//        /**是否登录*/
//        const val IS_LOGIN = "$USER/isLogin"
    }

    /**
     * 新的用户组件
     */
    @RouteGroup
    object Mine {
        /**组件根路径*/
        const val MINE = "/mine"

        /**身份认证页面*/
        @LoginPath
        const val PAGE_AUTHEN_ACTIVITY = "$MINE/AuthenticationActivity"

        /**我的收藏*/
        @LoginPath
        const val PAGE_MY_COLLECTION_ACTIVITY = "$MINE/MyCollectionActivity"

        /**我的内容*/
        @LoginPath
        const val PAGE_MY_CONTENTS_ACTIVITY = "$MINE/MyContentsActivity"

        /**草稿箱*/
        @LoginPath
        const val PAGE_MY_DRAFTS_ACTIVITY = "$MINE/MyDraftsActivity"

        /**我的勋章*/
        @LoginPath
        const val PAGE_MY_MEDAL_ACTIVITY = "$MINE/MyMedalActivity"

        /**设置*/
        @LoginPath
        const val PAGE_SETTING_ACTIVITY = "$MINE/SettingActivity"

        /**个人资料*/
        @LoginPath
        const val PAGE_PERSONAL_DATA_ACTIVITY = "$MINE/PersonalDataActivity"

        /**修改昵称*/
        @LoginPath
        const val PAGE_MODIFY_NICKNAME_ACTIVITY = "$MINE/NickNameInputActivity"

        /**编辑签名*/
        @LoginPath
        const val PAGE_EDIT_SIGN_ACTIVITY = "$MINE/EditSignInputActivity"

        /**账号设置*/
        @LoginPath
        const val PAGE_ACCOUNT_SETTING_ACTIVITY = "$MINE/AccountSettingActivity"

        /**第三方账号绑定*/
        @LoginPath
        const val PAGE_THIRD_ACCOUNT_BIND_ACTIVITY = "$MINE/ThirdAccountActivity"

        /**推送设置*/
        @LoginPath
        const val PAGE_PUSH_SETTING_ACTIVITY = "$MINE/PushSettingActivity"

        /**屏蔽设置*/
        @LoginPath
        const val PAGE_SHIELDING_SETTING_ACTIVITY = "$MINE/ShieldingSettingActivity"

        /**会员中心*/
        @LoginPath
        const val PAGE_MEMBER_CENTER_ACTIVITY = "$MINE/MemberCenterActivity"

        /**钱包*/
        @LoginPath
        const val PAGE_MY_WALLET = "$MINE/MyWalletActivity"

        /**券使用记录*/
        const val PAGE_MY_COUPON_USED_RECORD = "$MINE/CouponUsedRecordActivity"

        /**证照信息*/
        const val PAGE_LICENSE = "$MINE/LicenseActivity"

        /**10.0新版消息中心*/ //TODO 暂时注释登录
//        @LoginPath
        const val PAGE_MESSAGE_CENTER_10 = "$MINE/MessageCenter10"

        /**创作中心*/
        @LoginPath
        const val PAGE_CREAT_CENTER = "$MINE/CreatCenterActivity"

        /**任务中心*/
        @LoginPath
        const val PAGE_CREATOR_ACTIVITY = "$MINE/CreatorTaskActivity"

        /**权益说明*/
        const val PAGE_CREATOR_REWARD_ACTIVITY = "$MINE/RewardActivity"

        /**活动列表页*/
        const val PAGE_ACTIVITY_LIST = "$MINE/ActivityListActivity"

        /**数据中心*/
        @LoginPath
        const val PAGE_DATA_CENTER = "$MINE/DataCenterActivity"

        /**单篇分析详情*/
        const val PAGE_ANALYS_DETAIL = "$MINE/SingleAnalysDetailActivity"
    }

    /**今日推荐*/
    @RouteGroup
    object Daily {
        const val DAILY = "/daily"

        /**今日推荐页面*/
        const val PAGER_RCMD = "$DAILY/RecommendActivity"
    }

    /**票务模块*/
    @RouteGroup
    object Ticket {
        const val TICKET = "/ticket"

        /**影片详情页*/
        const val PAGER_MOVIE_DETAIL = "$TICKET/MovieDetailsActivity"

        /**影片排片页*/
        const val PAGER_MOVIE_SHOWTIME = "$TICKET/MovieShowtimeActivity"

        /**影院排片页*/
        const val PAGE_CINEMA_SHOWTIME = "$TICKET/NewCinemaShowtimeActivity"

        /**选座页面*/
        const val PAGE_SEAT_SELECT = "$TICKET/SeatSelectActivity"
    }

    /**
     * Simple
     */
    @RouteGroup
    object Simple {
        /**业务组件根路径*/
        const val SIMPLE = "/simple"

        /**MainActivity*/
        const val PAGER_MAIN_ACTIVITY = "$SIMPLE/MainActivity"

        /**CallActivity*/
        const val PAGER_CALL_ACTIVITY = "$SIMPLE/CallActivity"

        /**RepoActivity*/
        const val PAGER_REPO_ACTIVITY = "$SIMPLE/RepoActivity"

        /**BatchActivity*/
        const val PAGER_BATCH_ACTIVITY = "$SIMPLE/BatchActivity"

        /**PermissionActivity*/
        const val PAGER_PERMISSION_ACTIVITY = "$SIMPLE/PermissionActivity"

        /**ProgressDialogActivity*/
        const val PAGER_PROGRESS_DIALOG_ACTIVITY = "$SIMPLE/ProgressDialogActivity"

        /**DataBindingActivity*/
        const val PAGER_DATA_BINDING_ACTIVITY = "$SIMPLE/DataBindingActivity"

        /**LoginActivity*/
        const val PAGER_LOGIN_ACTIVITY = "$SIMPLE/LoginActivity"

        /**UserListActivity*/
        const val PAGER_USER_LIST_ACTIVITY = "$SIMPLE/UserListActivity"

        /**MultiTypeActivity*/
        const val PAGER_MULTI_TYPE_ACTIVITY = "$SIMPLE/MultiTypeActivity"

        /**UserDetailActivity*/
        const val PAGER_USER_DETAIL_ACTIVITY = "$SIMPLE/UserDetailActivity"

        /**MTimeApiActivity*/
        const val PAGER_MTIME_API_ACTIVITY = "$SIMPLE/MTimeApiActivity"

        /**MyActivity 对应时光app_我的Tab*/
        const val PAGER_MY_ACTIVITY = "$SIMPLE/MyActivity"

        /**SetActivity*/
        const val PAGER_SET_ACTIVITY = "$SIMPLE/SetActivity"

        /**ShareActivity*/
        const val PAGER_SHARE_ACTIVITY = "$SIMPLE/ShareActivity"

        /**ShapeActivity*/
        const val PAGER_SHAPE_ACTIVITY = "$SIMPLE/ShapeActivity"

        /**MultiStateViewActivity*/
        const val PAGER_MULTI_STATE_ACTIVITY = "$SIMPLE/MultiStateViewActivity"

        /**StatusBarActivity*/
        const val PAGER_STATUS_BAR_ACTIVITY = "$SIMPLE/StatusBarActivity"

        /**PublishActivity*/
        const val PAGER_PUBLISH_ACTIVITY = "$SIMPLE/PublishActivity"

        /**PhotoAlbumActivity*/
        const val PAGER_PHOTO_ALBUM_ACTIVITY = "$SIMPLE/PhotoAlbumActivity"

        /**CommentActivity*/
        const val PAGER_COMMENT_ACTIVITY = "$SIMPLE/CommentActivity"

        /**TipsActivity*/
        const val PAGER_TIPS_ACTIVITY = "$SIMPLE/TipsActivity"

        /**BrowserActivity*/
        const val PAGER_BROWSER_ACTIVITY = "$SIMPLE/BrowserActivity"

    }

    /**
     * 影评组件
     */
    @RouteGroup
    object Review {
        /**业务组件根基路径*/
        const val REVIEW = "/review"

        /**ReviewDetailActivity*/
        const val PAGE_REVIEW_DETAIL_ACTIVITY = "$REVIEW/ReviewDetailActivity"

        /**影评分享页面*/
        const val PAGE_REVIEW_SHARE_ACTIVITY = "$REVIEW/ReviewShareActivity"

        /**影片长影评列表页*/
        const val PAGE_MOVIE_REVIEW_LIST_ACTIVITY = "$REVIEW/MovieReviewListActivity"

        /**影片短影评列表页*/
        const val PAGE_MOVIE_SHORT_COMMENT_LIST_ACTIVITY = "$REVIEW/MovieShortCommentListActivity"

        /**影片评分详情*/
        const val PAGE_MOVIE_RATING_DETAIL_ACTIVITY = "$REVIEW/RatingDetailActivity"
    }

    /**
     * UGC组件
     */
    @RouteGroup
    object UgcDetail {
        const val UGC_DETAIL = "/ugcDetail"

        const val PAGE_UGC_MAIN_ACTIVITY = "$UGC_DETAIL/MainActivity"

        /**UgcDetailActivity*/
        const val PAGE_UGC_DETAIL_ACTIVITY = "$UGC_DETAIL/UgcDetailActivity"


        /**
         * ugc图集详情 UgcAlbumActivity
         */
        const val PAGE_UGC_DETAIL_ALBUM_ACTIVITY = "$UGC_DETAIL/UgcAlbumActivity"


        /**
         * ugc视频详情、音乐详情
         */
        const val PAGE_UGC_MEDIA_DETAIL_ACTIVITY = "$UGC_DETAIL/UgcMediaDetailActivity"
    }

    @RouteGroup
    object Article {
        const val ARTICLE = "/article"

        /**文章详情*/
        const val PAGE_ARTICLE_DETAIL_ACTIVITY = "$ARTICLE/ArticleDetailActivity"
    }

    @RouteGroup
    object Post {
        const val POST = "/post"


        /**
         * 帖子详情
         */
        const val PAGE_POST_DETAIL_ACTIVITY = "$POST/PostDetailActivity"
    }

    @RouteGroup
    object Comment {
        const val COMMENT = "/comment"

        /**评论详情*/
        const val PAGE_COMMENT_DETAIL_ACTIVITY = "$COMMENT/CommentDetailActivity"
    }

    @RouteGroup
    object Video {
        const val VIDEO = "/video"

        /**预告片向你全功能*/
        const val PAGE_VIDEO_PRE_VIDEO_ACTIVITY = "$VIDEO/VideoDetailActivity"
    }

    @RouteGroup
    object Publish {
        const val PUBLISH = "/publish"

        /**发布页面*/
        @LoginPath
        const val PAGER_PUBLISH_ACTIVITY = "$PUBLISH/PublishActivity"

        /**编辑页面*/
        @LoginPath
        const val PAGER_EDITOR_ACTIVITY = "$PUBLISH/EditorActivity"

        /**我的家族列表*/
        const val PAGER_FAMILY_LIST_ACTIVITY = "$PUBLISH/FamilyListActivity"

        /**视频发布页面*/
        const val PAGER_VIDEO_PUBLISH_ACTIVITY = "$PUBLISH/VideoPublishActivity"

        /**预览视频页面*/
        const val PAGER_PREVIEW_VIDEO_ACTIVITY = "$PUBLISH/PreviewVideoActivity"
    }

    @RouteGroup
    object TicketOrder {
        const val TICKET_ORDER = "/ticketOrder"

        /**电影票订单列表*/
        @LoginPath
        const val TICKET_ORDER_LIST_ACTIVITY = "$TICKET_ORDER/TicketOrderListActivity"
    }

    @RouteGroup
    object Search {
        const val SEARCH = "/search"

        /**搜索页面*/
        const val PAGE_SEARCH_ACTIVITY = "$SEARCH/SearchActivity"

        /**发布组件-搜索影片/影人*/
        const val PAGE_PUBLISH_SEARCH_ACTIVITY = "$SEARCH/PublishSearchActivity"

        /**搜索家族帖子*/
        const val PAGE_SEARCH_POST_GROUP_ACTIVITY = "$SEARCH/SearchGroupActivity"
    }

    @RouteGroup
    object JsSDK {
        const val JS_SDK = "/jsSDK"

        /**H5展示页面*/
        const val PAGE_H5_ACTIVITY = "$JS_SDK/H5Activity"
    }

    @RouteGroup
    object QRCode {
        const val QRCODE = "/qrcode"

        /**二维码扫描页面*/
        const val PAGE_QRCODE_ACTIVITY = "$QRCODE/CaptureActivity"

        /**二维码网页登录确认*/
        @LoginPath
        const val PAGE_QRCODE_LOGIN_ACTIVITY = "$QRCODE/QrcodeLoginActivity"
    }
//    object Share {
//        const val SHARE = "/share"
//        const val PAGER_SHARE_SIMPLE_ACTIVITY = "$SHARE/ShareSimpleActivity"
//    }

    //    直播相关
    @RouteGroup
    object Live {
        const val LIVE = "/live"

        /**直播详情*/
        @LoginPath
        const val PAGE_LIVE_DETAIL_ACTIVITY = "$LIVE/LiveDetailActivity"

        /**直播详情生成海报分享*/
        const val PAGE_LIVE_SHARE_POSTER_ACTIVITY = "$LIVE/LiveSharePosterActivity"

        /**直播测试页面入口*/
        const val PAGE_LIVE_TEST_ENTRANCE_ACTIVITY = "$LIVE/LiveTestEntranceActivity"

        /**直播列表*/
        const val PAGE_LIVE_LIST_ACTIVITY = "$LIVE/LiveListActivity"
    }

    //有赞的webview
    @RouteGroup
    object YOUZANWEB {
        const val YOUZANWEB = "/youzan"

        /**webview详情*/
        const val PAGE_YOUZAN_WEBVIEW = "$YOUZANWEB/YouzanActivity"
    }

    //片单组件
    @RouteGroup
    object TABLET {
        const val TABLET = "/tablet"

        //片单列表页面
        const val TABLET_MAIN = "$TABLET/TabletMainActivity"

        //片单详情页面
        const val FILM_LIST_DETAILS = "$TABLET/FilmListDetailsActivity"

        //片单创建页面
        const val FILM_LIST_CREATE = "$TABLET/FilmListCreateActivity"

        //片单创建成功页面
        const val FILM_LIST_CREATE_SUCCESS = "$TABLET/FilmListCreateSuccessActivity"

        //片单添加电影页
        const val FILM_LIST_ADD_FILM = "$TABLET/FilmListAddFilmActivity"

        //片单已添加电影
        const val FILM_LIST_SELECTED = "$TABLET/FilmSelectedActivity"

        //投稿当前主题
        const val FILM_LIST_CONTRIBUTE = "$TABLET/ContributeActivity"

        //投稿历史主题
        const val FILM_LIST_CONTRIBUTE_HISTORY = "$TABLET/ContributeHistoryActivity"

        //投稿我创建的片单
        const val FILM_LIST_CONTRIBUTE_MY_CREATE = "$TABLET/MyCreateActivity"

        //个人中心我的片单
        @LoginPath
        const val FILM_LIST_OF_MINE = "$TABLET/FilmListMineActivity"

        //片单列表搜索页面
        const val FILM_LIST_SEARCH = "$TABLET/FilmListSearchActivity"
    }

    //消息中心
    @RouteGroup
    object MessageCenter {

        /**组件根路径*/
        const val MESSAGE_CENTER = "/messageCenter"

        /**10.0新版消息中心*/
        @LoginPath
        const val PAGE_MESSAGE_CENTER = "$MESSAGE_CENTER/MessageCenterActivity"

        /**粉丝页面 */
        @LoginPath
        const val PAGE_FANS = "$MESSAGE_CENTER/FansActivity"

        /**观影通知 */
        @LoginPath
        const val PAGE_MOVIE_REMIND = "$MESSAGE_CENTER/MovieRemindActivity"

        /**评论回复 */
        @LoginPath
        const val PAGE_COMMENT = "$MESSAGE_CENTER/CommentActivity"

        /**点赞 */
        @LoginPath
        const val PAGE_PRAISE = "$MESSAGE_CENTER/PraiseActivity"

        /**发起私聊*/
        @LoginPath
        const val PAGE_PRIVATE_CHAT = "$MESSAGE_CENTER/PrivateChatActivity"

        /**黑名单*/
        @LoginPath
        const val PAGE_BLACK_LIST = "$MESSAGE_CENTER/BlackListActivity"

        /**私聊页面*/
        @LoginPath
        const val PAGE_CHAT = "$MESSAGE_CENTER/ChatActivity"
    }
}