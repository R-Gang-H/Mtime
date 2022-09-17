package com.kotlin.android.publish.component.widget.editor

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.publish.component.widget.article.sytle.TextAlign
import com.kotlin.android.publish.component.widget.article.sytle.TextColor
import com.kotlin.android.publish.component.widget.article.sytle.TextFontSize
import com.kotlin.android.publish.component.widget.article.sytle.TextStyle

/**
 * 编辑器菜单
 *
 * Created on 2022/4/21.
 *
 * @author o.s
 */
class EditorMenuFontView : LinearLayout {

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private var mItemHeight = 90.dp
    private var mPadding = 20.dp
    private var mItemMarginTop = 10.dp

    var textStyle: Int = TextStyle.NONE.bit
        get() = textStyleView.textStyle
        set(value) {
            field = value
            textStyleView.textStyle = value
        }

    var textAlign: TextAlign? = TextAlign.NONE
        get() = textAlignView.textAlign
        set(value) {
            field = value
            textAlignView.textAlign = value
        }

    var textFontSize: TextFontSize? = TextFontSize.STANDARD
        get() = textSizeView.textFontSize
        set(value) {
            field = value
            textSizeView.textFontSize = value
        }

    var textColor: TextColor? = TextColor.BLACK
        get() = textColorView.textColor
        set(value) {
            field = value
            textColorView.textColor = value
        }

    var actionStyle: ((Int) -> Unit)? = null
    var actionAlign: ((TextAlign) -> Unit)? = null
    var actionSize: ((TextFontSize) -> Unit)? = null
    var actionColor: ((TextColor) -> Unit)? = null

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setPadding(mPadding, mPadding, mPadding, mPadding)

        addView(textStyleView)
        addView(textAlignView)
        addView(textSizeView)
        addView(textColorView)
    }

    private val textStyleView by lazy {
        EditorMenuTextStyleView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight).apply {
            }
            action = {
                "textStyle:$it".e()
                actionStyle?.invoke(it)
            }
        }
    }

    private val textAlignView by lazy {
        EditorMenuTextAlignView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight).apply {
                topMargin = mItemMarginTop
            }
            action = {
                "textAlign:$it".e()
                actionAlign?.invoke(it)
            }
        }
    }

    private val textSizeView by lazy {
        EditorMenuTextSizeView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight).apply {
                topMargin = mItemMarginTop
            }
            action = {
                "textSize:$it".e()
                actionSize?.invoke(it)
            }
        }
    }

    private val textColorView by lazy {
        EditorMenuTextColorView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight).apply {
                topMargin = mItemMarginTop
            }
            action = {
                "textColor:$it".e()
                actionColor?.invoke(it)
            }
        }
    }
}