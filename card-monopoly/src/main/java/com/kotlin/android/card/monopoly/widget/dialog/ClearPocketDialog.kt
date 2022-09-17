package com.kotlin.android.card.monopoly.widget.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.provider.startCardMainActivity
import com.kotlin.android.app.data.annotation.CARD_MONOPOLY_MY_POCKET
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * 卡片大富翁清空口袋对话框：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class ClearPocketDialog : DialogFragment() {

    private val mActionHeight = 38.dp
    private val mIconWidth = 150.dp
    private val mIconHeight = 100.dp
    private val mCloseWidth = 37.dp
    private val mCloseHeight = 37.dp

    private val mPanelPaddingTop = 30.dp
    private val mPanelPaddingBottom = 30.dp
    private val mTitleMarginTop = 20.dp
    private val mActionMarginTop = 20.dp
    private val mCloseMargin = 20.dp
    private val mPadding = 20.dp
    private val mActionPadding = 45.dp
    private val mTitleTextSize = 16F
    private val mActionTextSize = 15F

    private val mContentView by lazy { initContentView() }
    private val mPanelView by lazy { initPanelLayout() }
    private val mCloseView by lazy { initCloseView() }

    private val mIconView by lazy { initIconView() }
    private val mTitleView by lazy { initTitleView() }
    private val mActionView by lazy { initActionView() }

    var dismiss: (() -> Unit)? = null

    var event: (() -> Unit)? = null

    var title: String = ""
        set(value) {
            field = value
            mTitleView.text = value
        }

    var message: String? = null
        set(value) {
            field = value
            mTitleView.text = value
        }

    var isCardMainPage: Boolean = false

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            decorView.setPadding(0, 0, 0, 0)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT,  WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(null)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mContentView.apply {
            mPanelView.addView(mIconView)
            mPanelView.addView(mTitleView)
            mPanelView.addView(mActionView)
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
    private fun initPanelLayout(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            }
            setPadding(0, mPanelPaddingTop, 0, mPanelPaddingBottom)
            setBackgroundResource(R.mipmap.ic_dialog_panel_open_box)
        }
    }

    private fun initIconView(): ImageView {
        return ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(mIconWidth, mIconHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            setImageResource(R.mipmap.ic_dialog_icon_open_box_ph)
        }
    }

    private fun initTitleView(): TextView {
        return TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = mTitleMarginTop
                gravity = Gravity.CENTER_HORIZONTAL
            }
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            setTextColor(getColor(R.color.color_1d2736))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize)
        }
    }

    private fun initActionView(): TextView {
        return TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, mActionHeight).apply {
                topMargin = mActionMarginTop
                gravity = Gravity.CENTER_HORIZONTAL
            }
            gravity = Gravity.CENTER
            setPadding(mActionPadding, 0, mActionPadding, 0)
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            setTextColor(getColor(R.color.color_ff5a36))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mActionTextSize)
            setText(R.string.clear_pocket)
            setBackground(
                    colorRes = R.color.color_ffffff,
                    cornerRadius = 19.dpF
            )
            setOnClickListener {
                event?.invoke()
                if (!isCardMainPage) {
                    startCardMainActivity(null, CARD_MONOPOLY_MY_POCKET)
                }
                dismissAllowingStateLoss()
            }
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
            var cardCover: String? = null,
            var message: String? = null,
    )
}