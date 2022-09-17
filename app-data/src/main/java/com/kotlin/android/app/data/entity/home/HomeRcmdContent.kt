package com.kotlin.android.app.data.entity.home

import com.kotlin.android.app.data.ProguardRule

data class HomeRcmdContent(
    var itemType: Long = 1, //内容类型：1-内容，2-广告
    var content: RcmdContent? = null,
    var adv: Adv? = null
): ProguardRule
