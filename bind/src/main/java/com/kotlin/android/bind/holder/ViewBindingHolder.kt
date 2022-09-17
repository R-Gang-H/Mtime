package com.kotlin.android.bind.holder

import android.view.View
import androidx.viewbinding.ViewBinding

/**
 * 基于 [ViewBinding] 的 [ViewHolder]
 *
 * Created on 2021/7/6.
 *
 * @author o.s
 */
abstract class ViewBindingHolder<T, VB : ViewBinding>(open val binding: VB) : ViewHolder {
    abstract fun onBind(binding: VB, data: T)
    var onClick: ((view: View, item: T) -> Unit)? = null
}