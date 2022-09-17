package com.kotlin.android.ktx.ext.list

/**
 * List 相关扩展
 *
 * Created on 2021/8/26.
 *
 * @author o.s
 */

/**
 * 获取列表中的 ids
 * 使用列表对象中某个属性，拼接成 [separator] 连接字符串。
 * 其中，[block] 返回 null 时，则跳过当前元素，适用于List<Any>混杂列表中使用某些对象的某些指定属性拼接的场景
 */
inline fun <T> List<T>.ids(separator: String = ",", block: (T) -> Any?): String {
    val sb = StringBuilder()
    forEach {
        block(it)?.apply {
            if (sb.isNotEmpty()) {
                sb.append(separator)
            }
            sb.append(this)
        }
    }
    return sb.toString()
}