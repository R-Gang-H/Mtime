package com.kotlin.android.mine.ui.wallet

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.mine.R
import com.kotlin.android.mine.WALLET_COUPON_EXPIRED
import com.kotlin.android.mine.WALLET_COUPON_USED
import com.kotlin.android.mine.databinding.ActivityMyWalletBinding
import com.kotlin.android.mine.ui.message.MessageCenterViewModel
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.CommonTitleBar
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.activity_my_collection.*

@Route(path = RouterActivityPath.Mine.PAGE_MY_COUPON_USED_RECORD)
class CouponUsedRecordActivity : BaseVMActivity<MessageCenterViewModel, ActivityMyWalletBinding>() {

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar().init(this).setTitle(getString(R.string.coupon_record_page)).create()
    }

    override fun initVM(): MessageCenterViewModel {
        return viewModels<MessageCenterViewModel>().value
    }

    override fun initView() {
        val creator = FragPagerItems(this@CouponUsedRecordActivity)
                .add(titleRes = (R.string.used), clazz = TicketCouponFragment::class.java, args = TicketCouponFragment().bundle(WALLET_COUPON_USED))
                .add(titleRes = (R.string.expired), clazz = TicketCouponFragment::class.java, args = TicketCouponFragment().bundle(WALLET_COUPON_EXPIRED))

        val adapter = FragPagerItemAdapter(this@CouponUsedRecordActivity.supportFragmentManager, creator)

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