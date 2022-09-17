package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 联合搜索/search/unionSearch
 * 影院特色
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
data class Feature(
        val has3D: Boolean? = null, // 是否3D
        val hasIMAX: Boolean? = null, // 是否IMAX
        val hasPark: Boolean? = null, // 是否停车场
        val hasServiceTicket: Boolean? = null, // 是否取票机
        val hasVIP: Boolean? = null, // 是否VIP
        val hasWifi: Boolean? = null // 是否有wifi
) : ProguardRule, Serializable
/**
"feature": {
"has3D": 1,
"hasIMAX": 1,
"hasVIP": 1,
"hasPark": 1,
"hasServiceTicket": 1,
"hasWifi": 1
}
 */