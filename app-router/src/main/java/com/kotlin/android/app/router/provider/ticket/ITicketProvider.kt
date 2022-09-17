package com.kotlin.android.app.router.provider.ticket

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/21
 *
 * 票务
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_TICKET)
interface ITicketProvider : IBaseProvider {
    fun startMovieDetailsActivity(movieId: Long)
    fun startMovieShowtimeActivity(movieId: Long)

    /**
     * 跳转到影院排片页
     */
    fun startCinemaShowTimeActivity(cinemaId:Long,movieId:String,showMovieDate:String)

    /**
     * 从订单列表跳转到选座页面
     */
    fun startSeatSelectFromOrderListActivity(orderId:Long,reselectAgain:Boolean)

    /**
     * 选座页面
     */
    fun startSeatSelectActivity(showTimeId: Long, date: String, movieId: Long, cinemaId: Long)
}