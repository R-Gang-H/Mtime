package com.kotlin.android.app.data.entity.common

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/11
 * description:点赞、点踩通用实体
 */
data class CommBizCodeResult(var bizCode: Long = -1L,
                             var bizMsg: String? = ""): ProguardRule {
    companion object {
        const val BIZ_CODE_SUCCESS = 0L
    }

    fun isSuccess() : Boolean {
        return bizCode == BIZ_CODE_SUCCESS
    }
}