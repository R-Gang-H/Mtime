package com.kotlin.android.community.family.component.ui.manage.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import com.kotlin.android.community.family.component.R
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * @author zhangjian
 * @date 2022/3/29 16:34
 */
class FamilyFilterView : LinearLayout {

    companion object {
        val TYPE_REPLY = 9L
        val TYPE_HOT = 2L
        val TYPE_NEW = 1L
        val TYPE_BEST = 3L
    }

    private var tvReply: TextView? = null
    private var tvNew: TextView? = null
    private var tvHot: TextView? = null
    private var tvBest: TextView? = null

    var onClickType: ((type: Long) -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, @Nullable attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, @Nullable attr: AttributeSet?, def: Int) : super(
        context,
        attr,
        def
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.view_filter_family, this, false)
        tvReply = view.findViewById(R.id.tvReply)
        tvNew = view.findViewById(R.id.tvNew)
        tvHot = view.findViewById(R.id.tvHot)
        tvBest = view.findViewById(R.id.tvBest)
        tvReplyState()
        tvReply?.onClick {
            onClickType?.invoke(TYPE_REPLY)
            tvReplyState()
        }
        tvHot?.onClick {
            onClickType?.invoke(TYPE_HOT)
            tvHotState()
        }
        tvNew?.onClick {
            onClickType?.invoke(TYPE_NEW)
            tvNewState()
        }
        tvBest?.onClick {
            onClickType?.invoke(TYPE_BEST)
            tvBestState()
        }
        addView(view)
    }

    private fun tvReplyState() {
        setTvState(tvReply, true)
        setTvState(tvNew, false)
        setTvState(tvHot, false)
        setTvState(tvBest, false)
    }

    private fun tvNewState() {
        setTvState(tvReply, false)
        setTvState(tvNew, true)
        setTvState(tvHot, false)
        setTvState(tvBest, false)
    }

    private fun tvHotState() {
        setTvState(tvReply, false)
        setTvState(tvNew, false)
        setTvState(tvHot, true)
        setTvState(tvBest, false)
    }

    private fun tvBestState() {
        setTvState(tvReply, false)
        setTvState(tvNew, false)
        setTvState(tvHot, false)
        setTvState(tvBest, true)
    }


    private fun setTvState(textView: TextView?, click: Boolean) {
        if (click) {
            textView?.setBackground(
                colorRes = R.color.color_20a0da, endColorRes = R.color.color_1bafe0,
                orientation = GradientDrawable.Orientation.LEFT_RIGHT,
                cornerRadius = 4.dpF,
            )
            textView?.setTextColor(getColor(R.color.color_ffffff))
            textView?.isEnabled = false
        } else {
            textView?.setBackground(
                colorRes = R.color.color_f3f3f4,
                cornerRadius = 4.dpF,
            )
            textView?.setTextColor(getColor(R.color.color_8798af))
            textView?.isEnabled = true
        }
    }
}