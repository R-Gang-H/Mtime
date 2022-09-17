package com.kotlin.android.widget.filter.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.filter.adapter.BaseBaseAdapter
import java.util.ArrayList

/**
 * 结果展示view
 */
class SingleGridView<T> : GridView, AdapterView.OnItemClickListener {
    private var mAdapter: BaseBaseAdapter<T>? = null
    var mOnItemClickListener: ((position:Int,item: T)->Unit)? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        choiceMode = CHOICE_MODE_SINGLE
        selector = ColorDrawable(Color.TRANSPARENT)
        numColumns = 4
        setBackgroundColor(Color.WHITE)
        isSmoothScrollbarEnabled = false
        verticalSpacing = 9.dp
        horizontalSpacing = 9.dp
        setPadding(15.dp, 5.dp, 15.dp, 10.dp)
        onItemClickListener = this
    }

    fun adapter(adapter: BaseBaseAdapter<T>?): SingleGridView<T> {
        mAdapter = adapter
        setAdapter(adapter)
        return this
    }

    fun setList(list: ArrayList<T>?, checkedPosition: Int) {
        mAdapter!!.setList(list)
        if (checkedPosition != -1) {
            setItemChecked(checkedPosition, true)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        if (isFastDoubleClick) {
            return
        }
        val item = mAdapter!!.getItem(position)
        mOnItemClickListener?.invoke(position,item)
    }
    var mLastClickTime: Long = 0
    private val isFastDoubleClick: Boolean
        get() {
            val time = System.currentTimeMillis()
            val timeD = time - mLastClickTime
            if (timeD in 1..499) {
                return true
            }
            mLastClickTime = time
            return false
        }
}