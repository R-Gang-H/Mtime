package com.kotlin.android.app.data.entity.community.home

import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/28
 *
 * 社区首页推荐精选列表实体
 */
data class RcmdSelectionList(
        var hasNext: Boolean,
        var items: List<RcmdSelection>?
) : ProguardRule {

    data class RcmdSelection(
            var rcmdText: String?,
            var rcmdTop: Boolean,
            var commContent: CommContent
    ) : ProguardRule
}