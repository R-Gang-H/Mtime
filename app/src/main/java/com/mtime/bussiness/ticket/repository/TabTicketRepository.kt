package com.mtime.bussiness.ticket.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.RegionPublish

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/4/13
 * 描述:
 */
class TabTicketRepository: BaseRepository() {

    /**
     * 获取banner数据
     */
    suspend fun loadBanner(codes: String): ApiResult<RegionPublish> {
        return request {
            apiMTime.getRegionPublishList(codes)
        }
    }

}