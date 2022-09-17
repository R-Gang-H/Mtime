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
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.EditorMenuBarTextStyleBinding
import com.kotlin.android.publish.component.widget.article.sytle.TextStyle

/**
 * 编辑器菜单：文字格式
 *
 * Created on 2022/4/21.
 *
 * @author o.s
 */
class EditorMenuTextStyleView : FrameLayout {
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

    private var mBinding: EditorMenuBarTextStyleBinding? = null

    private var cornerRadius = 6.dpF

    private var _textStyle: Int = TextStyle.NONE.bit

    var textStyle: Int
        get() = _textStyle
        set(value) {
            reset()
            "_textStyle$_textStyle value=$value".e()
            _textStyle = value
            if (value and TextStyle.BOLD.bit == TextStyle.BOLD.bit) {
                if (value and TextStyle.ITALIC.bit == TextStyle.ITALIC.bit) {
                    select(0)
                    select(1)
                } else {
                    select(0)
                }
            }
            if (value and TextStyle.ITALIC.bit == TextStyle.ITALIC.bit) {
                select(1)
            }
            if (value and TextStyle.UNDERLINE.bit == TextStyle.UNDERLINE.bit) {
                select(2)
            }
            if (value and TextStyle.LINE_THROUGH.bit == TextStyle.LINE_THROUGH.bit) {
                select(3)
            }
        }

    var action: ((Int) -> Unit)? = null

    private fun select(index: Int) {
        selected(index)
    }

    private fun initView() {
        mBinding = EditorMenuBarTextStyleBinding.inflate(LayoutInflater.from(context))
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
                click(view = it, textStyle = TextStyle.BOLD)
            }
            itemView2.setOnClickListener {
                click(view = it, textStyle = TextStyle.ITALIC)
            }
            itemView3.setOnClickListener {
                click(view = it, textStyle = TextStyle.UNDERLINE)
            }
            itemView4.setOnClickListener {
                click(view = it, textStyle = TextStyle.LINE_THROUGH)
            }
        }
    }

    private fun click(view: View, textStyle: TextStyle) {
        if (!view.isSelected) {
            _textStyle = _textStyle or textStyle.bit
            view.isSelected = true
            action?.invoke(_textStyle)
        } else {
            _textStyle = _textStyle and textStyle.bit.inv()
            view.isSelected = false
            action?.invoke(_textStyle)
        }
    }

    private fun selected(index: Int) {
        mBinding?.apply {
            when (index) {
                0 -> {
                    selected(view = itemView1, textStyle = TextStyle.BOLD)
                }
                1 -> {
                    selected(view = itemView2, textStyle = TextStyle.ITALIC)
                }
                2 -> {
                    selected(view = itemView3, textStyle = TextStyle.UNDERLINE)
                }
                3 -> {
                    selected(view = itemView4, textStyle = TextStyle.LINE_THROUGH)
                }
            }
        }
    }

    private fun selected(view: View, textStyle: TextStyle) {
        _textStyle = _textStyle or textStyle.bit
        view.isSelected = true
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