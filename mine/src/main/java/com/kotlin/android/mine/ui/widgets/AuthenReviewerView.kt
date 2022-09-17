package com.kotlin.android.mine.ui.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.AuthenReviewBean
import com.kotlin.android.mtime.ktx.ext.showToast

/**
 * create by lushan on 2020/9/8
 * description:影评人认证，显示影评数量
 */
class AuthenReviewerView @JvmOverloads constructor(var ctx: Context, var attrs: AttributeSet? = null, var defaultStyle: Int = -1) : LinearLayoutCompat(ctx, attrs, defaultStyle) {
    init {
        orientation = VERTICAL
    }

    private var reviewIdList: MutableList<Long> = mutableListOf()
    private var refreshListener: ((Int) -> Unit)? = null

    fun setRefreshListener(refreshListener: ((Int) -> Unit)?) {
        this.refreshListener = refreshListener
    }

    fun setData(list: MutableList<AuthenReviewBean>) {
        removeAllViews()
//        添加标题
        AppCompatTextView(ctx).apply {
            setTextColor(getColor(R.color.color_4e5e73))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            text = getReviewerTitle()
        }.also {
            val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                leftMargin = 15.dp
                rightMargin = 15.dp
                topMargin = 15.dp
            }
            addView(it, layoutParams)
        }



        list.forEachIndexed { index, authenReviewBean ->
            val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = if (index == 0) 27.dp else 14.dp
                leftMargin = 30.dp
                rightMargin = 30.dp
            }
            addView(getCheckBox(authenReviewBean), layoutParams)
        }


    }

    /**
     * 获取选中的影评
     */
    fun getSelectReviewIdList() = reviewIdList


    private fun getReviewDrawable(isSelected: Boolean): Drawable? {
        return getDrawable(if (isSelected) R.mipmap.ic_review_check else R.mipmap.ic_review_uncheck)?.apply {
            setBounds(0, 0, intrinsicWidth / 2, intrinsicHeight / 2)
        }
    }

    private fun getCheckBox(reviewBean: AuthenReviewBean): AppCompatTextView {
        return AppCompatTextView(ctx).apply {
            text = reviewBean.content
            setTextColor(getColor(R.color.color_8798af))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            this.isSelected = false
            setCompoundDrawables(getReviewDrawable(isSelected), null, null, null)
            compoundDrawablePadding = 14.dp
//            setPadding(14.dp, 0, 0, 0)
            setTag(R.string.app_name, reviewBean)
            onClick {
                val authenReviewBean = it.getTag(R.string.app_name) as? AuthenReviewBean
                if (it.isSelected.not() && reviewIdList.size >= 3) {
                    showToast(R.string.mine_authen_review_only_select_three)
                    it.isSelected = false
                    setCompoundDrawables(getReviewDrawable(it.isSelected), null, null, null)
                    return@onClick
                }

                if (it.isSelected) {
                    reviewIdList.remove(authenReviewBean?.reviewId ?: 0L)
                } else {
                    reviewIdList.add(authenReviewBean?.reviewId ?: 0L)
                }
                it.isSelected = it.isSelected.not()
                setCompoundDrawables(getReviewDrawable(it.isSelected), null, null, null)
                refreshListener?.invoke(reviewIdList.size)
                "选中影评id:$reviewIdList".e()
            }
        }
    }

    private fun getReviewerTitle(): SpannableString {
        val size = 3//        选择3篇
        return SpannableString(getString(R.string.mine_authen_review_num_format, size)).apply {
            setSpan(ForegroundColorSpan(getColor(R.color.color_ff5a36)), 17, 17 + size.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

}