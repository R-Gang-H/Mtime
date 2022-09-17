package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Box
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import kotlinx.android.synthetic.main.view_dig_limit_box.view.*

/**
 * 挖到限量宝箱提示视图
 *
 * Created on 2021/5/19.
 *
 * @author o.s
 */
class DigLimitBoxView : FrameLayout {

    var dismiss: (() -> Unit)? = null

    var data: Box? = null // RewardInfo?
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        boxImageView?.data = data
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
        val view = inflate(context, R.layout.view_dig_limit_box, null)
        addView(view)

        background = getShapeDrawable(
                colorRes = R.color.color_bb45717c,
                endColorRes = R.color.color_ee155f81
        )

        view?.closeView?.apply {
            setOnClickListener {
                dismiss?.invoke()
                hide()
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    fun hide() {
        gone()
    }

    fun show() {
        visible()
    }

}