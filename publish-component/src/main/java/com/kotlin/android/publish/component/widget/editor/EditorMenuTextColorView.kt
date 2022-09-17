package com.kotlin.android.publish.component.widget.editor

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.EditorMenuBarTextColorBinding
import com.kotlin.android.publish.component.widget.article.sytle.TextColor

/**
 * 编辑器菜单：文字颜色
 *
 * Created on 2022/4/21.
 *
 * @author o.s
 */
class EditorMenuTextColorView : FrameLayout {
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

    private var mBinding: EditorMenuBarTextColorBinding? = null

    private var innerRadius = 12.dpF
    private var outRadius = 15.dpF

    private var _textColor: TextColor? = TextColor.BLACK

    var textColor: TextColor?
        get() = _textColor
        set(value) {
            _textColor = value
            when (value) {
                TextColor.BLACK -> select(0)
                TextColor.GRAY -> select(1)
                TextColor.ORANGE -> select(2)
                TextColor.YELLOW -> select(3)
                TextColor.GREEN -> select(4)
                TextColor.CYAN -> select(5)
                TextColor.BLUE -> select(6)
                else -> reset()
            }
        }

    var action: ((TextColor) -> Unit)? = null

    private fun select(index: Int) {
        selected(index)
    }

    private fun initView() {
        mBinding = EditorMenuBarTextColorBinding.inflate(LayoutInflater.from(context))
        addView(mBinding?.root)

        setBackground(
            colorRes = R.color.color_ffffff,
            cornerRadius = 8.dpF
        )

        mBinding?.apply {
            setBgSelector(colorView0, TextColor.BLACK)
            setBgSelector(colorView1, TextColor.GRAY)
            setBgSelector(colorView2, TextColor.ORANGE)
            setBgSelector(colorView3, TextColor.YELLOW)
            setBgSelector(colorView4, TextColor.GREEN)
            setBgSelector(colorView5, TextColor.CYAN)
            setBgSelector(colorView6, TextColor.BLUE)

            colorView0.setOnClickListener {
                click(view = it, textColor = TextColor.BLACK)
            }
            colorView1.setOnClickListener {
                click(view = it, textColor = TextColor.GRAY)
            }
            colorView2.setOnClickListener {
                click(view = it, textColor = TextColor.ORANGE)
            }
            colorView3.setOnClickListener {
                click(view = it, textColor = TextColor.YELLOW)
            }
            colorView4.setOnClickListener {
                click(view = it, textColor = TextColor.GREEN)
            }
            colorView5.setOnClickListener {
                click(view = it, textColor = TextColor.CYAN)
            }
            colorView6.setOnClickListener {
                click(view = it, textColor = TextColor.BLUE)
            }
        }

        selected(index = 0)
    }

    private fun click(view: View, textColor: TextColor) {
        if (!view.isSelected) {
            reset()
            _textColor = textColor
            view.isSelected = true
            action?.invoke(textColor)
        }
    }

    private fun selected(index: Int) {
        mBinding?.apply {
            when (index) {
                0 -> {
                    selected(view = colorView0, textColor = TextColor.BLACK)
                }
                1 -> {
                    selected(view = colorView1, textColor = TextColor.GRAY)
                }
                2 -> {
                    selected(view = colorView2, textColor = TextColor.ORANGE)
                }
                3 -> {
                    selected(view = colorView3, textColor = TextColor.YELLOW)
                }
                4 -> {
                    selected(view = colorView4, textColor = TextColor.GREEN)
                }
                5 -> {
                    selected(view = colorView5, textColor = TextColor.CYAN)
                }
                6 -> {
                    selected(view = colorView6, textColor = TextColor.BLUE)
                }
            }
        }
    }
    private fun selected(view: View, textColor: TextColor) {
        reset()
        _textColor = textColor
        view.isSelected = true
    }

    private fun setBgSelector(view: ImageView, color: TextColor) {
        view.background = getDrawableStateList(
            normal = getShapeDrawable(
                strokeColorRes = R.color.color_f2f3f6,
                strokeWidth = 1.dp,
                cornerRadius = outRadius
            ),
            pressed = getShapeDrawable(
                strokeColorRes = color.colorRes,
                strokeWidth = 1.dp,
                cornerRadius = outRadius
            ),
            selected = getShapeDrawable(
                strokeColorRes = color.colorRes,
                strokeWidth = 1.dp,
                cornerRadius = outRadius
            ),
        )
        view.setImageDrawable(
            getShapeDrawable(
                colorRes = color.colorRes,
                cornerRadius = innerRadius
            )
        )
    }

    private fun reset() {
        mBinding?.apply {
            colorView0.isSelected = false
            colorView1.isSelected = false
            colorView2.isSelected = false
            colorView3.isSelected = false
            colorView4.isSelected = false
            colorView5.isSelected = false
            colorView6.isSelected = false
        }
    }

}