package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable

/**
 * 展示卡片大图样式
 */
data class CardImageDetailBean (
    /**
     * 图片的集合
     */
    var card: List<Card>? = null,
    var pointX:Float? = 0f,
    var pointY:Float? = 0f
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(Card),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(card)
        parcel.writeValue(pointX)
        parcel.writeValue(pointY)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardImageDetailBean> {
        override fun createFromParcel(parcel: Parcel): CardImageDetailBean {
            return CardImageDetailBean(parcel)
        }

        override fun newArray(size: Int): Array<CardImageDetailBean?> {
            return arrayOfNulls(size)
        }
    }
}