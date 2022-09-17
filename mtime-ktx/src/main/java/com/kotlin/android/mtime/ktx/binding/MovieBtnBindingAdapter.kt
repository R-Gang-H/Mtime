package com.kotlin.android.mtime.ktx.binding

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.setCompoundDrawablesAndPadding
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.R

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/8
 */

/**
 * 影片想看按钮背景
 */
@BindingAdapter(value = ["android:movie_btn_bg_state"])
fun bindMovieBtnBg(view: View, state: Long) {
    when(state) {
        CommConstant.MOVIE_BTN_STATE_TICKET -> { //购票
            view.setBackground(
                colorRes = R.color.color_20a0da,
                endColorRes = R.color.color_1bafe0,
                cornerRadius = 30.dpF,
                orientation = GradientDrawable.Orientation.TOP_BOTTOM
            )
        }
        CommConstant.MOVIE_BTN_STATE_PRESELL -> { //预售
            view.setBackground(
                colorRes = R.color.color_afd956,
                endColorRes = R.color.color_c0dc4d,
                cornerRadius = 13.dpF,
                orientation = GradientDrawable.Orientation.TOP_BOTTOM
            )
        }
        CommConstant.MOVIE_BTN_STATE_WANT_SEE -> { //想看
            view.setBackground(
                colorRes = R.color.color_feb12a,
                cornerRadius = 13.dpF
            )
        }
        CommConstant.MOVIE_BTN_STATE_WANT_SEEN -> { //已想看
            view.setBackground(
                strokeColorRes = R.color.color_feb12a,
                strokeWidth = 1.dp,
                cornerRadius = 13.dpF
            )
        }
    }
}

/**
 * 影片想看按钮文本
 */
@BindingAdapter(value = ["android:movie_btn_text_state"])
fun bindMovieBtnText(view: TextView, state: Long) {
    when(state) {
        CommConstant.MOVIE_BTN_STATE_TICKET -> { //购票
            view.setText(R.string.comm_movie_btn_ticket)
            view.setTextColorRes(R.color.color_ffffff)
            view.setCompoundDrawablesAndPadding()
        }
        CommConstant.MOVIE_BTN_STATE_PRESELL -> { //预售
            view.setText(R.string.comm_movie_btn_presell)
            view.setTextColorRes(R.color.color_ffffff)
            view.setCompoundDrawablesAndPadding()
        }
        CommConstant.MOVIE_BTN_STATE_WANT_SEE -> { //想看
            view.setText(R.string.comm_movie_btn_want_see)
            view.setTextColorRes(R.color.color_ffffff)
            view.setCompoundDrawablesAndPadding()
        }
        CommConstant.MOVIE_BTN_STATE_WANT_SEEN -> { //已想看
            view.setText(R.string.comm_movie_btn_want_see)
            view.setTextColorRes(R.color.color_feb12a)
            view.setCompoundDrawablesAndPadding(
                    leftResId = R.drawable.ic_checkb,
                    padding = 3
            )
        }
    }
}

