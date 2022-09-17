package com.kotlin.android.ktx.ext.uri

import java.net.URLDecoder

/**
 *
 * Created on 2021/3/23.
 *
 * @author o.s
 */

fun String?.urlDecode(encode: String = "UTF-8"): String {
    if (this.isNullOrEmpty()) {
        return ""
    }
    return URLDecoder.decode(this, encode)
}


fun Long?.isSuccess() = this == 0L

fun Int?.isSuccess() = this == 0