package com.kotlin.android.retrofit.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Cookie管理器：主要职责，
 *
 * Created on 2020/8/13.
 *
 * @author o.s
 */
class CookieManager private constructor() {

    /**
     * Cookie商店
     */
    private lateinit var store: CookieStore

    /**
     * [CookieJar] 接口实现类
     */
    val cookieJar: CookieJar = LocalCookieJar()

    fun init(store: CookieStore) {
        this.store = store
    }

    companion object {
        val instance by lazy {
            CookieManager().apply {
                init(CookieStore.instance)
            }
        }
    }

    /**
     * Token
     */
    var token: String = ""
        private set
        get() = getCookieByName("_mi_")?.value ?: ""

    /**
     * 获取Cookie列表
     */
    fun getCookies(): List<Cookie> = store.getCookies()

    /**
     * 通过名称获取对应的Cookie
     */
    fun getCookieByName(name: String): Cookie? = store.getCookieByName(name)

    /**
     * 获取Cookie列表的字符串描述信息
     */
    fun getCookiesDesc(): String = store.getCookiesDesc()

    /**
     * Cookie是否存在
     */
    fun isCookieExistByName(name: String): Boolean = store.isCookieExistByName(name)

    /**
     * 提供外部设置Cookies入口
     */
    fun setCookies(url: HttpUrl, cookieStr: String) = store.setCookies(url, cookieStr)

    /**
     * 删除全部 [Cookie]
     */
    fun clear() = store.clear()

    /**
     * CookieJar impl call
     */
    fun loadForRequest(url: HttpUrl): List<Cookie> = store.loadForRequest(url)

    /**
     * CookieJar impl call
     */
    fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>?) = store.saveFromResponse(url, cookies)
}