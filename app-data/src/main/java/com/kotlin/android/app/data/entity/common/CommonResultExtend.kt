package com.kotlin.android.app.data.entity.common

import com.kotlin.android.app.data.ProguardRule

/**
 * @desc 操作结果通用扩展实体
 */
data class CommonResultExtend<R, T>(
        var result: R,
        var extend: T, //扩展字段，用于UI刷新
        var addedValue: Long = 0 //扩展附加值字段（非api返回，主要用于同一页面相同的操作后为了区分）
): ProguardRule