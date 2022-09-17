package com.kotlin.android.ticket.order.component.ui.orderlist.history

import androidx.fragment.app.viewModels
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.ticket.order.component.databinding.FragHistoryTicketCinemaOrderBinding
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.frag_history_ticket_cinema_order.*

/**
 * create by lushan on 2020/10/23
 * description: 电子券、影院直销订单直接显示空页面
 */
class HistoryTicketCinemaOrderFragment : BaseVMFragment<HistoryTicketOrderViewModel, FragHistoryTicketCinemaOrderBinding>() {

    override fun initVM(): HistoryTicketOrderViewModel {
        return viewModels<HistoryTicketOrderViewModel>().value
    }

    override fun initView() {
        stateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    override fun destroyView() {

    }
}