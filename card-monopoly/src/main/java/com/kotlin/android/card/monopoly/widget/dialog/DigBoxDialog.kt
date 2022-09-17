package com.kotlin.android.card.monopoly.widget.dialog

import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.card.image.BoxImageView
import com.kotlin.android.app.data.entity.monopoly.Box
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 卡片大富翁挖（获取）宝箱对话框：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class DigBoxDialog : DialogFragment() {

    private val mPanelHeight = 200.dp
    private val mBoxBgWidth = 180.dp
    private val mBoxBgHeight = 100.dp
    private val mBoxWidth = 80.dp
    private val mCloseWidth = 37.dp
    private val mCloseHeight = 37.dp

    private val mPanelPaddingTop = 30.dp
    private val mPanelPaddingBottom = 30.dp
    private val mBoxPaddingTop = 10.dp
    private val mCloseMargin = 20.dp
    private val mPadding = 20.dp
    private val mTitleTextSize = 16F

    private val mContentView by lazy { initContentView() }
    private val mPanelView by lazy { initPanelLayout() }
    private val mCloseView by lazy { initCloseView() }

    private val mBoxBgView by lazy { initBoxBgView() }
    private val mBoxView by lazy { initBoxView() }
    private val mTitleView by lazy { initTitleView() }

    var dismiss: (() -> Unit)? = null

    var event: (() -> Unit)? = null

    var title: String = ""
        set(value) {
            field = value
            mTitleView.text = value
        }

    var data: Box? = null
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        data?.apply {
            mBoxView?.data = this
            mTitleView.text = cardBoxName?.run {
                getString(R.string.getting_box_tips_, this)
            } ?: ""
        }
    }

    fun show() {
        dialog?.show()
    }

    fun hide() {
        dialog?.hide()
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        dialog?.setCanceledOnTouchOutside(cancelable)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismiss?.invoke()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.apply {
            decorView.setPadding(0, 0, 0, 0)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT,  WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(null)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mContentView.apply {
            mPanelView?.addView(mBoxBgView)
            mPanelView?.addView(mBoxView)
            mPanelView?.addView(mTitleView)
            addView(mPanelView)
            addView(mCloseView)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        immersive().transparentStatusBar()
    }

    /**
     * ContentView
     */
    private fun initContentView(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setPadding(mPadding, 0, mPadding, 0)
        }
    }

    /**
     * 弹出框白板布局
     */
    private fun initPanelLayout(): FrameLayout? {
        return context?.let {
            FrameLayout(it).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mPanelHeight)
                setPadding(0, mPanelPaddingTop, 0, mPanelPaddingBottom)
                setBackgroundResource(R.mipmap.ic_dialog_panel_dig_box)
            }
        }
    }

    private fun initBoxBgView(): ImageView {
        return ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(mBoxBgWidth, mBoxBgHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            setBackgroundResource(R.drawable.ic_dialog_icon_bg)
        }
    }

    private fun initBoxView(): BoxImageView? {
        return context?.let {
            BoxImageView(it).apply {
                layoutParams = FrameLayout.LayoutParams(mBoxWidth, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    topMargin = mBoxPaddingTop
                }
            }
        }
    }

    private fun initTitleView(): TextView {
        return TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            }
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            setTextColor(getColor(R.color.color_1d2736))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize)
        }
    }

    /**
     * 关闭对话框按钮视图
     */
    private fun initCloseView(): ImageView {
        return ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(mCloseWidth, mCloseHeight).apply {
                topMargin = mCloseMargin
                gravity = Gravity.CENTER
            }
            setImageResource(R.drawable.ic_dialog_close)
            setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
    }

    data class Data(
            var boxCover: String? = null,
            var boxName: String? = null,
    )
}