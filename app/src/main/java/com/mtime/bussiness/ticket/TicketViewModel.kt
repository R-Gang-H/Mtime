package com.mtime.bussiness.ticket

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.constant.CommConstant.RCMD_REGION_TICKET_BANNER
import com.kotlin.android.app.data.entity.common.RegionPublish
import com.kotlin.android.core.BaseViewModel
import com.mtime.bussiness.ticket.repository.TabTicketRepository

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/4/12
 * 描述: 底部导航-购票tab ViewModel
 */
class TicketViewModel: BaseViewModel() {

    private val repo by lazy { TabTicketRepository() }

    // 搜索结果列表
    private val mUIModel = BaseUIModel<RegionPublish>()
    val uIState = mUIModel.uiState

    /**
     * 获取banner数据
     */
    fun loadBanner() {
        call(
                uiModel = mUIModel,
        ) {
            repo.loadBanner(RCMD_REGION_TICKET_BANNER)
        }
    }

}