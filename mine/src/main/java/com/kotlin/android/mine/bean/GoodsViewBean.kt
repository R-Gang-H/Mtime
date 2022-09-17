package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/12/23
 * description: M豆兑换商品实体
 */
data class GoodsViewBean(var id: Long = 0L,//商品id
                         var pic: String = "",//商品图片
                         var name: String = "",//商品名称
                         var des: String = "",//商品描述
                         var mNeedNum: Long = 0L,//兑换该商品需要的M豆数量
                         var isHiddenDes: Boolean = false//是否需要隐藏描述信息
) : ProguardRule