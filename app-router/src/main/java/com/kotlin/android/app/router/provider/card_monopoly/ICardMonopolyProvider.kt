package com.kotlin.android.app.router.provider.card_monopoly

import android.app.Activity
import android.content.Context
import android.view.View
import com.kotlin.android.app.data.annotation.CARD_MONOPOLY_UNKNOWN
import com.kotlin.android.app.data.annotation.CardMonopolyMainTab
import com.kotlin.android.app.data.entity.monopoly.CardImageDetailBean
import com.kotlin.android.app.data.entity.monopoly.Robot
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider


/**
 *
 * Created on 2020/8/10.
 *
 * @author o.s
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_CARD_MONOPOLY)
interface ICardMonopolyProvider : IBaseProvider {

    fun startCardMainActivity(
        context: Context,
        userId: Long? = null, // 不传默认是自己
        @CardMonopolyMainTab tab: Int = CARD_MONOPOLY_UNKNOWN
    )

    fun startCardRobotActivity(robot: Robot)
    fun startSuitActivity(userInfo: UserInfo)

    /**
     * [suitC]
     * "14"：代表简/限
     * "1"：代表简
     */
    fun startSuitSelectedActivity(
        from: Int,
        userId: Long,
        suitC: String,
    )
    fun startSuitDetailActivity(suitType: Long)
    fun startPropActivity(from: Long)
    fun startStoreActivity()

    /**
     * [tab] 0：我的愿望，1：留言板
     */
    fun startWishingActivity(userInfo: UserInfo? = null, tab: Int = 0)

    /**
     * [tab] 拍卖行 tab 标识
     * [suit] 套装信息，包含选中的卡片状态
     */
    fun startAuctionActivity(tab: Int = 0, suit: Suit? = null)
    fun startDealRecordsActivity()

    fun startCardFriend(jumpType: Int)
    fun startCardFriendForResult(activity: Activity?, code: Int, jumpType: Int)
    fun startSuitRank(suitId: Long, flag: Boolean)
    fun startGameGuideActivity()

    /**
     * 跳转查看大图页面
     * @param mActivity 当前activity
     * @param image 共享元素(shareElement)的image
     * @param data 传递的imageList对象
     */
    fun startImageDetailActivity(
        mActivity: Activity,
        image: View?,
        data: CardImageDetailBean
    )

    /**
     * 卡片大富翁留言板
     */
    fun startMessageBoardActivity(
        userId: Long,
        userName: String
    )

    /**
     * 跳转套装评论页面
     */
    fun startSuitCommentActivity(type: Long, contentId: Long, title: String)

}