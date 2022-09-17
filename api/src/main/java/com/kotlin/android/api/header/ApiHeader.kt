package com.kotlin.android.api.header

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.android.api.config.Env
import com.kotlin.android.api.key.HeaderKey
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.core.ext.putSpValue
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.getUUID
import com.kotlin.android.ktx.ext.hash.Hash
import com.kotlin.android.ktx.ext.hash.hash
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.v
import okhttp3.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created on 2020-01-02.
 *
 * @author o.s
 */
object ApiHeader {

    private const val MX_CID = "MX-CID"//设备UUID
    private const val MX_API = "MX-API"

    private const val HOST = "Host"
    private const val UA = "User-Agent"
    private const val AE = "Accept-Encoding"
    private const val ACCEPT = "Accept-Charset"
    private const val UTF8 = "UTF-8,*"

    // header key
    private const val ksCode = "sCode"
    private const val kcCode = "cCode"
    private const val kts = "ts"
    private const val kCheck = "check"
    private const val kVer = "ver"
    private const val k_mi_ = "_mi_"
    private const val kWidth = "width"
    private const val kHeight = "height"
    private const val kJson = "json"
    private const val kAppId = "appId"

    // 默认为true
    private const val isJson = true

    // saleSubjectCode 销售主体code
    private const val saleSubjectCode = "Wanda"

    // channelCode 渠道code
    private const val channelCode = "1_2"

    var PACKAGE_VERSION: String? = "6.6.3"
    var UA_STR: String? = ""
    val SERVICE_TIME_STAMP = "ServiceTimeStamp"//服务器返回的时间戳

    // clientKey 渠道秘钥
    private val clientKey = if (Env.isRelease()) {
        "001B069F645188F44093845A7AFA7A4D5659E2DBD0B727453EB4086638AB20C1"
    } else {
        "2CDA6075E48B701F57742FF37AB6C3DC1BDAD6B4252DF4AF82FE842CF87E6CA8"
    }

    fun getHeaders(url: HttpUrl, method: String, body: RequestBody?): Headers {
        val builder = Headers.Builder()
        val head = HashMap<String, Any>()
        head[ksCode] = saleSubjectCode
        head[kcCode] = channelCode
        head[kts] = time
        head[kCheck] = getCheck(url, method, body).hash(Hash.MD5)
        head[kVer] = PACKAGE_VERSION.toString()
        head[k_mi_] = getToken()
        head[kWidth] = screenWidth
        head[kHeight] = screenHeight
        head[kJson] = isJson
        head[AE] = "gzip, deflate"
        head[kAppId] = 2//1:ios , 2:android

        builder.add(MX_CID, getUUID())
        builder.add(MX_API, Gson().toJson(head))
        builder.add(HOST, "${url.host}:${url.port}")
        builder.add(UA, UA_STR.orEmpty())
        builder.add(ACCEPT, UTF8)
        return builder.build()
    }

    private fun getCheck(url: HttpUrl, method: String, body: RequestBody?): String {
        val api = url.toUrl().file
        val check = StringBuilder()
                .append(saleSubjectCode)
                .append(channelCode)
                .append(clientKey)
                .append(time)
                .append(api)
        if ("POST" == method) {
            val param = StringBuilder()
            if (body is FormBody) {
                body.apply {
                    (0 until body.size).forEach {
                        param.append(body.name(it))
                                .append("=")
                                .append(ApiEncode.urlEncodeUnicode(body.value(it)))
                        if (it < size - 1) {
                            param.append("&")
                        }
                    }
                }
            }
            "param :: $param".d()
            check.append(param)
        }
        "check :: $check".d()
        return check.toString()
    }

}

val time: Long
    get() = getServerTime() // System.currentTimeMillis()

fun getToken(): String {
    return getSpValue(HeaderKey.TOKEN, "")
}

/**
 * 从response 取 token
 */
fun storeToken(response: Response?) {
    if (response?.code != 200) {
        return
    }
    response.apply {
        val header = this.headers.values(HeaderKey.MXAPI)
        if (TextUtils.isEmpty(if (header.isNotEmpty()) header[0] else "").not()) {
            val head = Gson().fromJson<Map<String, Any>>(header[0], object : TypeToken<Map<String, Any>>() {
            }.type)
            Log.e("zl", "head " + head)
            if ((head?.containsKey(HeaderKey.TOKEN) == true)) {
                putSpValue(HeaderKey.TOKEN, head[HeaderKey.TOKEN])
            }
        }
    }
}

/**
 * 从response 取 服务器时间
 */
fun storeServerTime(response: Response?) {
    if (response?.code != 200) {
        return
    }
    response.apply {
        if (headers.names().contains(HeaderKey.DATE)) {
            val date = headers[HeaderKey.DATE]
            if (!TextUtils.isEmpty(date)) {
                try {
                    // 获取到gmt时间
                    val sdf = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
                    TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"))
                    saveServerTime(sdf.parse(date).time)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        }
    }
}

/**
 * time: GMT+8
 */
fun saveServerTime(time: Long?) {
    time?.apply {
        val correctTime = this - System.currentTimeMillis()
        "saveServerTime :: correctTime=$correctTime, time=$this, currTime = ${System.currentTimeMillis()}".v()
        putSpValue(HeaderKey.SERVICE_TIME_STAMP, this)
        putSpValue(HeaderKey.CORRECT_TIME, correctTime)
    }
}

/**
 * 获取服务器当前时间：毫秒（ms）
 */
fun getServerTime(): Long {
    return getSpValue(HeaderKey.CORRECT_TIME, 0L) + System.currentTimeMillis()
}