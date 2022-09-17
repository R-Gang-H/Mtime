package com.kotlin.android.card.monopoly.widget.coffer.tab

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 * 保险箱Tab视图：
 *
 * Created on 2020/9/1.
 *
 * @author o.s
 */
class CofferTabItemView : ConstraintLayout {

    private val mWidth = 58.dp
    private val mHeight = 95.dp
    private val mLabelHeight = 20.dp
    private val mIconWidth = mWidth
    private val mIconHeight = mIconWidth
    private val mLockWidth = 20.dp
    private val mLockHeight = 25.dp
    private val mIndicatorWidth = 14.dp
    private val mIndicatorHeight = 14.dp
    private val mOpenNowHeight = 18.dp
    private val mOpenNowPadding = 4.dp
    private val mIndicatorMargin = 4.dp
    private val mLabelTextSize = 14F
    private val mOpenNowTextSize = 12F

    private var labelView: TextView? = null
    private var iconView: ImageView? = null
    private var lockView: ImageView? = null
    private var indicatorView: ImageView? = null
    private var openNowView: TextView? = null

    var action: ((event: ActionEvent) -> Unit)? = null

    var state: State = State.LOCK
        set(value) {
            field = value
            notifyChange()
        }

    fun setLabel(label: String) {
        labelView?.text = label
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
        id = R.id.cofferTabView
        ViewGroup.LayoutParams(mWidth, mHeight).apply {
            layoutParams = this
        }
        setOnClickListener {
            if (state == State.NORMAL || state == State.SELECT) {
                val position = labelView?.text?.toString()?.toInt() ?: 1
                val index = position - 1
                val event = ActionEvent(
                        type = ActionType.SELECT,
                        position = position,
                        index = index,
                        left = it.left,
                        right = it.right,
                        width = it.width,
                )
                action?.invoke(event)
            }
        }

        labelView = TextView(context).apply {
            id = R.id.labelView
            gravity = Gravity.CENTER
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mLabelTextSize)
            setTextColor(getColorStateList(
                    normalColor = getColor(R.color.color_ffd22d),
                    pressColor = getColor(R.color.color_feb12a),
                    selectedColor = getColor(R.color.color_feb12a),
                    disableColor = getColor(R.color.color_ffd22d)
            ))
            addView(this)
        }
        iconView = ImageView(context).apply {
            id = R.id.iconView
            foreground = getDrawableStateList(
                    normal = getShapeDrawable(
                            colorRes = android.R.color.transparent,
                            strokeColorRes = R.color.color_ffd22d,
                            strokeWidth = 2.dp,
                            cornerRadius = 29.dpF
                    ),
                    selected = getShapeDrawable(
                            colorRes = android.R.color.transparent,
                            strokeColorRes = R.color.color_feb12a,
                            strokeWidth = 2.dp,
                            cornerRadius = 29.dpF
                    ),
                    disable = getShapeDrawable(
                            colorRes = android.R.color.transparent,
                            strokeColorRes = R.color.color_ffd22d,
                            strokeWidth = 2.dp,
                            cornerRadius = 29.dpF
                    )
            )
            setImageDrawable(getDrawableStateList(
                    normalRes = R.drawable.ic_coffer,
                    disableRes = R.drawable.ic_coffer_disable
            ))
            addView(this)
        }
        lockView = ImageView(context).apply {
            id = R.id.lockView
            setImageDrawable(getDrawableStateList(
                    normalRes = R.drawable.ic_coffer_lock,
                    disableRes = null
            ))
            addView(this)
        }
        indicatorView = ImageView(context).apply {
            id = R.id.indicatorView
            setImageDrawable(getDrawableStateList(
                    normalRes = R.drawable.ic_triangle_bottom,
                    disableRes = null
            ))
            addView(this)
        }
        openNowView = TextView(context).apply {
            id = R.id.openNowView
            setPadding(mOpenNowPadding, 0, mOpenNowPadding, 0)
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mOpenNowTextSize)
            setTextColor(getColor(R.color.color_ffffff))
            setText(R.string.right_now)
            gone()
            setBackground(
                    colorRes = R.color.color_feb12a,
                    cornerRadius = 9.dpF
            )
            setOnClickListener {
                val position = labelView?.text?.toString()?.toInt() ?: 1
                val index = position - 1
                val event = ActionEvent(
                        type = ActionType.OPEN_NOW,
                        position = position,
                        index = index,
                        left = it.left,
                        right = it.right,
                        width = it.width,
                )
                action?.invoke(event)
            }
            addView(this)
        }

        initConstraintSet()

        state = State.LOCK
    }

    private fun initConstraintSet() {
        val set = ConstraintSet()

        labelView?.apply {
            set.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            set.constrainWidth(id, ConstraintSet.WRAP_CONTENT)
            set.constrainHeight(id, mLabelHeight)
        }
        iconView?.apply {
            set.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            labelView?.id?.apply {
                set.connect(id, ConstraintSet.TOP, this, ConstraintSet.BOTTOM)
            }
            set.constrainWidth(id, mIconWidth)
            set.constrainHeight(id, mIconHeight)
        }
        lockView?.apply {
            set.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            iconView?.id?.apply {
                set.connect(id, ConstraintSet.BOTTOM, this, ConstraintSet.BOTTOM)
            }
            set.constrainWidth(id, mLockWidth)
            set.constrainHeight(id, mLockHeight)
        }
        indicatorView?.apply {
            set.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            iconView?.id?.apply {
                set.connect(id, ConstraintSet.TOP, this, ConstraintSet.BOTTOM, mIndicatorMargin)
            }
            set.constrainWidth(id, mIndicatorWidth)
            set.constrainHeight(id, mIndicatorHeight)
        }
        openNowView?.apply {
            set.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            set.constrainWidth(id, ConstraintSet.WRAP_CONTENT)
            set.constrainHeight(id, mOpenNowHeight)
        }

        set.applyTo(this)
    }

    /**
     * 通知状态改变
     */
    private fun notifyChange() {
        when (state) {
            State.NORMAL -> {
                labelView?.apply {
                    isEnabled = true
                    isSelected = false
                }
                iconView?.apply {
                    isEnabled = true
                    isSelected = false
                }
                lockView?.apply {
                    isEnabled = false
                }
                indicatorView?.apply {
                    isEnabled = false
                }
                openNowView?.gone()
            }
            State.SELECT -> {
                labelView?.apply {
                    isEnabled = true
                    isSelected = true
                }
                iconView?.apply {
                    isEnabled = true
                    isSelected = true
                }
                lockView?.apply {
                    isEnabled = false
                }
                indicatorView?.apply {
                    isEnabled = true
                }
                openNowView?.gone()
            }
            State.LOCK -> {
                labelView?.apply {
                    isEnabled = false
                    isSelected = false
                }
                iconView?.apply {
                    isEnabled = false
                    isSelected = false
                }
                lockView?.apply {
                    isEnabled = true
                }
                indicatorView?.apply {
                    isEnabled = false
                }
                openNowView?.gone()
            }
            State.CURRENT_LOCK -> {
                labelView?.apply {
                    isEnabled = false
                    isSelected = false
                }
                iconView?.apply {
                    isEnabled = false
                    isSelected = false
                }
                lockView?.apply {
                    isEnabled = true
                }
                indicatorView?.apply {
                    isEnabled = false
                }
                openNowView?.visible()
            }
        }
    }

    /**
     * 保险箱Tab视图的几种状态
     */
    enum class State {

        /**
         * 正常状态
         */
        NORMAL,

        /**
         * 选中状态
         */
        SELECT,

        /**
         * 锁定状态
         */
        LOCK,

        /**
         * 当前锁定状态（马上开通）
         */
        CURRENT_LOCK
    }

    enum class ActionType {
        SELECT,
        OPEN_NOW
    }

    data class ActionEvent(
            var type: ActionType,
            var position: Int,
            var index: Int,
            var left: Int,
            var right: Int,
            var width: Int,
    )
}