package com.kotlin.android.widget.filter.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.util.*

abstract class BaseBaseAdapter<T>(list: ArrayList<T>?, context: Context) : BaseAdapter() {
    private var list: ArrayList<T>? = null
    protected var context: Context

    /**
     * 设置数据。<BR></BR>
     * 会清空原集合所有数据,后添加。
     *
     * @param list
     */
    fun setList(list: ArrayList<T>?) {
        var list = list
        if (list == null) {
            list = ArrayList(0)
        }
        this.list = list
        notifyDataSetChanged()
    }

    fun getList(): ArrayList<T>? {
        return list
    }

    override fun getCount(): Int {
        return list!!.size
    }

    override fun getItem(position: Int): T {
        return list!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    abstract override fun getView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View

    init {
        setList(list)
        this.context = context
    }
}