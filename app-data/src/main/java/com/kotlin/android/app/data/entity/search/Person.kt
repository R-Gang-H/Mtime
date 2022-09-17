package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 联合搜索/search/unionSearch
 * 影人
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
data class Person(
        val birthLocation: String? = null, // 出生地
        val birthday: String? = null, // 出生日期
        val constellation: String? = null, // 星座
        val href: String? = null, // 链接
        val img: String? = null, // 影人图片url
        val loveDeep: Double? = null, // 喜爱度
        val name: String? = null, // 影人名
        val nameEn: String? = null, // 英文名
        val personId: Long? = null, // 影人Id
        val personMovies: List<PersonMovie>? = null, // 热门作品列表
        val profession: String? = null, // 职业
        val rating: Double? = null, // 综合分
        val sex: String? = null // 性别
) : ProguardRule, Serializable