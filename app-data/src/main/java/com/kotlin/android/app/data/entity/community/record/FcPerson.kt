package com.kotlin.android.app.data.entity.community.record

import com.kotlin.android.app.data.ProguardRule

/**
 * 影评影人
 * 关联影人
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */
data class FcPerson(
        var id: Long = 0, // 影人ID
        var nameCn: String? = null, // 影人中文名
        var nameEn: String? = null, // 影人英文名
        var imgUrl: String? = null, // 影人图片
        var birthDate: String? = null, // 生日
        var profession: String? = null, // 职业

        var rating: String? = null // 时光评分
) : ProguardRule