package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/14
 * description:分页查询收藏影院
 */
data class CollectionCinema(
    var items: List<Item>? = mutableListOf(),//收藏主体
):CollectionBase(), ProguardRule {
    data class Item(
//    var collectTime: CollectTime,
            var obj: Obj? = null//收藏主体
    ): ProguardRule



    data class Obj(
            var address: String? = "",//影院地址
            var cinemaCode: String? = "",//影院专资码
            var id: Long = 0L,//影院id
            var name: String? = "",//影院名称
            var telephone: String? = ""//影院电话

    ): ProguardRule
}

