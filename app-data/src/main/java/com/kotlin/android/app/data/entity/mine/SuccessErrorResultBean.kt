package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

data class SuccessErrorResultBean(val success: Boolean, val error: String, val status: Long? = 0L) :
    ProguardRule