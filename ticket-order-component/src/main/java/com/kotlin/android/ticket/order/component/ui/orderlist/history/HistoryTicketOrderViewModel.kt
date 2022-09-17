package com.kotlin.android.ticket.order.component.ui.orderlist.history

import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.ticket.order.component.R
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems

/**
 * create by lushan on 2020/10/23
 * description:
 */
class HistoryTicketOrderViewModel : BaseViewModel() {
    fun getPageItem(creator: FragPagerItems): FragPagerItems {
        creator.add(titleRes = R.string.ticket_mtime_order, clazz = HistoryTicketMtimeOrderFragment::class.java)
        creator.add(titleRes = R.string.ticket_cinema_order, clazz = HistoryTicketCinemaOrderFragment::class.java)
        return creator
    }
}