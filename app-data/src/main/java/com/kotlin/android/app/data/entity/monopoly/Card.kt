package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片信息
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class Card(
        var cardSuitId: Long? = null, // 卡片所属套装Id
        var userCardId: Long? = null, // 用户卡片Id
        var cardId: Long = 0, // 卡片Id
        var cardCover: String? = null, // 卡片封面图
        var hintCover: String? = null, // 卡片封面图
        var cardName:String? = null,//卡片的名称
        var type:Long? = 1L,//卡片的类型(1卡片，2道具卡，3套装)

        var sponsorOrAccepter: Long? = null, // 发起方1,接收方2 【gameRecord.api用，其他接口忽略此字段】

        var isSelected: Boolean = false, // 本地属性，选择状态
        var position: Int = 0, // 本地属性，列表位置
        var isPropCard: Boolean = false, // 本地属性，是否道具卡

        var suitShowId: Long? = null, // 套装展式Id
        var isLimit: Boolean = false, // 是否限量
) : ProguardRule,Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(cardSuitId)
        parcel.writeValue(userCardId)
        parcel.writeLong(cardId)
        parcel.writeString(cardCover)
        parcel.writeString(hintCover)
        parcel.writeString(cardName)
        parcel.writeValue(type)
        parcel.writeValue(sponsorOrAccepter)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeInt(position)
        parcel.writeByte(if (isPropCard) 1 else 0)
        parcel.writeValue(suitShowId)
        parcel.writeByte(if (isLimit) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }

}