package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule

/**
 * 机器人
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class Robot(
        var robotId: Long = 0, // 机器人Id
        var robotName: String? = null, // 机器人名称
        var openPocketCount: Long = 0, // 开放口袋数量
        var openPocketRemainCount: Long = 0, // 开放口袋剩余数量
        var openPocketCards: PocketCards? = null // 开放口袋卡片信息
) : ProguardRule, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readParcelable(PocketCards::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(robotId)
        parcel.writeString(robotName)
        parcel.writeLong(openPocketCount)
        parcel.writeLong(openPocketRemainCount)
        parcel.writeParcelable(openPocketCards, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Robot> {
        override fun createFromParcel(parcel: Parcel): Robot {
            return Robot(parcel)
        }

        override fun newArray(size: Int): Array<Robot?> {
            return arrayOfNulls(size)
        }
    }
}