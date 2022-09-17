package com.kotlin.android.publish.component.widget.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.publish.component.R

/**
 * 图片描述对话框：
 *
 * Created on 2020/10/21.
 *
 * @author o.s
 */
class DescDialog : DialogFragment() {

    private val mDialogView by lazy {
        context?.run {
            DescDialogView(this)
        }
    }

    var maxLength: Int = 20
        set(value) {
            field = value
            mDialogView?.maxLength = value
        }

    var desc: CharSequence = ""
        set(value) {
            field = value
            mDialogView?.desc = value
        }

    var event: ((desc: CharSequence) -> Unit)? = null

    var dismiss: (() -> Unit)? = null

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
            attributes.run {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
//                gravity = Gravity.BOTTOM
//                windowAnimations = R.style.BottomDialogAnimation
            }
            setBackgroundDrawable(null)
        }
        return dialog
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.ImmersiveDialog)
//    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        dialog?.window?.apply {
//            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
//            setWindowAnimations(R.style.BottomDialogAnimation)
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mDialogView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        immersive().transparentStatusBar()
        initView()
    }

    private fun initView() {
        mDialogView?.apply {
            setTitle(getString(R.string.picture_desc))
            positive(getString(R.string.ok)) {
                dismissAllowingStateLoss()
                event?.invoke(desc)
            }
            negative(getString(R.string.cancel)) {
                dismissAllowingStateLoss()
            }
        }
    }
}