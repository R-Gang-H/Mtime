package com.kotlin.android.publish.component.widget.article

import com.kotlin.android.publish.component.widget.article.content.IContent

/**
 *
 * Created on 2022/3/22.
 *
 * @author o.s
 */
class Article {
    private val paragraphList by lazy {
        ArrayList<IContent>()
    }

    fun addContent(content: IContent) {
        paragraphList.add(content)
    }
}