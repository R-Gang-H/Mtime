package com.kotlin.android.share.entity

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.core.graphics.drawable.toBitmap
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.common.CommonShare
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.mtime.ktx.ext.saveShareImage
import com.kotlin.android.share.ShareType
import com.tencent.connect.share.QQShare
import java.io.Serializable

/**
 *
 * Created on 2020/6/22.
 *
 * @author o.s
 */
data class ShareEntity(
    var shareType: ShareType = ShareType.SHARE_IMAGE_TEXT, // 分享类型
    var title: String? = null, // 分享标题
    var summary: String? = null, // 分享摘要信息
    var summaryQQ: String? = null, // 需要单独指定QQ摘要信息时赋值
    var wbDesc: String? = null, // 微博描述信息 同 summary ，一般会拼接超链接
    var targetUrl: String? = null, // 跳转目标URL
    var imageLocalUrl: String? = null, // 本地图片地址
    var audioUrl: String? = null, // 音频URL
    var appName: String? = null, // 当前app名称，手Q客户端顶部，替换"返回"按钮文字，如果为空，用"返回"代替
    var arkInfo: String? = null,
    var site: String? = null,
    var miniProgramAppId: String? = null, // 小程序appID
    var miniProgramPath: String? = null, // 小程序页面路径
    var miniProgramType: String? = null, // 小程序类型

    /**针对同一个弹框不同类型的分享添加属性*/
    // 独立的微信分享类型
    var shareTypeWX: ShareType? = null,
    // 独立的微博分享类型
    var shareTypeWB: ShareType? = null,
    // 独立的QQ分享类型
    var shareTypeQQ: ShareType? = null,

    /**
     * 分享额外选项，两种类型可选（默认：不自动打开，也不隐藏）
     * [QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN] 分享时自动打开分享到QZone的对话框
     * [QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE] 分享时隐藏分享到QZone按钮
     */
    var flag: Int? = null, // 分享额外选项，两种类型可选（默认0）
    var imageUrls: ArrayList<String>? = null, // 分享到QQ空间的图片，最多9张
    var imageLocalUrls: ArrayList<String>? = null, // 发布说说的图片，只支持本地图片，可多张，<=9张图片为发表说说，>9图片为上传图片相册
    var videoLocalUrl: String? = null, // 发布视频，只支持本地地址，发表视频时必填，上传视频的大小控制在100M以内（普通会员100M，黄钻1G，大于1G直接报错）
    var extraScene: String? = null, // 区分分享场景，用于异化 feeds 点击行为和小尾巴展示
    var callBack: String? = null // 游戏自定义字段，点击分享消息回到游戏时会传给游戏
) : Serializable, ProguardRule {
    var thumbImageCallback: ((Boolean) -> Unit)? = null

    /**
     * 图片地址：
     * 同时保证 WX 分享需要加载 bitmap
     */
    var imageUrl: String? = null
        set(value) {
            field = value
            LogUtils.d("分享图片加载图片加载开始--$value")
            if (!TextUtils.isEmpty(value)) {
                loadImage(value, 120, 120, useProxy = true, allowHardware = false, onSuccess = {
                    LogUtils.d("图片加载成功--$value")
                    thumbImage = it.toShareBitmap()
                    thumbImageCallback?.invoke(true)
                }, onError = {
                    LogUtils.d("图片加载失败--$value")
                }, onStart = {
                    LogUtils.d("图片加载开始--$value")
                }


                )
            }
        }
        get() {
            return field ?: imageLocalUrl
        }

    /**
     * 小程序图片地址：
     * 同时保证小程序分享需要加载 bitmap
     */
    var miniProgramImageUrl: String? = null
        set(value) {
            field = value
            if (!TextUtils.isEmpty(value)) {
                loadImage(value, useProxy = false, allowHardware = false) {
                    val b = it.toShareBitmap()
                    miniProgramImage = b
                }
            }
        }
        get() {
            return field ?: imageLocalUrl
        }

    /**
     * 设置分享本地Bitmap：
     * 1，并自动生成内部缩略图（可以不明确指定缩略图）
     * 2，保存大小合规的图片，并将 uri 赋值给 [imageLocalUrl]
     */
    var image: Bitmap? = null // 图片
        set(value) {
            field?.recycle()
            field = value
            imageByteArray = null
            imageWBByteArray = null
            autoThumbImageByteArray = null
            // 保存到本地将 uri 赋值给 imageLocalUrl
//            val path = value?.saveShareImage()?.run {
//                CoreApp.instance.getUriForPath(this)
//            }
            imageLocalUrl = value?.saveShareImage()
            if (shareType == ShareType.SHARE_IMAGE) {
                imageUrl = imageLocalUrl
            }
        }

    /**
     * 明确指定缩略图，需求确保与 [image] 是不同的Bitmap
     * 微信缩略图大小限制 32k
     */
    var thumbImage: Bitmap? = null // 缩略图
        set(value) {
            field?.recycle()
            field = value
            thumbImageByteArray = null
        }

    /**
     * 小程序消息封面图片，小于128k (微信分享)
     */
    var miniProgramImage: Bitmap? = null
        set(value) {
            field?.recycle()
            field = value
            miniProgramImageByteArray = null
        }

    /**
     * 分享图片 ByteArray （微信分享使用该字段，限制10M）
     */
    var imageByteArray: ByteArray? = null
        private set
        get() {
            return if (field != null) {
                field
            } else {
                field = image.compressByteArrayWithShareLimit(
                    limit = SHARE_BITMAP_LIMIT,
                    isRecycle = false
                )
                field
            }
        }

    /**
     * 分享图片 ByteArray （微博分享使用该字段，限制2M）
     */
    var imageWBByteArray: ByteArray? = null
        private set
        get() {
            return if (field != null) {
                field
            } else {
                field = image.compressByteArrayWithShareLimit(
                    limit = SHARE_BITMAP_LIMIT_WB,
                    isRecycle = false
                )
                field
            }
        }

    /**
     * 自动生成的内部缩略图，不可外部修改（限制32k）
     */
    var autoThumbImageByteArray: ByteArray? = null
        private set
        get() {
            return if (field != null) {
                field
            } else {
                field = image.compressByteArrayWithShareLimit(
                    limit = SHARE_THUMB_BITMAP_LIMIT,
                    isRecycle = false
                )
                field
            }
        }

    /**
     * 缩略图 ByteArray （微信分享使用该字段）（限制32k）
     */
    var thumbImageByteArray: ByteArray? = null
        private set
        get() {
            return if (field != null) {
                field
            } else {
                field = thumbImage.compressByteArrayWithShareLimit(
                    limit = SHARE_THUMB_BITMAP_LIMIT,
                    isRecycle = false
                )
                field
            }
        }

    /**
     * 小程序消息封面图片，小于128k (微信分享)
     */
    var miniProgramImageByteArray: ByteArray? = null
        private set
        get() {
            return if (field != null) {
                field
            } else {
                field = miniProgramImage.compressByteArrayWithShareLimit(
                    limit = SHARE_MINI_PROGRAM_BITMAP_LIMIT,
                    isRecycle = false
                )
                field
            }
        }

    fun recycle() {
        "分享回收资源".i()
        image?.recycle()
        thumbImage?.recycle()
        miniProgramImage?.recycle()
        imageByteArray = null
        imageWBByteArray = null
        autoThumbImageByteArray = null
        thumbImageByteArray = null
        miniProgramImageByteArray = null
    }

    companion object {
        fun getDefault() = ShareEntity(
            title = "分享测试标题",
            summary = "分享摘要信息",
            wbDesc = "分享摘要信息",
            targetUrl = "https://www.baidu.com/",
            imageLocalUrl = "",
            audioUrl = "",
            appName = "时光网"
        ).apply {
//            imageUrl = "http://a3.att.hudong.com/14/75/01300000164186121366756803686.jpg"
//            image = CoreApp.instance.getDrawable(R.mipmap.ic_wechat)?.toBitmap()
//            thumbImage = CoreApp.instance.getDrawable(R.mipmap.ic_sina)?.toBitmap()
        }

        fun build(share: CommonShare): ShareEntity {
            var title: String? = null
            var summary: String? = null
            var wbDesc: String? = null
            var targetUrl: String? = null
            var imageUrl: String? = null

            share.qq?.let {
                title = it.title
                summary = it.desc
                targetUrl = it.url
                imageUrl = it.img
            }

            if (TextUtils.isEmpty(imageUrl)) {
                share.weixin?.let {
                    title = it.title
                    summary = it.desc
                    targetUrl = it.url
                    imageUrl = it.img
                }
            }

            if (TextUtils.isEmpty(imageUrl)) {
                share.weibo?.let {
                    summary = it.desc
                    imageUrl = it.img
                }
            }

            share.weibo?.let {
                wbDesc = it.desc
                imageUrl = it.img
            }

            if (TextUtils.isEmpty(imageUrl)) {
                share.mtime?.let {
                    summary = it.desc
                    imageUrl = it.img
                }
            }

            return ShareEntity(
                title = title,
                summary = summary,
                wbDesc = wbDesc,
                targetUrl = targetUrl,
                appName = "时光网"
            ).apply {
                this.imageUrl = imageUrl
            }
        }
        fun buildWithoutImageUrl(share: CommonShare,imageCallback:((String)->Unit)? = null): ShareEntity {
            var title: String? = null
            var summary: String? = null
            var wbDesc: String? = null
            var targetUrl: String? = null
            var imageUrl: String? = null

            share.qq?.let {
                title = it.title
                summary = it.desc
                targetUrl = it.url
                imageUrl = it.img
            }

            if (TextUtils.isEmpty(imageUrl)) {
                share.weixin?.let {
                    title = it.title
                    summary = it.desc
                    targetUrl = it.url
                    imageUrl = it.img
                }
            }

            if (TextUtils.isEmpty(imageUrl)) {
                share.weibo?.let {
                    summary = it.desc
                    imageUrl = it.img
                }
            }

            share.weibo?.let {
                wbDesc = it.desc
                imageUrl = it.img
            }

            if (TextUtils.isEmpty(imageUrl)) {
                share.mtime?.let {
                    summary = it.desc
                    imageUrl = it.img
                }
            }
            imageCallback?.invoke(imageUrl.orEmpty())

            return ShareEntity(
                title = title,
                summary = summary,
                wbDesc = wbDesc,
                targetUrl = targetUrl,
                appName = "时光网"
            )

        }
    }

    fun build() {

    }

}