package com.kotlin.android.app.data.entity.home

import com.kotlin.android.app.data.ProguardRule

data class HomeRcmdContentList(
    var nextStamp: String,
    var hasNext: Boolean = true,
    var items: List<HomeRcmdContent>?
): ProguardRule
