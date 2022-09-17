package com.kotlin.android.app.data.entity.movie

import com.kotlin.android.app.data.entity.common.MovieSubItemRating
import com.kotlin.android.app.data.ProguardRule

data class UserInfo(
    val attitude: Long? = null, // 用户对电影态度：-1未表态 0看过 1想看
    val img: String? = null, // 用户头像
    val name: String? = null, // 用户昵称
    val rating: Double? = 0.0, // 用户对电影评分
    var userMovieSubItemRatings: List<MovieSubItemRating>? = listOf() //用户对电影打的分项分 - 202105新增
//    val directorRating: Double,
//    val impressionRating: Double,
//    val musicRating: Double,
//    val pictureRating: Double,
//    val showRating: Double,
//    val storyRating: Double
) : ProguardRule