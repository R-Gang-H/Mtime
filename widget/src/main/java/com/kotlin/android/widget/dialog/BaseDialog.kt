package com.kotlin.android.widget.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Point
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.widget.R

/**
 * 创建者: zl
 * 创建时间: 2020/6/8 4:58 PM
 * 描述:使用builder模式定义dialog
 */
class BaseDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    class Builder(private val context: Context) {
        private var title: String? = null
        private var content: String? = null
        private var isCanceledOnTouchOutside: Boolean = false

        private var positiveButtonContent: String? = null

        private var negativeButtonContent: String? = null

        private var positiveButtonListener: DialogInterface.OnClickListener? = null

        private var negativeButtonListener: DialogInterface.OnClickListener? = null

        /**宽度占比*/
        private var withOffSize: Float = 0.toFloat()
        /**高度占比*/
        private var heightOffSize: Float = 0.toFloat()

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setTitle(title: Int): Builder {
            this.title = context.getString(title)
            return this
        }

        fun setContent(content: Int): Builder {
            this.content = context.getString(content)
            return this
        }

        fun setContent(content: String): Builder {
            this.content = content
            return this
        }

        fun setCanceledOnTouchOutside(cancel: Boolean): Builder {
            this.isCanceledOnTouchOutside = cancel
            return this
        }

        fun setPositiveButton(text: String, listener: DialogInterface.OnClickListener): Builder {
            this.positiveButtonContent = text
            this.positiveButtonListener = listener
            return this
        }

        fun setPositiveButton(textId: Int, listener: DialogInterface.OnClickListener): Builder {
            this.positiveButtonContent = context.getText(textId) as String
            this.positiveButtonListener = listener
            return this
        }

        fun setNegativeButton(text: String, listener: DialogInterface.OnClickListener): Builder {
            this.negativeButtonContent = text
            this.negativeButtonListener = listener
            return this
        }

        fun setNegativeButton(textId: Int, listener: DialogInterface.OnClickListener): Builder {
            this.negativeButtonContent = context.getText(textId) as String
            this.negativeButtonListener = listener
            return this
        }

        fun create(): BaseDialog {
            val dialog = BaseDialog(context, R.style.common_dialog)
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_base, null)
            val dialogBg = (dialogView.findViewById<View>(R.id.dialogBg) as ConstraintLayout)
            val mTitle = (dialogView.findViewById<View>(R.id.mTitle) as TextView)
            val mContent = (dialogView.findViewById<View>(R.id.mContent) as TextView)
            val mSure = (dialogView.findViewById<View>(R.id.mSure) as TextView)
            val mCancel = (dialogView.findViewById<View>(R.id.mCancel) as TextView)
            ShapeExt.setShapeColorAndCorner(view = dialogBg, solidColor = R.color.color_ffffff, corner = 12)
            //设置内容可滚动
            mContent.movementMethod = ScrollingMovementMethod.getInstance()

            dialog.addContentView(
                    dialogView,
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            )

            if (!TextUtils.isEmpty(title)) {
                mTitle.text = title
            } else {
                mTitle.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(content)) {
                mContent.text = content
            } else {
                mContent.visibility = View.GONE
            }

            if (!TextUtils.isEmpty(positiveButtonContent)) {
                mSure.text = positiveButtonContent
                if (positiveButtonListener != null) {
                    mSure.setOnClickListener { positiveButtonListener?.onClick(dialog, -1) }
                }
            } else {
                mSure.visibility = View.GONE
            }

            if (!TextUtils.isEmpty(negativeButtonContent)) {
                mCancel.text = negativeButtonContent
                if (negativeButtonListener != null) {
                    mCancel.setOnClickListener { negativeButtonListener?.onClick(dialog, -2) }
                }
            } else {
                dialogView.findViewById<View>(R.id.line).visibility = View.GONE
                mCancel.visibility = View.GONE
            }

            if (TextUtils.isEmpty(negativeButtonContent) && TextUtils.isEmpty(positiveButtonContent)) {
                dialogView.findViewById<View>(R.id.hor_line).gone()
            }

            dialog.setContentView(dialogView)
            dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside)
            val window = dialog.window
            val context = this.context as Activity
            val windowManager = context.windowManager

            val defaultDisplay = windowManager.defaultDisplay

            val attributes = window?.attributes
            val point = Point()
            defaultDisplay.getSize(point)
            /***
             * point.x 宽 默认占屏幕宽度四分之三
             * point.y 高 可以不设置，目前未使用高度比例
             */
            if (withOffSize.toDouble() != 0.0) {
                attributes?.width = (point.x * withOffSize).toInt()
            } else {
                attributes?.width = (point.x * 0.75).toInt()
            }
            if (heightOffSize.toDouble() != 0.0) {
                attributes?.height = (point.y * heightOffSize).toInt()
            }
            window?.attributes = attributes
            return dialog
        }

    }

}