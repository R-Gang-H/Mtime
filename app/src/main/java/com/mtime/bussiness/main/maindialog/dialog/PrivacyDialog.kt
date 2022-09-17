package com.mtime.bussiness.main.maindialog.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.graphics.Point
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.mtime.R

/**
 * 隐私政策dialog
 */
class PrivacyDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    class Builder(private val context: Context) {
        private var title: CharSequence? = null
        private var content: CharSequence? = null

        private var positiveButtonContent: String? = null

        private var negativeButtonContent: String? = null

        private var userAgreementListener: DialogInterface.OnClickListener? = null

        private var privacyPolicyListener: DialogInterface.OnClickListener? = null

        private var positiveButtonListener: DialogInterface.OnClickListener? = null

        private var negativeButtonListener: DialogInterface.OnClickListener? = null

        private var withOffSize: Float = 0.toFloat()
        private var heightOffSize: Float = 0.toFloat()

        fun setTitle(title: CharSequence?): Builder {
            this.title = title
            return this
        }

        fun setTitle(title: Int): Builder {
            this.title = context.getString(title) as String
            return this
        }

        fun setContent(content: Int): Builder {
            this.content = context.getString(content) as String
            return this
        }

        fun setContent(content: CharSequence?): Builder {
            this.content = content
            return this
        }

        fun setUserAgreementButton(listener: DialogInterface.OnClickListener): Builder {
            this.userAgreementListener = listener
            return this
        }

        fun setPrivacyPolicyButton(listener: DialogInterface.OnClickListener): Builder {
            this.privacyPolicyListener = listener
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

        fun create(): PrivacyDialog {
            val dialog = PrivacyDialog(context, R.style.common_dialog)
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_privacy, null)
            val mTitle = (dialogView.findViewById<View>(R.id.mTitle) as TextView)
            val mContent = (dialogView.findViewById<View>(R.id.mContent) as TextView)
            val userAgreementTv = (dialogView.findViewById<View>(R.id.userAgreementTv) as TextView)
            userAgreementTv.paint.flags = Paint.UNDERLINE_TEXT_FLAG;
            val privacyPolicyTv = (dialogView.findViewById<View>(R.id.privacyPolicyTv) as TextView)
            privacyPolicyTv.paint.flags = Paint.UNDERLINE_TEXT_FLAG;
            val mSure = (dialogView.findViewById<View>(R.id.mSure) as TextView)
            val mCancel = (dialogView.findViewById<View>(R.id.mCancel) as TextView)
            dialog.addContentView(
                    dialogView,
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            )

            if (!TextUtils.isEmpty(title)) {
                mTitle.text = title
            } else {
                mTitle.visibility = View.GONE
            }
            mContent.movementMethod = LinkMovementMethod.getInstance()
            if (!TextUtils.isEmpty(content)) {
                mContent.text = content
            } else {
                mContent.visibility = View.INVISIBLE
            }
            if (userAgreementListener != null) {
                userAgreementTv.setOnClickListener { userAgreementListener?.onClick(dialog, -1) }
            }

            if (privacyPolicyListener != null) {
                privacyPolicyTv.setOnClickListener { privacyPolicyListener?.onClick(dialog, -2) }
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
                mCancel.visibility = View.GONE
            }

            dialog.setContentView(dialogView)
            dialog.setCanceledOnTouchOutside(false)

            val window = dialog.window
            /**去掉dialog的焦点，让父类获得焦点*/
            window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            val context = this.context as Activity
            val windowManager = context.windowManager

            val defaultDisplay = windowManager.defaultDisplay

            val attributes = window?.attributes
            val point = Point()
            defaultDisplay.getSize(point)
            /***
             * point.x 宽
             * point.y 高
             */
            if (withOffSize.toDouble() != 0.0) {
                attributes?.width = (point.x * withOffSize).toInt()
            } else {
                attributes?.width = (point.x * 0.78).toInt()
            }
            if (heightOffSize.toDouble() != 0.0) {
                attributes?.height = (point.y * heightOffSize).toInt()
            }
            window?.attributes = attributes
            return dialog
        }

    }

}