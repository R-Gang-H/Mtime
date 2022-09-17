package com.kotlin.android.share.tencent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.kotlin.android.bonus.scene.component.postShareEvent
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.share.R
import com.kotlin.android.share.ShareState
import com.kotlin.android.share.entity.ShareEntity
import com.tencent.connect.common.Constants
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzonePublish
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError

/**
 * #Tencent分享
 *
 * 分享类型：
 * [QQShare.SHARE_TO_QQ_TYPE_DEFAULT] （模式1） 分享图文（默认）
 * [QQShare.SHARE_TO_QQ_TYPE_IMAGE] （模式2） 分享纯图片
 * [QQShare.SHARE_TO_QQ_TYPE_AUDIO] （模式3） 分享音乐
 * [QQShare.SHARE_TO_QQ_TYPE_APP] （模式4） 分享应用
 * [QQShare.SHARE_TO_QQ_MINI_PROGRAM] （模式6）分享成QQ小程序
 * [QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT] 图文分享
 * [QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE] 分享纯图片
 * [QzoneShare.SHARE_TO_QZONE_TYPE_APP] 分享应用
 * [QzoneShare.SHARE_TO_QZONE_TYPE_MINI_PROGRAM] 分享成QQ小程序
 * [QzoneShare.SHARE_TO_QZONE_TYPE_NO_TYPE]
 *
 * 分享属性字段含义：
 * [QQShare.SHARE_TO_QQ_TITLE] 分享的标题，最长30字，若不填，则使用小程序名称作为标题
 * [QQShare.SHARE_TO_QQ_SUMMARY] 分享的信息摘要，最长40字，若不填，则使用小程序后台注册的描述摘要
 * [QQShare.SHARE_TO_QQ_TARGET_URL] 这条消息被好友点击后跳转URL
 * [QQShare.SHARE_TO_QQ_IMAGE_URL] 分享预览封面图的url，或者本地图路径 QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL
 * [QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL] 分享预览封面图的本地图路径
 * [QQShare.SHARE_TO_QQ_AUDIO_URL] 音乐文件远程连接，以URL的形式传入，不支持本地音乐
 * [QQShare.SHARE_TO_QQ_APP_NAME] 手Q客户端顶部，替换"返回"按钮文字，如果为空，用"返回"代替
 * [QQShare.SHARE_TO_QQ_ARK_INFO]  调用分享接口分享图文消息、纯图片、音乐、应用时可以携带额外ARK（手Q轻应用）参数JSON串，分享将被展示成ARK消息。
 * Ark JSON串例子: {"app":"com.dianping.dpscope","view":"RestaurantShare","meta":{"ShopData":{"shopId":"2511961"}}}
 * [QQShare.SHARE_TO_QQ_SITE]
 * [QQShare.SHARE_TO_QQ_EXT_INT] 分享额外选项，两种类型可选（默认：不自动打开，也不隐藏）
 * [QQShare.SHARE_TO_QQ_EXT_STR]
 * [QQShare.SHARE_TO_QQ_MINI_PROGRAM_APPID] 小程序appid，小程序与当前应用必须为同一个主体
 * [QQShare.SHARE_TO_QQ_MINI_PROGRAM_PATH] 分享小程序页面路径，若不需要指定，请填主页路径
 * [QQShare.SHARE_TO_QQ_MINI_PROGRAM_TYPE] 3表示正式版，1表示体验版
 *
 * [QzoneShare.SHARE_TO_QQ_IMAGE_URL] 分享图片，以 [ArrayList]<String> 的类型传入，以便支持多张图片，最多9张，多余的会被舍弃。
 *
 * 分享额外选项，两种类型可选（默认：不自动打开，也不隐藏）
 * [QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN] 分享时自动打开分享到QZone的对话框
 * [QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE] 分享时隐藏分享到QZone按钮
 *
 *
 * Created on 2020/6/22.
 *
 * @author o.s
 */
class ShareQQ(
        private val activity: Activity,
        private val appId: String,
        private val authorities: String,
        private val event: ((state: ShareState) -> Unit)? = null
) {

    private val mTencent by lazy { Tencent.createInstance(appId, activity, authorities) }

    private val uiListener = object : IUiListener {
        override fun onComplete(response: Any?) {
            showToast(activity, R.string.share_success)
            event?.invoke(ShareState.SUCCESS)
            postShareEvent()
        }

        override fun onCancel() {
            showToast(activity, R.string.share_cancel)
            event?.invoke(ShareState.CANCEL)
        }

        override fun onWarning(p0: Int) {

        }

        override fun onError(e: UiError?) {
            showToast(activity, R.string.share_fail)
            event?.invoke(ShareState.FAILURE)
        }
    }

    /**
     * 分享消息到QQ的接口，可将新闻、图片、文字、应用等分享给QQ好友、群和讨论组。
     * Tencent类的shareToQQ函数可直接调用，不用用户授权（使用手机QQ当前的登录态）。
     * 调用将打开分享的界面，用户选择好友、群或讨论组之后，点击确定即可完成分享，并进入与该好友进行对话的窗口。
     *
     * 1.（模式1） 分享图文消息
     */
    fun shareToQQByDefault(entity: ShareEntity) {
        val bundle = Bundle()
        // 必填
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, entity.targetUrl)
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, entity.title)
        // 可选
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, entity.summary)
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, entity.imageUrl)
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, entity.appName)
        entity.flag?.apply {
            when (this) {
                QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN -> bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, this)
                QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE -> bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, this)
            }
        }
        entity.arkInfo?.run {
            if(isNotEmpty()) {
                bundle.putString(QQShare.SHARE_TO_QQ_ARK_INFO, this)
            }
        }

        mTencent.shareToQQ(activity, bundle, uiListener)
    }

    /**
     * （模式2） 分享纯图片
     * 增加纯图分享图片不能大于5M的限制：
     */
    fun shareToQQByImage(entity: ShareEntity) {
        val bundle = Bundle()
        // 必填
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, entity.imageLocalUrl)
        // 可选
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, entity.appName)
        entity.flag?.apply {
            when (this) {
                QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN -> bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, this)
                QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE -> bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, this)
            }
        }
        entity.arkInfo?.run {
            if(isNotEmpty()) {
                bundle.putString(QQShare.SHARE_TO_QQ_ARK_INFO, this)
            }
        }

        mTencent.shareToQQ(activity, bundle, uiListener)
    }

    /**
     * （模式3） 分享音乐
     * 音乐分享后，发送方和接收方在聊天窗口中点击消息气泡即可开始播放音乐。
     */
    fun shareToQQByAudio(entity: ShareEntity) {
        val bundle = Bundle()
        // 必填
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO)
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, entity.targetUrl)
        bundle.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, entity.audioUrl)
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, entity.title)
        // 可选
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, entity.summary)
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, entity.imageUrl)
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, entity.appName)
        entity.flag?.apply {
            when (this) {
                QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN -> bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, this)
                QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE -> bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, this)
            }
        }
        entity.arkInfo?.run {
            if(isNotEmpty()) {
                bundle.putString(QQShare.SHARE_TO_QQ_ARK_INFO, this)
            }
        }

        mTencent.shareToQQ(activity, bundle, uiListener)
    }

    /**
     * （模式4） 分享应用 qq已废弃
     *  应用分享后，发送方和接收方在聊天窗口中点击消息气泡即可进入应用的详情页。
     */
//    fun shareToQQByApp(entity: ShareEntity) {
//        val bundle = Bundle()
//        // 必填
//        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP)
//        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, entity.title)
//        // 可选
//        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, entity.summary)
//        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, entity.imageUrl)
//        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, entity.appName)
//
//        entity.flag?.apply {
//            when (this) {
//                QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN -> bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, this)
//                QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE -> bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, this)
//            }
//        }
//        entity.arkInfo?.run {
//            if(isNotEmpty()) {
//                bundle.putString(QQShare.SHARE_TO_QQ_ARK_INFO, this)
//            }
//        }
//
//        mTencent.shareToQQ(activity, bundle, uiListener)
//    }

    /**
     * （模式5）分享携带ARK JSON串
     * 调用分享接口分享图文消息、纯图片、音乐、应用时可以携带额外ARK（手Q轻应用）参数JSON串，分享将被展示成ARK消息。
     * 接口额外参数
     * 调用分享接口分享分享图文消息、纯图片、音乐、应用为ARK展示，params参数在模式1、模式2、模式3、模式4接口参数基础上
     */

    /**
     * （模式6）分享成QQ小程序
     * 可以从外部app分享到手Q为一个QQ小程序，并可以指定预览图、文案、小程序路径：
     */
    fun shareToQQByMiniProgram(entity: ShareEntity) {
        val bundle = Bundle()
        // 必填
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_MINI_PROGRAM)
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, entity.imageUrl)
        bundle.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_APPID, entity.miniProgramAppId)
        bundle.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_PATH, entity.miniProgramPath)
        // 可选
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, entity.title)
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, entity.summary)
        bundle.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_TYPE, entity.miniProgramType)

        mTencent.shareToQQ(activity, bundle, uiListener)
    }

    /**
     * （模式7）分享QQ小程序到QQ空间
     * 可以从外部app分享到手Q空间为一个QQ小程序，并可以指定预览图、文案、小程序路径：
     */
    fun shareToQzoneByMiniProgram(entity: ShareEntity) {
        val bundle = Bundle()
        // 必填
        bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_MINI_PROGRAM)
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, entity.imageUrl)
        bundle.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_APPID, entity.miniProgramAppId)
        bundle.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_PATH, entity.miniProgramPath)
        // 可选
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, entity.title)
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, entity.summary)
        bundle.putString(QQShare.SHARE_TO_QQ_MINI_PROGRAM_TYPE, entity.miniProgramType)

        mTencent.shareToQzone(activity, bundle, uiListener)
    }

    /**
     * 完善了分享到QZone功能，分享类型参数Tencent.SHARE_TO_QQ_KEY_TYPE，目前支持图文分享、发表说说、视频、上传图片。
     * Tencent. shareToQzone()和Tencent. publishToQzone()函数可直接调用，不用用户授权（使用手机QQ当前的登录态），
     * 调用后将打开手机QQ内QQ空间的界面进行分享或发表操作。
     *
     * 1.图文分享
     *
     * 注意:
     * QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持。
     */
    fun shareToQzone(entity: ShareEntity) {
        val bundle = Bundle()
        // 必填
        bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT)
        bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, entity.targetUrl)
        bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, entity.title)
        // 可选
        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, entity.summary)
        bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, entity.imageUrls)

        mTencent.shareToQzone(activity, bundle, uiListener)
    }

    /**
     * 完善了分享到QZone功能
     * 2.发表说说或上传图片
     */
    fun publishToQzone(entity: ShareEntity) {
        val bundle = Bundle()
        // 必填
        bundle.putInt(QzonePublish.PUBLISH_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD)
        // 可选
        bundle.putString(QzonePublish.PUBLISH_TO_QZONE_SUMMARY, entity.summary)
        bundle.putStringArrayList(QzonePublish.PUBLISH_TO_QZONE_IMAGE_URL, entity.imageUrls)
        if (entity.extraScene?.isNotEmpty() == true || entity.callBack?.isNotEmpty() == true) {
            val extra = Bundle()
            entity.extraScene?.run {
                bundle.putString(QzonePublish.HULIAN_EXTRA_SCENE, this)
            }
            entity.callBack?.run {
                bundle.putString(QzonePublish.HULIAN_CALL_BACK, this)
            }
            bundle.putBundle(QzonePublish.PUBLISH_TO_QZONE_EXTMAP, extra)
        }

        mTencent.publishToQzone(activity, bundle, uiListener)
    }

    /**
     * 完善了分享到QZone功能
     * 3.发表视频
     */
    fun publishToQzoneByVideo(entity: ShareEntity) {
        val bundle = Bundle()
        // 必填
        bundle.putInt(QzonePublish.PUBLISH_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHVIDEO)
        bundle.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, entity.videoLocalUrl)
        // 可选
        bundle.putString(QzonePublish.PUBLISH_TO_QZONE_SUMMARY, entity.summary)
        if (entity.extraScene?.isNotEmpty() == true || entity.callBack?.isNotEmpty() == true) {
            val extra = Bundle()
            entity.extraScene?.run {
                bundle.putString(QzonePublish.HULIAN_EXTRA_SCENE, this)
            }
            entity.callBack?.run {
                bundle.putString(QzonePublish.HULIAN_CALL_BACK, this)
            }
            bundle.putBundle(QzonePublish.PUBLISH_TO_QZONE_EXTMAP, extra)
        }

        mTencent.publishToQzone(activity, bundle, uiListener)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_QQ_SHARE
                || requestCode == Constants.REQUEST_QZONE_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, uiListener)
        }
//        if (requestCode == Constants.REQUEST_EDIT_AVATAR
//                || requestCode == Constants.REQUEST_EDIT_DYNAMIC_AVATAR) {
//            // TODO 设置头像
//        }
    }
}