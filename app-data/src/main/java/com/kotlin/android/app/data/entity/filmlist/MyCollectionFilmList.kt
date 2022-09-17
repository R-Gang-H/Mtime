package com.kotlin.android.app.data.entity.filmlist

import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/27
 * 描述:我收藏的片单
 **/
data class MyCollectionFilmList(
    val count: Long? = 0L,//总片单数
    val nextStamp: String? = "",//下一页标识
    val hasNext: Boolean? = false,//是否有下一页
    val myFavorites: MutableList<MyFavorites>? = mutableListOf()//我创建的片单
) : ProguardRule

data class MyFavorites(
    val numRead: Long? = 0L,//已看数量
    val userId: Long? = 0L,//创建者id
    val userNickName: String? = "",//创建者昵称
    val userAvatarUrl: String? = "",//创建者头像
    val filmListId: Long? = 0L,//片单id
    val filmListType: Long? = 0L,//片单类型 1榜单，2片单
    val coverUrl: String? = "",//封面图url
    val title: String? = "",//片单名称
    val synopsis: String? = "",//片单简介
    val enterTime: Long? = 0L,//创建时间（单位：毫秒）
    val lastModifyTime: Long? = 0L,//最后修改时间（单位：毫秒）
    val numMovie: Long? = 0L,//影片数量
    val status: Long? = 0L,//状态 10有效，100删除
    val userUserAuthType: Long? = 0L//1, "个人" 2, "影评人" 3, "电影人" 4, "机构"
) : ProguardRule {

    fun isCert(): Boolean {
        return userUserAuthType?.let { it > 1 } ?: false
    }
}