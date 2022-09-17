package com.kotlin.android.app.data.entity.home

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhousuqiang on 2020-08-20
 *
 * 首页-Tab导航数据实体
 */
data class HomeTabNavList(var tabList: List<TabNav>) : ProguardRule {
    companion object {
        const val TYPE_RECOMMEND = 1L
        const val TYPE_ORIGINAL = 2L
        const val TYPE_TA_SHUO = 3L
        const val TYPE_ZHONG_CAO = 4L

        fun getDefaultNavList(): HomeTabNavList {
            val list: List<TabNav> = arrayListOf(
                    TabNav(name = "推荐", appPageCode = TYPE_RECOMMEND),
            )
            return HomeTabNavList(list)
        }
    }

    data class TabNav(
            var name: String? = "",
            var tips: String? = "",
            var icon: String? = "",
            var redirectType: Long = 1, //跳转方式：1-原生页面，2-H5页面
            var appPageCode: Long = 1,
            var h5Url: String? = ""
    ) : ProguardRule
}