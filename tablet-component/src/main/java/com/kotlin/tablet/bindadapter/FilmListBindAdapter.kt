package com.kotlin.tablet.bindadapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.tablet.R

@BindingAdapter(value = ["isAdd"])
fun updateAddBtn(tv: TextView, isAdd: Boolean) {
    if (!isAdd) {
        tv.setBackground(colorRes = R.color.color_20a0da, cornerRadius = 4.dpF)
        tv.apply {
            text = getString(R.string.tablet_film_list_add)
            setTextColor(getColor(R.color.color_ffffff))
        }
    } else {
        tv.setBackground(colorRes = R.color.color_f2f2f2, cornerRadius = 4.dpF)
        tv.apply {
            text = getString(R.string.tablet_film_list_have_add)
            setTextColor(getColor(R.color.color_8a9199))
        }
    }
}

/**
 * 我的片单审核状态
 * 10待审核,20审核不通过,30审核通过
 */
@BindingAdapter(value = ["approvalStatus"])
fun approvalStatus(tv: TextView, approvalStatus: Long) {
    "approvalStatus  --->${approvalStatus.toString()}".e()
    when (approvalStatus) {
        10L -> {
            tv.apply {
                visibility = View.VISIBLE
                setBackground(colorRes = R.color.color_fff9ef, cornerRadius = 1.dpF)
                setTextColor(getColor(R.color.color_feb12a))
            }
        }
        20L -> {
            tv.apply {
                visibility = View.VISIBLE
                setBackground(colorRes = R.color.color_fff1f0, cornerRadius = 1.dpF)
                setTextColor(getColor(R.color.color_ff4840))
            }
        }
        30L -> {
            tv.visibility = View.GONE
        }
    }
}