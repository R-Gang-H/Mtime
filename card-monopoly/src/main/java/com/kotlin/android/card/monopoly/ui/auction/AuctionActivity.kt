package com.kotlin.android.card.monopoly.ui.auction

import androidx.activity.viewModels
import androidx.fragment.app.FragmentTransaction
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.card.monopoly.*
import com.kotlin.android.card.monopoly.databinding.ActAuctionBinding
import com.kotlin.android.card.monopoly.ext.showFunctionMenuDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.nav.NavView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import kotlinx.android.synthetic.main.act_auction.*

/**
 * 卡片大富翁套拍卖行页面：
 *
 * Created on 2020/9/7.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_AUCTION)
class AuctionActivity : BaseVMActivity<CardMonopolyApiViewModel, ActAuctionBinding>(), AuctionBuyFragment.IClearSuitData {

    private var mSuit: Suit? = null
    private var pageType:Int = 0

    private var bidFragment: AuctionBidFragment? = null
    private var buyFragment: AuctionBuyFragment? = null
    private var auctionFragment: AuctionFragment? = null

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(false)
    }

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun initView() {
        window.setBackgroundDrawable(null)
        //获取suit套装列表
        mSuit = intent.getParcelableExtra(KEY_CARD_MONOPOLY_WISH_SUIT)
        //获取页面的tab
        pageType = intent.getIntExtra(KEY_CARD_MONOPOLY_AUCTION_TAB,TAB_AUCTION_BUY)
        initTitleView()
        initContentView()
    }

    override fun initNewData() {
        initData()
    }

    override fun initData() {
        navView?.selectItem(pageType)
    }

    override fun startObserve() {
    }

    private fun initTitleView() {
        titleBar?.apply {
            setThemeStyle(ThemeStyle.STANDARD_STATUS_BAR)
            setState(State.REVERSE)
            addItem(
                drawableRes = R.drawable.ic_title_bar_back_light,
                reverseDrawableRes = R.drawable.ic_title_bar_back_dark
            ) {
                finish()
            }
            setTitle(getString(R.string.auction), alwaysShow = true) {

            }
            addItem(
                drawableRes = R.drawable.ic_title_bar_more_light,
                reverseDrawableRes = R.drawable.ic_title_bar_more_dark,
                isReversed = true
            ) {
                showFunctionMenuDialog(
                    dismiss = {
                        syncStatusBar()
                    }
                )
            }
        }
    }

    private fun initContentView() {
        mainLayout?.background = getShapeDrawable(
            colorRes = R.color.color_a2edff,
            endColorRes = R.color.color_ffffff
        )
        navView?.apply {
            setItems(NavView.Category.SHOPPING, NavView.Category.AUCTION, NavView.Category.BIDDING)
            style = NavView.Style.TRIPLE
            action = {
                showFragment(it)
            }
        }
    }

    private fun showFragment(type: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragment(transaction)
        when (type) {
            TAB_AUCTION_BUY -> {
                buyFragment = AuctionBuyFragment.getInstance(mSuit)
                transaction.add(R.id.frameContainer, buyFragment!!)
                transaction.show(buyFragment!!)
            }
            TAB_AUCTION_AUCTION -> {
                auctionFragment = AuctionFragment.getInstance()
                transaction.add(R.id.frameContainer, auctionFragment!!)
                transaction.show(auctionFragment!!)
            }
            TAB_AUCTION_BID -> {
                bidFragment = AuctionBidFragment.getInstance()
                transaction.add(R.id.frameContainer, bidFragment!!)
                transaction.show(bidFragment!!)
            }
        }

        transaction.commitAllowingStateLoss()
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        buyFragment?.let { transaction.hide(it) }
        auctionFragment?.let { transaction.hide(it) }
        bidFragment?.let { transaction.hide(it) }
    }

    override fun onClearSuit() {
        mSuit = null
    }

}