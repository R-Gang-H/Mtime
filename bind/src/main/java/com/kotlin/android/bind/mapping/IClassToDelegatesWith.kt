package com.kotlin.android.bind.mapping

import com.kotlin.android.bind.Linker
import com.kotlin.android.bind.Type
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
interface IClassToDelegatesWith<T> {

    /**
     * 根据指定的链接器 [linker] 链接 [T] 和 [ItemViewDelegate]
     */
    fun withLinker(linker: Linker<T>)

    /**
     * 根据指定的链接器 [linker] 链接 [T] 和 [ItemViewDelegate]
     */
    fun withLinker(linker: (position: Int, item: T) -> Int) {
        withLinker(
            object : Linker<T> {
                /**
                 * 返回条目视图注册的代理索引下标（即：itemViewType）
                 */
                override fun index(position: Int, item: T): Int {
                    return linker.invoke(position, item)
                }
            }
        )
    }

    /**
     * 根据指定的链接器 [linker] 链接 [T] 和 [ItemViewDelegate]
     */
    fun withLinker(linker: (item: T) -> Int) {
        withLinker(
            object : Linker<T> {
                /**
                 * 返回条目视图注册的代理索引下标（即：itemViewType）
                 */
                override fun index(position: Int, item: T): Int {
                    return linker.invoke(item)
                }
            }
        )
    }
}