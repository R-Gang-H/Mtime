package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule

/**
 * 当前用户信息
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class UserInfo(
        var userId: Long = 0L,
        var avatarUrl: String? = null,
        var nickName: String? = null, // 昵称
        var signature: String? = null, // 用户签名
        var cardGold: Long = 0, // 用户金币数
        var cardGoldShow: String? = null, // 用户金币数展示字段 （单位：W）
        var rankInfo: RankInfo? = null, // 排行榜信息
        var suitCount: Long = 0, // 用户套装数量
        var friendCount: FriendInfo? = null, // 用户套装数量

        var title: String? = null, // 标题（最早合成终极套装/最早合成）
        var friendId: Long = 0, // 好友Id（时光小助手=864678）
        var isOnline: Boolean = false, // 是否在线（根据3分钟内是否访问过个人主页myPocket.api判断，不是根据是否登录判断）（个别接口有此字段）
) : ProguardRule, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readParcelable(RankInfo::class.java.classLoader),
            parcel.readLong(),
            parcel.readParcelable(FriendInfo::class.java.classLoader),
            parcel.readString(),
            parcel.readLong(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(userId)
        parcel.writeString(avatarUrl)
        parcel.writeString(nickName)
        parcel.writeString(signature)
        parcel.writeLong(cardGold)
        parcel.writeString(cardGoldShow)
        parcel.writeParcelable(rankInfo, flags)
        parcel.writeLong(suitCount)
        parcel.writeParcelable(friendCount, flags)
        parcel.writeString(title)
        parcel.writeLong(friendId)
        parcel.writeByte(if (isOnline) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfo> {
        override fun createFromParcel(parcel: Parcel): UserInfo {
            return UserInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserInfo?> {
            return arrayOfNulls(size)
        }
    }
}