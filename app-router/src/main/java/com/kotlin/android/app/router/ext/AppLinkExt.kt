package com.kotlin.android.app.router.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import com.kotlin.android.app.data.annotation.CARD_MONOPOLY_UNKNOWN
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_FILM_COMMENT
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.daily.IDailyProvider
import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.app.router.provider.live.ILiveProvider
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.app.router.provider.qrcode.IQRcodeProvider
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.app.router.provider.ticket_order.ITicketOrderProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.app.router.provider.video.IVideoProvider
import com.kotlin.android.core.entity.PageFlag
import com.kotlin.android.ktx.ext.handleJson
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.uri.urlDecode
import com.kotlin.android.router.ext.getProvider

/**
 * 创建者: zl
 * 创建时间: 2020/12/7 3:32 下午
 * 描述:AppLink全局跳转
 */
//{"handleType":"jumpPage","pageType":"movieDetail","movieId":"84078"}

fun parseAppLink(context: Context, appLink: String/*, appLinkForm: AppLinkFrom?, callback: AppLinkCallback?*/) {
    try {
        "appLink "+appLink.e()

        val baseData = appLink.urlDecode()
        "parseAppLink "+baseData.e()
        if (baseData.isEmpty()) {
            return
        }
        baseData.e()
        val appLinkExtra = handleJson(baseData, AppLinkExtra::class.java) ?: return
        jumpPage(context, appLinkExtra)
    } catch (e: Exception) {
        e.toString()
    }
}


fun jumpPage(context: Context, appLinkExtra: AppLinkExtra) {
    when (appLinkExtra.pageType) {
        PAGE_TYPE_h5 -> {
            //1、H5
            val url = appLinkExtra.url
            if (url.isNullOrEmpty()) {
                return
            }
            val isOpenByBrowser = TextUtils.equals(DEFAULT_YES, appLinkExtra.isOpenByBrowser.orEmpty())
            val isHorizontalScreen = TextUtils.equals(DEFAULT_YES, appLinkExtra.isHorizontalScreen.orEmpty())
            if (isOpenByBrowser) {
                callBrowser(context, url)
            } else {
                getProvider(IJsSDKProvider::class.java) {
                    startH5Activity(
                        BrowserEntity(title = "", url = url)
                    )
                }
                /*//todo 临时使用startDrawALotteryActivity，等待后期重构了webview页面后再废弃此方法
                val intent = Bundle()
                intent.put(KEY_WEB_OF_URL, url)
                intent.put(KEY_WEB_OF_PAGE_LABEL, "h5")
                intent.put(KEY_WEB_OF_SHOW_TITLE, true)
                intent.put(KEY_WEB_OF_SHOW_BACK, true)
                intent.put(KEY_WEB_OF_SHOW_CLOSE, true)
                intent.put(KEY_WEB_OF_IS_HORIZONTAL_SCREEN, isHorizontalScreen)
                getProvider(IUserProvider::class.java)?.startDrawALotteryActivity(intent)*/
            }
        }

        PAGE_TYPE_home -> {
            //2、首页
            openHome()
        }
        PAGE_TYPE_onShowList -> {
            //3、底部Tab购票-正在热映
            openFilm()
        }
        PAGE_TYPE_futureSchedule -> {
            //4、购票-即将上映
            openFilm(subPosition = 2)
        }
        PAGE_TYPE_movieDetail -> {
            //5、影片详情页
            getProvider(ITicketProvider::class.java)?.startMovieDetailsActivity(
                    appLinkExtra.getMovieId())
        }
        PAGE_TYPE_generalDetails -> {
            //6、通用详情页（文章详情页 、UGC详情页、半屏播放页、影评详情页、帖子详情页等 合成一个页面 统称为 通用详情页）
            getProvider(IUgcProvider::class.java)?.launchDetail(
                    appLinkExtra.getContentId(),
                    appLinkExtra.getType(),
                    needToComment = false)
        }
        PAGE_TYPE_newMovieScore -> {
            //7、新的电影评分页
            getProvider(IPublishProvider::class.java)?.startEditorActivity(
                    type = CONTENT_TYPE_FILM_COMMENT,
                    movieId = appLinkExtra.getMovieId(),
                    movieName = appLinkExtra.movieName)
        }
        PAGE_TYPE_memberCenter -> {
            //8、会员中心-影迷俱乐部
            getProvider(IMineProvider::class.java)?.startMemberCenterActivity(context)
        }
        PAGE_TYPE_ticketOrderList -> {
            //9、电影票订单列表页
            getProvider(ITicketOrderProvider::class.java)?.startTicketOrderListActivity(context)
        }
        PAGE_TYPE_wallet -> {
            //10、个人钱包
            getProvider(IMineProvider::class.java)?.startMyWalletActivity(context)
        }
        PAGE_TYPE_login -> {
            //11、登录
            /**
             * 有从推荐位直接跳转到登录页的业务需求？？？？
             */
            getProvider(IMainProvider::class.java)?.startLoginActivity(null)
        }

        PAGE_TYPE_starDetail -> {
            //12、影人详情页
            getProvider(IMainProvider::class.java)?.startActorViewActivity(
                    id = appLinkExtra.getStarId(),
                    name = ""
            )
        }
        PAGE_TYPE_cinemaTime -> {
            //13、影院排片页
            getProvider(IMainProvider::class.java)?.startCinemaShowtimeActivity(
                    context = context,
                    cinemaId = appLinkExtra.getCinemaId().toString(),
                    movieId = appLinkExtra.getMovieId().toString(),
                    newDate = appLinkExtra.date ?: ""
            )
        }
        PAGE_TYPE_movieTime -> {
            //14、影片排片页
            getProvider(ITicketProvider::class.java)
                    ?.startMovieShowtimeActivity(appLinkExtra.getMovieId())
        }
        PAGE_TYPE_videoList -> {
            //15、影片预告片列表页
            getProvider(IMainProvider::class.java)?.startVideoListActivity(
                    context = context,
                    movieId = appLinkExtra.getMovieId().toString()
            )
        }
        PAGE_TYPE_cinemaList -> {
            //16、购票-影院列表
            openFilm(subPosition = 1)
        }
        PAGE_TYPE_cinemaDetail -> {
            //17、影院详情页
            getProvider(IMainProvider::class.java)
                    ?.startCinemaViewActivity(appLinkExtra.getCinemaId())
        }
        PAGE_TYPE_onlineTicket -> {
            //18、选座页
            getProvider(ITicketProvider::class.java)
                    ?.startSeatSelectActivity(
                    showTimeId = appLinkExtra.getShowTimeId(),
                    date = appLinkExtra.date ?: "",
                    movieId = appLinkExtra.getMovieId(),
                    cinemaId = appLinkExtra.getCinemaId()
            )
        }
        PAGE_TYPE_dailyRecommend -> {
            //19、每日推荐
            getProvider(IDailyProvider::class.java)
                    ?.startDailyRecommendActivity()
        }
        PAGE_TYPE_community -> {
            //20、社区首页
            openCommunity()
        }
        PAGE_TYPE_groupDetails -> {
            //21、家族详情页
            getProvider(ICommunityFamilyProvider::class.java)
                    ?.startFamilyDetail(appLinkExtra.getGroupID())
        }
        PAGE_TYPE_groupSquare -> {
            //22、家族广场
            getProvider(ICommunityFamilyProvider::class.java)
                    ?.startFamilyClassList()
        }
        PAGE_TYPE_albumDetails -> {
            //23、相册详情页
            getProvider(IUgcProvider::class.java)
                    ?.startAlbumDetail(appLinkExtra.getAlbumId())
        }
        PAGE_TYPE_userPocket -> {
            //24、卡片大富翁- 用户口袋
            getProvider(ICardMonopolyProvider::class.java)
                    ?.startCardMainActivity(
                    context,
                    appLinkExtra.getUserId(),
                    CARD_MONOPOLY_UNKNOWN)
        }
        PAGE_TYPE_videoPlayDetail -> {
            //25、视频详情页  ( 同一页面不同叫法: 预告片播放详情页)
            getProvider(IVideoProvider::class.java)
                    ?.startPreVideoActivity(appLinkExtra.getVideoId());
        }
        PAGE_TYPE_commonListDetail -> {
            //26、榜单详情页 ( 备注：指的是 影人、影片、电视剧榜单详情页， iOS 是三个独立的页面、android 是一个通用页面)
            getProvider(IHomeProvider::class.java)
                    ?.startToplistDetailActivity(appLinkExtra.getListID());
        }
        PAGE_TYPE_userProfile -> {
            //27、用户个人主页
            getProvider(ICommunityPersonProvider::class.java)
                    ?.startPerson(userId = appLinkExtra.getUserId())
        }
        PAGE_TYPE_scanLogin -> {
            //28、扫码登录
            getProvider(IQRcodeProvider::class.java)
                    ?.startQrcodeLoginActivity(
                            uuid = appLinkExtra.uuid ?: "",
                            context = context)
        }

        PAGE_TYPE_live_detail ->{
            //29直播详情
            getProvider(ILiveProvider::class.java)?.launchLiveDetail(appLinkExtra.getLiveId())
        }

        PAGE_TYPE_contributionList ->{
            //30、投稿主题列表页
            getProvider(ITabletProvider::class.java)?.startContributeActivity()
        }

        PAGE_TYPE_findMovie ->{
            //31) 找电影
            getProvider(IHomeProvider::class.java)?.startFindMovie()
        }

        PAGE_TYPE_findFamily ->{
            //32) 找家族
            getProvider(ICommunityFamilyProvider::class.java)?.startFamilyFind()
        }

        PAGE_TYPE_filmList ->{
            //33) 片单
            getProvider(ITabletProvider::class.java)?.startTabletMainActivity()
        }

        PAGE_TYPE_filmListDetails ->{
            //34) 片单详情
            getProvider(ITabletProvider::class.java)?.startFilmListDetailsActivity(
                filmListId = appLinkExtra.getFilmListId()
            )
        }

        PAGE_TYPE_creatCenter ->{
            //35) 创作者中心页
            getProvider(IMineProvider::class.java)?.startActivityCreatorCenterActivity()
        }

        else -> {
            getProvider(IMainProvider::class.java)?.upgradeAppDialog(context)
        }
    }
}

fun callBrowser(activity: Context, url: String) {
    try {
        val i = Intent(Intent.ACTION_VIEW)
        if (activity !is Activity) {
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        i.data = Uri.parse(url)
        activity.startActivity(i)
    } catch (e: Exception) {
        Toast.makeText(activity, "暂无法从其他浏览器打开", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Tab 首页（0）：
 */
fun openHome() {
    getProvider(IMainProvider::class.java) {
        startMainActivity(
            flag = PageFlag(
                position = 0 // Tab 首页（0）
            )
        )
    }
}

/**
 * Tab 购票/影院（1）：
 * [switchPosition] Switch 正在热映（0）/ 影院（1） /即将上映（2）
 */
fun openFilm(subPosition: Int = 0) {
    getProvider(IMainProvider::class.java) {
        startMainActivity(
            flag = PageFlag(
                position = 1, // Tab 购票/影院（1）
                subFlag = PageFlag(
                    position = subPosition, // 正在热映（0）/ 影院（1） /即将上映（2）
                )
            )
        )
    }
}

/**
 * Tab 社区（3）：
 * [subPosition] 社区（0）/关注（1）/精选（2） /家族（3）
 */
fun openCommunity(subPosition: Int = 0) {
    getProvider(IMainProvider::class.java) {
        startMainActivity(
            flag = PageFlag(
                position = 3, // 社区 商城（3）
                subFlag = PageFlag(
                    position = subPosition // 商城-食品饮料（0）/商城-卡券优惠（1）/商城-星选商城（2）
                )
            )
        )
    }
}

/**
 * Tab 我的（4）：
 */
fun openMine() {
    getProvider(IMainProvider::class.java) {
        startMainActivity(
            flag = PageFlag(
                position = 4 // Tab 我的（4）
            )
        )
    }
}