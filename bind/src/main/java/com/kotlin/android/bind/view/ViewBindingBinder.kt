package com.kotlin.android.bind.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.kotlin.android.bind.ext.initViewBinding
import com.kotlin.android.bind.ext.initViewHolder
import com.kotlin.android.bind.holder.ViewBindingHolder

/**
 * 基于 [ViewBinding] 的视图绑定 [ViewBinder]
 *
 * Created on 2021/7/6.
 *
 * @author o.s
 */
open class ViewBindingBinder<T, VB : ViewBinding, VH : ViewBindingHolder<T, VB>>: ViewBinderInflater<T, VB, VH> {

    var holder: VH

    constructor(context: Context, parent: ViewGroup? = null) {
        holder = onCreateViewHolder(context, parent)

    }

    constructor(inflater: LayoutInflater, parent: ViewGroup? = null) {
        holder = onCreateViewHolder(inflater, parent)
    }

    final override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup?): VH {
        return initViewHolder(initViewBinding(inflater, parent))
    }

    final override fun onBindViewHolder(holder: VH, data: T) {
        holder.onBind(holder.binding, data)
    }

    override fun bind(data: T): ViewBindingBinder<T, VB, VH> {
        onBindViewHolder(holder, data)
        return this
    }

}