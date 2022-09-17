package com.mtime.bussiness.main.dialogs

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.publish.component.ui.selectedvideo.showVideoListDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.afterLogin
import com.mtime.R
import com.mtime.databinding.DialogMainPublishLayoutBinding

/**
 * Created by suq on 2022/4/7
 * des:
 */
class MainPublishDialog(var ctx: Context) : Dialog(ctx, R.style.bottom_out_dialog) {

    private val publishProvider by lazy { getProvider(IPublishProvider::class.java) }

    init {
        val binding = DialogMainPublishLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding)

        window?.apply {
            setGravity(Gravity.BOTTOM)
            attributes.width = screenWidth
        }
    }

    private fun initView(binding: DialogMainPublishLayoutBinding) {
        binding.apply {
            group1.onClick {
                //文章
                afterLogin {
                    publishProvider?.startEditorActivity(
                        type = CONTENT_TYPE_ARTICLE
                    )
                    dismiss()
                }
            }
            group2.onClick {
                //视频
                afterLogin {
                    (ctx as? FragmentActivity)?.showVideoListDialog()
                    dismiss()
                }

            }
            group3.onClick {
                //种草
                afterLogin {
                    publishProvider?.startEditorActivity(
                        type = CONTENT_TYPE_POST,
                        isFootmarks = true
                    )
                    dismiss()
                }
            }
            group4.onClick {
                //日志
                afterLogin {
                    publishProvider?.startEditorActivity(
                        type = CONTENT_TYPE_JOURNAL
                    )
                    dismiss()
                }
            }
            close.onClick {
                dismiss()
            }
        }
    }
}