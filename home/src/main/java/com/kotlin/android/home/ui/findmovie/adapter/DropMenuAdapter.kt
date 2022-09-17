package com.kotlin.android.home.ui.findmovie.adapter

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.kotlin.android.app.data.entity.search.ConditionResult
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.widget.filter.adapter.MenuAdapter
import com.kotlin.android.widget.filter.adapter.SimpleTextAdapter
import com.kotlin.android.widget.filter.view.FilterCheckedTextView
import com.kotlin.android.widget.filter.view.SingleGridView

/**
 * tab 的adapter 且设置对应view
 */
class DropMenuAdapter(
    private val mContext: Context,
    private val conditionResult: ConditionResult,
    titles: Array<String>,
    var onFilterDone: (type: Int, positionTitle: String?, value: Long, value2: String) -> Unit
) : MenuAdapter {
    private val titles: Array<String> = titles
    override val menuCount: Int = titles.size

    val TYPE_LEIXING = 0//类型
    val TYPE_AREAR = 1//地区
    val TYPE_SORT = 2//排序
    val TYPE_YEAR = 3//年代

    override fun getMenuTitle(position: Int): String {
        return titles[position]
    }

    override fun getBottomMargin(position: Int): Int {
        return 0
    }


    override fun getView(position: Int, parentContainer: FrameLayout?): View? {
        var view = parentContainer?.getChildAt(position)

        when (position) {
            TYPE_LEIXING -> view =
                conditionResult.genres?.let { createSingleGridView(it, TYPE_LEIXING) }
            TYPE_AREAR -> view =
                conditionResult.locations?.let { createSingleGridView(it, TYPE_AREAR) }
            TYPE_SORT -> view =
                conditionResult.sorts?.let { createSingleGridView(it, TYPE_SORT) }
            TYPE_YEAR -> view =
                conditionResult.years?.let { createSingleGridView(it, TYPE_YEAR) }
        }
        return view
    }

    private fun createSingleGridView(list: ArrayList<ConditionResult.Content>, type: Int): View {
        val singleGridView = SingleGridView<ConditionResult.Content>(mContext)
            .adapter(object : SimpleTextAdapter<ConditionResult.Content>(list, mContext) {
                override fun provideText(content: ConditionResult.Content): String? {
                    return content.name
                }

                override fun initCheckedTextView(checkedTextView: FilterCheckedTextView?) {
                }


            })
        //回调参数
        singleGridView.mOnItemClickListener = { _, item ->
            run {
                var valueYear =
                    if (type == TYPE_YEAR) item.from.toString() + ":" + item.to else item.value.toString()

                onFilterDone?.invoke(
                    type,
                    item.name,
                    item.value.orZero(),
                    valueYear
                )
            }
        }

        singleGridView.setList(list, 0)
        return singleGridView
    }
}