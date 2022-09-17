package com.kotlin.android.app.router.provider.ticket_order

import android.content.Context
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * create by lushan on 2020/9/16
 * description: 订单provider
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_TICKET_ORDER)
interface ITicketOrderProvider:IBaseProvider{
    /**
     * 跳转到电影票订单详情
     */
    fun startTicketOrderListActivity(context: Context)
}