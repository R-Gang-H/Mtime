package com.kotlin.android.film.ui.seat

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.film.seat.AutoSeatInfo
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.film.seat.SeatInfo
import com.kotlin.android.film.repository.SeatRepository

/**
 * 座位图ViewModel
 *
 * Created on 2022/2/8.
 *
 * @author o.s
 */
class SeatViewModel : BaseViewModel() {
    private val repo by lazy { SeatRepository() }

    private val uiModel by lazy { BaseUIModel<SeatInfo>() }
    private val autoSeatUIModel by lazy { BaseUIModel<AutoSeatInfo>() }
    val uiState = uiModel.uiState
    val autoSeatUIState = autoSeatUIModel.uiState

    fun getSeatInfo(
        dId: String,
        mobile: String? = null,
    ) {
        call(
            uiModel = uiModel,
        ) {
            repo.getSeatInfo(
                dId = dId,
                mobile = mobile
            )
        }
    }

    fun autoSeat(
        showtimeId: Long,
        count: Long,
    ) {
        call(
            uiModel = autoSeatUIModel,
        ) {
            repo.postAutoSeat(
                showtimeId = showtimeId,
                count = count
            )
        }
    }
}