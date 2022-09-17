package com.kotlin.android.bind.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.bind.adapter.MultiTypeAdapter

/**
 * Item视图绑定
 *
 * Created on 2021/7/6.
 *
 * @author o.s
 */
abstract class ItemViewBinder<T, VH : RecyclerView.ViewHolder> : ItemViewDelegate<T, VH> {
    private var _adapter: MultiTypeAdapter? = null

    override var adapter: MultiTypeAdapter
            get() {
            if (_adapter == null) {
                throw IllegalStateException("注册 $this 时没有附加 MultiTypeAdapter")
            }
            return _adapter!!
        }
        set(value) {
            _adapter = value
        }

    final override fun onCreateViewHolder(context: Context, parent: ViewGroup?): VH {
        return onCreateViewHolder(LayoutInflater.from(context), parent)
    }

    abstract fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup?): VH

}