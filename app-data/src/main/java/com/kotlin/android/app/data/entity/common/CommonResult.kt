package com.kotlin.android.app.data.entity.common

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/8/28
 * @desc 操作结果通用实体，具体值见各自api接口定义文档
 */
data class CommonResult(
        var status: Long ?= 0,
        var failMsg: String ?= ""
): ProguardRule