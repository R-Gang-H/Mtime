package com.kotlin.android.app.data.entity.review

import com.kotlin.android.app.data.entity.common.MovieSubItemRating
import com.kotlin.android.app.data.ProguardRule

data class RatingDetail(
        var img: String? = "",
        var movieSubItemRatings: List<MovieSubItemRating>? = listOf(),
        var overallRating: Float = 0.0f,
        var ratingCount: Long = 0,
        var ratingCountRatios: List<RatingCountRatio>? = listOf(),
        var ratingCountShow: String? = "",
        var subItemRatingCount: Long = 0,
        var subItemRatingCountShow: String? = ""
) : ProguardRule {

    data class RatingCountRatio(
            var ratio: Float = 0.0f,
            var title: String? = ""
    ) : ProguardRule
}