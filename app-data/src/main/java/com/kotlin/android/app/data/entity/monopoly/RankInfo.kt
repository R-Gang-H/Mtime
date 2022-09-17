package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule

/**
 * 排行榜信息
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class RankInfo(
        var rankType: Long = 0, // 排行榜分类：1昨日道具狂人，2昨日衰人，3昨日交易达人，4昨日收藏大富翁，5金币大富翁，6套装组合狂
        var rankName: String? = null, // 排行榜名称
        var ranking: Long? = null // 排名
) : ProguardRule, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(rankType)
        parcel.writeString(rankName)
        parcel.writeValue(ranking)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RankInfo> {
        override fun createFromParcel(parcel: Parcel): RankInfo {
            return RankInfo(parcel)
        }

        override fun newArray(size: Int): Array<RankInfo?> {
            return arrayOfNulls(size)
        }
    }
}