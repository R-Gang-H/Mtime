package com.kotlin.android.film

import com.kotlin.android.app.router.provider.film.IFilmProvider
import com.kotlin.android.router.ext.getProvider

/**
 * Java代码支持类
 *
 * Created on 2022/5/15.
 *
 * @author o.s
 */
object JavaOpenSeatActivity {

    /**
     * 打开座位图
     */
    fun openSeatActivity(
        dId: String,
        lastOrderId: String? = null,
        movieId: String? = null,
        cinemaId: String? = null,
        showtime: String? = null,
    ) {
        getProvider(IFilmProvider::class.java)?.startSeatActivity(
            dId = dId,
            lastOrderId = lastOrderId,
            movieId = movieId,
            cinemaId = cinemaId,
            showtime = showtime,
        )
    }
}