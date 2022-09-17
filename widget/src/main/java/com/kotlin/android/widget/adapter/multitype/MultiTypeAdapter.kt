package com.kotlin.android.widget.adapter.multitype

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.widget.adapter.multitype.adapter.binder.ClickBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.adapter.callback.DiffItemCallback
import com.kotlin.android.widget.adapter.multitype.adapter.holder.MultiTypeViewHolder

/**
 * 创建者: zl
 * 创建时间: 2020/7/1 10:09 AM
 * 描述:
 */
class MultiTypeAdapter : RecyclerView.Adapter<MultiTypeViewHolder>(), ClickBinder {
    private var mOnClickListener: ((view: View, binder: MultiTypeBinder<*>) -> Unit)? = null

    private var mOnLongClickListener: ((view: View, binder: MultiTypeBinder<*>) -> Unit)? = null

    /**
     * 设置View点击事件
     * 注意：这里的事件优先级高，将会覆盖MultiTypeBinder中的点击事件
     */
    fun setOnClickListener(listener: ((view: View, binder: MultiTypeBinder<*>) -> Unit)?): MultiTypeAdapter {
        this.mOnClickListener = listener
        return this
    }

    /**
     * 设置View长按点击事件
     * 注意：这里的事件优先级高，将会覆盖MultiTypeBinder中的点击事件
     */
    fun setOnLongClickListener(listener: (view: View, binder: MultiTypeBinder<*>) -> Unit): MultiTypeAdapter {
        this.mOnLongClickListener = listener
        return this
    }

    /**
     * 使用后台线程通过差异性计算来更新列表
     * 在后台线程中使用DiffUtil计算两个列表之间的差异的辅助类。AsyncListDiffer 的计算主要submitList 方法中。
     */
    private val mAsyncListChange by lazy {
        AsyncListDiffer(
            this,
            DiffItemCallback<MultiTypeBinder<*>>()
        )
    }

    //
    /***
     * 存储 MultiTypeBinder
     */
    private var mBinderList = ArrayList<MultiTypeBinder<*>>()

    init {
        setHasStableIds(true)
    }

    fun notifyAdapterClear() {
        mBinderList.clear()
        mAsyncListChange.submitList(null)
    }

    /**
     * 在某个位置添加binder
     */
    fun notifyAdapterInsert(tagPosition: Int, binder: MultiTypeBinder<*>) {
        notifyAdapterInsert(tagPosition, listOf(binder))
    }

    /**
     * 在某个位置添加binderList
     */
    fun notifyAdapterInsert(
        tagPosition: Int,
        binderList: List<MultiTypeBinder<*>>,
        callback: (() -> Unit)? = null
    ) {
        if (tagPosition < 0) {
            mBinderList.addAll(binderList)
        } else {
            mBinderList.addAll(tagPosition, binderList)
        }
        submitList(callback)
    }

    /**
     * 配置新数据，可以通过isScrollToTop来控制是否让列表从顶部开始显示
     */
    fun notifyAdapterDataSetChanged(
        binders: List<MultiTypeBinder<*>>?,
        isScrollToTop: Boolean = true,
        callback: (() -> Unit)? = null
    ) {
        if (isScrollToTop) {
            notifyAdapterRemovedAll {
                notifyAdapterAdded(binders ?: listOf(), callback)
            }
        } else {
            mBinderList.clear()
            notifyAdapterAdded(binders ?: listOf(), callback)
        }
    }

    /**
     * 追加单条数据
     */
    fun notifyAdapterAdded(binder: MultiTypeBinder<*>, callback: (() -> Unit)? = null) {
        notifyAdapterAdded(listOf(binder), callback)
    }

    /**
     * 追加一个列表数据
     */
    fun notifyAdapterAdded(binders: List<MultiTypeBinder<*>>, callback: (() -> Unit)? = null) {
        mBinderList.addAll(binders)
        submitList(callback)
    }

    /**
     * 移除单条数据
     */
    fun notifyAdapterRemoved(binder: MultiTypeBinder<*>, callback: (() -> Unit)? = null) {
        notifyAdapterRemoved(listOf(binder), callback)
    }

    /**
     * 移除指定列表中的数据
     */
    fun notifyAdapterRemoved(binderList: List<MultiTypeBinder<*>>, callback: (() -> Unit)? = null) {
        binderList.forEach {
            it.setAdapter(null)
            mBinderList.remove(it)
        }

        submitList(callback)
    }

    /**
     * 移除指定列表中的所有数据
     */
    fun notifyAdapterRemovedAll(callback: (() -> Unit)? = null) {
        mBinderList.clear()
        submitList(callback)
    }

    private fun submitList(callback: (() -> Unit)? = null) {
        mAsyncListChange.submitList(
            mBinderList.map { it.setAdapter(this@MultiTypeAdapter) },
            if (null == callback) null else Runnable(callback)
        )
    }

    /**
     * 添加 Binder 类型
     */
    override fun getItemViewType(position: Int): Int {
        val mItemBinder = mAsyncListChange.currentList[position]
        return mItemBinder?.layoutId().orZero()
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount(): Int = mAsyncListChange.currentList.size

    /**
     * 对 Binder 的 Layout 进行初始化，其中 inflateDataBinding 为 Kotlin 扩展，主要是将 Layout 转换为一个 ViewDataBinding 的对象
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiTypeViewHolder {
        try {
            return MultiTypeViewHolder(parent.inflateDataBinding(viewType))
        } catch (e: Throwable) {
            "viewType=${viewType}, e=$e".e()
            throw NullPointerException("不存在${viewType}类型的ViewHolder! :${e.message.orEmpty()}")
        }
    }

    /**
     * 调用 Binder 中的绑定方法，用以绑定数据。
     */
    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: MultiTypeViewHolder, position: Int) {
        val binder = mAsyncListChange.currentList[position] as MultiTypeBinder<ViewDataBinding>
        mOnClickListener?.let {
            binder.setOnClickListener(it)
        }
        mOnLongClickListener?.let {
            binder.setOnLongClickListener(it)
        }
        holder.onBindViewHolder(binder)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewAttachedToWindow(holder: MultiTypeViewHolder) {
        if (holder.bindingAdapterPosition >= 0 && !mAsyncListChange.currentList.isNullOrEmpty()) {
            holder.onViewAttachedToWindow(
                mAsyncListChange.currentList[holder.bindingAdapterPosition]
                        as MultiTypeBinder<ViewDataBinding>
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewDetachedFromWindow(holder: MultiTypeViewHolder) {
        if (holder.bindingAdapterPosition >= 0 && !mAsyncListChange.currentList.isNullOrEmpty()) {
            holder.onViewDetachedFromWindow(
                mAsyncListChange.currentList[holder.bindingAdapterPosition]
                        as MultiTypeBinder<ViewDataBinding>
            )
        }
    }

    fun getList() = mAsyncListChange.currentList
}