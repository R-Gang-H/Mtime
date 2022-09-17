package com.kotlin.android.app.data.entity.filmlist

import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/27
 * 描述:我创建的片单
 **/
data class MyCreateFilmList(
    val count: Long? = 0L,//总片单数
    val nextStamp: String? = "",//下一页标识
    val hasNext: Boolean? = false,//是否有下一页
    val myCreates: MutableList<MyCreate>? = mutableListOf(),//我创建的片单-个人中心
    val myManuscriptsFilmLists: MutableList<MyCreate>? = mutableListOf()//我创建的片单-投稿
) : ProguardRule

data class MyCreate(
    val numRead: Long? = 0L,//已看数量
    val approvalStatusStr: String? = "",//审批状态，10待审核,20审核不通过,30审核通过
    val approvalStatus: Long? = 0L,//审批状态，10待审核,20审核不通过,30审核通过
    val filmListId: Long? = 0L,//片单id
    val filmListType: Long? = 0L,//片单类型 1榜单，2片单
    val coverUrl: String? = "",//封面图url
    val title: String? = "",//片单名称
    val synopsis: String? = "",//片单简介
    val enterTime: Long? = 0L,//创建时间（单位：毫秒）
    val lastModifyTime: Long? = 0L,//最后修改时间（单位：毫秒）
    val numMovie: Long? = 0L,//影片数量
) : ProguardRule {
    fun getNumReadL(): Long {
        return numRead ?: 0L
    }

    fun getNumMovieL(): Long {
        return numMovie ?: 0L
    }

    /**
     * 是否审核通过
     */
    fun isApproved():Boolean{
        return approvalStatus != null && approvalStatus == 30L
    }
}