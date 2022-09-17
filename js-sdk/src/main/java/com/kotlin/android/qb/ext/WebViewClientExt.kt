package com.kotlin.android.qb.ext

import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * 创建者: zl
 * 创建时间: 2020/8/3 4:01 PM
 * 描述:
 */
open class WebViewClientExt : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        /**
         * 1.true 为拦截点击事件，表示超链接的内容不在当前 WebView 中加载。（若不做处理，则点击超链接没有任何反应，也就是点击事件被拦截）
         *  ,拦截点击事件，并通过回调，处理跳转逻辑
         * 2.false 就是正常的处理事件，点击超链接会在当前 WebView 加载超链接内容
         */
        return false
    }

    override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
        p1?.proceed()
    }
}