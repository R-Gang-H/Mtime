package com.kotlin.android.splash.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/11/23
 */
data class SplashAd(
        var appLink: String = "",
        var img: String = "",
        var countDown: Int = 3,
        var isGif: Boolean = false
) : ProguardRule