package com.kotlin.android.image

import com.kotlin.android.app.data.ext.VariateExt

/**
 *创建者: zl
 * 创建时间: 2020/8/10 2:29 PM
 * 描述:拼接图片代理地址
 */

/**
 * 拼接代理地址
 * @param imgUrl
 * @param width
 * @param height
 * @param useProxy
 * @return
 */
fun getProxyUrl(imgUrl: String?, width: Int, height: Int, useProxy: Boolean): String {
    if (imgUrl.isNullOrEmpty()) {
        return ""
    }
    /**
     * 如果不需要使用代理，直接返回imgUrl
     */
    if (!useProxy) {
        return imgUrl
    }
    if (width == 0 || height == 0) {
        if (BuildConfig.DEBUG) {
            throw IllegalArgumentException("使用了图片代理，宽或高不能为0")
        } else {
            return imgUrl
        }
    }

    val url = if (imgUrl.contains("aliyuncs.com")) {
        // 具体参数参见：https://help.aliyun.com/document_detail/44705.html
        "$imgUrl?x-oss-process=image/resize,m_fill,w_$width,h_$height/auto-orient,1"
    } else {
        "${VariateExt.imgProxyUrl}$imgUrl&width=$width&height=$height&clipType=4"
    }
//   ("$url").e()
    return url
}