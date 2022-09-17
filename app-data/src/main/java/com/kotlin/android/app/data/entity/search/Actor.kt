package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 联合搜索/search/unionSearch
 * 演员
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
data class Actor(
        val id: Long? = null,
        val name: String? = null,
) : ProguardRule, Serializable