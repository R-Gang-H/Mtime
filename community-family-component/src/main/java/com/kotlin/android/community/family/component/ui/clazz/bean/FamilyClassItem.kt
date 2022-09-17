package com.kotlin.android.community.family.component.ui.clazz.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/28
 *
 * 家族分类实体
 */
data class FamilyClassItem(
        var id: Long,
        var pic: String? = "",
        var name: String?,
        var isSelected: Boolean = false
) : ProguardRule