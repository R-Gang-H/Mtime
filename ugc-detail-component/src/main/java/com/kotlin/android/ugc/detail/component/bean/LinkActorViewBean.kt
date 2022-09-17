package com.kotlin.android.ugc.detail.component.bean

import android.text.TextUtils
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.community.content.CommunityContent

/**
 * create by lushan on 2022/3/15
 * des:关联影人
 **/
data class LinkActorViewBean(
    var personId: Long = 0L,//影人id
    var img: String = "",//影人海报
    var name: String = "",//影人名称
    var nameEn: String = "",//影人英文名
    var score: String = "",//影人评分
    var type: String = "",//影人类型
    var birthday: String = "",//生日
) : ProguardRule {
    companion object{
        fun build(bean: CommunityContent.RoPerson):LinkActorViewBean{
            return LinkActorViewBean(
                personId = bean.id,
                img = bean.imgUrl.orEmpty(),
                name = bean.nameCn.orEmpty(),
                nameEn = bean.nameEn.orEmpty(),
                score = bean.rating.orEmpty(),
                type = bean.profession.orEmpty(),
                birthday = bean.birthDate.orEmpty()
            )
        }
    }
    fun getActorName() = if (TextUtils.isEmpty(name).not()) name else nameEn

    fun showScore() = score.isNotEmpty()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LinkActorViewBean

        if (personId != other.personId) return false
        if (img != other.img) return false
        if (name != other.name) return false
        if (nameEn != other.nameEn) return false
        if (score != other.score) return false
        if (type != other.type) return false
        if (birthday != other.birthday) return false

        return true
    }

    override fun hashCode(): Int {
        var result = personId.hashCode()
        result = 31 * result + img.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + nameEn.hashCode()
        result = 31 * result + score.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + birthday.hashCode()
        return result
    }

}