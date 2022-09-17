package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable


/**
 * @desc  使用道具卡的参数bean
 * @author zhangjian
 * @date 2020/11/16 16:18
 */
data class UseToolsCardRequestBean(
        //要使用的道具卡Id
        var cardToolId:Long? = 0,
        //目标用户Id（对谁使用）
        var targetUserId:Long? = 0,
        //目标道具卡Id（使用复制卡时必传此参数）
        var targetToolId:Long? = 0,
        //目标卡片Id（使用打劫卡时必传此参数）
        var targetCardId:Long? = 0,
        //是否使用恶魔卡（使用奴隶卡或黑客卡时必传此参数）
        var useDemonCard:Boolean? = false
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(cardToolId)
        parcel.writeValue(targetUserId)
        parcel.writeValue(targetToolId)
        parcel.writeValue(targetCardId)
        parcel.writeValue(useDemonCard)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UseToolsCardRequestBean> {
        override fun createFromParcel(parcel: Parcel): UseToolsCardRequestBean {
            return UseToolsCardRequestBean(parcel)
        }

        override fun newArray(size: Int): Array<UseToolsCardRequestBean?> {
            return arrayOfNulls(size)
        }
    }
}