package com.kotlin.tablet.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ViewFilmCartBinding
import com.kotlin.tablet.ui.add.FilmCart

/**
 * 创建者: SunHao
 * 创建时间: 2022/4/2
 * 描述:搜索影片底部cart
 **/
class FilmCartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private var mBinding: ViewFilmCartBinding? = null
    private var mAction: ((ActionType) -> Unit)? = null

    init {
        initView()
    }

    private fun initView() {
        mBinding = ViewFilmCartBinding.inflate(LayoutInflater.from(context))
        addView(mBinding?.root)
        mBinding?.apply {
            setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 4.dpF,
                direction = Direction.RIGHT_TOP or Direction.LEFT_TOP
            )
            mSelectedTv.onClick {
                mAction?.invoke(ActionType.SELECTED_ACTION)
            }
            mSureBtn.apply {
                setBackground(colorRes = R.color.color_1cacde, cornerRadius = 21.dpF)
                onClick {
                    mAction?.invoke(ActionType.SURE_ACTION)
                }
            }
        }
    }

    fun notifyData() {
        val size = FilmCart.instance.getSelectedData().size
        mBinding?.mSelectedTv?.text =
            String.format(getString(R.string.tablet_film_list_selected, size))
        if (size > 0) {
            visible()
        } else {
            gone()
        }
    }

    fun addActionListener(action: (ActionType) -> Unit) {
        mAction = action
    }

    enum class ActionType {
        SELECTED_ACTION, SURE_ACTION
    }
}