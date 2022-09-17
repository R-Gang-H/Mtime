package com.kotlin.android.publish.component.widget.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.marginLeft
import com.kotlin.android.ktx.ext.core.marginRight
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.publish.component.R

/**
 * create by lushan on 2022/4/18
 * des:
 **/
class PublishSuccessDialog: DialogFragment() {
    private val mPublishSuccessView by lazy {
        context?.run {
            PublishSuccessView(this)
        }
    }

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

            }
            setBackgroundDrawable(null)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mPublishSuccessView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        immersive().statusBarColor(getColor(R.color.transparent))
        initView()
    }

    private fun initView() {
        mPublishSuccessView?.marginLeft = 15.dp
        mPublishSuccessView?.marginRight = 15.dp
    }

    fun initListener(listener:(()->Unit)? = null){
        mPublishSuccessView?.listener = listener
    }

    fun setTitle(@StringRes resId: Int) {
        mPublishSuccessView?.setTitle(resId)
    }

    fun setTitle(title: String) {
        mPublishSuccessView?.setTitle(title)
    }

}