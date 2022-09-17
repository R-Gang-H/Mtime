package com.kotlin.android.widget.adapter.multitype.adapter.binder

import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.widget.R
import com.kotlin.android.widget.BR
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter

/**
 * 创建者: zl
 * 创建时间: 2020/6/30 6:08 PM
 * 描述:
 */
abstract class MultiTypeBinder<V : ViewDataBinding> : ClickBinder {
    protected var mOnClickListener: ((view: View, binder: MultiTypeBinder<*>) -> Unit)? = null

    protected var mOnLongClickListener: ((view: View, binder: MultiTypeBinder<*>) -> Unit)? = null

    /**
     * 设置View点击事件
     * 注意：这里的事件优先级高，将会覆盖MultiTypeBinder中的点击事件
     */
    fun setOnClickListener(listener: (view: View, binder: MultiTypeBinder<*>) -> Unit) {
        this.mOnClickListener = listener
    }

    /**
     * 设置View长按点击事件
     * 注意：这里的事件优先级高，将会覆盖MultiTypeBinder中的点击事件
     */
    fun setOnLongClickListener(listener: (view: View, binder: MultiTypeBinder<*>) -> Unit) {
        this.mOnLongClickListener = listener
    }

    /**
     * BR.data
     */
    protected open val variableId = BR.data

    /**
     * 绑定的adapter
     */
    private var mAdapter: MultiTypeAdapter? = null

    /**
     * item的adapter position
     */
    open var mPosition: Int = -1

    /**
     * 被绑定的ViewDataBinding
     */
    open var binding: V? = null

    /**
     * 给绑定的View设置tag
     */
    private var bindingViewVersion = (0L until Long.MAX_VALUE).random()

    /**
     * 返回LayoutId，供Adapter使用
     */
    @LayoutRes
    abstract fun layoutId(): Int

    /**
     * 数据内容比较，比较两次内容是否一致，刷新UI时用到。
     */
    abstract fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean

    /**
     * 比较两次MultiTypeBinder是否时同一个Binder
     */
    open fun areItemsTheSame(other: MultiTypeBinder<*>): Boolean {
        return hashCode() == other.hashCode()
    }

    /**
     * 绑定ViewDataBinding
     */
    fun bindViewDataBinding(binding: V, position: Int) {
        mPosition = position
        onPreBindViewHolder(binding, position)
        // 如果此次绑定与已绑定的一至，则不做绑定
        if (this.binding === binding && binding.root.getTag(R.id.bindingVersion) == bindingViewVersion) return
        binding.root.setTag(R.id.bindingVersion, ++bindingViewVersion)
        onUnBindViewHolder(this.binding)
        this.binding = binding
        binding.setVariable(variableId, this)

        // 给 binding 绑定生命周期，方便观察LiveData的值，进而更新UI。如果不绑定，LiveData的值改变时，UI不会更新
        if (binding.root.context is LifecycleOwner) {
            binding.lifecycleOwner = binding.root.context as LifecycleOwner
        } else {
            binding.lifecycleOwner = AlwaysActiveLifecycleOwner()
        }
        onBindViewHolder(binding, position)
        // 及时更新绑定数据的View
        binding.executePendingBindings()
    }

    /**
     * 解绑ViewDataBinding
     */
    fun unbindDataBinding() {
        if (this.binding != null) {
            onUnBindViewHolder(binding)
            this.binding = null
        }
    }

    /**
     * 设置多类型adapter
     */
    fun setAdapter(adapter: MultiTypeAdapter?): MultiTypeBinder<V> {
        mAdapter = adapter
        return this
    }

    /**
     * 自身数据变化，通知adapter刷新
     */
    fun notifyAdapterSelfChanged() {
//        mAdapter?.notifyAdapterChanged(this)
        mAdapter?.notifyItemChanged(mPosition)
    }

    fun getItemCount() = mAdapter?.itemCount ?: 0

    val lastPosition: Int
        get() = getItemCount() - 1

    /**
     * 移除自身，通知adapter刷新
     */
    fun notifyAdapterSelfRemoved() {
        mAdapter?.notifyAdapterRemoved(this)
        mAdapter = null
    }

    /**
     * view渲染到视图通知
     */
    fun viewAttachedToWindow(binding: V, position: Int) {
        onViewAttachedToWindow(binding, position)
    }

    /**
     * view将从视频移除通知
     */
    fun viewDetachedFromWindow(binding: V, position: Int) {
        onViewDetachedFromWindow(binding, position)
    }

    protected open fun onPreBindViewHolder(binding: V, position: Int) {

    }

    /**
     * 绑定后对View的一些操作，如：赋值，修改属性
     * 与RecyclerView.Adapter中的onBindViewHolder方法功能一致，在该方法中做一些数据绑定与处理，不过这里推荐使用DataBinding去绑定数据，以数据去驱动UI。
     */
    protected open fun onBindViewHolder(binding: V, position: Int) {

    }

    /**
     * 解绑操作
     * 处理一些需要释放的资源
     */
    protected open fun onUnBindViewHolder(binding: V?) {

    }

    /**
     * view渲染到视图
     */
    protected open fun onViewAttachedToWindow(binding: V, position: Int) {

    }

    /**
     * view将从视频移除
     */
    protected open fun onViewDetachedFromWindow(binding: V, position: Int) {

    }

    /**
     * 将整个binder隐藏
     */
    fun gone() {
        val layoutParams = binding?.root?.layoutParams
        layoutParams?.height = 0
        binding?.root?.layoutParams = layoutParams
    }

    /**
     * 将整个binder展示
     */
    fun visible(px: Int) {
        val layoutParams = binding?.root?.layoutParams
        layoutParams?.height = px
        binding?.root?.layoutParams = layoutParams
    }

    /**
     * 触发View点击事件时回调，携带参数
     */
    open fun onClick(view: View) {
        mOnClickListener?.invoke(view, this)
    }

    /**
     * 触发View长按事件时回调，携带参数
     */
    open fun onLongClick(view: View) {
        mOnLongClickListener?.invoke(view, this)
    }

    /**
     * 为 Binder 绑定生命周期，在 {@link Lifecycle.Event#ON_RESUME} 时响应
     */
    internal class AlwaysActiveLifecycleOwner : LifecycleOwner {

        override fun getLifecycle(): Lifecycle = object : LifecycleRegistry(this) {
            init {
                handleLifecycleEvent(Event.ON_RESUME)
            }
        }
    }
}