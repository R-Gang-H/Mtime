package com.kotlin.android.share.observer

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.app.data.entity.common.ShareResultExtend
import com.kotlin.android.ktx.ext.core.copyToClipboard
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.share.*
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ext.dismissShareDialog

/**
 * create by lushan on 2020/11/30
 * description:分享Observer
 */
class ShareObserver(var activity: FragmentActivity) : Observer<BaseUIModel<ShareResultExtend<Any>>> {
    override fun onChanged(t: BaseUIModel<ShareResultExtend<Any>>?) {
        t?.apply {
            activity.showOrHideProgressDialog(showLoading)
            success?.apply {
                val sharePlatform = extend as SharePlatform
                when (sharePlatform) {
                    SharePlatform.WE_CHAT,
                    SharePlatform.WE_CHAT_TIMELINE,
                    SharePlatform.WEI_BO,
                    SharePlatform.QQ -> {

                        var imageUrl:String? = ""
                        var shareData =  ShareEntity.buildWithoutImageUrl(result){
                            imageUrl = it
                        }
                        shareData.thumbImageCallback = {
                            if (it && ShareSupport.isPlatformInstalled(sharePlatform)) {
                                shareData?.apply {
                                    ShareManager.share(sharePlatform,this)
                                }
                            }
                        }
                        shareData.imageUrl = imageUrl.orEmpty()
                    }
                    SharePlatform.COPY_LINK -> {
                        activity.copyToClipboard(result.url.orEmpty())
                        showToast(R.string.share_copy_link_success)
                        activity.dismissShareDialog()
                    }
                    else -> showToast(getString(R.string.share_not_register).format(getString(sharePlatform.title)))
                }
            }
            error?.showToast()
            netError?.showToast()
        }
    }

}