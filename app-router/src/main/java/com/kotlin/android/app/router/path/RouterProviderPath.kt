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
 * ?????????: zl
 * ????????????: 2020/6/5 10:20 AM
 * ??????:Provider??????
 */
@RoutePath
class RouterProviderPath {

    /**
     * ????????????
     */
    @RouteGroup
    object Provider {
        /**???????????????*/
        private const val PROVIDER = "/provider"

        /**main??????*/
        const val PROVIDER_MAIN = "$MAIN$PROVIDER"

        /**Simple??????*/
        const val PROVIDER_SIMPLE = "$SIMPLE$PROVIDER"

        /**????????????*/
        const val PROVIDER_HOME = "$HOME$PROVIDER"

        /**????????????*/
        const val PROVIDER_FILM = "$FILM$PROVIDER"

        /**????????????*/
        const val PROVIDER_COMMUNITY = "$COMMUNITY$PROVIDER"

        /**?????????????????????*/
        const val PROVIDER_CARD_MONOPOLY = "$CARD_MONOPOLY$PROVIDER"

        /**??????????????????*/
        const val PROVIDER_MINE = "$MINE$PROVIDER"

        /**??????????????????*/
        const val PROVIDER_COMMUNITY_FAMILY = "$COMMUNITY_FAMILY$PROVIDER"

        /**????????????*/
        const val PROVIDER_DAILY_RCMD = "$DAILY$PROVIDER"

        /**????????????*/
        const val PROVIDER_REVIEW = "$REVIEW$PROVIDER"

        /**ugc-detail??????*/
        const val PROVIDER_UGC_DETAIL = "$UGC_DETAIL$PROVIDER"

        /**????????????????????????*/
        const val PROVIDER_APP_USER = "$APP_USER$PROVIDER"

        /**User??????*/
        const val PROVIDER_USER = "$USER$PROVIDER"

        /**????????????*/
        const val PROVIDER_COMMUNITY_POST = "$POST$PROVIDER"

        /**????????????*/
        const val PROVIDER_ARTICLE = "$ARTICLE$PROVIDER"

        /**????????????*/
        const val PROVIDER_COMMENT = "$COMMENT$PROVIDER"

        /**????????????*/
        const val PROVIDER_TICKET = "$TICKET$PROVIDER"

        /**????????????*/
        const val PROVIDER_VIDEO = "$VIDEO$PROVIDER"

        /**????????????*/
        const val PROVIDER_PUBLISH = "$PUBLISH$PROVIDER"

        /**??????????????????*/
        const val PROVIDER_TICKET_ORDER = "$TICKET_ORDER$PROVIDER"

        /**????????????*/
        const val PROVIDER_SEARCH = "$SEARCH$PROVIDER"

        /**JS-SDK??????*/
        const val PROVIDER_JS_SDK = "$JS_SDK$PROVIDER"

        /**JS-SDK??????*/
        const val PROVIDER_SPLASH= "$SPLASH$PROVIDER"
        /**qrcode??????*/
        const val PROVIDER_QRCODE = "$QRCODE$PROVIDER"

        /**????????????*/
        const val PROVIDER_LIVE = "$LIVE$PROVIDER"

        /**??????webview??????*/
        const val PROVIDER_YOUZAN_WEB = "$YOUZANWEB$PROVIDER"

        /** ????????????*/
        const val PROVIDER_TABLET = "$TABLET$PROVIDER"

        /** ??????????????????*/
        const val PROVIDER_MESSAGE_CENTER = "$MESSAGE_CENTER$PROVIDER"
    }
}