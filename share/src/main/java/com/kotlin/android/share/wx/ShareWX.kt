package com.kotlin.android.share.wx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.kotlin.android.core.CoreApp
import com.kotlin.android.share.ShareState
import com.kotlin.android.share.entity.ShareEntity
import com.tencent.mm.opensdk.constants.Build
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.File

/**
 * 微信分享：
 *
 * [SendMessageToWX.Req.transaction]: 对应该请求的事务 ID，通常由 Req 发起，回复 Resp 时应填入对应事务 ID
 *
 * [SendMessageToWX.Req.scene]: 发送的目标场景，如下4种情况:
 * [SendMessageToWX.Req.WXSceneSession] 分享到朋友
 * [SendMessageToWX.Req.WXSceneTimeline] 分享到朋友圈
 * [SendMessageToWX.Req.WXSceneFavorite] 分享到收藏
 * [SendMessageToWX.Req.WXSceneSpecifiedContact] 分享到指定联系人
 *
 * [SendMessageToWX.Req.message]: [WXMediaMessage]
 * [WXMediaMessage] 微信媒体消息内容说明:
 * sdkVer	    int	                        sdk 版本号
 * title	    String	                    消息标题	        限制长度不超过 512Bytes
 * description	String	                    消息描述	        限制长度不超过 1KB
 * thumbData	byte[]	                    缩略图的二进制数据	限制内容大小不超过 32KB
 * mediaObject	WXMediaMessage.IMediaObject	消息对象	        用于描述一个媒体对象的接口，媒体对象包括:
 * [WXTextObject] 文本
 * [WXImageObject] 图片
 * [WXMusicObject] 音乐
 * [WXVideoObject] 视频
 * [WXWebpageObject] Web页面
 * [WXFileObject] 文件
 * [WXAppExtendObject] 应用扩展
 * [WXMiniProgramObject] 小程序
 *
 * 现在，你的程序要发送请求或发送响应到微信终端，可以通过 IWXAPI 的 [IWXAPI.sendReq] 和 [IWXAPI.sendResp] 两个方法来实现。
 * [IWXAPI.sendReq] 是第三方 app 主动发送消息给微信，发送完成之后会切回到第三方 app 界面。
 * [IWXAPI.sendResp] 是微信向第三方 app 请求数据，第三方 app 回应数据之后会切回到微信界面。
 *
 * Created on 2020/6/22.
 *
 * @author o.s
 */
class ShareWX(
    private val context: Context,
    private val appId: String
) {

    companion object {
        var shareState: ((state: ShareState) -> Unit)? = null
    }

    private val api: IWXAPI by lazy {
        WXAPIFactory.createWXAPI(context, appId, true).apply {
            registerApp(appId)
        }
    }

    init {
        //建议动态监听微信启动广播进行注册到微信
        context.run {
            registerReceiver(object : BroadcastReceiver() {

                override fun onReceive(context: Context?, intent: Intent?) {
                    // 将该app注册到微信
                    api.registerApp(appId)
                }

            }, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))
        }
    }

    /**
     * 是否支持朋友圈
     */
    fun isSupportTimeline() = api.wxAppSupportAPI >= Build.TIMELINE_SUPPORTED_SDK_INT

    /**
     * 一、文字类型分享示例
     * WXTextObject （WXMediaMessage.IMediaObject 的派生类，用于描述一个文本对象）
     *
     * text	String	文本数据	长度需大于 0 且不超过 10KB
     */
    fun shareToWXByText(entity: ShareEntity, scene: Scene = Scene.SESSION) {
        // 初始化一个 WXTextObject 对象，填写分享的文本内容
        val text = WXTextObject()
        text.text = entity.summary

        // 用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage()
        msg.mediaObject = text
        msg.description = text.text

        val req = SendMessageToWX.Req()
        req.scene = scene.value
        req.transaction = buildTransaction("text")
        req.message = msg

        // 调用api接口，发送数据到微信
        api.sendReq(req)
    }


    fun getFileUri(context: Context, file: File?): String? {
        if (file == null || !file.exists()) {
            return null
        }
        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "com.mtime.fileprovider",  // 要与`AndroidManifest.xml`里配置的`authorities`一致，假设你的应用包名为com.example.app
            file
        )

        // 授权给微信访问路径
        context.grantUriPermission(
            "com.tencent.mm",  // 这里填微信包名
            contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        return contentUri.toString() // contentUri.toString() 即是以"content://"开头的用于共享的路径
    }
    /**
     * 二、图片类型分享示例
     * WXImageObject （WXMediaMessage.IMediaObject 的派生类，用于描述一个图片对象）
     *
     * imageData 	byte[]	图片的二进制数据    内容大小不超过 10MB
     * imagePath	String	图片的本地路径     对应图片内容大小不超过 10MB
     */
    fun shareToWXByImage(entity: ShareEntity, scene: Scene = Scene.SESSION) {
        // 初始化 WXImageObject 和 WXMediaMessage 对象
        val img = WXImageObject()
        if (TextUtils.isEmpty(entity.imageUrl).not()) {
            if (api?.wxAppSupportAPI>=0x27000D00 &&android.os.Build.VERSION.SDK_INT>= android.os.Build.VERSION_CODES.N){
                img.imagePath = getFileUri(CoreApp.instance,File(entity.imageUrl))
            } else{
                img.imagePath = entity.imageUrl
            }
        } else {
            img.imagePath = entity.imageUrl
        }
//        entity.imageByteArray?.run {
//            img.imageData = this
//        }
        val msg = WXMediaMessage()
        msg.mediaObject = img
        fillThumbData(entity, msg)

        // 构造一个Req
        val req = SendMessageToWX.Req()
        req.scene = scene.value
        req.transaction = buildTransaction("img")
        req.message = msg
//        req.userOpenId = getUserOpenId()

        //调用api接口，发送数据到微信
        api.sendReq(req)
    }

    /**
     * 三、音乐类型分享示例
     * WXMusicObject（WXMediaMessage.IMediaObject 的派生类，用于描述一个音频对象）
     *
     * musicUrl	            String	音频网页的 URL 地址	            限制长度不超过 10KB
     * musicLowBandUrl	    String	供低带宽环境下使用的音频网页 URL 地址	限制长度不超过 10KB
     * musicDataUrl	        String	音频数据的 URL 地址	            限制长度不超过 10KB
     * musicLowBandDataUrl	String	供低带宽环境下使用的音频数据 URL 地址	限制长度不超过 10KB
     *
     * 注意：musicUrl 和 musicLowBandUrl 不能同时为空
     * 注意：分享至微信的音乐，直接点击好友会话或朋友圈下的分享内容会跳转至第三方APP，
     * 点击会话列表顶部的音乐分享内容将跳转至微信原生音乐播放器播放。
     */
    fun shareToWXByMusic(entity: ShareEntity, scene: Scene = Scene.SESSION) {
        // 初始化一个WXMusicObject，填写url
        val music = WXMusicObject()
        music.musicUrl = entity.audioUrl

        // 用 WXMusicObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage()
        msg.mediaObject = music
        msg.title = entity.title
        msg.description = entity.summary
        fillThumbData(entity, msg)

        val req = SendMessageToWX.Req()
        req.scene = scene.value
        req.transaction = buildTransaction("music")
        req.message = msg
//        req.userOpenId = getUserOpenId()

        // 调用api接口，发送数据到微信
        api.sendReq(req)
    }

    /**
     * 四、视频类型分享示例
     * WXVideoObject （WXMediaMessage.IMediaObject 的派生类，用于描述一个视频对象）
     *
     * videoUrl	        String	视频链接	                    限制长度不超过 10KB
     * videoLowBandUrl	String	供低带宽的环境下使用的视频链接	限制长度不超过 10KB
     *
     * 注意：videoUrl 和 videoLowBandUrl 不能同时为空
     */
    fun shareToWXByVideo(entity: ShareEntity, scene: Scene = Scene.SESSION) {
        // 初始化一个 WXVideoObject，填写url
        val video = WXVideoObject()
        video.videoUrl = entity.videoLocalUrl

        // 用 WXVideoObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage()
        msg.mediaObject = video
        msg.title = entity.title
        msg.description = entity.summary
        fillThumbData(entity, msg)

        val req = SendMessageToWX.Req()
        req.scene = scene.value
        req.transaction = buildTransaction("video")
        req.message = msg
//        req.userOpenId = getUserOpenId()

        // 调用api接口，发送数据到微信
        api.sendReq(req)
    }

    /**
     * 五、网页类型分享示例
     * WXWebpageObject （WXMediaMessage.IMediaObject 的派生类，用于描述一个网页对象）
     *
     * webpageUrl	String	html 链接	限制长度不超过 10KB
     */
    fun shareToWXByWeb(entity: ShareEntity, scene: Scene = Scene.SESSION) {
        // 初始化一个 WXWebpageObject，填写url
        val web = WXWebpageObject()
        web.webpageUrl = entity.targetUrl

        // 用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage()
        msg.mediaObject = web
        msg.title = entity.title
        msg.description = entity.summary
        fillThumbData(entity, msg)

        val req = SendMessageToWX.Req()
        req.scene = scene.value
        req.transaction = buildTransaction("webpage")
        req.message = msg
//        req.userOpenId = getUserOpenId()

        // 调用api接口，发送数据到微信
        api.sendReq(req)
    }

    /**
     * 六、小程序类型分享示例
     * WXMiniProgramObject （WXMediaMessage.IMediaObject 的派生类，用于描述一个小程序对象）
     *
     * webpageUrl	    String	兼容低版本的网页链接	        限制长度不超过 10KB
     * userName	        String	小程序的原始 id	            小程序原始 ID 获取方法：登录小程序管理后台-设置-基本设置-帐号信息
     * path	            String	小程序的 path	            小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
     * withShareTicket	boolean	是否使用带 shareTicket 的分享	通常开发者希望分享出去的小程序被二次打开时可以获取到更多信息，
     * 例如群的标识。可以设置 withShareTicket 为 true，当分享卡片在群聊中被其他用户打开时，可以获取到 shareTicket，用于获取更多分享信息。
     * 详见小程序获取更多分享信息 ，最低客户端版本要求：6.5.13
     * miniprogramType	int	    小程序的类型，默认正式版
     * 正式版: WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;
     * 测试版: WXMiniProgramObject.MINIPROGRAM_TYPE_TEST;
     * 预览版: WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW
     *
     * 注意：
     * 发起分享的 App 与小程序属于同一微信开放平台帐号。 支持分享小程序类型消息至会话，暂不支持分享至朋友圈。
     * 若客户端版本低于 6.5.6 或在 iPad 客户端接收，小程序类型分享将自动转成网页类型分享。
     * 发者必须填写网页链接字段，确保低版本客户端能正常打开网页链接。
     */
    fun shareToWXByMiniProgram(entity: ShareEntity) {
        // 初始化一个 WXMiniProgramObject，填写url
        val miniProgram = WXMiniProgramObject()
        miniProgram.webpageUrl = entity.targetUrl
        // 正式版:0，测试版:1，体验版:2
        miniProgram.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE
        // 小程序原始id
        miniProgram.userName = entity.miniProgramAppId
        //小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
        miniProgram.path = entity.miniProgramPath

        // 用 WXMiniProgramObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage()
        msg.mediaObject = miniProgram
        // 小程序消息title
        msg.title = entity.title
        // 小程序消息desc
        msg.description = entity.summary
        // 小程序消息封面图片，小于128k
        entity.miniProgramImageByteArray?.run {
            msg.thumbData = this
        }

        val req = SendMessageToWX.Req()
        req.scene = SendMessageToWX.Req.WXSceneSession // 目前只支持会话
        req.transaction = buildTransaction("miniProgram")
        req.message = msg

        // 调用api接口，发送数据到微信
        api.sendReq(req)
    }

    /**
     * 填充分享缩略图数据
     */
    private fun fillThumbData(entity: ShareEntity, msg: WXMediaMessage) {
        entity.thumbImageByteArray?.run {
            msg.thumbData = this
        } ?: entity.autoThumbImageByteArray?.apply {
            msg.thumbData = this
        }
    }

//    /**
//     * 须要对图片进行处理,不然微信会在log中输出thumbData搜检错误
//     */
//    fun Bitmap.toByteArray(isRecycle: Boolean): ByteArray {
//        val size = 80
//        val localBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
//        val localCanvas = Canvas(localBitmap)
//        val w: Int = if (height > width) width else height
//
//        while (true) {
//            localCanvas.drawBitmap(this, Rect(0, 0, w, w), Rect(0, 0, size, size), null)
//            if (isRecycle) {
//                recycle()
//            }
//            val bos = ByteArrayOutputStream()
//            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
//            localBitmap.recycle()
//            val arrayOfByte = bos.toByteArray()
//            try {
//                bos.close()
//                return arrayOfByte
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    /**
     * 构建事务ID
     */
    private fun buildTransaction(tag: String): String {
        return "$tag - ${System.currentTimeMillis()}"
    }

    enum class Scene(val value: Int) {
        /**
         * 微信
         */
        SESSION(SendMessageToWX.Req.WXSceneSession),

        /**
         * 朋友圈
         */
        TIMELINE(SendMessageToWX.Req.WXSceneTimeline),

        /**
         * 收藏
         */
        FAVORITE(SendMessageToWX.Req.WXSceneFavorite),

        /**
         * 指定联系人
         */
        SPECIFIED_CONTACT(SendMessageToWX.Req.WXSceneSpecifiedContact)
    }
}