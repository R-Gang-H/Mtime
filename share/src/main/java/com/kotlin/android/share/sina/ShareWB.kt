package com.kotlin.android.share.sina

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.kotlin.android.bonus.scene.component.postShareEvent
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.share.R
import com.kotlin.android.share.ShareEnv
import com.kotlin.android.share.ShareState
import com.kotlin.android.share.entity.ShareEntity
import com.sina.weibo.sdk.api.*
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.common.UiError
import com.sina.weibo.sdk.openapi.IWBAPI
import com.sina.weibo.sdk.openapi.WBAPIFactory
import com.sina.weibo.sdk.share.WbShareCallback

/**
 *
 * Created on 2020/6/22.
 *
 * @author o.s
 */
class ShareWB(
        private val context: Activity,
        private val appId: String,
        redirectUrl: String,
        scope: String = ShareEnv.SCOPE,
        private val event: ((state: ShareState) -> Unit)? = null
) {

    lateinit var api: IWBAPI

    init {
        api = WBAPIFactory.createWBAPI(context)
        api.registerApp(context, AuthInfo(context, appId, redirectUrl, scope))
    }

    /**
     * 微博分享回调
     */
    private val wbShareCallback = object : WbShareCallback {
        override fun onError(error: UiError) {
            showToast(context, R.string.share_fail)
            event?.invoke(ShareState.FAILURE)
        }

        override fun onCancel() {
            showToast(context, R.string.share_cancel)
            event?.invoke(ShareState.CANCEL)
        }

        override fun onComplete() {
            showToast(context, R.string.share_success)
            event?.invoke(ShareState.SUCCESS)
            postShareEvent()
        }
    }

    /**
     * [Activity.onNewIntent] 回调
     */
    fun doResultIntent(data: Intent?) {
        api.doResultIntent(data, wbShareCallback)
    }

    /**
     * [Activity.onActivityResult] 回调
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        api.doResultIntent(data, wbShareCallback)
    }

    /**
     * 分享图片和文本
     */
    fun shareToWBByImageText(entity: ShareEntity) {
        val msg = WeiboMultiMessage()

        val text = TextObject()
        text.title = entity.title
        text.text = entity.wbDesc
        text.description = entity.wbDesc
        text.actionUrl = entity.targetUrl
        fillThumbImage(entity, text)

        val img = ImageObject()
        img.imagePath = entity.imageUrl
//        entity.imageWBByteArray?.run {
//            img.imageData = this
//        }
        fillThumbImage(entity, img)

        msg.imageObject = img
        msg.textObject = text
        api.shareMessage(msg, false)
    }

    /**
     * 分享文本
     */
    fun shareToWBByText(entity: ShareEntity) {
        val msg = WeiboMultiMessage()

        val text = TextObject()
        text.title = entity.title
        text.text = entity.wbDesc
        text.description = entity.wbDesc
        text.actionUrl = entity.targetUrl
        fillThumbImage(entity, text)

        msg.textObject = text
        api.shareMessage(msg, false)
    }

    /**
     * 分享图片
     */
    fun shareToWBByImage(entity: ShareEntity) {
        val msg = WeiboMultiMessage()

        val img = ImageObject()
        img.imagePath = entity.imageUrl

        // E/ActivityTaskManager: Transaction too large, intent: Intent { cmp=com.mtime/com.sina.weibo.sdk.share.WbShareTransActivity (has extras) }, extras size: 904904, icicle size: 0
//        entity.imageWBByteArray?.run {
//            img.imageData = this
//        }
        fillThumbImage(entity, img)

        msg.imageObject = img
        api.shareMessage(msg, false)
    }

    /**
     * 分享对图，最多9张
     */
    fun shareToWBByMultiImage(entity: ShareEntity) {
        val msg = WeiboMultiMessage()

        val multiImg = MultiImageObject()
        multiImg.actionUrl = entity.targetUrl
        val imageList = ArrayList<Uri>()
        entity.imageLocalUrls?.forEach {
            imageList.add(Uri.parse(it))
        }
        multiImg.imageList = imageList

        msg.multiImageObject = multiImg
        api.shareMessage(msg, false)
    }

    /**
     * 分享视频
     */
    fun shareToWBByVideo(entity: ShareEntity) {
        val msg = WeiboMultiMessage()

        val video = VideoSourceObject()
        video.videoPath = Uri.parse(entity.videoLocalUrl)

        msg.videoSourceObject = video
        api.shareMessage(msg, false)
    }

    /**
     * 分享Web页面
     */
    fun shareToWBByWebPage(entity: ShareEntity) {
        val msg = WeiboMultiMessage()

        val web = WebpageObject()
        web.title = entity.title
        web.description = entity.wbDesc
        web.actionUrl = entity.targetUrl
        fillThumbImage(entity, web)

        msg.mediaObject = web
        api.shareMessage(msg, false)
    }

    /**
     * 填充分享文本缩略图
     */
    private fun fillThumbImage(entity: ShareEntity, text: TextObject) {
        entity.thumbImageByteArray?.run {
            // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
            text.thumbData = this
        } ?: entity.autoThumbImageByteArray?.run {
            text.thumbData = this
        }
    }

    /**
     * 填充分享图片缩略图
     */
    private fun fillThumbImage(entity: ShareEntity, img: ImageObject) {
        entity.thumbImageByteArray?.run {
            // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
            img.thumbData = this
        } ?: entity.autoThumbImageByteArray?.run {
            img.thumbData = this
        }
    }

    /**
     * 填充分享WEB缩略图
     */
    private fun fillThumbImage(entity: ShareEntity, web: WebpageObject) {
        entity.thumbImageByteArray?.run {
            // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
            web.thumbData = this
        } ?: entity.autoThumbImageByteArray?.run {
            web.thumbData = this
        }
    }
}