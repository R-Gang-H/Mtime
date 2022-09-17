package com.kotlin.android.app.api.upload

import androidx.databinding.adapters.ViewGroupBindingAdapter.setListener
import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.provider.IUserProvider

/**
 * create by lushan on 2022/4/13
 * des:腾讯云点播上传视频
 **/
object TencentUploadManager {
    private var publishMap:HashMap<String,TXUGCPublish?> = hashMapOf()
    private var userId: Long = 0L
    fun init(token: String) {
        if (publishMap[token] == null) {
            getProvider(IUserProvider::class.java) {
                val newUserId = getUserId()
                val publish = TXUGCPublish(
                    CoreApp.instance,
                    if (userId != newUserId) newUserId.toString() else userId.toString()
                )
                userId = newUserId
                publishMap[token] = publish
            }
        }


    }

    /**
     * 上传视频
     * @param videoPath 本地视频地址
     * @param token 云点播签名
     * @param listener 是否上传结束、是否上传成功、上传进度、视频id、上传到云点播的视频地址
     */
    fun upload(
        videoPath: String,
        token: String,
        listener: ((Boolean, Boolean, Double, String, String, String) -> Unit)? = null
    ) {
        init(token)
        //获取文件后缀名
        val lastIndexOf = videoPath.lastIndexOf(".")
        var prefName = if (lastIndexOf < 0) {
            ".mp4"
        } else {
            videoPath.substring(lastIndexOf)
        }

        publishMap[token]?.apply {
            setListener(object : TXUGCPublishTypeDef.ITXVideoPublishListener {
                override fun onPublishProgress(uploadBytes: Long, totalBytes: Long) {
                    if (totalBytes != 0L) {
                        listener?.invoke(false, false, uploadBytes.toDouble() / totalBytes, "", "", "")
                    }
                }

                override fun onPublishComplete(result: TXUGCPublishTypeDef.TXPublishResult?) {
                    listener?.invoke(
                        true,
                        result?.retCode == 0,
                        0.0,
                        result?.videoId.orEmpty(),
                        result?.videoURL.orEmpty(),
                        result?.coverURL.orEmpty()
                    )
                }

            })
            val params = TXUGCPublishTypeDef.TXPublishParam()
            params.signature = token
            params.videoPath = videoPath
            params.fileName = "video_${userId}_${System.currentTimeMillis()}${prefName}"//上传到腾讯云的视频文件名称，不填写默认用本地文件名
            params.enableHttps = true
            //上传视频
            publishVideo(params)
        }
    }


    fun cancelUpload(token: String) {
        publishMap[token]?.canclePublish()
    }

}