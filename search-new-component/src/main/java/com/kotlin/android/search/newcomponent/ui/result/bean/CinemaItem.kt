package com.kotlin.android.search.newcomponent.ui.result.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * 搜索结果页显示用影院数据
 */
data class CinemaItem(
    var cinemaId: Long = 0L,        // 影院Id
    var name: String = "",          // 影院名称
    var address: String = "",       // 地址
    var featureInfos: String = "",  // 影设备设施
    var distance: Double = 0.0      // 当前位置到该影院的距离 单位 公里
                                    // 传入longitude和latitude两个参数,而且影院已采集经度、纬度，则显示
                                    // 否则 显示 0.0
): ProguardRule
