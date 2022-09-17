package com.kotlin.android.app.data.entity.monopoly

import android.os.Parcel
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule

data class EarliestMixUser(
        var avatarUrl: String? = null, // zmpwp
        var nickName: String? = null, // qIlk
        var title: String? = null, // ejeLA
        var userId: Long? = 0 // 1025
) : ProguardRule, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(avatarUrl)
        parcel.writeString(nickName)
        parcel.writeString(title)
        parcel.writeValue(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EarliestMixUser> {
        override fun createFromParcel(parcel: Parcel): EarliestMixUser {
            return EarliestMixUser(parcel)
        }

        override fun newArray(size: Int): Array<EarliestMixUser?> {
            return arrayOfNulls(size)
        }
    }
}