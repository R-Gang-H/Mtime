package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 联合搜索/search/unionSearch
 * 商品列表
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
data class GoodsList(
        val goodsList: List<Goods>? = null,
        val keywordUrl: String? = null,
        val goodsCount: Long? = null,
) : ProguardRule, Serializable