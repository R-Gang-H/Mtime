package com.kotlin.android.card.monopoly.widget.wish

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.WishInfo
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import kotlinx.android.synthetic.main.view_help_wish.view.*

/**
 * TA 的主页愿望入口
 *
 * Created on 2021/5/17.
 *
 * @author o.s
 */
class HelpWishView : FrameLayout {

    var action: ((ActionEvent) -> Unit)? = null

    var data: WishInfo? = null
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        cardImageView?.card = data?.cardInfo
        titleView?.text = data?.cardInfo?.cardName.orEmpty()
        descView?.text = data?.content.orEmpty()
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
        val view = inflate(context, R.layout.view_help_wish, null)
        addView(view)

        taWishLayout?.apply {
            setBackground(
                    colorRes = R.color.color_ffffff,
                    cornerRadius = 8.dpF
            )
            setOnClickListener {
                action?.invoke(ActionEvent.WISH)
            }
        }

        taWishView?.setOnClickListener {
            action?.invoke(ActionEvent.WISH)
        }

        actionView?.setOnClickListener {
            action?.invoke(ActionEvent.HELP)
        }
    }

    enum class ActionEvent {
        WISH,
        HELP
    }
}