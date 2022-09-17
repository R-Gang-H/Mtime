package com.kotlin.android.card.monopoly.widget.card.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.SUIT_HEIGHT
import com.kotlin.android.card.monopoly.SUIT_WIDTH
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import kotlinx.android.synthetic.main.view_suit_image.view.*

/**
 * 卡套视图：
 *
 * Created on 2020/11/13.
 *
 * @author o.s
 */
class SuitImageView : FrameLayout {

    /**
     * true: 不显示默认卡套
     */
    var noDisplay: Boolean = false

    var data: Suit? = null
        set(value) {
            field = value
            fillCardView()
        }

    var iconView: ImageView? = null

    private fun setDrawable(drawable: Drawable) {
        imageView?.setImageDrawable(drawable)
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
        val view = inflate(context, R.layout.view_suit_image, null)
        addView(view)
        iconView = imageView
    }

    private fun fillCardView() {
        data?.apply {
            imageView?.loadImage(
                    data = suitCover,
                    width = SUIT_WIDTH.dp,
                    height = SUIT_HEIGHT.dp,
                    defaultImgRes = R.drawable.ic_card_center_placeholder
            )
        } ?: reset()
    }

    private fun reset() {
        if (noDisplay) {
            imageView.setImageResource(R.drawable.transparent)
        } else {
            imageView.setImageResource(R.drawable.transparent)
        }
    }
//
//    override fun setTag(tag: Any?) {
//        super.setTag(tag)
//        (tag as? SuitGalleryView.Data)?.apply {
//            if (needRefresh) {
//                drawable?.apply {
//                    setDrawable(this)
//                }
//            }
//        }
//    }
}