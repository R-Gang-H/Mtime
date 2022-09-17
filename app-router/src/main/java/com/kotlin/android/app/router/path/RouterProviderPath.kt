package com.kotlin.android.app.router.path

import com.kotlin.android.app.router.path.RouterActivityPath.Article.ARTICLE
import com.kotlin.android.app.router.path.RouterActivityPath.CardMonopoly.CARD_MONOPOLY
import com.kotlin.android.app.router.path.RouterActivityPath.Comment.COMMENT
import com.kotlin.android.app.router.path.RouterActivityPath.Community.COMMUNITY
import com.kotlin.android.app.router.path.RouterActivityPath.CommunityFamily.COMMUNITY_FAMILY
import com.kotlin.android.app.router.path.RouterActivityPath.Daily.DAILY
import com.kotlin.android.app.router.path.RouterActivityPath.Home.HOME
import com.kotlin.android.app.router.path.RouterActivityPath.JsSDK.JS_SDK
import com.kotlin.android.app.router.path.RouterActivityPath.Live.LIVE
import com.kotlin.android.app.router.path.RouterActivityPath.Main.MAIN
import com.kotlin.android.app.router.path.RouterActivityPath.Mine.MINE
import com.kotlin.android.app.router.path.RouterActivityPath.User.USER
import com.kotlin.android.app.router.path.RouterActivityPath.Post.POST
import com.kotlin.android.app.router.path.RouterActivityPath.Publish.PUBLISH
import com.kotlin.android.app.router.path.RouterActivityPath.QRCode.QRCODE
import com.kotlin.android.app.router.path.RouterActivityPath.Review.REVIEW
import com.kotlin.android.app.router.path.RouterActivityPath.Search.SEARCH
import com.kotlin.android.app.router.path.RouterActivityPath.Simple.SIMPLE
import com.kotlin.android.app.router.path.RouterActivityPath.Splash.SPLASH
import com.kotlin.android.app.router.path.RouterActivityPath.Ticket.TICKET
import com.kotlin.android.app.router.path.RouterActivityPath.TicketOrder.TICKET_ORDER
import com.kotlin.android.app.router.path.RouterActivityPath.UgcDetail.UGC_DETAIL
import com.kotlin.android.app.router.path.RouterActivityPath.AppUser.APP_USER
import com.kotlin.android.app.router.path.RouterActivityPath.Film.FILM
import com.kotlin.android.app.router.path.RouterActivityPath.MessageCenter.MESSAGE_CENTER
import com.kotlin.android.app.router.path.RouterActivityPath.TABLET.TABLET
import com.kotlin.android.app.router.path.RouterActivityPath.Video.VIDEO
import com.kotlin.android.app.router.path.RouterActivityPath.YOUZANWEB.YOUZANWEB
import com.kotlin.android.router.annotation.RouteGroup
import com.kotlin.android.router.annotation.RoutePath

/**
 * 创建者: zl
 * 创建时间: 2020/6/5 10:20 AM
 * 描述:Provider路径
 */
@RoutePath
class RouterProviderPath {

    /**
     * 业务组件
     */
    @RouteGroup
    object Provider {
        /**组件根路径*/
        private const val PROVIDER = "/provider"

        /**main组件*/
        const val PROVIDER_MAIN = "$MAIN$PROVIDER"

        /**Simple组件*/
        const val PROVIDER_SIMPLE = "$SIMPLE$PROVIDER"

        /**首页组件*/
        const val PROVIDER_HOME = "$HOME$PROVIDER"

        /**电影组件*/
        const val PROVIDER_FILM = "$FILM$PROVIDER"

        /**社区组件*/
        const val PROVIDER_COMMUNITY = "$COMMUNITY$PROVIDER"

        /**卡片大富翁组件*/
        const val PROVIDER_CARD_MONOPOLY = "$CARD_MONOPOLY$PROVIDER"

        /**个人中心组件*/
        const val PROVIDER_MINE = "$MINE$PROVIDER"

        /**社区家族组件*/
        const val PROVIDER_COMMUNITY_FAMILY = "$COMMUNITY_FAMILY$PROVIDER"

        /**今日推荐*/
        const val PROVIDER_DAILY_RCMD = "$DAILY$PROVIDER"

        /**影评组件*/
        const val PROVIDER_REVIEW = "$REVIEW$PROVIDER"

        /**ugc-detail组件*/
        const val PROVIDER_UGC_DETAIL = "$UGC_DETAIL$PROVIDER"

        /**旧的个人中心组件*/
        const val PROVIDER_APP_USER = "$APP_USER$PROVIDER"

        /**User组件*/
        const val PROVIDER_USER = "$USER$PROVIDER"

        /**帖子组件*/
        const val PROVIDER_COMMUNITY_POST = "$POST$PROVIDER"

        /**文章组件*/
        const val PROVIDER_ARTICLE = "$ARTICLE$PROVIDER"

        /**评论组件*/
        const val PROVIDER_COMMENT = "$COMMENT$PROVIDER"

        /**票务组件*/
        const val PROVIDER_TICKET = "$TICKET$PROVIDER"

        /**视频组件*/
        const val PROVIDER_VIDEO = "$VIDEO$PROVIDER"

        /**发布组件*/
        const val PROVIDER_PUBLISH = "$PUBLISH$PROVIDER"

        /**影票订单组件*/
        const val PROVIDER_TICKET_ORDER = "$TICKET_ORDER$PROVIDER"

        /**搜索组件*/
        const val PROVIDER_SEARCH = "$SEARCH$PROVIDER"

        /**JS-SDK组件*/
        const val PROVIDER_JS_SDK = "$JS_SDK$PROVIDER"

        /**JS-SDK组件*/
        const val PROVIDER_SPLASH= "$SPLASH$PROVIDER"
        /**qrcode组件*/
        const val PROVIDER_QRCODE = "$QRCODE$PROVIDER"

        /**直播组件*/
        const val PROVIDER_LIVE = "$LIVE$PROVIDER"

        /**有赞webview组件*/
        const val PROVIDER_YOUZAN_WEB = "$YOUZANWEB$PROVIDER"

        /** 片单组件*/
        const val PROVIDER_TABLET = "$TABLET$PROVIDER"

        /** 消息中心组件*/
        const val PROVIDER_MESSAGE_CENTER = "$MESSAGE_CENTER$PROVIDER"
    }
}