package com.kotlin.android.ktx.ext.time

import androidx.annotation.IntDef

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/9
 */
class TimeConstants {

    companion object {
        const val MSEC = 1
        const val SEC = 1000
        const val MIN = 60000
        const val HOUR = 3600000
        const val DAY = 86400000
    }

    @IntDef(MSEC, SEC, MIN, HOUR, DAY)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Unit
}


