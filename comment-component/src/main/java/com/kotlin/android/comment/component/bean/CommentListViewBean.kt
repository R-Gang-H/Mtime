package com.kotlin.android.comment.component.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2021/4/7
 * description:
 */
data class CommentListViewBean(var totalCount:Long = 0L,
var commentBinderList:MutableList<MultiTypeBinder<*>> = mutableListOf()): ProguardRule