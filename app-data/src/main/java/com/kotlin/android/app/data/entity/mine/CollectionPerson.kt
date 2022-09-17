package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/14
 * description: 分页查询收藏应人呢
 */
data class CollectionPerson(
    var items: List<Item>? = mutableListOf(),//分页查询具体影人内容
) : CollectionBase(), ProguardRule {
    data class Item(
//            var collectTime: CommContent.UserCreateTime? = null,//收藏时间
        var obj: Person?//收藏影人
    ) : ProguardRule


    data class Person(
        var birthDate: String? = "",//生日
        var id: Long = 0L,//影人id
        var imgUrl: String? = "",//影人海报
        var nameCn: String? = "",//中文名
        var nameEn: String? = "",//英文名
        var profession: String? = "",//职业
        var rating: String? = "",//时光评分
        var mainMovies: ArrayList<MovieItem>? = arrayListOf()
    ) : ProguardRule

    data class MovieItem(
        var nameEn: String? = "",
        var id: Long = 0L,
        var name: String? = ""
    ) : ProguardRule
}

