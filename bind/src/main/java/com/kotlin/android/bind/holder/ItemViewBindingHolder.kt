package com.kotlin.android.bind.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.kotlin.android.bind.event.Action
import com.kotlin.android.bind.item.ItemViewBindingBinder

/**
 * 基于 [ViewBinding] 的 [RecyclerView.ViewHolder]
 *
 * Created on 2021/7/6.
 *
 * @author o.s
 */
abstract class ItemViewBindingHolder<T, VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
    private val mListener by lazy {
        View.OnClickListener { v ->
            onItemClick?.invoke(Action(view = v, holder = this))
        }
    }

    /**
     * 当前 Item 位置
     */
    var mPosition: Int = -1

    /**
     * 当前 Item 持有数据
     */
    var data: T? = null

    /**
     * 当前 Item 持有的 Binder 对象
     */
    var binder: ItemViewBindingBinder<T, VB, *>? = null

    /**
     * 点击事件监听
     */
    var onItemClick: ((action: Action<Any>) -> Unit)? = null

    /**
     * View绑定数据
     */
    abstract fun onBind(binding: VB, position: Int, item: T)

    /**
     * 内部绑定调用
     */
    fun bind(binding: VB, position: Int, item: T) {
        mPosition = position
        data = item
        onBind(binding, position, item)
    }

    /**
     * 给View设置点击事件
     */
    fun setOnItemClick(view: View) {
        view.setOnClickListener(mListener)
    }

    /**
     * 通知修改当前Item：data数据发生变化更新UI
     */
    fun notifyItemChanged() {
        data?.apply {
            onBind(binding, mPosition, this)
        }
    }
}