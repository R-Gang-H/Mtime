package com.kotlin.android.bind

import com.kotlin.android.bind.item.ItemViewDelegate

/**
 * Type
 *
 * Created on 2021/7/7.
 *
 * @author o.s
 */
data class Type<T>(
    val clazz: Class<T>,
    val delegate: ItemViewDelegate<T, *>,
    val linker: Linker<T>
)