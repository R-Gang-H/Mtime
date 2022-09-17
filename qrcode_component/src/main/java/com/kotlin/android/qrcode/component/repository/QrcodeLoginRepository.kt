package com.kotlin.android.qrcode.component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.CommBizCodeResult

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/12/30
 */
class QrcodeLoginRepository : BaseRepository() {

    /**
     * 扫码登陆 - 进行扫码登陆
     */
    suspend fun qrcodeLogin(uuid: String): ApiResult<CommBizCodeResult> {
        return request {
            apiMTime.qrcodeLogin(uuid)
        }
    }
}