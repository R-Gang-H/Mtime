package com.kotlin.android.app.data.entity.community.group

import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/9/1
 */
data class GroupListByClass(
        var hasMore: Boolean,
        var groupList: List<Group>?,
        var rcmdGroupList: List<Group>?
): ProguardRule