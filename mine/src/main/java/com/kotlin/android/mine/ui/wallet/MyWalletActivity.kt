package com.kotlin.android.mine.ui.wallet

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.mine.R
import com.kotlin.android.mine.WALLET_COUPON_ALL
import com.kotlin.android.mine.databinding.ActivityMyWalletBinding
import com.kotlin.android.mine.ui.message.MessageCenterViewModel
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.CommonTitleBar
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.activity_my_collection.*

@Route(path = RouterActivityPath.Mine.PAGE_MY_WALLET)
class MyWalletActivity : BaseVMActivity<MessageCenterViewModel, ActivityMyWalletBinding>() {

    override fun initTheme() {
        super.initTheme()
        immersive().statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar().init(this).setTitle("我的钱包").create()
    }

    override fun initVM(): MessageCenterViewModel {
        return viewModels<MessageCenterViewModel>().value
    }

    override fun initView() {
        val creator = FragPagerItems(this@MyWalletActivity)
                .add(titleRes = (R.string.coupon_title), clazz = TicketCouponFragment::class.java, args = TicketCouponFragment().bundle(WALLET_COUPON_ALL))
                .add(titleRes = (R.string.gift_card_title), clazz = GiftCardFragment::class.java)
                .add(titleRes = (R.string.account_balance), clazz = BalanceFragment::class.java)

        val adapter = FragPagerItemAdapter(this@MyWalletActivity.supportFragmentManager, creator)

        viewPager.adapter = adapter
        tableLayout.setViewPager(viewPager)
        tableLayout.setSelectedAnim()
        viewPager.currentItem = 0

    }

    override fun initData() {
    }

    override fun startObserve() {
    }
}