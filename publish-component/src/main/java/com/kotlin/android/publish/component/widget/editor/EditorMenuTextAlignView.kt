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
import com.kotlin.android.publish.component.databinding.EditorMenuBarTextAlignBinding
import com.kotlin.android.publish.component.widget.article.sytle.TextAlign

/**
 * 编辑器菜单：对齐方式
 *
 * Created on 2022/4/21.
 *
 * @author o.s
 */
class EditorMenuTextAlignView : FrameLayout {
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

    private var mBinding: EditorMenuBarTextAlignBinding? = null

    private var cornerRadius = 6.dpF

    private var _textAlign: TextAlign? = TextAlign.NONE

    var textAlign: TextAlign?
        get() = _textAlign
        set(value) {
            _textAlign = value
            when (value) {
                TextAlign.JUSTIFY -> select(0)
                TextAlign.LEFT -> select(1)
                TextAlign.CENTER -> select(2)
                TextAlign.RIGHT -> select(3)
                TextAlign.NONE -> {
                    reset()
                    _textAlign = TextAlign.NONE
                }
                else -> reset()
            }
        }

    var action: ((TextAlign) -> Unit)? = null

    private fun select(index: Int) {
        selected(index)
    }

    private fun initView() {
        mBinding = EditorMenuBarTextAlignBinding.inflate(LayoutInflater.from(context))
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
                click(view = it, textAlign = TextAlign.JUSTIFY)
            }
            itemView2.setOnClickListener {
                click(view = it, textAlign = TextAlign.LEFT)
            }
            itemView3.setOnClickListener {
                click(view = it, textAlign = TextAlign.CENTER)
            }
            itemView4.setOnClickListener {
                click(view = it, textAlign = TextAlign.RIGHT)
            }
        }
    }

    private fun click(view: View, textAlign: TextAlign) {
        if (!view.isSelected) {
            reset()
            _textAlign = textAlign
            view.isSelected = true
            action?.invoke(textAlign)
//        } else {
//            _textAlign = TextAlign.NONE
//            view.isSelected = false
//            action?.invoke(TextAlign.NONE)
        }
    }

    private fun selected(index: Int) {
        mBinding?.apply {
            when (index) {
                0 -> {
                    selected(view = itemView1, textAlign = TextAlign.JUSTIFY)
                }
                1 -> {
                    selected(view = itemView2, textAlign = TextAlign.LEFT)
                }
                2 -> {
                    selected(view = itemView3, textAlign = TextAlign.CENTER)
                }
                3 -> {
                    selected(view = itemView4, textAlign = TextAlign.RIGHT)
                }
            }
        }
    }

    private fun selected(view: View, textAlign: TextAlign) {
        reset()
        _textAlign = textAlign
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