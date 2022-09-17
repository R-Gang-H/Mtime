package com.kotlin.android.app.data.entity.filmlist

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.search.FilmList

/**
 * 片单搜索
 */
data class FilmListSearchEntity(
    var totalCount: Long? = null,                // 片单总数
    var filmListItems: List<FilmList>? = null,           // 片单列表
) : ProguardRule