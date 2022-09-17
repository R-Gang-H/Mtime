package com.kotlin.android.ugc.web.component.html


interface MakeHtmlCallback {
    fun onBeforeAddContent(html: Html)

    fun onAfterAddContent(html: Html)
}