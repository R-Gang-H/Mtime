package com.kotlin.android.publish.component.widget.article.view

/**
 *
 * Created on 2022/3/30.
 *
 * @author o.s
 */

fun <T> ArrayList<T>?.safeGet(index: Int, def: T): T {
    return this?.let {
        if (index in 0 until it.size) {
            get(index)
        } else {
            def
        }
    } ?: def
}

fun <T> ArrayList<T>?.safeGet(index: Int): T? {
    return this?.let {
        if (index in 0 until it.size) {
            get(index)
        } else {
            null
        }
    }
}