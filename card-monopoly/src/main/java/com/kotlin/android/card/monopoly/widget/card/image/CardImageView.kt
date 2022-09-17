package com.kotlin.android.card.monopoly.widget.card.image

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.card.monopoly.CARD_HEIGHT
import com.kotlin.android.card.monopoly.CARD_WIDTH
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import kotlinx.android.synthetic.main.view_card_image.view.*

/**
 * 卡片视图：
 *
 * Created on 2020/11/13.
 *
 * @author o.s
 */
class CardImageView : FrameLayout {

    var card: Card? = null
        set(value) {
            field = value
            fillCardView()
        }

    var state: CardState = CardState.EMPTY
        set(value) {
            field = value
            notifyChange()
        }

    var position: Int = -1

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
        val view = inflate(context, R.layout.view_card_image, null)
        addView(view)
    }

    private fun fillCardView() {
//        "position=$position 填充卡片数据".d()
        card?.apply {
            imageView?.loadImage(
                    data = cardCover,
                    width = CARD_WIDTH.dp,
                    height = CARD_HEIGHT.dp,
                    defaultImgRes = R.drawable.ic_card_bg,
                    errorImgRes = R.drawable.ic_card_bg,
            )
            state = CardState.FILL
            stateChange?.invoke(state)
        } ?: resetEmpty()
    }

    private fun resetEmpty() {
        state = CardState.EMPTY
        stateChange?.invoke(state)
//        "position=$position reset EMPTY".d()
    }

    private fun notifyChange() {
        when (state) {
            CardState.SELECTED,
            CardState.FILL -> {
//                "position=$position 加载完成 $state".d()
            }
            CardState.EMPTY,
            CardState.LOCK -> {
//                "position=$position 占位图 $state".d()
//                imageView?.setImageResource(R.mipmap.ic_card_placeholder_none)
                imageView?.loadImage(
                        data = R.drawable.ic_card_bg,
                        defaultImgRes = R.drawable.ic_card_bg,
                )
            }
            CardState.NO_DISPLAY -> {
                imageView?.setImageResource(R.drawable.transparent)
            }
        }
    }
}