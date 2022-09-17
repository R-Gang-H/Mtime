package com.kotlin.android.widget.adapter.bindingadapter

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

import com.kotlin.android.widget.BR
import com.kotlin.android.widget.adapter.initViewBinding
import com.kotlin.android.widget.adapter.multitype.bindingErrorView

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/28
 *
 * RecyclerView普通列表Adapter
 */
abstract class BaseBindingAdapter<T, VB : ViewBinding>(var items: List<T> = emptyList())
    : RecyclerView.Adapter<BaseBindingHolder<T, VB>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingHolder<T, VB> {
        return BaseBindingHolder(
            parent.context,
            initViewBinding(parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseBindingHolder<T, VB>, position: Int) {
        holder.bind(items[position])
        onBinding(binding = holder.binding, item = items[position], position = position)
    }

    abstract fun onBinding(binding: VB?, item: T, position: Int)

    open fun setData(datas: List<T>?): BaseBindingAdapter<T, VB> {
        items = datas ?: emptyList()
        notifyDataSetChanged()
        return this
    }

    open fun getData() = items
}

class BaseBindingHolder<T, VB : ViewBinding>(val context: Context, val binding: VB?):
    RecyclerView.ViewHolder(binding?.root ?: bindingErrorView(context)) {
    fun bind(itemData: T) {
        if (binding is ViewDataBinding) {
            binding.setVariable(BR.data, itemData)
            binding.executePendingBindings()
        }
    }
}