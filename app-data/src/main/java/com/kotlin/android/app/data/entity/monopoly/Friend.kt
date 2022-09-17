package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule

/**
 * 好友
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class Friend(
        var userId: Long = 0, // 用户Id
        var nickName: String? = null,
        var avatarUrl: String? = null,
        var isOnline: Boolean = false, // 是否在线
        var cardGold: Long = 0, // 金币数
        var suitCount: Long = 0, // 套装数
        var openPocketRemainCount: Long = 0, // 开放口袋剩余数量
        var openPocketCount: Long = 0, // 开放口袋数量
        var isRobot: Boolean = false
) : ProguardRule, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(userId)
        parcel.writeString(nickName)
        parcel.writeString(avatarUrl)
        parcel.writeByte(if (isOnline) 1 else 0)
        parcel.writeLong(cardGold)
        parcel.writeLong(suitCount)
        parcel.writeLong(openPocketRemainCount)
        parcel.writeLong(openPocketCount)
        parcel.writeByte(if (isRobot) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Friend> {
        override fun createFromParcel(parcel: Parcel): Friend {
            return Friend(parcel)
        }

        override fun newArray(size: Int): Array<Friend?> {
            return arrayOfNulls(size)
        }
    }
}