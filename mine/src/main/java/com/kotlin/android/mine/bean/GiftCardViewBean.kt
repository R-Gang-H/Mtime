package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by WangWei on 2020/10/25
 * description:礼品卡
 */
data class GiftCardViewBean(var hasNext:Boolean = true,//是否还有下一页
                            var totalCount:Long = 0L,//总数
                            var list: MutableList<MultiTypeBinder<*>> = mutableListOf()//
): ProguardRule