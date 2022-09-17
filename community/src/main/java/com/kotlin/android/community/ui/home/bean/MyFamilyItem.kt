package com.kotlin.android.community.ui.home.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/13
 *
 * 我的家族列表中的实体
 */
data class MyFamilyItem(
        var id: Long,
        var pic: String,
        var name: String,
        var updateCount: String
) : ProguardRule