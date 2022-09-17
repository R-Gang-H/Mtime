package com.kotlin.android.bind.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kotlin.android.bind.DelegateNotFoundException
import com.kotlin.android.bind.MutableTypes
import com.kotlin.android.bind.Type
import com.kotlin.android.bind.Types
import com.kotlin.android.bind.impl.DefaultLinker
import com.kotlin.android.bind.item.ItemViewDelegate
import com.kotlin.android.bind.mapping.ClassToDelegatesBuilder
import com.kotlin.android.bind.mapping.IClassToDelegates
import kotlin.reflect.KClass

/**
 * 多类型适配器
 *
 * Created on 2021/7/7.
 *
 * @author o.s
 */
open class MultiTypeAdapter(
    open var items: ArrayList<Any> = ArrayList(),
    open val initialCapacity: Int = 1,
    open val types: Types = MutableTypes(initialCapacity)
) : RecyclerView.Adapter<ViewHolder>() {

    /***********************************************************************************************
     * register [Type]<T>
     *     clazz: Class<T>,
     *     delegate: ItemViewDelegate<T, *>,
     *     linker: Linker<T>
     **********************************************************************************************/

    /**
     * 使用 [delegate] 注册 [Type]
     */
    inline fun <reified T: Any> register(delegate: ItemViewDelegate<T, *>) {
        register(
            clazz = T::class.java,
            delegate = delegate
        )
    }

    /**
     * 使用 [clazz] 和 [delegate] 注册 [Type]
     */
    fun <T> register(
        clazz: Class<T>,
        delegate: ItemViewDelegate<T, *>
    ) {
        unregisterAllTypesIfNeeded(clazz)
        register(
            Type(
                clazz = clazz,
                delegate = delegate,
                linker = DefaultLinker()
            )
        )
    }

    /**
     * 使用 [clazz] 和 [delegate] 注册 [Type]
     */
    fun <T : Any> register(
        clazz: KClass<T>,
        delegate: ItemViewDelegate<T, *>
    ) {
        unregisterAllTypesIfNeeded(clazz.java)
        register(
            Type(
                clazz = clazz.java,
                delegate = delegate,
                linker = DefaultLinker()
            )
        )
    }

    /**
     * 构建 [Type] 时，一个 [T] 类型对应多个 [ItemViewDelegate]
     */
    inline fun <reified T : Any> register(): IClassToDelegates<T> {
        return register(T::class.java)
    }

    /**
     * 构建 [Type] 时，一个 [KClass] 类型对应多个 [ItemViewDelegate]
     */
    fun <T : Any> register(clazz: KClass<T>): IClassToDelegates<T> {
        return register(clazz.java)
    }

    /**
     * 构建 [Type] 时，一个 [Class] 类型对应多个 [ItemViewDelegate]
     */
    fun <T> register(clazz: Class<T>): IClassToDelegates<T> {
        unregisterAllTypesIfNeeded(clazz = clazz)
        return ClassToDelegatesBuilder(this, clazz)
    }

    /**
     * 注册 [Type]
     */
    fun <T> register(type: Type<T>) {
        types.register(type)
        type.delegate.adapter = this
    }

    /**
     * 注册 [Types] 类型集合
     */
    fun <T> registerAll(types: Types) {
        val size = types.size
        for (i in 0 until size) {
            val type = types.getType<T>(i)
            unregisterAllTypesIfNeeded(type.clazz)
            register(type)
        }
    }

    /***********************************************************************************************
     * override supper class start
     **********************************************************************************************/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return getDelegateByIndex(viewType).onCreateViewHolder(parent.context, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getDelegateByViewHolder(holder).onBindViewHolder(holder, position, items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return indexInTypesOf(position, items[position])
    }

    override fun getItemId(position: Int): Long {
        val item = items[position]
        val viewType = getItemViewType(position)
        return getDelegateByIndex(viewType).getItemId(item)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        getDelegateByViewHolder(holder).onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: ViewHolder): Boolean {
        return getDelegateByViewHolder(holder).onFailedToRecyclerView(holder)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        getDelegateByViewHolder(holder).onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        getDelegateByViewHolder(holder).onViewDetachedFromWindow(holder)
    }

    /***********************************************************************************************
     * override supper class end
     **********************************************************************************************/

    /**
     * 根据 [ViewHolder] 获取 [ItemViewDelegate] 对象
     */
    private fun getDelegateByViewHolder(holder: ViewHolder): ItemViewDelegate<Any, ViewHolder> {
        return getDelegateByIndex(holder.itemViewType)
    }

    /**
     * 根据索引下标（itemViewType）获取 [ItemViewDelegate] 对象
     */
    private fun getDelegateByIndex(index: Int): ItemViewDelegate<Any, ViewHolder> {
        @Suppress("UNCHECKED_CAST")
        return types.getType<Any>(index).delegate as ItemViewDelegate<Any, ViewHolder>
    }

    /**
     * 根据 [position] 和 [item] 获取索引下标（itemViewType）
     */
    @Throws(DelegateNotFoundException::class)
    private fun indexInTypesOf(position: Int, item: Any): Int {
        val index = types.firstIndexOf(item.javaClass)
        if (index != -1) {
            val linker = types.getType<Any>(index).linker
            return index + linker.index(position, item)
        }
        throw DelegateNotFoundException(item.javaClass)
    }

    private fun unregisterAllTypesIfNeeded(clazz: Class<*>) {
        if (types.unregister(clazz)) {
//            "将覆盖之前注册的 ${clazz.name} 类型".e()
        }
    }
}