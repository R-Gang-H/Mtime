package com.kotlin.android.publish.component.widget.article.view.entity

import com.kotlin.android.publish.component.widget.article.xml.entity.Element

/**
 * 元素数据：图片、电影、影人、视频、超链接、富文本等段落元素的【根元素】
 *
 * Created on 2022/3/30.
 *
 * @author o.s
 */
interface IElementData {

    /**
     * 段落根元素
     */
    var element: Element
}