package com.kotlin.android.app.data.entity.community.record

import com.kotlin.android.app.data.annotation.ShowType
import com.kotlin.android.app.data.ProguardRule

/**
 * 影评电影
 * 关联影人
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */
data class FcMovie(
        var id: Long = 0, // 电影ID
        var name: String? = null, // 电影中文名
        var nameEn: String? = null, // 电影英文名
        var imgUrl: String? = null, // 图片url
        @ShowType var btnShow: Long = 0L, // 按钮显示:1:预购2:购票3:想看4:已想看 null:不显示按钮
        var genreTypes: String? = null, // 类型
        var rating: String? = null, // 时光评分
        var minutes: String? = null, // 时长:分钟
        var releaseDate: String? = null, // 上映日期
        var releaseArea: String? = null // 上映地区
) : ProguardRule