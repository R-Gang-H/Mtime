package com.kotlin.android.bind.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.kotlin.android.bind.holder.ViewHolder

/**
 * 使用 [LayoutInflater] 填充方式，视图绑定
 *
 * Created on 2021/7/6.
 *
 * @author o.s
 */
abstract class ViewBinderInflater<T, VB : ViewBinding, VH: ViewHolder> : ViewBinder<T, VB, VH>() {

    final override fun onCreateViewHolder(context: Context, parent: ViewGroup?): VH {
        return onCreateViewHolder(LayoutInflater.from(context), parent)
    }

    protected abstract fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup?): VH

}