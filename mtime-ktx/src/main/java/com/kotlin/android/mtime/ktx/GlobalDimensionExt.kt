package com.kotlin.android.mtime.ktx

import android.text.TextUtils
import androidx.core.text.isDigitsOnly
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.core.ext.putSpValue
import com.kotlin.android.ktx.ext.KEY_LOC_CITY_ID
import com.kotlin.android.ktx.ext.KEY_LOC_CITY_NAME
import com.kotlin.android.ktx.ext.KEY_LOC_LATITUDE
import com.kotlin.android.ktx.ext.KEY_LOC_LONGITUDE

/**
 * 创建者: zl
 * 创建时间: 2020/9/29 2:59 下午
 * 描述:全局保存用户选择的城市
 */
object GlobalDimensionExt {
    /**
     * 城市维度默认值
     */
    const val CITY_ID = 290L
    const val CITY_NAME = "北京"

    /**
     * 保存用户的经纬度
     */
    fun saveLongitudeAndLatitude(longitude: Double, latitude: Double) {
        putSpValue(KEY_LOC_LONGITUDE, longitude)
        putSpValue(KEY_LOC_LATITUDE, latitude)
    }

    /**
     * 获取经度
     */
    fun getLongitude(): Double {
        return getSpValue(KEY_LOC_LONGITUDE, 0.0)
    }

    /**
     * 获取纬度
     */
    fun getLatitude(): Double {
        return getSpValue(KEY_LOC_LATITUDE, 0.0)
    }

    /**
     * 保存用户设置的城市信息
     */
    fun saveCurrentCityInfo(cityId: String, cityName: String) {
        putSpValue(KEY_LOC_CITY_ID, cityId)
        putSpValue(KEY_LOC_CITY_NAME, cityName)
    }

    /**
     * 获取当前城市名称
     */
    fun getCurrentCityName(): String {
        return getSpValue(KEY_LOC_CITY_NAME, CITY_NAME)
    }

    /**
     * 获取当前城市id
     */
    fun getCurrentCityId(): String {
        return getSpValue(KEY_LOC_CITY_ID, CITY_ID.toString())
    }

    /**
     * 获取long类型城市id
     */
    fun getDigitsCurrentCityId(): Long {
        val currentCityId = getCurrentCityId()
        return if (TextUtils.isEmpty(currentCityId).not() && currentCityId.isDigitsOnly()) {
            currentCityId.toLong()
        } else {
            CITY_ID
        }

    }
}