package com.kotlin.android.publish.component.widget.dialog

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.core.view.children
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.publish.component.R

/**
 * 描述对话框视图：TitleView, EditText, PositiveView, NegativeView
 * |-----------------------|
 * |         title         |
 * |  -------------------  |
 * |                       |
 * |       EditText        |
 * |                       |
 * |   negative  positive  |
 * |-----------------------|
 *
 * Created on 2020/10/21.
 *
 * @author o.s
 */
class DescDialogView : LinearLayout {

    private val mTitleHeight = 44.dp
    private val mDivideHeight = 1.dp
    private val mFooterHeight = 35.dp
    private val mActionHeight = 35.dp
    private val mMargin = 20.dp
    private val mDivideMargin = 15.dp
    private val mActionMargin = 5.dp
    private val mPadding = 20.dp
    private val mPanelPadding = 20.dp
    private val mActionPadding = 30.dp
    private val mContentPaddingLeft = 25.dp
    private val mContentPaddingTop = 15.dp
    private val mInputPadding = 10.dp
    private val mTitleTextSize = 15F

    private var mPanelLayout: LinearLayout? = null
    private var mTitleView: TextView? = null
    private var mContentView: FrameLayout? = null
    private var mInputView: EditText? = null
    private var mFooterView: LinearLayout? = null
    private var mPositiveView: TextView? = null
    private var mNegativeView: TextView? = null

    var maxLength = 20
        set(value) {
            field = value
            mInputView?.filters = Array<InputFilter>(1) { InputFilter.LengthFilter(value) }
        }

    var desc: CharSequence = ""
        set(value) {
            field = value
            mInputView?.setText(value)
        }
        get() {
            return mInputView?.text.toString()
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
        orientation = VERTICAL
//        layoutParams = MarginLayoutParams(270.dp, MarginLayoutParams.WRAP_CONTENT).apply {
//            marginStart = 30.dp
//            marginEnd = 30.dp
//        }
//        setPadding(mPadding, 0, mPadding, 0)

        // 弹窗白板布局
        mPanelLayout = addPanelLayout()

        // 头部标题
        mTitleView = addHeaderView()
//        addDivideView()

        // 中部内容实体，根据不同的 [style] 样式，显示不同的布局
        mContentView = addContentView()
        mInputView = createInputView()
        mContentView?.addView(mInputView)

        // 底部确认/取消按钮布局
        mFooterView = addFooterView()
        mNegativeView = addActionView(
                textColorRes = R.color.color_20a0da,
                bg = getShapeDrawable(
                colorRes = R.color.color_00000000,
                strokeColorRes = R.color.color_20a0da,
                strokeWidth = 1.dp,
                cornerRadius = 18.dpF
        ))
        mPositiveView = addActionView(
                textColorRes = R.color.color_ffffff,
                bg = getShapeDrawable(
                colorRes = R.color.color_20a0da,
                cornerRadius = 18.dpF
        ))
    }

    /**
     * 设置标题
     */
    fun setTitle(title: String) {
        mTitleView?.text = title
    }

    /**
     * 确定视图：文本为空时隐藏
     */
    fun positive(title: String? = null, action: ((view: View) -> Unit)? = null) {
        mPositiveView?.apply {
            title?.apply {
                visible()
                text = title
                setOnClickListener {
                    action?.invoke(it)
                }
            } ?: gone()
        }
    }

    /**
     * 取消视图：文本为空时隐藏
     */
    fun negative(title: String? = null, action: ((view: View) -> Unit)? = null) {
        mNegativeView?.apply {
            title?.apply {
                visible()
                text = title
                setOnClickListener {
                    action?.invoke(it)
                }
            } ?: gone()
        }
    }

    /**
     * 弹出框白板布局
     */
    private fun addPanelLayout(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                marginStart = mMargin
                marginEnd = mMargin
            }
            setPadding(0, 0, 0, mPanelPadding)
            setBackground(
                    colorRes = R.color.color_ffffff,
                    cornerRadius = 8.dpF
            )
            this@DescDialogView.addView(this)
        }
    }

    /**
     * 头部标题视图
     */
    private fun addHeaderView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mTitleHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            gravity = Gravity.CENTER
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize)
            setTextColor(getColor(R.color.color_4e5e73))

            mPanelLayout?.addView(this)
        }
    }

    private fun addDivideView(): View {
        return View(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mDivideHeight).apply {
                marginStart = mDivideMargin
                marginEnd = mDivideMargin
            }
            setBackgroundColor(getColor(R.color.color_f2f3f6))

            mPanelLayout?.addView(this)
        }
    }

    /**
     * 中部内容体视图
     * Desc 输入框
     */
    private fun addContentView(): FrameLayout {
        return FrameLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {

            }
            setPadding(mContentPaddingLeft, 0, mContentPaddingLeft, mContentPaddingTop)

            mPanelLayout?.addView(this)
        }
    }

    private fun createInputView(): EditText {
        return EditText(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setPadding(mInputPadding, mInputPadding, mInputPadding, mInputPadding)
            filters = Array<InputFilter>(1) { InputFilter.LengthFilter(maxLength) }
            gravity = Gravity.START or Gravity.TOP
            minLines = 4
            setBackground(
                    colorRes = R.color.color_00000000,
                    strokeColorRes = R.color.color_20a0da,
                    strokeWidth = 1.dp,
                    cornerRadius = 4.dpF
            )
        }
    }
    /**
     * 底部视图布局
     */
    private fun addFooterView(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mFooterHeight).apply {
                gravity = Gravity.CENTER
            }

            mPanelLayout?.addView(this)
        }
    }

    /**
     * 底部确认/取消按钮视图
     */
    private fun addActionView(@ColorRes textColorRes: Int, bg: Drawable): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mActionHeight).apply {
                marginStart = mActionMargin
                marginEnd = mActionMargin
            }
            gravity = Gravity.CENTER
            setPadding(mActionPadding, 0, mActionPadding, 0)
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize)
            setTextColor(getColor(textColorRes))
            background = bg
            gone()

            mFooterView?.addView(this)
        }
    }

}