package com.kotlin.android.card.monopoly.provider

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.card.monopoly.*
import com.kotlin.android.card.monopoly.constants.Constants.KEY_CARD_DETAIL
import com.kotlin.android.card.monopoly.constants.Constants.PROPS_USE_TYPE_FROM
import com.kotlin.android.card.monopoly.ui.comment.CardCommentActivity
import com.kotlin.android.app.data.annotation.CardMonopolyMainTab
import com.kotlin.android.app.data.entity.monopoly.CardImageDetailBean
import com.kotlin.android.app.data.entity.monopoly.Robot
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.ktx.ext.KEY_USER_ID
import com.kotlin.android.ktx.ext.KEY_USER_NAME
import com.kotlin.android.router.RouterManager
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider

/**
 *
 * Created on 2020/8/10.
 *
 * @author o.s
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_CARD_MONOPOLY)
class CardMonopolyProvider : ICardMonopolyProvider {

    override fun startCardMainActivity(
        context: Context,
        userId: Long?,
        @CardMonopolyMainTab tab: Int
    ) {
        Bundle().apply {
            putInt(KEY_CARD_MONOPOLY_MAIN_TAB, tab)
            putLong(KEY_CARD_MONOPOLY_USER_ID, userId ?: 0L)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CardMonopoly.PAGER_CARD_MAIN,
                bundle = this,
                context = context
            )
        }
    }

    override fun startCardRobotActivity(robot: Robot) {
        Bundle().apply {
            putParcelable(KEY_CARD_MONOPOLY_ROBOT, robot)
        }.run {
            RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_CARD_ROBOT, this)
        }
    }

    override fun startSuitActivity(userInfo: UserInfo) {
        Bundle().apply {
            putParcelable(KEY_CARD_MONOPOLY_USER_INFO, userInfo)
        }.run {
            RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_SUIT, this)
        }
    }

    override fun startSuitSelectedActivity(
        from: Int,
        userId: Long,
        suitC: String,
    ) {
        Bundle().apply {
            putLong(KEY_CARD_MONOPOLY_USER_ID, userId)
            putString(KEY_CARD_MONOPOLY_SUIT_C, suitC)
            putInt(KEY_CARD_MONOPOLY_SUIT_FROM, from)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CardMonopoly.PAGER_SUIT_SELECTED,
                bundle = this,
            )
        }
    }

    override fun startSuitDetailActivity(suitType: Long) {
        Bundle().apply {
            putLong(KEY_CARD_MONOPOLY_SUIT_TYPE, suitType)
        }.run {
            RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_SUIT_DETAIL, this)
        }
    }

    override fun startPropActivity(from: Long) {
        Bundle().apply {
            putLong(PROPS_USE_TYPE_FROM, from)
        }.run {
            RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_PROP, this)
        }

    }

    override fun startStoreActivity() =
        RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_STORE)

    /**
     * [tab] 0：我的愿望，1：留言板
     */
    override fun startWishingActivity(userInfo: UserInfo?, tab: Int) {
        Bundle().apply {
            putInt(KEY_CARD_MONOPOLY_WISH_TAB, tab)
            putParcelable(KEY_CARD_MONOPOLY_USER_INFO, userInfo)
        }.run {
            RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_WISHING, this)
        }
    }

    override fun startAuctionActivity(tab: Int, suit: Suit?) {
        Bundle().apply {
            putInt(KEY_CARD_MONOPOLY_AUCTION_TAB, tab)
            putParcelable(KEY_CARD_MONOPOLY_WISH_SUIT, suit)
        }.run {
            RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_AUCTION, this)
        }
    }

    override fun startDealRecordsActivity() =
        RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_DEAL_RECORDS)

    override fun startCardFriend(jumpType: Int) {
        Bundle().apply {
            putInt(KEY_CARD_FRIEND, jumpType)
        }.run {
            RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_CARD_FRIEND, this)
        }
    }

    override fun startCardFriendForResult(mActivity: Activity?, code: Int, jumpType: Int) {
        Bundle().apply {
            putInt(KEY_CARD_FRIEND, jumpType)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CardMonopoly.PAGER_CARD_FRIEND,
                bundle = this,
                context = mActivity,
                requestCode = code
            )
        }

    }


    override fun startSuitRank(suitId: Long, flag: Boolean) {
        Bundle().apply {
            putLong(KEY_SUIT_ID, suitId)
            putBoolean(KEY_SUIT_TYPE, flag)
        }.run {
            RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_SUIT_RANK, this)
        }
    }

    override fun startGameGuideActivity() =
        RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_GAME_GUIDE)

    override fun startImageDetailActivity(
        mActivity: Activity,
        image: View?,
        data: CardImageDetailBean
    ) {
        val bundle = Bundle()
        bundle.putParcelable(KEY_CARD_DETAIL, data)
        if (image != null) {
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, image, "image")
            RouterManager.instance.navigation(
                path = RouterActivityPath.CardMonopoly.PAGER_IMAGE_DETAIL,
                bundle = bundle,
                context = mActivity as Context,
                activityOptionsCompat = optionsCompat
            )
        } else {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CardMonopoly.PAGER_IMAGE_DETAIL,
                bundle = bundle,
                context = mActivity as Context
            )
        }
    }

    override fun startMessageBoardActivity(
        userId: Long,
        userName: String
    ) {
        Bundle().apply {
            putLong(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
        }.run {
            RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_MESSAGE_BOARD, this)
        }
    }

    override fun startSuitCommentActivity(type: Long, contentId: Long, title: String) {
        val bundle = Bundle()
        bundle.putLong(CardCommentActivity.ARTICLE_TYPE, type)
        bundle.putLong(CardCommentActivity.ARTICLE_CONTENT_ID, contentId)
        bundle.putString(CardCommentActivity.CARD_COMMENT_TITLE, title)
        RouterManager.instance.navigation(RouterActivityPath.CardMonopoly.PAGER_SUIT_COMMENT, bundle)
    }

}