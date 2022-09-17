package com.kotlin.android.app.api.api

import androidx.annotation.Nullable
import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.PersonDetail
import com.kotlin.android.retrofit.CancelManager
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * 时光网APP老板票务API接口服务：
 *
 * Created on 2020/6/18.
 *
 * @author o.s
 */
interface ApiMTimeTicket {

    // https://ticket-api-m.mtime.cn/person/detail.api?personId=956786&cityId=290
    companion object {
        const val PERSON_DETAIL = "/person/detail.api"
    }

    /**
     * 获取适用活动提示信息列表
     * GET ("/person/detail.api")
     *
     * personId Long    必填  xxx
     * cityId   Int     必填  xxx
     */
    @GET(PERSON_DETAIL)
    suspend fun getPersonDetail(
            @Header(CancelManager.HEADER_CANCEL_TAG) @Nullable tag: String,
            @Query("personId") personId: Long = 956786,
            @Query("cityId") cityId: Int = 290
//        @Query("json") json: Boolean = true
    ): ApiResponse<PersonDetail>

}