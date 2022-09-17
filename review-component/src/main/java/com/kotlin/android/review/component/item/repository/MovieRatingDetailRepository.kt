package com.kotlin.android.review.component.item.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.review.component.item.bean.MovieSubItemRatingBean
import com.kotlin.android.review.component.item.bean.RatingCountRatioBean
import com.kotlin.android.review.component.item.bean.RatingDetailBean

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/25
 */
class MovieRatingDetailRepository : BaseRepository() {

    suspend fun getMovieRatingDetail(movieId: Long): ApiResult<RatingDetailBean> {
        return request(
            converter = {
                RatingDetailBean(
                    img = it.img.orEmpty(),
                    movieSubItemRatings = it.movieSubItemRatings?.map { itemRating ->
                        MovieSubItemRatingBean(
                            index = itemRating.index,
                            rating = itemRating.rating ?: 0f,
                            title = itemRating.title.orEmpty()
                        )
                    } ?: emptyList(),
                    overallRating = it.overallRating,
                    ratingCount = it.ratingCount,
                    ratingCountRatios = it.ratingCountRatios?.map { itemRatio ->
                        RatingCountRatioBean(
                            ratio = itemRatio.ratio,
                            title = itemRatio.title.orEmpty()
                        )
                    } ?: emptyList(),
                    ratingCountShow = it.ratingCountShow.orEmpty(),
                    subItemRatingCount = it.subItemRatingCount,
                    subItemRatingCountShow = it.subItemRatingCountShow.orEmpty()
                )
            },
            api = {
                apiMTime.getMovieRatingDetail(movieId)
            })
    }
}