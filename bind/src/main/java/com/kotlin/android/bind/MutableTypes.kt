package com.kotlin.android.bind

/**
 * 多类型集合
 *
 * Created on 2021/7/7.
 *
 * @author o.s
 */
class MutableTypes(
    val initialCapacity: Int,
    val types: MutableList<Type<*>> = ArrayList(initialCapacity)
) : Types {
    /**
     * 类型数量
     */
    override val size: Int
        get() = types.size

    /**
     * 注册指定类型
     */
    override fun <T> register(type: Type<T>) {
        types.add(type)
    }

    /**
     * 反注册指定类型的所有索引下标
     */
    override fun unregister(clazz: Class<*>): Boolean {
        return types.removeAll { it.clazz == clazz }
    }

    /**
     * 获取指定索引下标 [index] 位置的 [Type]
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> getType(index: Int): Type<T> {
        return types[index] as Type<T>
    }

    /**
     * 获取指定类型的索引下标
     */
    override fun firstIndexOf(clazz: Class<*>): Int {
        val index = types.indexOfFirst { it.clazz == clazz }
        if (index != -1) {
            return index
        }
        return types.indexOfFirst { it.clazz.isAssignableFrom(clazz) }
    }
}