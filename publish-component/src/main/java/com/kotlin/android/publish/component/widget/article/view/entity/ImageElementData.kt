package com.kotlin.android.publish.component.widget.article.view.entity

import android.net.Uri
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.publish.component.widget.article.sytle.TextColor
import com.kotlin.android.publish.component.widget.article.view.safeGet
import com.kotlin.android.publish.component.widget.article.xml.entity.Element

/**
 * 图片卡片的数据模型，关联图片相关 Element 元素
 *
 * Created on 2022/3/30.
 *
 * @author o.s
 */
class ImageElementData : IElementData {

    /**
     * 描述文本元素
     */
    private var textElement: Element = Element()

    /**
     * 描述文本段落
     */
    private var pElement: Element = Element(
        tag = "p",
        style = TextColor.GRAY.style,
        editable = "false",
        items = arrayListOf(textElement)
    )

    /**
     * 图片元素
     */
    private var imgElement: Element = Element(
        tag = "img",
    )

    /**
     * 描述元素
     */
    private var figCaptionElement: Element = Element(
        tag = "figcaption",
        style = "width: 100%;",
        items = arrayListOf(pElement)
    )

    /**
     * 段落根元素
     */
    override var element: Element = Element(
        tag = "figure",
        clazz = "image",
        items = arrayListOf(imgElement, figCaptionElement),
    )
        set(value) {
            field = value
            value.apply {
                items?.find { it.tag == "img" }?.apply {
                    imgElement = this
                }
                items?.find { it.tag == "figcaption" }?.apply {
                    figCaptionElement = this
                    items?.find { it.tag == "p" }?.apply {
                        pElement = this
                        items.safeGet(0)?.apply {
                            textElement = this
                        }
                    }
                }
            }
        }

    /**
     * 设置图片信息
     */
    var photoInfo: PhotoInfo? = null
        set(value) {
            field = value
            value?.let {
                url = it.url
                uri = it.uri
                fileId = it.fileID
                imageFormat = it.imageFormat
            }
        }

    /**
     * 图片网络url（src）
     */
    var url: String? = null
        get() = imgElement.src
        set(value) {
            field = value
            imgElement.src = value
        }

    /**
     * 本地图片Uri，提升本地加载速度
     */
    var uri: Uri? = null

    /**
     * 图片上传文件ID
     */
    var fileId: String? = null
        get() = imgElement.fileId
        set(value) {
            field = value
            imgElement.fileId = value
        }

    /**
     * 图片格式
     */
    var imageFormat: String? = null
        get() = imgElement.imageFormat
        set(value) {
            field = value
            imgElement.imageFormat = value
        }

    /**
     * 图片描述文本
     */
    var text: CharSequence? = null
        get() = textElement.text
        set(value) {
            field = value
            textElement.text = value?.toString()
        }
}