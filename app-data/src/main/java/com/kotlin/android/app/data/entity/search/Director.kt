package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 联合搜索/search/unionSearch
 * 导演
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
data class Director(
        val id: Long? = null,
        val name: String? = null,
) : ProguardRule, Serializable