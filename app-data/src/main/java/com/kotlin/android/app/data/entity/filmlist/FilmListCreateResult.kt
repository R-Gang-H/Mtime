package com.kotlin.android.app.data.entity.filmlist

import android.os.Parcel
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: sunhao
 * 创建时间: 2022/3/11
 * 描述:创建片单Result
 **/
data class FilmListCreateResult(
    val bizCode: Long = -1L,// 业务返回码 0成功，1片单名称中包含敏感信息，请重新输入！，2片单简介中包含敏感信息，请重新输入！，3创建失败，请稍后再试
    val bizMsg: String? = "",//业务返回消息
    val simpleFilmList: SimpleFilmListInfo? = null,//片单信息
) : ProguardRule {

    data class SimpleFilmListInfo(
        val filmListId: Long = 0L,//片单id
        val filmListType: Long = 0L,//片单类型 1榜单，2片单
        val coverUrl: String? = "",//封面图url
        val title: String? = "",//片单名称
        val synopsis: String? = "",//片单简介
        val enterTime: Long = 0L,//创建时间（单位：毫秒）
        val enterTimeStr: String? = "",//创建时间 格式：yyyy-MM-dd HH:mm:ss
        val lastModifyTime: Long = 0L,//最后修改时间（单位：毫秒）
        val lastModifyTimeStr: String? = "",//最后修改时间 格式：yyyy-MM-dd HH:mm:ss
        val numMovie: Long = 0L,//影片数量
    ) : ProguardRule, Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readLong()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeLong(filmListId)
            parcel.writeLong(filmListType)
            parcel.writeString(coverUrl)
            parcel.writeString(title)
            parcel.writeString(synopsis)
            parcel.writeLong(enterTime)
            parcel.writeString(enterTimeStr)
            parcel.writeLong(lastModifyTime)
            parcel.writeString(lastModifyTimeStr)
            parcel.writeLong(numMovie)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SimpleFilmListInfo> {
            override fun createFromParcel(parcel: Parcel): SimpleFilmListInfo {
                return SimpleFilmListInfo(parcel)
            }

            override fun newArray(size: Int): Array<SimpleFilmListInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}