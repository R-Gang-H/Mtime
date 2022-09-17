package com.kotlin.android.app.data.entity.user

import java.io.Serializable

/**
 * 用户所在地信息
 *
 * Created on 2020/8/14.
 *
 * @author o.s
 */
data class UserLocation(
        var locationId: Int = 0,
        val locationName: String? = null,
        val levelRelation: String? = null // 20-2-290  层级关系  国家id-省id-城市id
) : Serializable