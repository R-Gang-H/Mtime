package com.kotlin.android.search.newcomponent.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/19
 */
data class HotSearchBean(
        var title: String,
        var score: String,
        var hotLevel: Int, //0:平值，1：上升，2：下降
        var id: Long,
        var type: Int //0:电影，1：人物，2家族
) : ProguardRule {
    companion object {
        const val HOT_LEVEL_NOTHING = -1
        const val HOT_LEVEL_FLAT = 0
        const val HOT_LEVEL_UP = 1
        const val HOT_LEVEL_DOWN = 2

        const val HOT_TYPE_FILM = 1
        const val HOT_TYPE_PEOPLE = 2
        const val HOT_TYPE_FAMILY = 4
    }
}