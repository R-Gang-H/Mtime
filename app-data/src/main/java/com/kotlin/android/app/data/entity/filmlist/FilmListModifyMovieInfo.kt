package com.kotlin.android.app.data.entity.filmlist

import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: SunHao
 * 创建时间: 2022/4/11
 * 描述:已选电影
 **/
data class FilmListModifyMovieInfo(
    val movies: MutableList<ModifyMovie>? = mutableListOf()
) : ProguardRule

data class ModifyMovie(
    val filmListId: Long? = 0L,//片单id
    val movieId: Long? = 0L,//影片id
    val serialNumber: Long? = 0L,// 序号
    val enterTime: String? = "",//创建时间
    val nextStamp: Long? = 0L,//分页游标 雪花id
    val imageUrl: String? = "",//电影图片（常规封面图）
    val titleCn: String? = "",//中文片名
    val titleEn: String? = "",//英文片名
    val year: String? = "",//出品年代
) : ProguardRule