package com.kotlin.android.share

/**
 *
 * Created on 2020/6/23.
 *
 * @author o.s
 */
class ShareRepository private constructor() {

    companion object {
        val instance by lazy { ShareRepository() }
    }

}