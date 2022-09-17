package com.kotlin.android.widget.adapter.multitype

import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.v
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 多类型适配器扩展
 *
 * Created on 2020/12/8.
 *
 * @author o.s
 */

/**
 * 浸没更新Binder数据，并通知Adapter进行Item增加、删除、修改等操作。
 */
inline fun <Binder : MultiTypeBinder<*>, T> notifyBinderChanged(
        adapter: MultiTypeAdapter?,
        binderList: ArrayList<Binder>,
        dataList: List<T>?,
        crossinline isSame: (binder: Binder, data: T) -> Boolean,
        crossinline syncDataAndNotify: (binder: Binder, data: T) -> Boolean,
        createBinder: (data: T) -> Binder,
) {
    if (adapter == null) {
        return
    }
    if (dataList.isNullOrEmpty()) {
        binderList.clear()
        adapter.notifyAdapterClear()
    } else {
        // 先遍历旧数据，把不存在（匹配不上）的删除掉
        if (binderList.isNullOrEmpty()) {
            adapter.notifyAdapterAdded(binderList)
        }
        binderList.removeIf {
            var has = false
            dataList.forEach tag@{ data ->
                if (isSame(it, data)) {
                    has = true
                    return@tag
                }
            }
            if (!has) {
                // 不存在（匹配不上）则删除
                "删除 ${it.mPosition}".d()
                it.notifyAdapterSelfRemoved()
            }
            !has
        }
        // 再遍历新数据，把新增（匹配不上）的添加到列表中
        dataList.forEachIndexed { index, data ->
            var has = false
            binderList.forEach tag@{
                if (isSame(it, data)) {
                    if (syncDataAndNotify(it, data)) {
                        "改变 $data".v()
                        it.notifyAdapterSelfChanged()
                    } else {
                        "不变 $data".v()
                    }
                    has = true
                    return@tag
                }
            }
            if (!has) {
                // 新增（匹配不上）则添加
                "添加 index=$index $data".v()
                val binder = createBinder(data)
                val batterIndex = if (index > binderList.size) {
                    binderList.size
                } else {
                    index
                }
                binderList.add(batterIndex, binder)
                adapter.notifyAdapterInsert(batterIndex, binder)
            }
        }
    }
}