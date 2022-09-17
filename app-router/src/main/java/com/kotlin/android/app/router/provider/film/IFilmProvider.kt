package com.kotlin.android.app.router.provider.film

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * 电影
 *
 * Created on 2022/2/8.
 *
 * @author o.s
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_FILM)
interface IFilmProvider : IBaseProvider {

    /**
     * 座位图页
     */
    fun startSeatActivity(
        dId: String,
        lastOrderId: String? = null,
        movieId: String? = null,
        cinemaId: String? = null,
        showtime: String? = null,
    )
}