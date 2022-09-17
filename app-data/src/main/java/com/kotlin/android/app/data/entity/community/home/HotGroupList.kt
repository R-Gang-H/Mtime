package com.kotlin.android.app.data.entity.community.home

import com.kotlin.android.app.data.entity.community.group.Group
import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/31
 */
data class HotGroupList(
        var list: List<Group>? = null,
        var hasMore: Boolean = true
): ProguardRule