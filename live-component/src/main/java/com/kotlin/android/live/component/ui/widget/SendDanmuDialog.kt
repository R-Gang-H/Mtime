package com.kotlin.android.live.component.ui.widget

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.ktx.ext.keyboard.showSoftInput
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.live.component.R
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import kotlinx.android.synthetic.main.dlg_send_danmu.*

/**
 * create by lushan on 2021/4/9
 * description: 全屏情况下发送弹幕
 */
class SendDanmuDialog : DialogFragment {
    companion object {
        private var inputDanmuContent: String = ""

        fun getInputDanmu(): String = inputDanmuContent.trim()

        fun setInputDanmu(content: String) {
            inputDanmuContent = content
        }
    }

    constructor() : super()

    var action: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dlg_send_danmu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            window?.apply {
                decorView.setBackgroundColor(Color.TRANSPARENT)
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                setGravity(Gravity.BOTTOM)
            }
            isCancelable = true
            setCanceledOnTouchOutside(true)
            chatTv?.showSoftInputOnFocus = true
            setOnShowListener {
                val fullScreenKeyboardHeightProvider = FullScreenKeyboardHeightProvider(activity as Activity)
                fullScreenKeyboardHeightProvider.setHeightListener(object :FullScreenKeyboardHeightProvider.HeightListener{
                    override fun onKeyboardHeightChanged(height: Int) {
                        if (height == 0){
                            if (this@SendDanmuDialog.isVisible){
                                this@SendDanmuDialog.dismiss()
                            }
                        }
                    }
                }).init()

                chatTv?.postDelayed({ chatTv?.showSoftInput() }, 200L)
            }
        }

        ShapeExt.setShapeColorAndCorner(chatTv, R.color.color_a86e6e6e, 13)
        ShapeExt.setShapeColorAndCorner(sendBtn, R.color.color_a86e6e6e, 13)
        sendBtn?.onClick {//发送按钮
            action?.invoke(chatTv?.text?.toString().orEmpty())
        }
        chatTv?.apply {
            doOnTextChanged { text, start, before, count ->
                setInputDanmu(chatTv?.text?.toString().orEmpty())
            }
            setText(getInputDanmu())
            setSelection(getInputDanmu().length)
        }

    }
}

const val TAG_FRAGMENT_SEND_DANMU_DIALOG = "tag_fragment_send_danmu_dialog"

fun FragmentActivity.showDanmuDialog(contentAction: (String) -> Unit): SendDanmuDialog {
    return getOrGenerateDanmuDialog().apply {
        action = contentAction
    }
}

fun FragmentActivity.getOrGenerateDanmuDialog(): SendDanmuDialog {
    var fragment = getDanmuDialog()
    if (fragment == null) {
        fragment = SendDanmuDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_SEND_DANMU_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getDanmuDialog(): SendDanmuDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_SEND_DANMU_DIALOG) as? SendDanmuDialog
}

fun FragmentActivity.dismissDanmuDialog() {
    getDanmuDialog()?.dismiss()
}
