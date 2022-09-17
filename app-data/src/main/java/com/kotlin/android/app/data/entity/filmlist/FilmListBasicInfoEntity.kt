package com.kotlin.android.app.data.entity.filmlist

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 片单详情-上半部分
 */
data class FilmListBasicInfoEntity(
    val approvalStatus: Long?,//审批状态，10待审核,20审核不通过,30审核通过
    val approvalStatusStr: String?,//审批状态，10待审核,20审核不通过,30审核通过
    val coverUrl: String?,//封面图url
    val enterTime: Long?,//创建时间（单位：毫秒）
    val favorites: Boolean = false,//当前用户是否已收藏此片单 true已收藏，false未收藏
    val filmListId: Long?,//片单id
    val filmListType: Long?,//片单类型 1榜单，2片单
    val lastModifyTime: Long?,//最后修改时间（单位：毫秒）
    val numFavorites: Long?,//收藏数
    var numMovie: Long?,//影片数量
    var numRead: Long?,//已看数量
    val synopsis: String?,//片单简介
    val title: String?,//片单名称
    val userAvatarUrl: String?,//创建者头像
    val userId: Long?,//创建者id
    val userNickName: String?//创建者昵称
) : ProguardRule

/**
 * 片单详情下半部分
 */
data class FilmListPageMoviesEntity(
    val filmListId: Long?,//片单id
    val filmListType: Long?,//片单类型 1榜单，2片单
    val hasNext: Boolean = false,//是否有下一页
    val movies: MutableList<Movy>?,
    val nextStamp: String? = "",//下一页标识
    val numPlayable: Long?,//可播放数量
    val numUnread: Long?//未看数量
) : ProguardRule

data class Movy(
    val date: String?,//时间轴字段 如果是当前年，显示：月日 如果是去年，显示：年月日 如果翻页，与上一页最后一条日期相同，此字段为空字符串
    val filmTypes: String? = "",//影片类型
    val imageUrl: String?,//影片海报（常规封面图）
    val mainActors: String? = "",//主演
    val movieFormalVideos: List<MovieFormalVideo>?,//电影正片信息
    val movieId: Long?,//影片id
    val playableTag: Boolean = false,//可播放标记:true可播放，false不能播放
    val rating: String?,//时光评分
    val readTag: Boolean = false,//看过标记:true已看，false未看
    val serialNumber: String?,//榜单顺序号
    val synopsis: String?,//影片简介
    val titleCn: String?,//影片中文名
    val titleEn: String?,//影片英文名
    val year: String?//影片年份
) : ProguardRule

data class MovieFormalVideo(
    val h5Url: String?,//H5地址
    val iconUrl: String?,//合作商Logo
    val pcUrl: String?,//pc地址
    val platformName: String?//合作平台
) : ProguardRule

/**
 * 片单分享
 */
data class FilmListShareEntity(
    val coverUrl: String?,//封面图url
    val enterTime: Long?,//创建时间（单位：毫秒）
    val filmListId: Long?,//片单id
    val filmListType: Long?,//片单类型 1榜单，2片单
    val lastModifyTime: Long?,// 最后修改时间（单位：毫秒）
    val shareMovies: List<ShareMovy>,//影片列表
    val synopsis: String?,//片单简介
    val title: String?,//片单名称
    val userAvatarUrl: String?,//创建者头像
    val userId: Long?,//创建者id
    val userNickName: String?//创建者昵称
) : ProguardRule

data class ShareMovy(
    val imageUrl: String?,//影片海报（常规封面图）
    val movieId: Long?,//影片id
    val serialNumber: String?,//榜单序号
    val titleCn: String?,//影片中文名
    val titleEn: String?,//影片英文名
    val year: String?//影片年份
) : ProguardRule