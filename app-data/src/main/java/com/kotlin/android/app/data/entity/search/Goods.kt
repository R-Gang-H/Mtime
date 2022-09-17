package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 联合搜索/search/unionSearch
 * 商品
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
data class Goods(
        val goodsId: Long? = null,
        val iconText: String? = null,
        val background: String? = null,
        val name: String? = null,
        val longName: String? = null,
        val image: String? = null,
        val marketPrice: Long? = null,
        val minSalePrice: Long? = null,
        val goodsUrl: String? = null,
        val marketPriceFormat: String? = null,
        val minSalePriceFormat: String? = null,
        val goodsTip: String? = null,
        val imageSrc: String? = null
) : ProguardRule, Serializable