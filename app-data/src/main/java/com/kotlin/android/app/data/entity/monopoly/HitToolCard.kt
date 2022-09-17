package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule

/**
 * 命中道具卡信息
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class HitToolCard(
        var hackerCardCover: String? = null, // 黑客卡封面图
        var isHitHackerCard: Boolean = false, // 是否命中黑客卡
        var isHitSlaveCard: Boolean = false, // 是否命中奴隶卡
        var toolCardDescList: List<String>? = null // 道具卡效果描述列表
) : ProguardRule, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.createStringArrayList()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(hackerCardCover)
        parcel.writeByte(if (isHitHackerCard) 1 else 0)
        parcel.writeByte(if (isHitSlaveCard) 1 else 0)
        parcel.writeStringList(toolCardDescList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HitToolCard> {
        override fun createFromParcel(parcel: Parcel): HitToolCard {
            return HitToolCard(parcel)
        }

        override fun newArray(size: Int): Array<HitToolCard?> {
            return arrayOfNulls(size)
        }
    }
}