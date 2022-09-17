package com.kotlin.android.app.data.entity.common

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2022/4/15
 * des:
 **/
data class CollectionResult(
    var objCount: Long = 0L,
    var bizCode: Long = 0L,
    var bizMsg: String? = ""
) : ProguardRule {
    fun isSuccess() = bizCode == 0L
}