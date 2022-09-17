package com.kotlin.android.widget.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import com.kotlin.android.widget.R

/**
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */

/**
 * 显示对话框
 */
fun showDialog(
        context: Context?,
        @StringRes title: Int? = null,
        @StringRes content: Int? = null,
        @StringRes positive: Int = R.string.widget_sure,
        @StringRes negative: Int = R.string.widget_cancel,
        negativeAction: (() -> Unit)? = null,
        positiveAction: () -> Unit
) {
    context?.apply {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> positiveAction.invoke()
                DialogInterface.BUTTON_NEGATIVE -> negativeAction?.invoke()
            }
        }
        val builder = BaseDialog.Builder(this)
                .setNegativeButton(negative, listener)
                .setPositiveButton(positive, listener)
                if (title != null) {
                    builder.setTitle(title)
                }
                if (content != null) {
                    builder.setContent(content)
                }
        builder.create().show()
    }
}

/**
 * 显示对话框
 */
fun showDialog(
        context: Context?,
        title: String = "",
        content: String = "",
        @StringRes positive: Int = R.string.widget_sure,
        @StringRes negative: Int = R.string.widget_cancel,
        negativeAction: (() -> Unit)? = null,
        positiveAction: () -> Unit
) {
    context?.apply {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> positiveAction.invoke()
                DialogInterface.BUTTON_NEGATIVE -> negativeAction?.invoke()
            }
        }
        val builder = BaseDialog.Builder(this)
                .setNegativeButton(negative, listener)
                .setPositiveButton(positive, listener)
                if (title.isNotEmpty()) {
                    builder.setTitle(title)
                }
                if (content.isNotEmpty()) {
                    builder.setContent(content)
                }
        builder.create().show()
    }
}