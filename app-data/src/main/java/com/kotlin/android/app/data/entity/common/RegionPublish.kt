package com.kotlin.android.app.data.entity.common

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zl on 2020-09-04
 * 获取推荐位数据信息（/rcmd_region_publish/list.api）
 */
data class RegionPublish(var codes: List<String>?,
                         var regionList: List<RegionList>?) : ProguardRule {
    data class RegionList(var code: String?,
                          var items: List<Map<String, String>>?) : ProguardRule
}