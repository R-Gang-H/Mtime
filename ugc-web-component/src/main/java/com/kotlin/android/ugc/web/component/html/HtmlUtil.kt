package com.kotlin.android.ugc.web.component.html


interface HtmlUtil {
    fun getHtml(content: String, callback: MakeHtmlCallback? = null): String

}