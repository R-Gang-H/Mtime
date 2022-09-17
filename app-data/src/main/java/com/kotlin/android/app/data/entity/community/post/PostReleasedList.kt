package com.kotlin.android.app.data.entity.community.post

import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/9/1
 *
 * 已发布群组帖子接口实体
 */
data class PostReleasedList(
        var hasNext: Boolean,
        var items: List<CommContent>?,
        var nextStamp: String? = "",
        var pageSize: Long? = 0L,
): ProguardRule