package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/14
 * description:列表页需要总数和是否有下页的接口数据返回
 */
data class CollectionViewBean(var hasNext:Boolean = true,//是否还有下一页
                              var totalCount:Long = 0L,//总数
                              var list: MutableList<MultiTypeBinder<*>> = mutableListOf()//
): ProguardRule