package com.kotlin.android.publish.component.widget.dialog

import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.core.annotation.DialogFragmentTag
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.DialogEditorInputLinkBinding
import com.kotlin.android.publish.component.databinding.DialogInputPkLabelBinding

/**
 * 添加超链接
 *
 * Created on 2022/4/15.
 *
 * @author o.s
 */
@DialogFragmentTag(tag = "tag_dialog_fragment_editor_input_link_dialog")
class EditorInputLinkDialog : BaseVMDialogFragment<BaseViewModel, DialogEditorInputLinkBinding>() {

    var event: ((CharSequence, CharSequence) -> Unit)? = null

    var title: CharSequence = ""
        set(value) {
            field = value
            mBinding?.titleView?.text = value
        }

    override fun initEnv() {
        window = {
            decorView.setPadding(0, 0, 0, 0)
            attributes.run {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
//                gravity = Gravity.BOTTOM
//                windowAnimations = R.style.BottomDialogAnimation
            }
            setBackgroundDrawable(null)
        }
    }

    override fun initView() {
        mBinding?.apply {
            rootLayout.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF,
            )
            inputView1.setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = 8.dpF,
            )
            inputView2.setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = 8.dpF,
            )
            cancelView.setBackground(
                colorRes = R.color.color_00000000,
                strokeColorRes = R.color.color_20a0da,
                strokeWidth = 1.dp,
                cornerRadius = 20.dpF
            )
            okView.setBackground(
                colorRes = R.color.color_20a0da,
                cornerRadius = 20.dpF
            )
            okView.apply {
                background = getDrawableStateList(
                    normal = getShapeDrawable(
                        colorRes = R.color.color_20a0da,
                        cornerRadius = 20.dpF
                    ),
                    pressed = getShapeDrawable(
                        colorRes = R.color.color_20a0da,
                        cornerRadius = 20.dpF
                    ),
                    disable = getShapeDrawable(
                        colorRes = R.color.color_9920a0da,
                        cornerRadius = 20.dpF
                    ),
                )
            }
            okView.isEnabled = false
            inputView1.doAfterTextChanged {
                okView.isEnabled = (it?.trim()?.length ?: 0) > 0 && inputView2.text?.trim().isNullOrEmpty().not()
            }
            inputView2.doAfterTextChanged {
                okView.isEnabled = (it?.trim()?.length ?:0 ) > 0 && inputView1.text?.trim().isNullOrEmpty().not()
            }
            okView.setOnClickListener {
                event?.invoke(inputView1.text, inputView2.text.trim())
                dismiss()
            }
            cancelView.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

}