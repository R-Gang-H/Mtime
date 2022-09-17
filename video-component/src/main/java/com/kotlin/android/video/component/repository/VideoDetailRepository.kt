package com.kotlin.android.video.component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.comment.component.repository.DetailBaseRepository
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.community.praisestate.PraiseState
import com.kotlin.android.app.data.entity.video.VideoDetail
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.app.data.entity.video.VideoPlayUrl

/**
 * create by lushan on 2020/9/2
 * description: 预告片
 */
class VideoDetailRepository : DetailBaseRepository() {
    /**
     * 加载预告片详情
     */
    suspend fun loadVideoDetail(videoId: Long, locationId: Long): ApiResult<VideoDetail> {
        return request { apiMTime.getMovieVideoDetail(videoId, locationId) }
    }


    /**
     * 获取预告片详情地址
     */
    suspend fun getVideoPraiseState(videoId: Long): ApiResult<PraiseState> {
        return request {
            apiMTime.getPraiseState(
                CommConstant.PRAISE_OBJ_TYPE_MOVIE_TRAILER,
                videoId
            )
        }
    }

}