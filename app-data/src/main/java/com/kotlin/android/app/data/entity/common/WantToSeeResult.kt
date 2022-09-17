package com.kotlin.android.app.data.entity.common

/**
 * @author vivian.wei
 * @date 2020/8/28
 * @desc 想看操作结果实体
 */
data class WantToSeeResult(
        var wantToSeeNumberShow: String? = ""
): StatusResult() {
    fun isSuccess():Boolean = status == 1L
}