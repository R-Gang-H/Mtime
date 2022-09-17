package com.kotlin.android.publish.component.widget.editor

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.EditorMenuBarTextSizeBinding
import com.kotlin.android.publish.component.widget.article.sytle.TextFontSize

/**
 * 编辑器菜单：文字大小
 *
 * Created on 2022/4/21.
 *
 * @author o.s
 */
class EditorMenuTextSizeView : FrameLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private var mBinding: EditorMenuBarTextSizeBinding? = null

    private var cornerRadius = 6.dpF

    private var _textFontSize: TextFontSize? = TextFontSize.STANDARD

    var textFontSize: TextFontSize?
        get() = _textFontSize
        set(value) {
            _textFontSize = value
            when (value) {
                TextFontSize.SMALL -> select(0)
                TextFontSize.STANDARD -> select(1)
                TextFontSize.BIG -> select(2)
                TextFontSize.LARGER -> select(3)
                else -> reset()
            }
        }

    var action: ((TextFontSize) -> Unit)? = null

    private fun select(index: Int) {
        selected(index)
    }

    private fun initView() {
        mBinding = EditorMenuBarTextSizeBinding.inflate(LayoutInflater.from(context))
        addView(mBinding?.root)

        setBackground(
            colorRes = R.color.color_ffffff,
            cornerRadius = 8.dpF
        )

        mBinding?.apply {
            setBgSelector(itemView1)
            setBgSelector(itemView2)
            setBgSelector(itemView3)
            setBgSelector(itemView4)

            itemView1.setOnClickListener {
                selected(view = it, textFontSize = TextFontSize.SMALL, isClick = true)
            }
            itemView2.setOnClickListener {
                selected(view = it, textFontSize = TextFontSize.STANDARD, isClick = true)
            }
            itemView3.setOnClickListener {
                selected(view = it, textFontSize = TextFontSize.BIG, isClick = true)
            }
            itemView4.setOnClickListener {
                selected(view = it, textFontSize = TextFontSize.LARGER, isClick = true)
            }
        }

        selected(index = 1)
    }

    private fun selected(index: Int) {
        mBinding?.apply {
            when (index) {
                0 -> {
                    selected(view = itemView1, textFontSize = TextFontSize.SMALL)
                }
                1 -> {
                    selected(view = itemView2, textFontSize = TextFontSize.STANDARD)
                }
                2 -> {
                    selected(view = itemView3, textFontSize = TextFontSize.BIG)
                }
                3 -> {
                    selected(view = itemView4, textFontSize = TextFontSize.LARGER)
                }
            }
        }
    }

    private fun selected(view: View, textFontSize: TextFontSize, isClick: Boolean = false) {
        if (!view.isSelected) {
            reset()
            _textFontSize = textFontSize
            view.isSelected = true
            if (isClick){
                action?.invoke(textFontSize)
            }
        }
    }

    private fun setBgSelector(view: View) {
        view.background = getDrawableStateList(
            normal = getDrawable(R.color.transparent),
            pressed = getShapeDrawable(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = cornerRadius
            ),
            selected = getShapeDrawable(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = cornerRadius
            ),
        )
    }

    private fun reset() {
        mBinding?.apply {
            itemView1.isSelected = false
            itemView2.isSelected = false
            itemView3.isSelected = false
            itemView4.isSelected = false
        }
    }

}