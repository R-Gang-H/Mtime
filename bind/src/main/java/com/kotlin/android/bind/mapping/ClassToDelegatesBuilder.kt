package com.kotlin.android.bind.mapping

import com.kotlin.android.bind.Linker
import com.kotlin.android.bind.Type
import com.kotlin.android.bind.adapter.MultiTypeAdapter
import com.kotlin.android.bind.item.ItemViewDelegate

/**
 * 在构建 [Type] 时，一个 [T] 类型对应多个 [ItemViewDelegate]
 * 根据指定的链接器 [Linker] 链接 [T] 和 [ItemViewDelegate]
 *
 * withLinker(Linker)
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
class ClassToDelegatesBuilder<T>(
    val adapter: MultiTypeAdapter,
    val clazz: Class<T>
) : IClassToDelegates<T>, IClassToDelegatesWith<T> {

    private var delegates: Array<ItemViewDelegate<T, *>>? = null

    override fun toDelegates(vararg delegate: ItemViewDelegate<T, *>): IClassToDelegatesWith<T> {
        @Suppress("UNCHECKED_CAST")
        delegates = delegate as Array<ItemViewDelegate<T, *>>
        return this
    }

    override fun withLinker(linker: Linker<T>) {
        delegates?.forEach {
            adapter.register(
                Type(
                    clazz = clazz,
                    delegate = it,
                    linker = linker
                )
            )
        }
    }
}