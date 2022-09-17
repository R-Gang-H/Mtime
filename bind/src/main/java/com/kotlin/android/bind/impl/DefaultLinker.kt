package com.kotlin.android.bind.impl

import com.kotlin.android.bind.Linker

/**
 * 默认返回的 [index] : itemViewType = 0
 *
 * Created on 2021/7/7.
 *
 * @author o.s
 */
class DefaultLinker<T> : Linker<T> {
    /**
     * 返回条目视图注册的代理索引下标（即：itemViewType）
     */
    override fun index(position: Int, item: T): Int = 0
}