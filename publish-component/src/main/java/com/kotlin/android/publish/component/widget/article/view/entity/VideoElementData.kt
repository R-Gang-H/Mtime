package com.kotlin.android.publish.component.widget.article.view.entity

import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.publish.component.bean.VideoUploadViewBean
import com.kotlin.android.publish.component.scope.UploadVideoScope
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import com.kotlin.android.publish.component.widget.selector.LocalMedia

/**
 * 视频卡片的数据模型，关联视频相关 Element 元素
 *
 * Created on 2022/3/30.
 *
 * @author o.s
 */
class VideoElementData : IElementData {

    /**
     * <source>
     */
    private var sourceElement: Element = Element(
        tag = "source",
    )

    /**
     * <video>
     */
    private var videoElement: Element = Element(
        tag = "video",
        items = arrayListOf(sourceElement)
    )

    /**
     * 段落根元素
     */
    override var element: Element = Element(
        tag = "p",
        items = arrayListOf(videoElement),
    )
        set(value) {
            field = value
            value.apply {
                items?.find { it.tag == "video" }?.apply {
                    videoElement = this
                    items?.find { it.tag == "source" }?.apply {
                        sourceElement = this
                    }
                }
            }
        }

    /**
     * 视频是否就绪
     */
    val isReady: Boolean
        get() = videoElement.videoId.isNullOrEmpty().not()

    /**
     * 视频是否错误
     */
    var isError: Boolean = false

    /**
     * 编辑回显封面图
     */
    val poster: String?
        get() = videoElement.poster

    /**
     * 上传视频路径
     */
    val videoPath: String?
        get() = localMedia?.realPath

    /**
     * 视频资源对象
     */
    var data: VideoUploadViewBean? = null
        set(value) {
            field = value
            "VideoElementData data:$value".e()
            value?.apply {
                videoElement.videoId = videoId.toString()
                videoElement.src = mediaUrl
                videoElement.poster = coverUrl
//                videoElement.width = width.toString() // 300
//                videoElement.height = height.toString() // 150
                videoElement.controls = "controls"
                videoElement.videoType = "3" // data-video-type:时光视频来源类型1.电影预告片2.自媒体3.媒资
//                videoElement.mceFragment = "mceFragment"
                sourceElement.src = mediaUrl
                sourceElement.type = "audio/ogg"
            }
        }

    var progress: ((Int) -> Unit)? = null

    /**
     * 待上传原始视频资源
     */
    var localMedia: LocalMedia? = null
        set(value) {
            field = value
            uploadMedia()
        }

    fun uploadMedia() {
        localMedia?.let { media ->
            if (!media.realPath.isNullOrEmpty()) {
                UploadVideoScope(
                    path = media.realPath.toString(),
                    width = media.width,
                    height = media.height,
                ).upload(
                    error = {
                        showToast("$it")
                        progress?.invoke(-2)
                        isError = true
                    },
                    netError = {
                        showToast(it)
                        progress?.invoke(-2)
                        isError = true
                    },
                    progress = progress
                ) {
                    isError = false
                    data = this
                }
            }
        }
    }
}