package com.kotlin.android.card.monopoly.widget.card.image

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.card.monopoly.*
import com.kotlin.android.app.data.entity.monopoly.Box
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import kotlinx.android.synthetic.main.view_box_image.view.*

/**
 * 宝箱视图：
 *
 * Created on 2020/11/19.
 *
 * @author o.s
 */
class BoxImageView : FrameLayout {

    var data: Box? = null
        set(value) {
            field = value
            fillData()
        }

    var state: CardState = CardState.EMPTY
        set(value) {
            field = value
            notifyChange()
        }

    var stateChange: ((state: CardState) -> Unit)? = null

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
        val view = inflate(context, R.layout.view_box_image, null)
        addView(view)
    }

    private fun fillData() {
        data?.apply {
            imageView?.loadImage(
                    data = cover,
                    width = BOX_WIDTH.dp,
                    height = BOX_HEIGHT.dp,
                    defaultImgRes = R.drawable.ic_none_box
            )
            state = CardState.FILL
            stateChange?.invoke(state)
        } ?: resetEmpty()
    }

    private fun resetEmpty() {
        state = CardState.EMPTY
        stateChange?.invoke(state)
    }

    private fun notifyChange() {
        when (state) {
            CardState.SELECTED,
            CardState.FILL -> {
            }
            CardState.EMPTY,
            CardState.LOCK -> {
                imageView?.setImageResource(R.drawable.ic_none_box)
            }
            CardState.NO_DISPLAY -> {
                imageView?.setImageResource(R.drawable.transparent)
            }
        }
    }
}