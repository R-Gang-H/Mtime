package com.kotlin.android.app.data.entity.home

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.CommContent

data class RcmdContent(
    var rcmdText: String? = "",
    var rcmdTop: Boolean = false,
    var commContent: CommContent
): ProguardRule
