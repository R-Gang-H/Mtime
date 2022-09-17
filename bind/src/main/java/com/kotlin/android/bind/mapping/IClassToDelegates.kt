package com.kotlin.android.bind.mapping

import com.kotlin.android.bind.Type
import com.kotlin.android.bind.item.ItemViewDelegate

/**
 * 构建 [Type] 时，一个 [T] 类型对应多个 [ItemViewDelegate]
 *
 * [Type]:
 *      clazz: Class<T>
 *      delegate: ItemViewDelegate<T, *>
 *      linker: Linker<T>
 *
 * Created on 2021/7/8.
 *
 * @author o.s
 */
interface IClassToDelegates<T> {
    fun toDelegates(vararg delegate: ItemViewDelegate<T, *>): IClassToDelegatesWith<T>
}