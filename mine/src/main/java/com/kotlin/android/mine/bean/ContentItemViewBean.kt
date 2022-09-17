package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.community.content.ContentList

data class ContentItemViewBean(
    var id: Long? = 0L,
    var name: String? = "",
    var picUrl: String? = "",
    var userCreateTime: String? = "",
    var videoDuration: String? = "",
    var commentCount: Long? = 0L,
    var praiseUpCount: Long? = 0L,
    var creatorContentStatus: Long? = 0L,
    var showCreatorContentStatus: String? = "",
    var item: ContentList.Item
) : ProguardRule