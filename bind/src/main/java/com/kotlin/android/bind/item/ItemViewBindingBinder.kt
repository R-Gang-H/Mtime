package com.kotlin.android.bind.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.kotlin.android.bind.event.Action
import com.kotlin.android.bind.ext.initViewBinding
import com.kotlin.android.bind.ext.initViewHolder
import com.kotlin.android.bind.holder.ItemViewBindingHolder

/**
 * Item视图绑定
 *
 * Created on 2021/7/6.
 *
 * @author o.s
 */
abstract class ItemViewBindingBinder<T, VB : ViewBinding, VH : ItemViewBindingHolder<T, VB>> : ItemViewBinder<T, VH>() {

    lateinit var holder: VH

    private var onItemClick: ((action: Action<VH>) -> Unit)? = null

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup?): VH {
        holder = initViewHolder(initViewBinding(inflater, parent))
        holder.binder = this
        return holder
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: VH, position: Int, item: T) {
        holder.bind(holder.binding, position, item)
        holder.onItemClick = onItemClick as ((action: Action<Any>) -> Unit)?
    }

    fun withClick(click: ((action: Action<VH>) -> Unit)? = null): ItemViewBindingBinder<T, VB, VH> {
        onItemClick = click
        return this
    }
}