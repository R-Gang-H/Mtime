package com.kotlin.android.ticket.order.component.ui.orderlist.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.kotlin.android.core.BaseActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.ticket.order.component.R
import kotlinx.android.synthetic.main.activity_history_order_list.*

/**
 * 三个月前历史订单
 */
class HistoryOrderListActivity : BaseActivity() {
    private var ticketOrderFragment: HistoryTicketOrderFragment? = null//电影票
    private var electronicOrderFragment: HistoryTicketCinemaOrderFragment? = null//电子券fragmetn
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_order_list)
        backIv?.onClick {//返回按钮
            finish()
        }
        ticketOrderTv?.onClick {//电影票
            showOrderFragment(true)
        }
        electronicTv?.onClick {//电影券
            showOrderFragment(false)
        }
        showOrderFragment(true)//进入显示电影票订单

    }

    private fun showOrderFragment(isTicket: Boolean) {
        setTitleBackground(isTicket)
        backIv?.setImageDrawable(getDrawable(R.drawable.icon_back)?.mutate())
        val beginTransaction = supportFragmentManager.beginTransaction()
        hideAll(beginTransaction)
        val fragment = getFragment(isTicket)
        if (fragment.isAdded.not()) {
            beginTransaction.add(R.id.containerCL, fragment)
        }
        beginTransaction.show(fragment)
        beginTransaction.commit()

    }

    private fun hideAll(beginTransaction: FragmentTransaction) {
        ticketOrderFragment?.apply {
            beginTransaction.hide(this)
        }
        electronicOrderFragment?.apply {
            beginTransaction.hide(this)
        }
    }

    /**
     * 设置title背景
     */
    private fun setTitleBackground(ticket: Boolean) {
        ShapeExt.setShapeColorAndCorner(orderChangeCL, R.color.color_f9f9fb, 12)
        ShapeExt.setShapeColorAndCorner(if (ticket) ticketOrderTv else electronicTv, R.color.color_ffffff, 12)
        if (ticket) {
            electronicTv?.background = null
        } else {
            ticketOrderTv?.background = null
        }

    }

    private fun getFragment(isTicket: Boolean): Fragment {
        return if (isTicket) {
            ticketOrderFragment ?: HistoryTicketOrderFragment()
        } else {
            electronicOrderFragment ?: HistoryTicketCinemaOrderFragment()
        }
    }


}