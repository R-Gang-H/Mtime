package com.kotlin.android.app.data.entity.community.home

import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/28
 *
 * 社区首页关注列表实体
 */
data class CommunityFollowList(
        var pageStamp: Long = 0,
        var hasNext: Boolean = true,
        var items: List<Post>? = null
) : ProguardRule {

    data class Post(
            var commContent: CommContent? = null
    ) : ProguardRule
}