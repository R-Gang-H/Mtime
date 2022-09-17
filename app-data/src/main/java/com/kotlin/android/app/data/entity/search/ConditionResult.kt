package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule

/**
 * 找电影搜索条件bean
 */
data class ConditionResult(
    var genres: ArrayList<Content>? = arrayListOf(),
    var locations: ArrayList<Content>? = arrayListOf(),
    var sorts: ArrayList<Content>? = arrayListOf(),
    var years: ArrayList<Content>? = arrayListOf(),
) : ProguardRule {
    data class Content(
        var name: String = "",
        var value: Long? = 0L,
        var from: Long? = 0L,
        var to: Long? = 0L,
    )
}
