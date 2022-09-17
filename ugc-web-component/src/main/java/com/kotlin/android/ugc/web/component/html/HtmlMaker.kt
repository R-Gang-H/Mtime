package com.kotlin.android.ugc.web.component.html

import com.kotlin.android.app.data.entity.community.content.CommunityContent

abstract class HtmlMaker : HtmlUtil, Html {
    private val html: StringBuilder = StringBuilder()
    var relMovie: CommunityContent.RoMovie? = null//相关影片



    override fun getHtml(content: String, callback: MakeHtmlCallback?): String {
        html.run {
            delete(0, this.length)
            append("<!DOCTYPE html>\n")
            append("<html>\n")

            append("<head>\n")
            append("<meta charset=\"utf-8\">\n")
            append("<meta name=\"viewport\" content=\"width=device-width, minimum-scale=1, maximum-scale=1, user-scalable=no\">\n")
            append("<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\" />\n")
            append("<meta content=\"telephone=no\" name=\"format-detection\" />\n")
            buildHeadHtml()
            html.append("</head>\n")

            html.append("<body>\n")
            buildBodyHtml(content, callback)
            html.append("</body>\n")

            html.append("</html>")
        }
        return html.toString()

    }

    fun buildBodyHtml(content: String, callback: MakeHtmlCallback?) {
        html.let {
            append("<section class=\"")
            append(sectionClass())
            append("\">\n")
            append("<div class=\"")
            append(divClass())
            append("\">\n")

            buildBeforeContentHtml()
            callback?.onBeforeAddContent(this)
            html.append(content)
            callback?.onAfterAddContent(this)
            buildAfterContentHtml()

            html.append("</div>\n")
            html.append("</section>\n")


            buildBodyScriptHtml()
        }
    }


    override fun append(htmlText: String) {
        html.append(htmlText)
    }


    open fun addArticleBridgeJs() {
        addJs("Player/article_bridge_new.js")
    }

    open fun addArticleJs() {
        addJs("Player/article_new.js")
    }

    /**
     * ugc详情计算字体大小
     */
    open fun addScreenJs(){
        html.append("<script type=\"text/javascript\">\n" +
                "        (function (doc, win) {\n" +
                "            var docEl = doc.documentElement || doc.body,\n" +
                "                resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',\n" +
                "                recalc = function () {\n" +
                "                    var clientWidth = docEl.clientWidth;\n" +
                "                    if (!clientWidth) return;\n" +
                "                    docEl.style.fontSize = 100 * (clientWidth / 750) + 'px';\n" +
                "                };\n" +
                "            recalc();\n" +
                "            if (!doc.addEventListener) return;\n" +
                "            win.addEventListener(resizeEvt, recalc, false);\n" +
                "        })(document, window)\n" +
                "        \n" +
                "    </script>")
    }

    open fun addJs(name: String?) {
        html.append("<script src=\"file:///android_asset/").append(name).append("\"></script>\n")
    }

    open fun addCss(name: String?) {
        html.append("<link href=\"file:///android_asset/").append(name).append("\" rel=\"stylesheet\" type=\"text/css\" />\n")
    }

    abstract fun buildBodyScriptHtml()

    abstract fun buildHeadHtml()

    open fun buildBeforeContentHtml() {}

    open fun buildAfterContentHtml() {}

    open fun sectionClass(): String = "m_bg_white movietxt"
    abstract fun divClass(): String

}