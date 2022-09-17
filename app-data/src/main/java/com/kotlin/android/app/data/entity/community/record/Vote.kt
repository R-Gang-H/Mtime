package com.kotlin.android.app.data.entity.community.record

import com.kotlin.android.app.data.ProguardRule

/**
 * 投票/PK?
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */
data class Vote(
        var multiple: Boolean = false, // 是否多选 必填
        var opts: List<Opts>? = null // 选项列表 必填
) : ProguardRule {
    data class Opts(
            var optId: Long = 0, // 选项Id 必填
            var optDesc: String? = null // 选项描述 必填
    ) : ProguardRule
}