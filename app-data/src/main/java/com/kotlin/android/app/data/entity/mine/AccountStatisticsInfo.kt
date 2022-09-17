package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.activity.Activity

/**
 * create by lushan on 2020/9/30
 * description: 我的页面用户统计信息
 */
data class AccountStatisticsInfo(
        var errorMsg: String? = "",//错误信息
        var myClub: MyClub? = null,//影迷俱乐部
        var myCommunity: MyCommunity? = null,//我的社区数据
        var myOrders: MyOrders? = null,//我的订单数据
        var myRecords: MyRecords? = null,//我的记录
        var myWallet: MyWallet? = null,//我的钱包
        var myActivity: MyActivity? = null, // 当前活动
        var success: Boolean = false//是否成功获取到数据
) : ProguardRule {
    data class MyClub(
            var checkInUrl: String? = "",//签到抽奖地址url
            var todayMBean: Long = 0L//今日新增M豆数
    ) : ProguardRule

    data class MyCommunity(
            var fansCount: Long = 0L,//用户粉丝数
            var followCount: Long = 0L//关注数
    ) : ProguardRule

    data class MyOrders(
            var items: MutableList<Order>? = mutableListOf()//我的订单列表（目前只返回订单时间最近的一条，如果没有则为空）
    ) : ProguardRule

    data class MyRecords(
            var ratingWaitCount: Long = 0L,//看过的等待评价的影片数量
            var wantSeeCount: Long = 0L,//想看总数
            var hotFilmWantSeeCount: Long = 0L, // 想看电影中正在热映的电影总数
            var wantSeeMovies: MutableList<WantSeeMovie>? = mutableListOf(),//想看影片的信息列表(最多两部)
            var watchedCount: Long = 0L,//看过总数
            var watchedMovies: MutableList<WantSeeMovie>? = mutableListOf(), //看过影片的信息类表(最多两部)
            var favoriteCount: Long = 0L,       // 收藏数
            var creator: Boolean? = null,                // 是否是创作者标签
            var creatorLevelId: String? = null,          // 创作中心-等级id
            var creatorLevelName: String? = null,        // 创作中心-等级名称
            var creatorAppLogoUrl: String? = null,       // 创作中心-app等级图标url
            var contentCount: Long? = null,              // 已创作内容数
            var sevenDayBrowseCount: Long? = null,       // 7天浏览数
            var unreadMedals: List<UnreadMedal>? = null, // 创作中心-未读勋章提示列表
    ) : ProguardRule

    data class MyWallet(
            var cardWillExpire: String? = "",//距即将过期日期十天以内最近一张礼品卡名称（根据最新需求，这个字段应该废弃）
            var couponWillExpire: String? = "",//距即将过期日期十天以内最近一张观影券名称（根据最新需求，这个字段应该废弃）
            var msgUp: String? = "",//最新需求：msgUp 和 msgDown两个字段组合返回展示信息 有优惠券即将过期时：msgUp=N张优惠券 msgDown=即将过期 （N表示即将过期的优惠券张数）有礼品卡即将过期时（不区分点卡还是次卡）：msgUp=N张礼品卡 msgDown=即将过期 （N表示即将过期的优惠券张数）
            //同时存在优惠券和礼品卡即将过期时：msgUp=一大波权益 msgDown=即将过期 没有即将过期的卡券时：msgUp=玩转社区 msgDown=得礼包
            var msgDown: String? = ""//msgUp 和 msgDown两个字段组合返回展示信息。同上。
    ) : ProguardRule

    data class Order(
            var cinemaName: String? = "",//电影院名称
            var movieId: Long = 0L,//影片id
            var movieImg: String? = "",//电影封面
            var movieName: String? = "",//电影名称
            var orderId: Long = 0L,//订单id
            var payStatus: Long = 0L,//订单是否需要支付(0不需要 1需要支付)
            var openFlag: Boolean = false,//是否即将开映
            var openTime: String? = ""//开映时间
    ) : ProguardRule

    data class WantSeeMovie(
            var btnShow: Long? = 0L,//按钮显示：1.预售 2.购票 其他或null：不显示按钮
            var img: String? = "",//电影封面图
            var movieId: Long = 0L,//电影id
            var releaseDate: String? = ""//上映日期("xx月xx日上映"，空则说明不需要展示上映日)
    ) : ProguardRule

    data class UnreadMedal(
            var unreadCount: Long? = null,	// 同类型勋章未读数
            var medalName: String? = null,  // 勋章名称
    ): ProguardRule

    data class MyActivity(
            var items: List<Activity>? = null,  // 活动列表
            var activityCount: Long? = null,    // 活动总数量
    ) : ProguardRule

}

