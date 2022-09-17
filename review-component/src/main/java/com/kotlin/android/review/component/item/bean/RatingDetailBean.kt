package com.kotlin.android.review.component.item.bean

import com.kotlin.android.app.data.ProguardRule

data class RatingDetailBean(
        var img: String = "",
        var movieSubItemRatings: List<MovieSubItemRatingBean> = listOf(),
        var overallRating: Float = 0.0f,
        var ratingCount: Long = 0,
        var ratingCountRatios: List<RatingCountRatioBean> = listOf(),
        var ratingCountShow: String = "",
        var subItemRatingCount: Long = 0,
        var subItemRatingCountShow: String = ""
) : ProguardRule

data class MovieSubItemRatingBean(
        var index: Long = 0,
        var rating: Float = 0.0f,
        var title: String = ""
) : ProguardRule

data class RatingCountRatioBean(
        var ratio: Float = 0.0f,
        var title: String = ""
) : ProguardRule