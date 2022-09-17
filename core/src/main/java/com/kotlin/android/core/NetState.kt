package com.kotlin.android.core

import com.kotlin.android.ktx.ext.core.isInternetAvailable

/**
 *
 * Created on 2020/4/28.
 *
 * @author o.s
 */
object NetState {

    fun isOk(): Boolean {
        return CoreApp.instance.isInternetAvailable()
    }
}