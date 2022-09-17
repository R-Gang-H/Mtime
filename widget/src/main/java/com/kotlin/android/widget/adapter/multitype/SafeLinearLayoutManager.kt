package com.kotlin.android.widget.adapter.multitype

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView 存在的一个明显的 bug 一直没有修复：
 * java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid item position…
 *
 * Created on 2020/12/10.
 *
 * @author o.s
 */
class SafeLinearLayoutManager : LinearLayoutManager {

    constructor(context: Context) : super(context)
    constructor(context: Context, @RecyclerView.Orientation orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

    }
}