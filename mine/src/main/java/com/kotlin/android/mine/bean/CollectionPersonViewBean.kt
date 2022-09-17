package com.kotlin.android.mine.bean

import android.text.TextUtils
import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/14
 * description:收藏影人viewBean
 */
data class CollectionPersonViewBean(var personId: Long = 0L,
                                    var name: String = "",//中文名
                                    var nameEn: String = "",//英文名
                                    var imageUrl: String = "",//影人海报
                                    var profession: String = "",//职业
                                    var personInfo: String = "",//生日及出生地
                                    var likeRate: String = ""//喜欢度
) : ProguardRule {

    fun isLikeRateEmpty() = TextUtils.isEmpty(likeRate)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CollectionPersonViewBean

        if (personId != other.personId) return false
        if (name != other.name) return false
        if (nameEn != other.nameEn) return false
        if (imageUrl != other.imageUrl) return false
        if (profession != other.profession) return false
        if (personInfo != other.personInfo) return false
        if (likeRate != other.likeRate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = personId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + nameEn.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + profession.hashCode()
        result = 31 * result + personInfo.hashCode()
        result = 31 * result + likeRate.hashCode()
        return result
    }
}