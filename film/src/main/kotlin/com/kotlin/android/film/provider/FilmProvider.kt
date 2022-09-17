package com.kotlin.android.film.provider

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.router.RouterManager
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.film.IFilmProvider
import com.kotlin.android.film.*

/**
 *
 * Created on 2022/2/8.
 *
 * @author o.s
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_FILM)
class FilmProvider : IFilmProvider {
    /**
     * 座位图页
     */
    override fun startSeatActivity(
        dId: String,
        lastOrderId: String?,
        movieId: String?,
        cinemaId: String?,
        showtime: String?,
    ) {
        Bundle().apply {
            putString(KEY_SEATING_DID, dId)
            putString(KEY_SEATING_LAST_ORDER_ID, lastOrderId)
            putString(KEY_MOVIE_ID, movieId)
            putString(KEY_CINEMA_ID, cinemaId)
            putString(KEY_SHOWTIME_DATE, showtime)
        }.also {
            RouterManager.instance.navigation(
                path = RouterActivityPath.Film.PAGER_SEAT,
                bundle = it
            )
        }
    }
}