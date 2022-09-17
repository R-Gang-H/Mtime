package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule

/**
 * @desc 好友和我的口袋卡片bean
 * @author zhangjian
 * @date 2020/9/29 16:14
 */
data class PocketCards(
        var cardList: List<Card>? = null, // 卡片列表
//        var hitToolCard: HitToolCard? = null, // 命中道具卡信息
        var pocketCount: Long? = 0L // 卡位数量
) : ProguardRule, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.createTypedArrayList(Card),
//            parcel.readParcelable(HitToolCard::class.java.classLoader),
            parcel.readValue(Long::class.java.classLoader) as? Long) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(cardList)
//        parcel.writeParcelable(hitToolCard, flags)
        parcel.writeValue(pocketCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PocketCards> {
        override fun createFromParcel(parcel: Parcel): PocketCards {
            return PocketCards(parcel)
        }

        override fun newArray(size: Int): Array<PocketCards?> {
            return arrayOfNulls(size)
        }
    }
}