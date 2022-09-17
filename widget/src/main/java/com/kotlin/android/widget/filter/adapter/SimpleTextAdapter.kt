package com.kotlin.android.widget.filter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlin.android.widget.R
import com.kotlin.android.widget.filter.view.FilterCheckedTextView

abstract class SimpleTextAdapter<T>(list: ArrayList<T>?, context: Context) :
    BaseBaseAdapter<T>(list, context) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    class FilterItemHolder {
        var checkedTextView: FilterCheckedTextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: FilterItemHolder
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lv_item_filter, parent, false)
            holder = FilterItemHolder()
            holder.checkedTextView = convertView as FilterCheckedTextView
            initCheckedTextView(holder.checkedTextView)
            convertView.setTag(holder)
        } else {
            holder = convertView.tag as FilterItemHolder
        }
        val t = getList()!![position]
        holder.checkedTextView!!.text = provideText(t)
        return convertView
    }

    abstract fun provideText(t: T): String?
    protected open fun initCheckedTextView(checkedTextView: FilterCheckedTextView?) {}

}