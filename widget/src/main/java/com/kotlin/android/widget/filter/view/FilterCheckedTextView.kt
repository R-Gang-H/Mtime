package com.kotlin.android.widget.filter.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * 状态textview
 */
class FilterCheckedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr), Checkable {


    private var mChecked = false
    override fun setChecked(checked: Boolean) {
        if (checked != mChecked) {
            mChecked = checked
            refreshDrawableState()
            if (mChecked) setBackground(
                cornerRadius = 4f.dpF,
                colorRes = com.kotlin.android.widget.R.color.color_20a0da,
                endColorRes = com.kotlin.android.widget.R.color.color_1bafe0
            )
            else setBackground(
                cornerRadius = 4f.dpF,
                colorRes = com.kotlin.android.widget.R.color.color_f2f2f2
            )
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(
            android.R.attr.state_checked
        )
    }
}