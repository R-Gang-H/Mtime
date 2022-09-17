package com.kotlin.android.ticket.order.component.ui.orderlist

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.ticket.order.component.bean.ListViewBean
import com.kotlin.android.ticket.order.component.repository.TicketOrderListRepository
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/9/16
 * description:影片订单列表
 */
class TicketOrderListViewModel : BaseViewModel() {
    private val repo by lazy {
        TicketOrderListRepository()
    }

    //    三个月之前的订单列表
    private val orderListOutThreeMonths = BaseUIModel<ListViewBean>()
    val orderListOutThreeMonthsState = orderListOutThreeMonths.uiState

    //    三个月以内的订单列表
    private val orderListInThreeMonths = BaseUIModel<ListViewBean>()
    val orderListInThreeMonthsState = orderListInThreeMonths.uiState


    /**
     * 加载三个月以外的订单列表
     */
    fun getOrderListOutThreeMonths(isMore: Boolean) {
        viewModelScope.launch(main) {
            val result = withOnIO {
//                platformId默认为0（时光） orderType 1在线选座订单，电子券订单显示空页面
                repo.getTicketOrderListDataOutThreeMonths(isMore, 0, "1")
            }
            checkResult(result, error = { orderListOutThreeMonths.emitUIState(error = it) }, netError = { orderListOutThreeMonths.emitUIState(netError = it) }, success = { orderListOutThreeMonths.emitUIState(success = it, loadMore = isMore) },
                    empty = { orderListOutThreeMonths.emitUIState(isEmpty = true) })
        }
    }

    /**
     * 查询三个月内的订单列表
     */
    fun getOrderListInThreeMonths() {
        viewModelScope.launch(main) {
            val result = withOnIO {
                repo.getTicketOrderListDataInThreeMonths()
            }
            orderListInThreeMonths.checkResultAndEmitUIState(result)
        }


    }


}