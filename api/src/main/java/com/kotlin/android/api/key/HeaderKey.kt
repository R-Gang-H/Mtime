package com.kotlin.android.api.key

/**
 *
 * Created on 2020/4/21.
 *
 * @author o.s
 */
object HeaderKey {
    const val HOST = "Host"
    const val UTF8 = "UTF-8,*"
    const val ACCEPT = "Accept-Charset"
    const val AE = "Accept-Encoding"
    const val MXCID = "MX-CID" //设备UUID
    const val MXAPI = "MX-API"
    const val TOKEN = "_mi_" //验证是否登录
    const val DATE = "date" //日期
    const val SERVICE_TIME_STAMP = "ServiceTimeStamp" //服务器返回的时间戳
    const val CORRECT_TIME = "correct_time" //用于修改本地时间和服务器时间时间差的key

    var PACKAGE_VERSION: String? = null

}
