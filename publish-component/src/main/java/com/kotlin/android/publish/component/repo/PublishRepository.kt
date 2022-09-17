package com.kotlin.android.publish.component.repo

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.common.StatusResult
import com.kotlin.android.app.data.entity.community.content.CheckReleasedResult
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.record.PostRecord
import com.kotlin.android.app.data.entity.community.record.RecordId
import com.kotlin.android.app.data.entity.movie.LatestComment
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.retrofit.toRequestBody

/**
 * 发布社区内容：
 *
 * Created on 2020/10/9.
 *
 * @author o.s
 */
class PublishRepository : BaseRepository() {

    suspend fun loadRecordId(@ContentType type: Long): ApiResult<RecordId> {
        return request {
            apiMTime.postCommunityRecordId(type)
        }
    }

    suspend fun postRecord(record: PostRecord): ApiResult<CommBizCodeResult> {
        return request {
            apiMTime.postCommunityRecord(record.toRequestBody())
        }
    }

    suspend fun getRecord(@ContentType type: Long, id: Long): ApiResult<CommunityContent> {
        return request {
            apiMTime.getCommunityRecord(GlobalDimensionExt.getDigitsCurrentCityId(), type, id)
        }
    }

    suspend fun getMovieRating(
        movieId: Long,
        rating: Double,
        subItemRating: String
    ): ApiResult<StatusResult> {
        return request {
            apiMTime.getMovieRating(movieId, rating, subItemRating)
        }
    }

    suspend fun getMovieLatestComment(movieId: Long): ApiResult<LatestComment> {
        return request {
            apiMTime.getMovieLatestComment(movieId.toString())
        }
    }

    suspend fun postDeleteContent(
        @ContentType type: Long,
        contentId: Long
    ): ApiResult<CommBizCodeResult> {
        return request {
            apiMTime.postDeleteContent(type, contentId)
        }
    }

}