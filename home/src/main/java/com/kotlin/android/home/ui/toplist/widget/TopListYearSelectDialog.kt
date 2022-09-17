package com.kotlin.android.home.ui.toplist.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import com.kotlin.android.home.R
import com.kotlin.android.ktx.ext.click.onClick
import kotlinx.android.synthetic.main.dialog_toplist_year_select.*
import java.util.*

/**
 * @author vivian.wei
 * @date 2020/8/25
 * @desc 类描述
 */
class TopListYearSelectDialog(selectYear: Long, years: List<Long>, private val callback: (year: Long) -> Unit): DialogFragment() {

    private var mSelectYear = selectYear
    private var mYears = years

    companion object {
        fun newInstance(selectYear: Long, years: List<Long>, callback: (year: Long) -> Unit)
                = TopListYearSelectDialog(selectYear, years, callback)
        const val TAG_YEAR_SELECT_DIALOG_FRAGMENT = "tag_year_select_dialog_fragment"
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_toplist_year_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false
        mDialogToplistYearSelectDatePicker.init(mSelectYear.toInt(), 1, 1,
                null)
        //只显示年，隐藏掉月日
        ((mDialogToplistYearSelectDatePicker
                .getChildAt(0) as ViewGroup)
                ?.getChildAt(0) as ViewGroup)?.let {
            it.getChildAt(2)?.let { child ->
                child.isGone = true
            }
            it.getChildAt(1)?.let { child ->
                child.isGone = true
            }
        }
        // 设置起止年份
        var minCalendar = Calendar.getInstance()
        minCalendar.set(mYears[0].toInt(), 0, 1)
        var maxCalendar = Calendar.getInstance()
        maxCalendar.set(mYears[mYears.size - 1].toInt(), 0, 1)
        mDialogToplistYearSelectDatePicker.minDate = minCalendar.timeInMillis
        mDialogToplistYearSelectDatePicker.maxDate = maxCalendar.timeInMillis

        // 确定
        mDialogToplistYearSelectConfirmBtnTv.onClick {
            var newYear = mDialogToplistYearSelectDatePicker.year.toLong()
            if(newYear != mSelectYear) {
                callback.invoke(newYear)
            }
            dismiss()
        }

        // 取消
        mDialogToplistYearSelectCancelBtnTv.onClick {
            dismiss()
        }

    }

}