package com.kotlin.android.qb.ext

import android.net.Uri
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

/**
 * 创建者: zl
 * 创建时间: 2020/8/3 1:38 PM
 * 描述:
 */
open class WebChromeClientExt : WebChromeClient() {
    /**
     * 返回的标题也许不是h5真正的标题，如果需要用到此属性，需要跟H5协商好
     */
    override fun onReceivedTitle(p0: WebView?, p1: String?) {
        super.onReceivedTitle(p0, p1)
    }

    /**
     * Url加载进度
     */
    override fun onProgressChanged(p0: WebView?, p1: Int) {
        super.onProgressChanged(p0, p1)
    }


    // For Android  > 4.1.1 弃用
    override fun openFileChooser(p0: ValueCallback<Uri>?, p1: String?, p2: String?) {
        super.openFileChooser(p0, p1, p2)
    }

    // For Android  >= 5.0
    override fun onShowFileChooser(p0: WebView?, p1: ValueCallback<Array<Uri>>?, p2: FileChooserParams?): Boolean {
        return super.onShowFileChooser(p0, p1, p2)
    }
}