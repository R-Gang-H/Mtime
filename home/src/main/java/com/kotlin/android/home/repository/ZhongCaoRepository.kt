package com.kotlin.android.home.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.common.CommSubType
import com.kotlin.android.app.data.entity.common.CommSubTypeList
import com.kotlin.android.home.ui.bean.BannerItem

class ZhongCaoRepository: BaseRepository() {

    /**
     * 获取banner数据
     */
    suspend fun loadBanner(): ApiResult<List<BannerItem>> {
        return request(
            converter = {
                BannerItem.converter2BannerItems(it)
            }
        ) {
            apiMTime.getRegionPublishList(CommConstant.RCMD_REGION_ZHONG_CAO_BANNER)
        }
    }
    
    suspend fun loadSubClass(): ApiResult<CommSubTypeList> {
        return request { apiMTime.getCommunityRcmdSubtypes(7) }

//        return ApiResult.Success(
//            CommSubTypeList(
//                subTypes = (0..8).map {
//                    CommSubType(
//                        subTypeId = it.toLong(),
//                        name = "子类型$it"
//                    )
//                }
//            )
//        )
    }
}