package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.upload.ApplyUpload
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * create by lushan on 2022/4/13
 * des:上传音视频
 **/
interface ApiUpload {
    companion object {
        const val UPLOAD_APPLY_UPLOAD = "/video/apply_upload_v2"//发起音视频上传
        const val UPLOAD_COMPLETE_UPLOAD = "/video/complete_upload_v2"//上传完成
    }

    /**
     * 上传-发起上传(/video/apply_upload)
     * 用于音视频文件上传前获取token
     * GET(/video/apply_upload)
     * @param   fileName    String  源文件名 必填
     * @param   mediaType   String 媒体类型：视频 video，音频 audio （音频只允许传mp3格式的文件） 必填
     */
    @GET(UPLOAD_APPLY_UPLOAD)
    suspend fun getApplyUpload(
        @Query("fileName") fileName: String,
        @Query("mediaType") mediaType: String
    ): ApiResponse<ApplyUpload>

    /**
     * 上传-上传完成(/video/complete_upload)
     * 上传完成后的工作，比如转码、更新数据库
     * GET(/video/complete_upload)
     * @param   video_id    Long    视频ID 必选
     * @param   media_id    String  腾讯云返回fileId 必选
     * @param   media_url   String  腾讯云返回视频地址 必选
     * @param   need_transcoding    Boolean 是否需要转码 音频不需要 false，视频需要转码 true 必选
     * @param   transcoding_format  String  需要转码的直接传转码格式，多个用英文逗号分割：480p,720p,1080p 当need_transcoding=true时必填，false时不填
     */
    @GET(UPLOAD_COMPLETE_UPLOAD)
    suspend fun getCompleteUpload(
        @Query("video_id") video_id: Long,
        @Query("media_id") media_id: String,
        @Query("media_url") media_url: String,
        @Query("need_transcoding") need_transcoding: Boolean,
        @Query("transcoding_format") transcoding_format: String
    ): ApiResponse<CommBizCodeResult>

}