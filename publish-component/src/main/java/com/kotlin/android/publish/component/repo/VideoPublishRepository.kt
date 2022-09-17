package com.kotlin.android.publish.component.repo

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_VIDEO
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.common.CommonResultExtend
import com.kotlin.android.app.data.entity.common.PublishResult
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.content.ContentInit
import com.kotlin.android.app.data.entity.community.record.PostContent
import com.kotlin.android.app.data.entity.upload.ApplyUpload
import com.kotlin.android.comment.component.repository.DetailBaseRepository
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.retrofit.toRequestBody

/**
 * create by lushan on 2022/4/8
 * des:视频发布
 **/
class VideoPublishRepository : DetailBaseRepository() {

    /**
     * 加载视频分类列表
     */
    suspend fun postMovieClassifies(): ApiResult<ContentInit> {
        val body = arrayMapOf<String, Any>(
            "type" to CONTENT_TYPE_VIDEO,
            "include" to arrayListOf<String>("CLASSIFIES")
        ).toRequestBody()

        return request(api = { apiMTime.postContentInit(body) }, converter = { it })
    }

    suspend fun getDetail(contentId: Long, recId: Long): ApiResult<CommunityContent> {
        val locationId = GlobalDimensionExt.getDigitsCurrentCityId()
        return request {
            apiMTime.getCommunityContent(
                getContentDetailParams(
                    locationId,
                    CONTENT_TYPE_VIDEO,
                    contentId,
                    recId
                )
            )
        }
    }

    /**
     * 保存内容
     */
    suspend fun postVideoDetail(
        params: PostContent,
        isPublish: Boolean,
        finished: Boolean
    ): ApiResult<CommonResultExtend<PublishResult, Pair<Boolean, Boolean>>> {
        return request(
            api = { apiMTime.postCommunityContent(params.toRequestBody()) },
            converter = {
                CommonResultExtend(it, Pair(isPublish, finished))
            })
    }

    /**
     * 发起上传
     */
    suspend fun applyUpload(fileName: String): ApiResult<ApplyUpload> {
        return request { apiMTime.getApplyUpload(fileName, "video") }
    }

    /**
     * 上传完成
     */
    suspend fun completeUpload(
        videoId: Long,
        mediaId: String,
        mediaUrl: String
    ): ApiResult<CommBizCodeResult> {
        return request { apiMTime.getCompleteUpload(videoId, mediaId, mediaUrl, true, "720P") }
    }
}