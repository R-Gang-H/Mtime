package com.kotlin.android.bind

/**
 * 类型集合接口
 *
 * Created on 2021/7/7.
 *
 * @author o.s
 */
interface Types {

    /**
     * 类型数量
     */
    val size: Int

    /**
     * 注册指定类型
     */
    fun <T> register(type: Type<T>)

    /**
     * 反注册指定类型的所有索引下标
     */
    fun unregister(clazz: Class<*>): Boolean

    /**
     * 获取指定索引下标 [index] 位置的 [Type]
     */
    fun <T> getType(index: Int): Type<T>

    /**
     * 获取指定类型的索引下标
     */
    fun firstIndexOf(clazz: Class<*>): Int
}