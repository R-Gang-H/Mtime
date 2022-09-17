package com.kotlin.android.publish.component.widget.tick

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.util.Range
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatTextView
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.publish.component.R

/**
 * 勾选框视图：
 * 支持超链接及点击回调
 *
 * Created on 2020/7/20.
 *
 * @author o.s
 */

class TickView : AppCompatTextView {

    private val mLabelBound = 16.dp

    private val checkNormal: Drawable? by lazy {
        getDrawable(R.drawable.icon_publish_check_gray)?.apply {
            setBounds(0, 0, mLabelBound, mLabelBound)
        }
    }
    private val checkSelected: Drawable? by lazy {
        getDrawable(R.drawable.icon_publish_check_blue)?.apply {
            setBounds(0, 0, mLabelBound, mLabelBound)
        }
    }

    var action: ((text: String) -> Unit)? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        compoundDrawablePadding = 5.dp
        selectedChange()
        setOnClickListener {
            isSelected = !isSelected
            selectedChange()
        }
    }

    /**
     * 设置富文本，包含超链接点击事件
     * [str]: 文本
     * [range]: 超链接范围，多个超链接可以设置多个范围参数
     * [linkColor]: 超链接颜色
     */
    fun setTextWithLink(
            str: String,
            vararg range: Range<Int>,
            @ColorRes linkColor: Int = R.color.color_20a0da
    ) {
        val len = str.length
        val ssb = SpannableStringBuilder()
        ssb.append(str)
        range.forEach {
            val start = it.lower
            val end = it.upper
            if (end <= len) {
                val subStr = str.substring(start, end)
                val clickSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        // 点击超链接吧选中状态反转
                        isSelected = !isSelected
                        action?.invoke(subStr)
                    }
                }
                ssb.setSpan(clickSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                val colorSpan = ForegroundColorSpan(getColor(linkColor))
                val underLineSpan = UnderlineSpan()
                ssb.setSpan(underLineSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                ssb.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }
        movementMethod = LinkMovementMethod.getInstance()
        text = ssb
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        selectedChange()
    }

    private fun selectedChange() {
        val drawable = if (isSelected) {
            checkSelected
        } else {
            checkNormal
        }
        setCompoundDrawables(drawable, null, null, null)
    }

}