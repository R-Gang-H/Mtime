package com.kotlin.android.publish.component.widget.dialog

import android.text.InputFilter
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.core.annotation.DialogFragmentTag
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.publish.component.Publish.EDITOR_CUSTOM
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.DialogInputLabelBinding

/**
 * 选择发布权限
 *
 * Created on 2022/4/15.
 *
 * @author o.s
 */
@DialogFragmentTag(tag = "tag_dialog_fragment_input_label_dialog")
class InputLabelDialog : BaseVMDialogFragment<BaseViewModel, DialogInputLabelBinding>() {

    var event: ((CharSequence) -> Unit)? = null

    var title: CharSequence = ""
        set(value) {
            field = value
            mBinding?.titleView?.text = value
        }

    var hint: CharSequence = ""
        set(value) {
            field = value
            mBinding?.inputView?.hint = value
        }

    var maxLength: Int = 20
        set(value) {
            field = value
            mBinding?.inputView?.filters = Array<InputFilter>(1) { InputFilter.LengthFilter(value) }
        }

    var inputType: Int = EditorInfo.TYPE_CLASS_TEXT
        set(value) {
            field = value
            mBinding?.inputView?.inputType = value // EditorInfo.TYPE_CLASS_NUMBER
        }

    /**
     * 回显文本（根据需要）
     */
    var echo: CharSequence = ""
        set(value) {
            field = value
            mBinding?.inputView?.setText(value)
        }

    var errorMessage: CharSequence? = null

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
            inputView.setBackground(
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
            inputView.doAfterTextChanged {
                okView.isEnabled = (it?.trim()?.length ?: 0) > 0
            }
            okView.setOnClickListener {
                val key = inputView.text.trim().toString()
                if (EDITOR_CUSTOM == key) {
                    showToast(errorMessage)
                } else {
                    event?.invoke(key)
                    dismiss()
                }
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