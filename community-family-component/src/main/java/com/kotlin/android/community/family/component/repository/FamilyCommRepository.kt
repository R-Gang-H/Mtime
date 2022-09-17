package com.kotlin.android.community.family.component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.common.CommonResultExtend

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/9/9
 */
class FamilyCommRepository : BaseRepository() {

    /**
     * 加入家族
     */
    suspend fun <T> joinFamily(
        id: Long,
        extend: T
    ): ApiResult<CommonResultExtend<CommonResult, T>> {
        return request(
            converter = {
                CommonResultExtend(
                    result = it,
                    extend = extend
                )
            },
            api = {
                apiMTime.joinGroup(id)
            })
    }

    /**
     * 退出家族
     */
    suspend fun <T> outFamily(id: Long, extend: T): ApiResult<CommonResultExtend<CommonResult, T>> {
        return request(
            converter = {
                CommonResultExtend(
                    result = it,
                    extend = extend
                )
            },
            api = {
                apiMTime.outGroup(id)
            })
    }

}