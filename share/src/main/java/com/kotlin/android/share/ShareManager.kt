package com.kotlin.android.share

import android.app.Activity
import android.content.Intent
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.sina.ShareWB
import com.kotlin.android.share.tencent.ShareQQ
import com.kotlin.android.share.wx.ShareWX

/**
 * 分享管理器
 *
 * Created on 2020/6/23.
 *
 * @author o.s
 */
object ShareManager {
    private var shareWX: ShareWX? = null
    private var shareQQ: ShareQQ? = null
    private var shareWB: ShareWB? = null

    /**
     * 调用 [share] 分享前需要先安装当前 [activity]
     */
    fun install(
            activity: Activity,
            event: ((state: ShareState) -> Unit)? = null
    ) {
        shareQQ = ShareQQ(activity, ShareEnv.APP_ID_TENCENT, ShareEnv.APP_AUTHORITIES, event)
        shareWX = ShareWX(activity, ShareEnv.APP_ID_WX)
        ShareWX.shareState = event
        shareWB = ShareWB(activity, ShareEnv.APP_ID_WB, ShareEnv.REDIRECT_URL, ShareEnv.SCOPE, event)
    }

    fun share(platform: SharePlatform, entity: ShareEntity) {
        val type = entity.shareType
        when (platform) {
            SharePlatform.WE_CHAT -> {
                when (type) {
                    ShareType.SHARE_IMAGE_TEXT -> shareWX?.shareToWXByWeb(entity)
                    ShareType.SHARE_TEXT -> shareWX?.shareToWXByText(entity)
                    ShareType.SHARE_IMAGE -> shareWX?.shareToWXByImage(entity)
                    ShareType.SHARE_MUSIC -> shareWX?.shareToWXByMusic(entity)
                    ShareType.SHARE_VIDEO -> shareWX?.shareToWXByVideo(entity)
                    else -> {}
                }
            }
            SharePlatform.WE_CHAT_TIMELINE -> {
                val scene = if (shareWX?.isSupportTimeline() == true) {
                    ShareWX.Scene.TIMELINE
                } else {
                    ShareWX.Scene.SESSION
                }
                when (type) {
                    ShareType.SHARE_IMAGE_TEXT -> shareWX?.shareToWXByWeb(entity, scene)
                    ShareType.SHARE_TEXT -> shareWX?.shareToWXByText(entity, scene)
                    ShareType.SHARE_IMAGE -> shareWX?.shareToWXByImage(entity, scene)
                    ShareType.SHARE_MUSIC -> shareWX?.shareToWXByMusic(entity, scene)
                    ShareType.SHARE_VIDEO -> shareWX?.shareToWXByVideo(entity, scene)
                    else -> {}
                }
            }
            SharePlatform.WEI_BO -> {
                when (type) {
                    ShareType.SHARE_IMAGE_TEXT -> shareWB?.shareToWBByWebPage(entity) // shareWB?.shareToWBByImageText(entity)
                    ShareType.SHARE_TEXT -> shareWB?.shareToWBByText(entity)
                    ShareType.SHARE_IMAGE -> shareWB?.shareToWBByImage(entity)
                    ShareType.SHARE_MULTI_IMAGE -> shareWB?.shareToWBByMultiImage(entity)
                    ShareType.SHARE_VIDEO -> shareWB?.shareToWBByVideo(entity)
                    else -> {}
                }
            }
            SharePlatform.QQ -> {
                when (type) {
                    ShareType.SHARE_IMAGE_TEXT -> shareQQ?.shareToQQByDefault(entity)
                    ShareType.SHARE_IMAGE -> shareQQ?.shareToQQByImage(entity)
                    ShareType.SHARE_MUSIC -> shareQQ?.shareToQQByAudio(entity)
//                    ShareType.SHARE_APP -> shareQQ?.shareToQQByApp(entity)//qq已废弃
                    ShareType.SHARE_MINI_PROGRAM -> shareQQ?.shareToQQByMiniProgram(entity)
                    else -> {}
                }
            }
//            SharePlatform.Q_ZONE -> {
//                when (type) {
//                    ShareType.SHARE_TIMELINE -> ShareManager.shareTencent?.shareToQzone(entity)
//                    ShareType.SHARE_MINI_PROGRAM_TO_TIMELINE -> ShareManager.shareTencent?.shareToQzoneByMiniProgram(entity)
//                    ShareType.PUBLISH_IMAGE_TEXT -> ShareManager.shareTencent?.publishToQzone(entity)
//                    ShareType.PUBLISH_VIDEO -> ShareManager.shareTencent?.publishToQzoneByVideo(entity)
//                    else -> {}
//                }
//            }
            else -> {

            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Tencent QQ
        shareQQ?.onActivityResult(requestCode, resultCode, data)
        // Sina WB
        shareWB?.onActivityResult(requestCode, resultCode, data)
    }

    fun doResultIntent(data: Intent?) {
        shareWB?.doResultIntent(data)
    }
}

enum class ShareState {
    SUCCESS,
    FAILURE,
    CANCEL
}