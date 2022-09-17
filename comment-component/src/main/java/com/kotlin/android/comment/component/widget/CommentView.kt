package com.kotlin.android.comment.component.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.kotlin.android.comment.component.R
import com.kotlin.android.comment.component.bar.BarButton
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import org.jetbrains.anko.*

/**
 * 评论/回复视图：
 *
 * Created on 2020/8/3.
 *
 * @author o.s
 */
@Deprecated("@see PublishCommentView")
class CommentView : RelativeLayout {

    private val mPaddingStart = 15.dp
    private val mPaddingTop = 5.dp
    private val mPaddingBottom = 5.dp
    private val mInputPaddingStart = 10.dp
    private val mInputPaddingEnd = 2.dp
    private val mHeight = 60.dp
    private val mInputHeight = 40.dp
    private val inputTextSize = 16F

    private var inputView: TextView? = null
    private var editView: EditText? = null
    private var barButton: BarButton? = null
    private var bottomBarButton: BarButton? = null

    var style: Style = Style.COMMENT
        set(value) {
//            if (field != value) {
                field = value
                notifyChangeStyle()
//            }
        }

    var action: ((type: BarButtonItem.Type, isSelected: Boolean) -> Unit)? = null
        set(value) {
            field = value
            barButton?.action = value
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

    private fun initView() {
        setBackgroundResource(android.R.color.white)
        setPadding(mPaddingStart, mPaddingTop, 0, mPaddingBottom)

        initBarButton()
        initInputView()
    }

    /**
     * 根据类型 [type] 顺序依次添加工具条目（水平布局）
     * [isReverse]: 指示反向添加，
     * addItem(A).addItem(B).addItem(C)
     * .addItem(F, true).addItem(G, true)
     * 如图：
     * --A-B-C ... G-F--
     */
    fun addItem(type: BarButtonItem.Type, isReverse: Boolean = false): CommentView {
        barButton?.addItem(type, isReverse)
        return this
    }

    /**
     * 删除指定类型的栏按钮项
     */
    fun removeItem(type: BarButtonItem.Type) {
        barButton?.removeItem(type)
    }

    /**
     * 删除所有的栏按钮项
     */
    fun removeAllItems() {
        barButton?.removeAllItems()
    }

    /**
     * 设置指定类型栅按钮项的提示信息
     */
    fun setTipsByType(type: BarButtonItem.Type, tips: Long) {
        barButton?.setTipsByType(type, tips)
    }

    /**
     * 设置指定类型栅按钮项的选中状态
     */
    fun isSelectedByType(type: BarButtonItem.Type, isSelected: Boolean) {
        barButton?.isSelectedByType(type, isSelected)
    }

    /**
     * 输入模式：
     * 否可以输入文本
     */
    fun inputEnable(enable: Boolean = true) {
        inputView?.apply {
            isEnabled = enable
            hint = if (enable) {
                getString(R.string.hint_leave_what_i_want_to_say)
            } else {
                getString(R.string.hint_text_not_comment)
            }
        }

    }

    private fun initInputView() {
        inputView = TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mInputHeight).apply {
                centerVertically()
                alignParentStart()
                barButton?.apply { startOf(R.id.commentBarButton) }
            }
            gravity = Gravity.CENTER_VERTICAL
            background = getDrawableStateList(
                    normal = getShapeDrawable(colorRes = R.color.color_f2f4f7, cornerRadius = 4.dpF),
                    pressed = getShapeDrawable(colorRes = R.color.color_20a0da, cornerRadius = 4.dpF),
                    disable = getShapeDrawable()
            )
            setHintTextColor(
                    getColorStateList(
                            normalColor = getColor(R.color.color_8798af),
                            pressColor = getColor(R.color.color_c9cedc),
                            disableColor = getColor(R.color.color_c9cedc)
                    )
            )
            maxLines = 1
            setPadding(mInputPaddingStart, 0, mInputPaddingEnd, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, inputTextSize)
            inputEnable()
            setOnClickListener {
//                style = Style.EDIT
            }
        }
        addView(inputView)
    }

    private fun initEditView() {
        editView = EditText(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                centerVertically()
                alignParentStart()
                barButton?.apply { startOf(R.id.commentBarButton) }
            }
            setPadding(mInputPaddingStart, mInputPaddingStart, mInputPaddingStart, mInputPaddingStart)
            setTextColor(getColor(R.color.color_303a47))
            setHintTextColor(getColor(R.color.color_8798af))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, inputTextSize)
            setHint(R.string.hint_leave_what_i_want_to_say)

        }
    }

    private fun initBarButton() {
        barButton = BarButton(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                centerVertically()
                alignParentEnd()
            }
            id = R.id.commentBarButton
        }
        addView(barButton)
    }

    private fun initBottomBarButton() {
        bottomBarButton = BarButton(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                centerVertically()
            }
            id = R.id.commentBottomBarButton
            addItem(BarButtonItem.Type.PHOTO)
            addItem(BarButtonItem.Type.EMOJI)
            addItem(BarButtonItem.Type.KEYBOARD)
        }
    }

    /**
     * 通知样式改变
     */
    private fun notifyChangeStyle() {
        when (style) {
            Style.NOT_COMMENT -> {
                notCommentStyle()
            }
            Style.REPLY -> {
                replyStyle()
            }
            Style.COMMENT -> {
                commentStyle()
            }
            Style.LONG_COMMENT -> {
                longCommentStyle()
            }
            Style.SHARE_COMMENT -> {
                shareCommentStyle()
            }
            Style.EDIT -> {
                editStyle()
            }
        }
    }

    private fun notCommentStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.PRAISE)
        addItem(BarButtonItem.Type.FAVORITE)
        inputEnable(false)
    }

    private fun replyStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.PRAISE)
        inputEnable()
    }

    private fun commentStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.COMMENT)
        addItem(BarButtonItem.Type.PRAISE)
        addItem(BarButtonItem.Type.FAVORITE)
        inputEnable()
    }

    private fun longCommentStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.COMMENT)
        addItem(BarButtonItem.Type.DISPRAISE)
        addItem(BarButtonItem.Type.PRAISE)
        addItem(BarButtonItem.Type.FAVORITE)
        inputEnable()
    }

    private fun shareCommentStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.COMMENT)
        addItem(BarButtonItem.Type.PRAISE)
        addItem(BarButtonItem.Type.FAVORITE)
        addItem(BarButtonItem.Type.SHARE)
        inputEnable()
    }

    private fun editStyle() {
//        editState()
    }

    private fun editState(isEdit: Boolean = true) {
        if (isEdit) {
//            removeView(inputView)
            removeAllViews()
            addView(editView)
            addView(bottomBarButton)
        } else {
            removeAllViews()
            addView(barButton)
            addView(inputView)
        }
    }

    enum class Style {
        NOT_COMMENT,
        REPLY,
        COMMENT,
        LONG_COMMENT,
        SHARE_COMMENT,
        EDIT,
    }
}