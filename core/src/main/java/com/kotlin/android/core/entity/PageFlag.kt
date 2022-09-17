package com.kotlin.android.core.entity

import android.os.Parcel
import android.os.Parcelable

const val PAGE_FLAG = "page_flag"

/**
 * 跳转页面标示对象。
 * [subFlag] 无限延伸。
 *
 * 例如：
 * PageFlag(
        position = 1, // Tab 影片（0）/影城（1）
        key = "film",
        subFlag = PageFlag(
            position = 1, // Switch 影片（0）/影院（1）
            key = "xxx",
            subFlag = PageFlag(
                position = 1, // 正在热映（0）/即将上映（1）
            )
        )
    )
 *
 * Created on 2022/1/7.
 *
 * @author o.s
 */
data class PageFlag(
    val position: Int = 0, // 页面位置从 0 开始
    val key: String = "", // 页面表示关键字
    val subFlag: PageFlag? = null, // 子页面标识符
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().orEmpty(),
        parcel.readParcelable(PageFlag::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(position)
        parcel.writeString(key)
        parcel.writeParcelable(subFlag, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PageFlag> {
        override fun createFromParcel(parcel: Parcel): PageFlag {
            return PageFlag(parcel)
        }

        override fun newArray(size: Int): Array<PageFlag?> {
            return arrayOfNulls(size)
        }
    }

}