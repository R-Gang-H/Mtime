package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/14
 * description:收藏影片
 */
data class CollectionMovie(
    var items: List<Item>? = mutableListOf(),//电影信息
) : CollectionBase(), ProguardRule {
    data class Item(
        var collectTime: CollectionTime? = null,//收藏时间
        var obj: Movie? = null//收藏影片
    ) : ProguardRule


    data class Movie(
        var genreTypes: String? = "",//类型
        var id: Long = 0L,//电影id
        var imgUrl: String?,//图片url
        var minutes: String?,//时长：分钟
        var name: String?,//中文名
        var nameEn: String?,//英文名
        var year: String?,//年
        var rating: String? = "",//时光评分
        var releaseArea: String?,//上映地区
        var releaseDate: String?,//上映日期
        var earliestReleaseDate: String?,//上映日期
        var mainActors: ArrayList<Person>? = arrayListOf(),//主要演员列表
        var mainDirectors: ArrayList<Person>? = arrayListOf(),//主要导演列表
        var play: Long? = 0L,//显示播放按钮 1播放
        var btnShow: Long? = 0L,
    ) : ProguardRule

    data class Person(
        var personNameEn: String? = "",
        var personNameCn: String? = "",
        var personId: Long = 0L
    ) : ProguardRule

    data class CollectionTime(
        var show: String? = "",
        var stamp: Long = 0L
    ) : ProguardRule
}

