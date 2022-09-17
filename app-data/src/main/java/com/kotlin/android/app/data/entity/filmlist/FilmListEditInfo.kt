package com.kotlin.android.app.data.entity.filmlist

import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: SunHao
 * 创建时间: 2022/4/8
 * 描述:片单编辑
 **/
data class FilmListEditInfo(
    val id: Long? = 0L,//片单id
    val title: String? = "",//片单名称
    val synopsis: String? = "",// 片单简介
    val coverUrl: String? = "",//封面图url(原图)
    val coverFieldId: String? = "",//封面图FiledId
    val atlasUrl: String? = "",//图集
    val numMovie: Long? = 0L,//影片数量
    val movies: List<EditMovie>? = listOf(),
    val privacyStatus: Long? = 1L//隐私状态：int 枚举FilmListPrivacyStatusEnum：1公开，2私密
) : ProguardRule

data class EditMovie(
    val imageUrl: String? = "",
    val movieId: Long? = 0L,
    val titleCn: String? = "",
    val titleEn: String? = ""
) : ProguardRule
        