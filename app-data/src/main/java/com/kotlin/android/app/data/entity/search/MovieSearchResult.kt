package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule

/**
 *
 *找电影 筛选电影 结果bean
 */
data class MovieSearchResult(
    var total: Long = 0L,
    var movies: List<Movie>? = arrayListOf()
) : ProguardRule