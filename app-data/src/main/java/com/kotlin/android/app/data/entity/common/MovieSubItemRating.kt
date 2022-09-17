package com.kotlin.android.app.data.entity.common

import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/28
 */
data class MovieSubItemRating(
        var index: Long = 0,
        var rating: Float? = 0.0f,
        var title: String? = ""
) : ProguardRule