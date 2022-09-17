package com.kotlin.android.bind

import androidx.annotation.IntRange
import com.kotlin.android.bind.item.ItemViewDelegate

/**
 * 通过索引下标 [index]（即：itemViewType）链接 [T] 和 [ItemViewDelegate] 的接口
 *
 * Created on 2021/7/7.
 *
 * @author o.s
 */
interface Linker<T> {

    /**
     * 返回条目视图注册的代理索引下标（即：itemViewType）
     */
    @IntRange(from = 0)
    fun index(position: Int, item: T): Int
}