package com.kotlin.android.ticket.order.component.provider

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.router.RouterManager
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ticket_order.ITicketOrderProvider

/**
 * create by lushan on 2020/9/16
 * description: 电影票订单组件
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_TICKET_ORDER)
class TicketOrderProvider:ITicketOrderProvider {
    /**
     * 跳转到电影票订单列表
     */
    override fun startTicketOrderListActivity(context: Context) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.TicketOrder.TICKET_ORDER_LIST_ACTIVITY,
                context = context)
    }

    override fun init(context: Context?) {
    }

}