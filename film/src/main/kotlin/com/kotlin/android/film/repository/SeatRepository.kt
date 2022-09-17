package com.kotlin.android.film.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.film.seat.AutoSeatInfo
import com.kotlin.android.app.data.entity.film.seat.SeatInfo

/**
 * 座位图
 *
 * Created on 2022/2/8.
 *
 * @author o.s
 */
class SeatRepository : BaseRepository() {

    /**
     * 获取实时座位图分区
     */
    suspend fun getSeatInfo(
        dId: String,
        mobile: String? = null,
    ): ApiResult<SeatInfo> {
        return request {
            apiCinema.postSeatInfo(dId = dId, mobile = mobile)
        }
    }

    /**
     * 自动选座
     */
    suspend fun postAutoSeat(
        showtimeId: Long,
        count: Long,
    ): ApiResult<AutoSeatInfo> {
        return request {
            apiCinema.postAutoSeat(showtimeId = showtimeId, count = count)
        }
    }
}