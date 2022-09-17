package com.kotlin.android.ticket.order.component.ui.orderlist.history

import androidx.fragment.app.viewModels
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.ticket.order.component.databinding.FragHistoryTicketOrderBinding
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.frag_history_ticket_order.*

/**
 * create by lushan on 2020/10/23
 * description:电影票订单fragment  底部有时光网订单和影院直销订单
 */
class HistoryTicketOrderFragment : BaseVMFragment<HistoryTicketOrderViewModel, FragHistoryTicketOrderBinding>() {

    override fun initVM(): HistoryTicketOrderViewModel {
        return viewModels<HistoryTicketOrderViewModel>().value
    }

    override fun initView() {
        mViewModel?.getPageItem(FragPagerItems(mContext))?.apply {
            val pagerAdapter = FragPagerItemAdapter(childFragmentManager, this)
            viewPager?.apply {
                adapter = pagerAdapter
                offscreenPageLimit = 2
            }
            tableLayout?.apply {
                setViewPager(viewPager)
                setSelectedAnim()
            }
        }

    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    override fun destroyView() {
    }

}