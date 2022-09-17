package com.kotlin.android.widget.adapter.multitype.adapter.callback

import androidx.recyclerview.widget.DiffUtil
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: zl
 * 创建时间: 2020/7/1 10:06 AM
 * 描述:areItemsTheSame(oldItem: T, newItem: T)：比较两次MultiTypeBinder是否时同一个Binder
 * areContentsTheSame(oldItem: T, newItem: T)：比较两次MultiTypeBinder的类容是否一致。
 */
class DiffItemCallback <T : MultiTypeBinder<*>> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
//        return oldItem.layoutId() == newItem.layoutId()
        return oldItem.areItemsTheSame(newItem)//比较两次MultiTypeBinder是否时同一个Binder
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }

}