package com.kotlin.android.widget.adapter.multitype.adapter.holder

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: zl
 * 创建时间: 2020/7/1 10:07 AM
 * 描述:MultiTypeViewHolder继承自RecyclerView.ViewHolder，传入一个ViewDataBinding对象，
 * 在这里对MultiTypeBinder中的ViewDataBinding对象进行解绑和绑定操作。
 */
class MultiTypeViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root), AutoCloseable {

    private var mAlreadyBinding: MultiTypeBinder<ViewDataBinding>? = null

    /**
     * 绑定Binder
     */
    fun onBindViewHolder(items: MultiTypeBinder<ViewDataBinding>) {
        // 如果两次绑定的 Binder 不一致，则直接销毁
        if (mAlreadyBinding != null && items !== mAlreadyBinding) close()
        // 开始绑定
        items.bindViewDataBinding(binding, adapterPosition)
        // 保存绑定的 Binder
        mAlreadyBinding = items
    }

    fun onViewAttachedToWindow(items: MultiTypeBinder<ViewDataBinding>) {
        items.viewAttachedToWindow(binding, adapterPosition)
    }

    fun onViewDetachedFromWindow(items: MultiTypeBinder<ViewDataBinding>) {
        items.viewDetachedFromWindow(binding, adapterPosition)
    }

    /**
     * 销毁绑定的Binder
     */
    override fun close() {
        mAlreadyBinding?.unbindDataBinding()
        mAlreadyBinding = null
    }
}