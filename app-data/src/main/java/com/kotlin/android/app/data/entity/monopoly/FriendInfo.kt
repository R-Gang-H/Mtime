package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule

/**
 * 好友数量（我的主页专用）
 *
 * Created on 2021/7/22.
 *
 * @author o.s
 */
data class FriendInfo(
    var totalCount: Long, // 全部好友数量
    var onlineCount: Long, // 在线好友数量
) : ProguardRule, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(totalCount)
        parcel.writeLong(onlineCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FriendInfo> {
        override fun createFromParcel(parcel: Parcel): FriendInfo {
            return FriendInfo(parcel)
        }

        override fun newArray(size: Int): Array<FriendInfo?> {
            return arrayOfNulls(size)
        }
    }
}
