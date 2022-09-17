package com.kotlin.android.bind.view

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.kotlin.android.bind.holder.ViewHolder

/**
 * 视图绑定
 *
 * Created on 2021/7/6.
 *
 * @author o.s
 */
abstract class ViewBinder<T, VB : ViewBinding, VH: ViewHolder> {

    protected abstract fun onCreateViewHolder(context: Context, parent: ViewGroup?): VH
    protected abstract fun onBindViewHolder(holder: VH, data: T)

    abstract fun bind(data: T): ViewBinder<T, VB, VH>

}