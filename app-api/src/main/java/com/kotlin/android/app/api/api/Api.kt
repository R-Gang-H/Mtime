package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.api.config.Env
import com.kotlin.android.app.data.entity.*
import retrofit2.http.*

/**
 *
 * Created on 2020-01-10.
 *
 * @author o.s
 */
interface Api {

    @GET
    suspend fun getGateway(
            @Url url: String = Env.instance.gatewayUrl,
            @Query("json") json: Boolean = true
    ): ApiResponse<GatewayInfo>

    /**
     * 批量请求接口
     * POST ([Env.instance.batchUrl])
     *
     * postInfos	List<PostInfo>	必须  批量访问api请求参数
     *
     * PostInfo结构
     * host	    string	        是   LocalDns 属性名称，例： miscApiHost。参见 LocalDns
     * path	    string	        是   请求Path 。例：/hotnote/info.api
     * method	string	        是   请求方式 “GET” 或 “POST”
     * params	List<ParamInfo>	是   请求参数列表。params 必填 key=json value=true 参数
     *
     * ParamInfo结构
     * key	    string	        是   参数名称
     * value	string	        是   参数值
     */
    @POST
    @FormUrlEncoded
    suspend fun getBatch(
        @Field("postInfos") postJson: String,
        @Url url: String = Env.instance.batchUrl,
        @Field("json") json: Boolean = true
    ): ApiResponse<Batch>

}