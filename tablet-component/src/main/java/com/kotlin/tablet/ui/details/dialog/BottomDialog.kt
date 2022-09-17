package com.kotlin.tablet.ui.details.dialog

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.tablet.R

class BottomDialog : DialogFragment() {
    var isShow = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.common_dialog)
    }

    private lateinit var callBack: (type: DialogEnum, view: View) -> Unit

    fun setCallBack(callBack: (type: DialogEnum, view: View) -> Unit) {
        this.callBack = callBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.run {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.run {
                decorView.setPadding(0, 0, 0, 0)
                attributes.run {
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.BOTTOM
                    windowAnimations = R.style.BottomDialogAnimation
                }
            }
        }
        val view = inflater.inflate(R.layout.layout_film_bottom_dialog, null)
        view.setBackground(
            colorRes = android.R.color.white,
            cornerRadius = 4.dpF,
            direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )
        val tvShare = view.findViewById<TextView>(R.id.tv_share)
        val tvEdit = view.findViewById<TextView>(R.id.tv_edit)
        val tvDelete = view.findViewById<TextView>(R.id.tv_delete)
        val tvCancel = view.findViewById<TextView>(R.id.tv_cancel)
        if (isShow) {
            tvShare.visible()
        } else {
            tvShare.gone()
        }
        tvEdit.onClick {
            callBack.invoke(DialogEnum.EDIT, it)
            dismiss()
        }
        tvShare.onClick {
            callBack.invoke(DialogEnum.SHARE, it)
            dismiss()
        }
        tvDelete.onClick {
            callBack.invoke(DialogEnum.DELETE, it)
            dismiss()
        }
        tvCancel.onClick {//关闭
            dismiss()
        }
        return view
    }
}