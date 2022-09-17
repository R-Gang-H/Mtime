package com.kotlin.android.widget.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.widget.adapter.bindingadapter.BaseBindingAdapter
import java.lang.reflect.ParameterizedType

/**
 * Created by suq on 2021/12/6
 * des: Adapter相关ViewBinding 各种初始化情况的扩展
 */


/**
 * 初始化 [BaseBindingAdapter] 中的 [ViewBinding]
 * 适用于第二个范型参数为 [ViewBinding] 的 [BaseBindingAdapter]
 */
@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> BaseBindingAdapter<*, VB>.initViewBinding(
    container: ViewGroup? = null,
    attachToRoot: Boolean = false
): VB? {
    var viewBinding: VB? = null
    val type = javaClass.genericSuperclass
    if (type is ParameterizedType) {
        val clazz = type.actualTypeArguments[1] as? Class<VB>
        val method = clazz?.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        try {
            val vb = method?.invoke(
                null,
                LayoutInflater.from(container?.context),
                container,
                attachToRoot)
            viewBinding = (vb as? VB).apply {
                if (container?.context is LifecycleOwner && this is ViewDataBinding) {
                    lifecycleOwner = container.context as LifecycleOwner
                }
            }
        } catch (e: Exception) {
            "initViewBinding >>> ${clazz?.simpleName}, e=$e".e()
            e.cause?.printStackTrace()
            return null
        } finally {
            return viewBinding
        }
    }
    return viewBinding
}

///**
// * 初始化 [MultiTypeBinder] 中的 [ViewBinding]
// * 适用于第一个范型参数为 [ViewBinding] 的 [MultiTypeBinder]
// */
//@Suppress("UNCHECKED_CAST")
//fun <VB : ViewDataBinding> MultiTypeBinder<VB>.initViewBinding(
//    container: ViewGroup? = null,
//    attachToRoot: Boolean = false
//): VB? {
//    var viewBinding: VB? = null
//    val type = javaClass.genericSuperclass
//    if (type is ParameterizedType) {
//        val clazz = type.actualTypeArguments[0] as? Class<VB>
//        val method = clazz?.getMethod(
//            "inflate",
//            LayoutInflater::class.java,
//            ViewGroup::class.java,
//            Boolean::class.java
//        )
//        viewBinding = (method?.invoke(
//            null,
//            LayoutInflater.from(container?.context),
//            container,
//            attachToRoot) as? VB)
//    }
//    return viewBinding
//}