package com.kotlin.android.ugc.web.component.html



const val TYPE_ARTICLE = 1//文章
const val TYPE_TOPIC = 2//话题
const val TYPE_POST = 3//帖子
const val TYPE_POST_REPLY = 4//回帖详情


//文章详情H5
val ARTICLE = object : HtmlMaker() {
    override fun buildBodyScriptHtml() {
        addScreenJs()
        addArticleBridgeJs()
        addArticleJs()
    }

    override fun buildHeadHtml() {
        addCss("Player/article_style_new.css")
    }

    override fun divClass(): String = "txtmid"
}


//帖子评论详情
val POST_COMMENT = object :HtmlMaker(){
    override fun buildBodyScriptHtml() {
        addArticleBridgeJs()
        addArticleJs()
    }

    override fun buildHeadHtml() {
        addCss("Player/post_reply_detail_new.css")
    }

    override fun divClass(): String = "txtmid"

}