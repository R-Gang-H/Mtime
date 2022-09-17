package com.kotlin.android.review.component.item.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.review.MovieReviewList

/**
 * @author vivian.wei
 * @date 2020/12/28
 * @desc 影片影评请求接口数据类
 */
class MovieReviewRepository : BaseRepository() {

    /**
     * 获取影片长评列表
     *
     * @param movieId      电影Id
     * @param pageIndex    当前页索引
     * @param pageSize     每页记录数（默认20条）
     * @param orderType    排序类型：1最热（默认） 2最新
     */
    suspend fun getMovieLongComments(
        movieId: String, pageIndex: Long, pageSize: Long,
        orderType: Long
    ): ApiResult<MovieReviewList> {
        return request {
            apiMTime.getMovieLongComments(movieId, pageIndex, pageSize, orderType)
        }
    }

    /**
     * 获取影片短评列表
     *
     * @param movieId      电影Id
     * @param pageIndex    当前页索引
     * @param pageSize     每页记录数（默认20条）
     * @param orderType    排序类型：1最热（默认） 2最新
     */
    suspend fun getMovieComments(movieId: String, pageIndex: Long, pageSize: Long, orderType: Long):
            ApiResult<MovieReviewList> {
        return request {
            apiMTime.getMovieComments(movieId, pageIndex, pageSize, orderType)
        }
    }

    /**
     * 赞/取消赞
     * @param action    动作 1.点赞 2.取消点赞
     * @param objType   点赞主体类型
     * @param objId     点赞主体
     */
    suspend fun praiseUp(action: Long, objType: Long, objId: Long): ApiResult<CommBizCodeResult> {
        return request {
            apiMTime.praiseUp(action, objType, objId)
        }
    }

    /**
     * 踩/取消踩
     * @param action    动作 1.点踩 2.取消点
     * @param objType   点踩主体类型
     * @param objId     点踩主体
     */
    suspend fun praiseDown(action: Long, objType: Long, objId: Long): ApiResult<CommBizCodeResult> {
        return request {
            apiMTime.praiseDown(action, objType, objId)
        }
    }

}