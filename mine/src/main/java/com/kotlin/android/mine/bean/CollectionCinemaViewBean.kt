package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/14
 * description:收藏影院viewBean
 */
data class CollectionCinemaViewBean(var cinemaId: Long = 0L,//影院id
                                    var cinemaName: String = "",//影院名称
                                    var cinemaAddress: String = ""//影院地址
) : ProguardRule {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CollectionCinemaViewBean

        if (cinemaId != other.cinemaId) return false
        if (cinemaName != other.cinemaName) return false
        if (cinemaAddress != other.cinemaAddress) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cinemaId.hashCode()
        result = 31 * result + cinemaName.hashCode()
        result = 31 * result + cinemaAddress.hashCode()
        return result
    }
}