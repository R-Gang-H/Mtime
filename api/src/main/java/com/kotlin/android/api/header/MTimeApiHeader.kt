package com.kotlin.android.api.header

import android.os.Build
import com.kotlin.android.api.R
import com.kotlin.android.api.config.AppConfig
import com.kotlin.android.core.CoreApp
import com.kotlin.android.core.Phone
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.core.ext.versionName
import com.kotlin.android.ktx.ext.KEY_JPUSH_REGID
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.hash.Hash
import com.kotlin.android.ktx.ext.hash.hash
import com.kotlin.android.ktx.ext.log.d
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.RequestBody

/**
 * MTimeApiHeader
 *
 * Created on 2020/8/6.
 *
 * @author o.s
 */
object MTimeApiHeader {
    private const val MX_CID = "MX-CID"
    private const val CHECK_VALUE = "X-Mtime-Mobile-CheckValue"
    private const val DEVICE_INFO = "X-Mtime-Mobile-DeviceInfo"
    private const val CIP_LOCATION = "X-Mtime-Mobile-Location"
    private const val CIP_TELEPHONE_INFO = "X-Mtime-Mobile-TelephoneInfo"
    private const val PUSH_TOKEN = "X-Mtime-Mobile-PushToken"
    private const val J_PUSH_ID = "X-Mtime-Mobile-JPushID"
    private const val REFERER = "Referer"

    private const val UA = "User-Agent"
    private const val AC = "Accept-Charset"
    private const val AE = "Accept-Encoding"
    private const val HOST = "Host"

    private const val B2C = "B2C"
    private const val UTF8 = "UTF-8,*"
    private const val GZIP = "gzip, deflate"

    private const val APP_ID = 6
    private var CLIENT_KEY = CoreApp.instance.applicationContext.getString(R.string.key_mtime_api_client)

    fun getHeaders(url: HttpUrl, method: String, body: RequestBody?): Headers {
        val builder = Headers.Builder()
        builder.add(MX_CID, CID.get())
        builder.add(CHECK_VALUE, getCheck(url, method, body))
        builder.add(DEVICE_INFO, getDeviceInfo())
        builder.add(CIP_LOCATION, "")
        builder.add(CIP_TELEPHONE_INFO, "")//getPhoneInfo()
        builder.add(PUSH_TOKEN, getToken())
        builder.add(J_PUSH_ID, getJPushId())
        builder.add(REFERER, B2C)
        builder.add(UA, apiUA())
        builder.add(AC, UTF8)
        /**
         * https://www.jianshu.com/p/a9d861732445
         * 代码里没有手动设置 Accept-Encoding = gzip ，那么 OkHttp 会自动处理 gzip 的解压缩；反之，需要手动对返回的数据流进行 gzip 解压缩。
         */
//        builder.add(AE, GZIP) // Retrofit gzip有坑
        builder.add(HOST, url.host) // "${url.host}:${url.port}"
        return builder.build()
//                .apply {
//                    i()
//                }
    }

    private fun getCheck(url: HttpUrl, method: String, body: RequestBody?): String {
        val api = url.toUrl().file
        val sb = StringBuilder()
                .append(APP_ID)
                .append(CLIENT_KEY)
                .append(time)
                .append(url)
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
            sb.append(param)
        }
        val check = StringBuilder()
                .append(APP_ID)
                .append(",")
                .append(time)
                .append(",")
                .append(sb.toString().hash(Hash.MD5))
                .append(",")
                .append(AppConfig.instance.channel)
//        "check :: $check".d()
        return check.toString()
    }

    /**
     * API UserAgent
     */
    private fun apiUA() = " Mtime_Android_App${getCommonUAParams(0)}"

    /**
     * 获取通用UA参数
     * http://wiki.inc-mtime.com/pages/viewpage.action?pageId=82313229
     */
    fun getCommonUAParams(height: Int): String {
        val sb = StringBuilder()
        sb.append("/channel_").append(AppConfig.instance.channel).append("/").append(versionName)
                .append("(WebView Width ").append(screenWidth)
                .append(" Height ").append(screenHeight - height).append(")")
                .append(" (Device ").append(Build.MODEL).append(")")
                .append(" (Token ").append(getToken()).append(")")
                .append(" (UDID ").append(getToken()).append(")")
                .append(" (Brand ").append(Build.BRAND).append(")")
//        "MTimeApiHeader getCommonUAParams = $sb".v()
        return sb.toString()
    }

    /**
     * 获取Token
     */
    fun getToken(): String = Phone.instance.androidID

    /**
     * 获取推送ID
     */
    fun getJPushId(): String = getSpValue(KEY_JPUSH_REGID, "")

    /**
     * 获取设备信息
     */
    fun getDeviceInfo(): String = "${Build.MODEL}_${Build.HARDWARE}"// String.format("%s_%s", Build.MODEL, Build.HARDWARE)

    /**
     * CIP数据设备信息
     */
    fun getPhoneInfo(): String = StringBuffer()
            .append("CELLID=")
            .append(Phone.instance.cellId)
            .append(";")
            .append("CID=")
            .append(Phone.instance.cid)
            .append(";")
            .append("IMEI=")
            .append(Phone.instance.imei)
            .append(";")
            .append("IMSI=")
            .append(Phone.instance.imsi)
            .append(";")
            .append("MAC=")
            .append(Phone.instance.mac)
            .append(";")
            .append("LANGUAGE=")
            .append(Phone.instance.language)
            .toString()
}