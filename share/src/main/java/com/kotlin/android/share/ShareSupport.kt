package com.kotlin.android.share

import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.core.isApkInstalled
import com.kotlin.android.mtime.ktx.ext.showToast

/**
 * 分享支持
 *
 * Created on 2020/6/23.
 *
 * @author o.s
 */
object ShareSupport {

    /**
     * 分享平台是否安装
     */
    fun isPlatformInstalled(platform: SharePlatform): Boolean {
        return when (platform) {
            SharePlatform.WE_CHAT,
            SharePlatform.WE_CHAT_TIMELINE -> {
                isWxInstalled()
            }
            SharePlatform.WEI_BO -> isWeiBoInstalled()
            SharePlatform.QQ -> isQQInstalled()
            else -> {
                false
            }
        }
    }

    fun isWxInstalled(): Boolean = CoreApp.instance.let {
        if (it.isApkInstalled(ShareEnv.PACKAGE_WX)) {
            true
        } else {
            it.showToast(R.string.we_chat_is_not_install)
            false
        }
    }

    fun isWeiBoInstalled(): Boolean = CoreApp.instance.let {
        if (it.isApkInstalled(ShareEnv.PACKAGE_WB)) {
            true
        } else {
            it.showToast(R.string.wei_bo_is_not_install)
            false
        }
    }

    fun isQQInstalled(): Boolean = CoreApp.instance.let {
        if (it.isApkInstalled(ShareEnv.PACKAGE_QQ)) {
            true
        } else {
            it.showToast(R.string.qq_is_not_install)
            false
        }
    }

}