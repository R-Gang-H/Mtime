package com.kotlin.android.card.monopoly.widget.card.item

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import kotlinx.android.synthetic.main.view_suit_item.view.*

/**
 * 套装封套条目视图：
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class SuitsItemView : FrameLayout {

    var action: ((data: Suit?) -> Unit)? = null

    var data: Suit? = null
        set(value) {
            field = value
            fillCardView()
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
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val view = inflate(context, R.layout.view_suit_item, null)
        addView(view)
        labelView?.setBackground(
                colorRes = R.color.color_feb12a,
                cornerRadius = 8.dpF,
                direction = Direction.LEFT_TOP or Direction.LEFT_BOTTOM
        )
        setOnClickListener {
            action?.invoke(data)
        }
    }

    private fun fillCardView() {
        suitImageView?.data = data
        titleView?.text = data?.suitName.orEmpty()
        labelView?.text = getString(R.string.collect_suit_count, data?.mixCount ?: 0)
    }

}