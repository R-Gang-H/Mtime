package com.kotlin.android.widget.adapter.multitype

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import java.lang.reflect.ParameterizedType

/**
 * 创建者: zl
 * 创建时间: 2020/7/1 10:08 AM
 * 描述:
 */
/**
 * 创建一个MultiTypeGeneralAdapter
 */
fun createMultiTypeAdapter(
    recyclerView: RecyclerView,
    layoutManager: RecyclerView.LayoutManager? = null
): MultiTypeAdapter {
    if (null != layoutManager) {
        recyclerView.layoutManager = layoutManager
    }
    val mMultiTypeAdapter = MultiTypeAdapter()
    recyclerView.adapter = mMultiTypeAdapter
    if (layoutManager !is StaggeredGridLayoutManager) {//保留瀑布流添加数据改变布局特性
        recyclerView.itemAnimator?.changeDuration = 0L
    }
    // 处理RecyclerView的触发回调
    recyclerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View?) {
            mMultiTypeAdapter.onDetachedFromRecyclerView(recyclerView)
        }

        override fun onViewAttachedToWindow(v: View?) {}
    })
    return mMultiTypeAdapter
}

/**
 * MultiTypeGeneralAdapter扩展函数，重载MultiTypeGeneralAdapter类，使用invoke操作符调用MultiTypeGeneralAdapter内部函数。
 */
inline operator fun MultiTypeAdapter.invoke(block: MultiTypeAdapter.() -> Unit): MultiTypeAdapter {
    this.block()
    return this
}

fun bindingErrorView(context: Context): View {
    return TextView(context).apply { 
        text = "ViewBinding inflate error!!"
    }
}

/**
 * Layout converter ViewDataBinding
 */
fun <T : ViewDataBinding> ViewGroup.inflateDataBinding(layoutId: Int, any: Any? = null): T {
//    return try {
        return DataBindingUtil.inflate(
            LayoutInflater.from(context), layoutId, this, false
        )
//    } catch (e: Throwable) {
//        "layoutId=${layoutId}, lastItemBinder=$any, e=$e".e()
//        null
//    }
}

fun <VB : ViewDataBinding> MultiTypeBinder<VB>.initViewBinding(
    container: ViewGroup? = null,
    attachToRoot: Boolean = false
): VB? {
    var viewBinding: VB? = null
    val type = javaClass.genericSuperclass
    if (type is ParameterizedType) {
        val clazz = type.actualTypeArguments[0] as? Class<VB>
        val method = clazz?.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        try {
            viewBinding = (method?.invoke(null, LayoutInflater.from(container?.context), container, attachToRoot) as? VB)?.apply {
                if (this is ViewDataBinding) {
                    if (viewBinding?.root?.context is LifecycleOwner) {
                        this.lifecycleOwner = viewBinding?.root?.context as? LifecycleOwner
                    } else {
                        this.lifecycleOwner = MultiTypeBinder.AlwaysActiveLifecycleOwner()
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            "clazz=$clazz, e=${e.message}".e()
            viewBinding = null
        }
    }
    return viewBinding
}

