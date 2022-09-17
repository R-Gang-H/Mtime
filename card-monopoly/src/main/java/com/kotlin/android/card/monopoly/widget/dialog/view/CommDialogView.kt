package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * 对话框基本框架视图：TitleView, ContentView, PositiveView, NegativeView, CloseView
 * |-----------------------|
 * |         title         |
 * |  -------------------  |
 * |                       |
 * |        content        |
 * |                       |
 * |   positive  negative  |
 * |-----------------------|
 *           close
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class CommDialogView : LinearLayout {

    private val mTitleHeight = 44.dp
    private val mDivideHeight = 1.dp
    private val mFooterHeight = 35.dp
    private val mActionHeight = 35.dp
    private val mCloseWidth = 37.dp
    private val mCloseHeight = 37.dp
    private val mDivideMargin = 15.dp
    private val mActionMargin = 5.dp
    private val mCloseMargin = 15.dp
    private val mPadding = 20.dp
    private val mPanelPadding = 20.dp
    private val mActionPadding = 30.dp
    private val mContentPaddingLeft = 25.dp
    private val mContentPaddingTop = 15.dp
    private val mTitleTextSize = 15F

    private var mPanelLayout: LinearLayout? = null
    private var mTitleView: TextView? = null
    private var mContentView: FrameLayout? = null
    private var mFooterView: LinearLayout? = null
    private var mPositiveView: TextView? = null
    private var mNegativeView: TextView? = null
    private var mCloseView: ImageView? = null

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
        layoutParams = MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT)
        setPadding(mPadding, 0, mPadding, 0)

        // 弹窗白板布局
        mPanelLayout = addPanelLayout()

        // 头部标题
        mTitleView = addHeaderView()
        addDivideView()

        // 中部内容实体，根据不同的 [style] 样式，显示不同的布局
        mContentView = addContentView()

        // 底部确认/取消按钮布局
        mFooterView = addFooterView()
        mPositiveView = addActionView(R.color.color_feb12a)
        mNegativeView = addActionView(R.color.color_1fc4ca)

        // 关闭对话框按钮
//        mCloseView = addCloseView()
    }

    /**
     * 设置标题
     */
    fun setTitle(title: CharSequence) {
        mTitleView?.text = title
    }

    /**
     * 设置内容视图
     */
    fun setContextView(view: View) {
        mContentView?.apply {
            removeAllViews()
            addView(view)
        }
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
     * 关闭视图
     */
    fun close(action: (view: View) -> Unit) {
        mCloseView?.setOnClickListener {
            action.invoke(it)
        }
    }

    /**
     * 弹出框白板布局
     */
    private fun addPanelLayout(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, mPanelPadding)
            setBackground(
                    colorRes = R.color.color_ffffff,
                    cornerRadius = 15.dpF
            )
            this@CommDialogView.addView(this)
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
     */
    private fun addContentView(): FrameLayout {
        return FrameLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {

            }
            setPadding(mContentPaddingLeft, mContentPaddingTop, mContentPaddingLeft, mContentPaddingTop)

            mPanelLayout?.addView(this)
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
    private fun addActionView(@ColorRes colorRes: Int): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mActionHeight).apply {
                marginStart = mActionMargin
                marginEnd = mActionMargin
            }
            gravity = Gravity.CENTER
            setPadding(mActionPadding, 0, mActionPadding, 0)
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize)
            setTextColor(getColor(R.color.color_ffffff))
            setBackground(
                    colorRes = colorRes,
                    cornerRadius = 18.dpF
            )
            gone()

            mFooterView?.addView(this)
        }
    }

    /**
     * 关闭对话框按钮视图
     */
    private fun addCloseView(): ImageView {
        return ImageView(context).apply {
            layoutParams = LayoutParams(mCloseWidth, mCloseHeight).apply {
                topMargin = mCloseMargin
                gravity = Gravity.CENTER
            }
            setImageResource(R.drawable.ic_dialog_close)

            this@CommDialogView.addView(this)
        }
    }

}