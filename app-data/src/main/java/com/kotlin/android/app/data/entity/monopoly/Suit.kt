package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule

/**
 * 套装
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class Suit(
        var suitId: Long = 0, // 套装Id
        var suitName: String? = null, // 套装名称
        var suitUserId: Long? = null, // 套装所属用户Id
        var suitCover: String? = null, // 套装封面图
        var suitClass: String? = null, // 套装级别
        var mixCount: Long = 0, // 已合成数量

        var suitType: Long = 1, // 套装类型：1普通（电影），2普通（影人），3限量套装

        var isBorder: Boolean = false, // （自定义）边界

        var cardCount: Long? = null, // 9611
        var cardList: List<Card>? = null,
        var description: String? = null, // 5f
        var earliestMixUser: EarliestMixUser? = null,
        var issueTime: Long? = null, // 7157

        var type: Long = 1L, // （自定义）type 用来区分标签
        var isSelected: Boolean = false, // （自定义）选中标记

        var suitCategoryId: Long = 1, // 分类Id：1简装版套装，2精装版套装，3终极版套装，4限量版套装
        var hasMixed: Boolean = true,

        var suitShowId: Long? = null, // 套装展式Id

        var cardUserSuitId: Long? = null, // 用户展示套装ID 用户套装Id SuitShow

        var cardCover:String? = "",

) : ProguardRule, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.createTypedArrayList(Card),
        parcel.readString(),
        parcel.readParcelable(EarliestMixUser::class.java.classLoader),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(suitId)
        parcel.writeString(suitName)
        parcel.writeValue(suitUserId)
        parcel.writeString(suitCover)
        parcel.writeString(suitClass)
        parcel.writeLong(mixCount)
        parcel.writeLong(suitType)
        parcel.writeByte(if (isBorder) 1 else 0)
        parcel.writeValue(cardCount)
        parcel.writeTypedList(cardList)
        parcel.writeString(description)
        parcel.writeParcelable(earliestMixUser, flags)
        parcel.writeValue(issueTime)
        parcel.writeLong(type)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeLong(suitCategoryId)
        parcel.writeByte(if (hasMixed) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Suit> {
        override fun createFromParcel(parcel: Parcel): Suit {
            return Suit(parcel)
        }

        override fun newArray(size: Int): Array<Suit?> {
            return arrayOfNulls(size)
        }
    }
}