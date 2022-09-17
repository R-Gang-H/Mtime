package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 联合搜索/search/unionSearch
 * 影院
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
data class Cinema(

        val address: String? = null, // 地址
        val baiduLatitude: Double? = null, // 百度维度
        val baiduLongitude: Double? = null, // 百度经度
        val business: String? = null,
        val cinemaId: Long? = null, // 影院Id
        val cityId: Long? = null, // 城市Id 取 LocationIdCity
        val cityName: String? = null,
        val cover: String? = null,
        val district: String? = null,
        val feature: Feature? = null, // 影院特色
        val latitude: Double? = null, // google 维度
        val longitude: Double? = null, //google 经度
        val loveDeep: Long? = null, // 喜爱度
        val name: String? = null, // 影院名称
        // 二期新增
        val distance: Double? = null,     // 当前位置到该影院的距离 单位 公里
                                          // 传入longitude和latitude两个参数,而且影院已采集经度、纬度，则显示
                                          // 否则 显示 0.0
        val featureInfos: String? = null  // 影设备设施
) : ProguardRule, Serializable