package com.kotlin.android.app.data.entity.home

import com.kotlin.android.app.data.ProguardRule

data class RcmdContentList(
    var nextStamp: String,
    var hasNext: Boolean = true,
    var items: List<RcmdContent>?
): ProguardRule
