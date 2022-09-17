package com.kotlin.android.app.data.entity.home.tashuo

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.CommContentList
import com.kotlin.android.app.data.entity.home.RcmdContentList

data class TaShuoRcmdList(
    var rcmdUserList: List<RcmdFollowUser>? = null,
    var rcmdContentList: RcmdContentList? = null
): ProguardRule