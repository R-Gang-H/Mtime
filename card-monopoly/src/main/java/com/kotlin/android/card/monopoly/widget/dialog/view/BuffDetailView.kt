package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.BufferInfo
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import kotlinx.android.synthetic.main.view_buff_detail.view.*

/**
 * 大富翁用户buff详细信息
 *
 * Created on 2021/5/24.
 *
 * @author o.s
 */
class BuffDetailView : FrameLayout {

    var action: (() -> Unit)? = null
    var dismiss: (() -> Unit)? = null

    var data: BufferInfo? = null
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        data?.apply {
            val sb = StringBuilder()
            if ((protectBufferDescList?.size ?: 0) > 0) {
                sb.append("受到的保护：\n")
                protectBufferDescList?.forEach {
                    sb.append(it)
                    sb.append("\n")
                }
                sb.append("\n")
            }
            if ((effectBufferDescList?.size ?: 0) > 0) {
                sb.append("拥有的特效：\n")
                effectBufferDescList?.forEach {
                    sb.append(it)
                    sb.append("\n")
                }
            }

            buffView?.text = sb.toString()
        }
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }

    private fun initView() {
        val view = inflate(context, R.layout.view_buff_detail, null)
        addView(view)

        buffView?.apply {
            setBackground(
                    colorRes = R.color.color_ffffff,
                    cornerRadius = 4.dpF
            )
            setOnClickListener {
            }
        }
    }

    fun showAt(anchorView: View?) {
        val arr = IntArray(2)
        anchorView?.getLocationOnScreen(arr)
        buffView?.apply {
            layoutParams = (layoutParams as? MarginLayoutParams)?.apply {
                topMargin = arr[1] + (anchorView?.height ?: 0)
                marginStart = arr[0]
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        close()
        return true
    }

    fun hide() {
        gone()
    }

    fun show() {
        visible()
    }

    private fun close() {
        dismiss?.invoke()
        hide()
    }
}