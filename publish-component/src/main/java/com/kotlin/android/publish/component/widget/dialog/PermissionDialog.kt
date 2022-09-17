package com.kotlin.android.publish.component.widget.dialog

import android.view.Gravity
import android.view.WindowManager
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.core.annotation.DialogFragmentTag
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.DialogPermissionChooseBinding

/**
 * 选择发布权限
 *
 * Created on 2022/4/15.
 *
 * @author o.s
 */
@DialogFragmentTag(tag = "tag_dialog_fragment_permission_dialog")
class PermissionDialog : BaseVMDialogFragment<BaseViewModel, DialogPermissionChooseBinding>() {

    var event: ((CharSequence) -> Unit)? = null

    override fun initEnv() {
        window = {
            decorView.setPadding(0, 0, 0, 0)
            attributes.run {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.BOTTOM
                windowAnimations = R.style.BottomDialogAnimation
            }
            setBackgroundDrawable(null)
        }
    }

    override fun initView() {
        mBinding?.apply {
            itemView1.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 8.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )
            itemView2.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 8.dpF,
                direction = Direction.LEFT_BOTTOM or Direction.RIGHT_BOTTOM
            )
            cancelView.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 8.dpF,
            )
            itemView1.setOnClickListener {
                event?.invoke(itemView1.text)
                dismiss()
            }
            itemView2.setOnClickListener {
                event?.invoke(itemView2.text)
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