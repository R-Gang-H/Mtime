package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 联合搜索/search/unionSearch
 * 影人作品
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
data class PersonMovie(
        val movieId: Long? = null, // 影片Id
        val title: String? = null, // 标题
        val url: String? = null, // 封面图片地址
        val year: Long? = null // 年代
) : ProguardRule, Serializable