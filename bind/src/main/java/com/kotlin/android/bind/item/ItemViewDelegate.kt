package com.kotlin.android.bind.item

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kotlin.android.bind.adapter.MultiTypeAdapter

/**
 * Item视图绑定代理
 *
 * Created on 2021/7/5.
 *
 * @author o.s
 */
interface ItemViewDelegate<T, VH : ViewHolder> {
//    var _adapter: MultiTypeAdapter?

    var adapter: MultiTypeAdapter
//        get() {
//            if (_adapter == null) {
//                throw IllegalStateException("注册 $this 时没有附加 MultiTypeAdapter")
//            }
//            return _adapter!!
//        }
//        set(value) {
//            _adapter = value
//        }

    var adapterItems: ArrayList<Any>
        get() {
            return adapter.items
        }
        set(value) {
            adapter.items = value
        }

    fun onCreateViewHolder(context: Context, parent: ViewGroup?): VH
    fun onBindViewHolder(holder: VH, position: Int, item: T)
    fun getItemId(item: T): Long = RecyclerView.NO_ID
    fun onViewRecycled(holder: VH) {}
    fun onFailedToRecyclerView(holder: VH): Boolean {
        return false
    }

    fun onViewAttachedToWindow(holder: VH) {}
    fun onViewDetachedFromWindow(holder: VH) {}
}