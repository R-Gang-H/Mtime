package com.kotlin.android.app.data.entity.filmlist

import com.kotlin.android.app.data.ProguardRule

/**
 * 片单推荐列表页面
 */
data class FilmListEntity(
    val categories: MutableList<Category>?,//全部分类
    val currCategoryId : Long?,//当前分类id
    val currCategoryName: String? = "",//当前分类名称
    val hasNext: Boolean = false,//是否有下一页
    val nextStamp: String = "",//下一页标识
    val pageRcmds: MutableList<PageRcmd>?//片单基本信息
) : ProguardRule

data class Category(
    var isSelect: Boolean = false,
    val categoryId : Long?,//分类id
    val categoryName: String? = "",//分类名称
) : ProguardRule

data class PageRcmd(
    val coverUrl: String? = "",//封面图url
    val filmListId: Long?,//片单id
    val filmListType: Long?,//片单类型 1榜单，2片单
    var numFavorites: Long?,//收藏数
    var numRead: Long?,//已看数量
    val userAvatarUrl: String? = "",//创建者头像
    val userId: Long?,//创建者id
    val userNickName: String?,//创建者昵称
    val enterTime : Long?,//创建时间（单位：毫秒）
    val lastModifyTime : Long?,//最后修改时间（单位：毫秒）
    var numMovie : Long?,//影片数量
    val title: String?,//片单名称
) : ProguardRule
