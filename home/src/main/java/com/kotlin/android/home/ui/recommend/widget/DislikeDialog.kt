package com.kotlin.android.home.ui.recommend.widget

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kotlin.android.home.R
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.setCompoundDrawablesAndPadding
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.setTextWithFormat
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.android.synthetic.main.dialog_article_dislike_options.view.*

/**
 * 创建者: zl
 * 创建时间: 2020/6/8 4:58 PM
 * 描述:使用builder模式定义dialog
 */
class DislikeDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    companion object {
        const val TYPE_ARTICLE = 1
        const val TYPE_FAMILY_POST = 2
    }

    class Builder(
            private val context: Context,
            private val type: Int,
            private val binder: MultiTypeBinder<*>
    ) {
        private var userProfile: String = ""
        private var userName: String = ""

        private var contentPic: String = ""
        private var contentTitle: String = ""

        /**宽度占比*/
        private var widthOffSize: Float = 1f
        /**高度占比*/
        private var heightOffSize: Float = 0.toFloat()

        /**
         * 当前用户选择的选项
         * 1代表为选择用户
         * 2代表为选择内容
         * 0没有任何选择
         * */
        private var selectedIndex = 0

        fun setWidthOffSize(percent: Float): Builder {
            this.widthOffSize = percent
            return this
        }

        fun setHeightOffSize(percent: Float): Builder {
            this.heightOffSize = percent
            return this
        }

        fun setUserInfo(profile: String, name: String): Builder {
            this.userProfile = profile
            this.userName = name
            return this
        }

        fun setContentInfo(pic: String, title: String): Builder {
            this.contentPic = pic
            this.contentTitle = title
            return this
        }

        fun create(): DislikeDialog {
            val dialog = DislikeDialog(context, R.style.common_dialog)
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_article_dislike_options, null)

            dialog.addContentView(
                    dialogView,
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            )

            ShapeExt.setGradientColor(
                    view = dialogView.mDialogArticleDislikeTopView,
                    startColor = R.color.color_f2f3f6,
                    endColor = R.color.color_ffffff
            )

            dialogView.mDialogArticleDislikeUserImgIv.loadImage(
                    data = userProfile,
                    useProxy = false
            )
            val userType = if (type == TYPE_FAMILY_POST) "家族" else "用户"
            dialogView.mDialogArticleDislikeUserNameTv.text = userName
            dialogView.mDialogArticleDislikeUserTypeTv.text = userType
            dialogView.mDialogArticleDislikeUserBtnTv
                  .setTextWithFormat(R.string.home_dislike_dialog_user_btn, userType)
            dialogView.mDialogArticleDislikeUserBtnFl.setBackground(
                    colorRes = R.color.color_f2f3f6,
                    cornerRadius = 14.dpF
            )

            dialogView.mDialogArticleDislikeContentImgIv.loadImage(
                    data = contentPic,
                    useProxy = false
            )
            dialogView.mDialogArticleDislikeContentTitleTv.text = contentTitle
            dialogView.mDialogArticleDislikeContentBtnFl.setBackground(
                    colorRes = R.color.color_f2f3f6,
                    cornerRadius = 14.dpF
            )

            //选项点击事件
            dialogView.mDialogArticleDislikeUserBtnFl.setOnClickListener {
                selectedIndex = 1
                dialogView.mDialogArticleDislikeUserBtnFl.setBackground(
                        strokeColorRes = R.color.color_4e5e73,
                        strokeWidth = 2,
                        cornerRadius = 14.dpF
                )
                dialogView.mDialogArticleDislikeUserBtnTv.setCompoundDrawablesAndPadding(
                        leftResId = R.drawable.ic_checka,
                        padding = 2
                )
                dialogView.mDialogArticleDislikeContentBtnTv.setCompoundDrawablesAndPadding()
                dialogView.mDialogArticleDislikeContentBtnFl.setBackground(
                        colorRes = R.color.color_f2f3f6,
                        cornerRadius = 14.dpF
                )
            }
            dialogView.mDialogArticleDislikeContentBtnFl.setOnClickListener {
                selectedIndex = 2
                dialogView.mDialogArticleDislikeUserBtnFl.setBackground(
                        colorRes = R.color.color_f2f3f6,
                        cornerRadius = 14.dpF
                )
                dialogView.mDialogArticleDislikeUserBtnTv.setCompoundDrawablesAndPadding()
                dialogView.mDialogArticleDislikeContentBtnTv.setCompoundDrawablesAndPadding(
                        leftResId = R.drawable.ic_checka,
                        padding = 2
                )
                dialogView.mDialogArticleDislikeContentBtnFl.setBackground(
                        strokeColorRes = R.color.color_4e5e73,
                        strokeWidth = 2,
                        cornerRadius = 14.dpF
                )
            }

            //底部按钮
            dialogView.mDialogArticleDislikeCancelBtnTv.run {
                setBackground(
                        strokeColorRes = R.color.color_20a0da,
                        strokeWidth = 2,
                        cornerRadius = 15.dpF
                )
                setOnClickListener {
                    dialog.cancel()
                }
            }
            dialogView.mDialogArticleDislikeConfirmBtnTv.run {
                setBackground(
                        colorRes = R.color.color_20a0da,
                        cornerRadius = 15.dpF
                )
                setOnClickListener {
                    dialog.cancel()
                    if (selectedIndex != 0) {
                        binder.notifyAdapterSelfRemoved()
                    }
                }
            }

            dialog.setContentView(dialogView)
            dialog.setCanceledOnTouchOutside(false)
            val window = dialog.window
            val attributes = window?.attributes

            /***
             * point.x 宽 默认占屏幕宽度四分之三
             * point.y 高 可以不设置，目前未使用高度比例
             */
            if (widthOffSize.toDouble() != 0.0) {
                attributes?.width = (screenWidth * widthOffSize).toInt()
            } else {
                attributes?.width = (screenWidth * 0.75).toInt()
            }
            if (heightOffSize.toDouble() != 0.0) {
                attributes?.height = (screenHeight * heightOffSize).toInt()
            }
            window?.attributes = attributes
            return dialog
        }

    }

}