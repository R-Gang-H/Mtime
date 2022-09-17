package com.kotlin.android.retrofit.cookie

import android.content.Context
import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.store.clearSp
import com.kotlin.android.ktx.ext.store.getSpAll
import com.kotlin.android.ktx.ext.store.putSpValue
import com.kotlin.android.ktx.ext.store.removeKey
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.internal.and
import okhttp3.internal.toHexString
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

/**
 * Cookie Store
 *
 * Created on 2020/8/12.
 *
 * @author o.s
 */
class CookieStore private constructor(private val context: Context) {

    val cookiesPrefsName = "cookies_prefs"
    private val cookiesStore = ConcurrentHashMap<String, Cookie>()

    init {
        loadCookies()
    }

    companion object {

        var applicationContext: Context = CoreApp.instance

        val instance by lazy { CookieStore(applicationContext) }
    }

    /**
     * 从SP中读取Cookies加载到 [cookiesStore] 缓存中
     */
    private fun loadCookies() {
        CoreApp.instance.apply {
            getSpAll(cookiesPrefsName).run {
                entries.forEach { entry ->
                    val name = entry.key
                    val value = entry.value as? String ?: ""
                    SerializableCookie().decode(value)?.run {
                        cookiesStore.put(name, this)
                    }
                }
            }
            "loadCookies cookiesStore = $cookiesStore".i()
        }
    }

    /**
     * 获取Cookie列表
     */
    fun getCookies(): List<Cookie> = cookiesStore.values.toList()

    /**
     * 通过名称获取对应的Cookie
     */
    fun getCookieByName(name: String): Cookie? {
        getCookies().forEach {
            if (it.name.equals(name, true)) {
                return it
            }
        }
        return null
    }

    /**
     * 获取Cookie列表的字符串描述信息
     */
    fun getCookiesDesc(): String {
        val sb = StringBuffer()
        cookiesStore.values.forEachIndexed { index, cookie ->
            if (index > 0) {
                sb.append(";")
            }
            sb.append(cookie)
        }
        return sb.toString()
    }

    /**
     * Cookie是否存在
     */
    fun isCookieExistByName(name: String): Boolean = getCookieByName(name) != null

    /**
     * 提供外部设置Cookies入口
     */
    @Synchronized
    fun setCookies(url: HttpUrl, cookieStr: String) {
        Cookie.parse(url, cookieStr)?.apply {
            save(url, this)
        }
    }

    /**
     * 加载关联指定 [url] 的 Cookies
     */
    fun loadForRequest(url: HttpUrl): List<Cookie> {
        val validList = ArrayList<Cookie>()
        val expiresList = ArrayList<Cookie>()
        cookiesStore.values.forEach {
            if (it.isCookieExpired()) {
                expiresList.add(it)
            } else {
                validList.add(it)
            }
        }
        expiresList.forEach {
            remove(url, it)
        }
        return validList
    }

    /**
     * 保存关联指定 [url] 的 Cookies
     */
    fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>?) {
        cookies?.forEach {
            save(url, it)
        }
    }

    /**
     * 保存关联指定 [url] 的 Cookies
     */
    fun save(url: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)
        if (cookie.persistent) {
            cookiesStore[name] = cookie
            // 持久化 Cookie 到SP中
            context.apply {
                val encodeCookie = SerializableCookie().encode(cookie)
                putSpValue(name, encodeCookie, cookiesPrefsName)
                "save cookie name = $name, cookie.persistent = ${cookie.persistent}, cookiesStore = $cookiesStore".i()
            }
        } else {
            remove(url, cookie)
        }
    }

    /**
     * 删除全部 [Cookie]
     */
    fun clear() {
        context.clearSp(cookiesPrefsName)
        cookiesStore.clear()
    }

    /**
     * 删除指定 [Cookie]
     */
    fun remove(url: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)
        context.apply {
            if (cookiesStore.contains(name)) {
                cookiesStore.remove(name)
                removeKey(name, cookiesPrefsName)
            }
        }
    }

    private fun getCookieToken(cookie: Cookie): String =  "${getProtocol(cookie)}${cookie.domain}${cookie.path}|${cookie.name}"

    private fun getProtocol(cookie: Cookie): String = if (cookie.secure) {
        "https://"
    } else {
        "http://"
    }
}

/**
 * Cookie是否过期
 */
fun Cookie.isCookieExpired(): Boolean = expiresAt < System.currentTimeMillis()

/**
 * 十六进制字符串转二进制数组
 */
fun String.hexStringToByteArray(): ByteArray {
    val bytes = ByteArray(length / 2)
    var i = 0
    while (i < length) {
        bytes[i / 2] = ((Character.digit(this[i], 16) shl 4)
                + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }
    return bytes
}

/**
 * 二进制数组转十六进制字符串
 */
fun ByteArray.byteArrayToHexString(): String {
    val sb = StringBuilder()
    this.forEach {
        val value = it and 0xFF
        if (value < 16) {
            sb.append(0)
        }
        sb.append(value.toHexString())
    }
    return sb.toString().toLowerCase(Locale.US)
}