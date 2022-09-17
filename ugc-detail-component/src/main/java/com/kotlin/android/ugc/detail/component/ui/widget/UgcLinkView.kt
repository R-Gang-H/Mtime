package com.kotlin.android.ugc.detail.component.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ugc.detail.component.R

/**
 * create by lushan on 2022/3/14
 * des:关联内容入口
 **/
class UgcLinkView @JvmOverloads constructor(
    var ctx: Context,
    var attrs: AttributeSet? = null,
    var defStyleAttr: Int = 0
) : FrameLayout(ctx, attrs, defStyleAttr) {

    init {
        removeAllViews()
        val textView = getTextView()
        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        layoutParams.gravity = Gravity.CENTER_VERTICAL
        layoutParams.leftMargin = 10.dp
        layoutParams.rightMargin = 14.dp
        addView(textView, layoutParams)
        setBackground(R.color.color_20a0da, cornerRadius = 30.dpF, direction = Direction.RIGHT_TOP or Direction.RIGHT_BOTTOM)
    }

    private fun getTextView(): TextView {
        return TextView(ctx).apply {
            setTextColor(getColor(R.color.white))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            text = getString(R.string.ugc_detail_component_link_content)
            val drawable = getDrawable(R.drawable.ic_content_link)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            setCompoundDrawables(drawable, null, null, null)
            compoundDrawablePadding = 6.dp
            gravity = Gravity.CENTER
        }
    }
}