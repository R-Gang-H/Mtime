package com.kotlin.android.publish.component.widget.article.content.link

import android.text.SpannableStringBuilder
import com.kotlin.android.publish.component.widget.article.content.IContent
import com.kotlin.android.publish.component.widget.article.content.element.A

/**
 * 超链接（内容）
 *
 * Created on 2022/3/22.
 *
 * @author o.s
 */
class LinkContent : IContent {
    override val body: SpannableStringBuilder
        get() = SpannableStringBuilder().apply {
            if (url.isNotEmpty()) {
                append(A.start(url))
                append(text)
                append(A.end)
            }
        }

    /**
     * 图片上传后url
     */
    var url: CharSequence = ""

    /**
     * 图片上传后url
     */
    var text: CharSequence = ""

}