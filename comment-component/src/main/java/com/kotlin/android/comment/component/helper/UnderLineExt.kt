package com.kotlin.android.comment.component.helper

import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.annotation.StringRes
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mtime.ktx.getString

/**
 * create by lushan on 2022/3/16
 * des:
 **/

fun TextView.setTvUnderline(@StringRes resId: Int) {
    val content = getString(resId)
    val spannableString = SpannableString(content)
    spannableString.setSpan(UnderlineSpan(), 0, content.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
    this.text = spannableString
}

fun TextView.setExpandVisibility(expandTv: TextView?, maxLine: Int) {
    this.maxLines = maxLine + 1//避免第二次刷新的时候maxLine为3导致展示显示不出来
    val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (this@setExpandVisibility.lineCount > maxLine) {
                this@setExpandVisibility.maxLines = maxLine
                expandTv?.visible()
            } else {
                this@setExpandVisibility.maxLines = this@setExpandVisibility.lineCount
                expandTv?.gone()
            }
            this@setExpandVisibility.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        }
    }
    this@setExpandVisibility.viewTreeObserver?.addOnGlobalLayoutListener(listener)
}