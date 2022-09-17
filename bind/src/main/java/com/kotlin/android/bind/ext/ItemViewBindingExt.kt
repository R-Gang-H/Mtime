package com.kotlin.android.bind.ext

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.kotlin.android.bind.holder.ItemViewBindingHolder
import com.kotlin.android.bind.item.ItemViewBindingBinder
import java.lang.reflect.ParameterizedType

/**
 * Item View Binding Ext
 * RecyclerView.Adapter ItemView 视图
 *
 * Created on 2021/7/7.
 *
 * @author o.s
 */

/**
 * 在 [ItemViewBindingBinder] 中初始化 [ItemViewBindingHolder] （即：[RecyclerView.ViewHolder]）
 */
@Suppress("UNCHECKED_CAST")
fun <T, VB : ViewBinding, VH : ItemViewBindingHolder<T, VB>> ItemViewBindingBinder<T, VB, VH>.initViewHolder(
    binding: VB,
): VH {
    val type = javaClass.genericSuperclass as ParameterizedType
    val clazzVB = type.actualTypeArguments[1] as Class<VB>
    val clazzVH = type.actualTypeArguments[2] as Class<VH>
    val c = clazzVH.getConstructor(clazzVB)
    return c.newInstance(binding)
}

/**
 * 在 [ItemViewBindingBinder] 中初始化 [ViewBinding]
 */
@Suppress("UNCHECKED_CAST")
fun <T, VB : ViewBinding, VH : ItemViewBindingHolder<T, VB>> ItemViewBindingBinder<T, VB, VH>.initViewBinding(
    layoutInflater: LayoutInflater,
    container: ViewGroup? = null,
): VB {
    val type = javaClass.genericSuperclass as ParameterizedType
    val clazz = type.actualTypeArguments[1] as Class<VB>
    val method = clazz.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )
    return method.invoke(null, layoutInflater, container, false) as VB
}