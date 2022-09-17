package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.mine.CommonPush
import com.kotlin.android.app.data.entity.mine.SuccessErrorResultBean
import com.kotlin.android.app.data.entity.mine.UpdateMemberInfoBean
import com.kotlin.android.app.data.entity.mine.UserBindViewBean
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiSetting {

    companion object {
        //修改昵称
        const val CHANGE_NICKNAME = "/user/user/nickname/edit.api"

        //修改会员基本信息（生日1/居住地2/签名3）
        const val UPDATE_MEMBERINFO = "/user/account/updateMemberInfo.api"

        // 修改性别
        const val UPDATE_SEX = "/user/user/updateUserSex.api"

        // 用户查询三方绑定信息
        const val USER_BIND_LIST = "/user/user/oauth/band/list.api"

        // 用户解除三方绑定
        const val USER_UNBIND = "/user/user/oauth/unbind.api"

        // 获取消息通知设置
        const val COMMON_PUSH_GET = "/common/push/getMessageConfigesByDevice.api"

        // 设置消息通知
        const val COMMON_PUSH_SET = "/common/push/setMessageConfigs.api"
    }

    @POST(CHANGE_NICKNAME)
    @FormUrlEncoded
    suspend fun getSaveOrder(
        @Field("nickname") nickname: String,
    ): ApiResponse<SuccessErrorResultBean>

    @POST(UPDATE_MEMBERINFO)
    @FormUrlEncoded
    suspend fun updateMemberInfo(
        @Field("birthday") birthday: String? = null,
        @Field("locationId") locationId: String? = null,
        @Field("userSign") userSign: String? = null,
        @Field("type") type: String
    ): ApiResponse<UpdateMemberInfoBean>

    @POST(UPDATE_SEX)
    @FormUrlEncoded
    suspend fun updateSex(
        @Field("sex") sex: String,
    ): ApiResponse<SuccessErrorResultBean>

    @GET(USER_BIND_LIST)
    suspend fun getUserBind(): ApiResponse<UserBindViewBean>

    @POST(USER_UNBIND)
    @FormUrlEncoded
    suspend fun userUnbind(@Field("platformId") platformId: Int): ApiResponse<SuccessErrorResultBean>

    @GET(COMMON_PUSH_GET)
    suspend fun getCommonPush(): ApiResponse<CommonPush>

    @POST(COMMON_PUSH_SET)
    suspend fun setCommonPush(@Body body: RequestBody): ApiResponse<SuccessErrorResultBean>
}