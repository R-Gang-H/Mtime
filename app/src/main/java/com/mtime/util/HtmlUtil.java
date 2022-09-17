package com.mtime.util;

/**
 * Created by yinguanping on 2017/5/24.
 */

public interface HtmlUtil {

    String getHtml(String content, MakeHtmlCallback callback);

    String getHtml(String content);

    interface Html {
        void append(String htmlText);
    }

    interface MakeHtmlCallback {

        void onBeforeAddContent(Html html);

        void onAfterAddContent(Html html);
    }

    abstract class HtmlMaker implements HtmlUtil, Html {

        private final StringBuilder html = new StringBuilder();

        @Override
        public final String getHtml(String content) {
            return getHtml(content, null);
        }

        @Override
        public final String getHtml(String content, MakeHtmlCallback callback) {
            html.delete(0, html.length());

            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n");

            html.append("<head>\n");
            html.append("<meta charset=\"utf-8\">\n");
            html.append("<meta name=\"viewport\" content=\"width=device-width, minimum-scale=1, maximum-scale=1, user-scalable=no\">\n");
            html.append("<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\" />\n");
            html.append("<meta content=\"telephone=no\" name=\"format-detection\" />\n");
            buildHeadHtml();
            html.append("</head>\n");

            html.append("<body>\n");
            buildBodyHtml(content, callback);
            html.append("</body>\n");

            html.append("</html>");
            return html.toString();
        }

        abstract void buildHeadHtml();

        final void buildBodyHtml(String content, MakeHtmlCallback callback) {
            html.append("<section class=\"").append(sectionClass()).append("\">\n");
            html.append("<div class=\"").append(divClass()).append("\">\n");

            buildBeforeContentHtml();
            if (callback != null) {
                callback.onBeforeAddContent(this);
            }
            html.append(content);
            if (callback != null) {
                callback.onAfterAddContent(this);
            }
            buildAfterContentHtml();

            html.append("</div>\n");
            html.append("</section>\n");

            buildBodyScriptHtml();
        }

        void addArticleBridgeJs() {
            addJs("Player/article_bridge.js");
        }

        void addArticleJs() {
            addJs("Player/article.js");
        }

        void addJs(String name) {
            html.append("<script src=\"file:///android_asset/").append(name).append("\"></script>\n");
        }

        void addCss(String name) {
            html.append("<link href=\"file:///android_asset/").append(name).append("\" rel=\"stylesheet\" type=\"text/css\" />\n");
        }

        String sectionClass() {
            return "m_bg_white movietxt";
        }

        abstract String divClass();

        abstract void buildBodyScriptHtml();

        void buildBeforeContentHtml() {
        }

        void buildAfterContentHtml() {
        }

        @Override
        public final void append(String text) {
            html.append(text);
        }
    }

    // 文章详情 h5
    HtmlUtil ARTICLE = new HtmlMaker() {

        @Override
        void buildHeadHtml() {
            addCss("Player/article_style.css");
        }

        @Override
        String divClass() {
            return "txtmid";
        }

        @Override
        void buildBodyScriptHtml() {
            addArticleBridgeJs();
            addArticleJs();
        }

    };

    // 话题详情 h5
    HtmlUtil TOPIC = new HtmlMaker() {

        @Override
        void buildHeadHtml() {
            addCss("topic/topic_style.css");
        }

        @Override
        String divClass() {
            return "topic_content";
        }

        @Override
        void buildBodyScriptHtml() {
            addArticleBridgeJs();
            addArticleJs();
        }

    };

    // 帖子详情 h5
    HtmlUtil POST = new HtmlMaker() {

        @Override
        void buildHeadHtml() {
            addCss("Player/article_style.css");
            addCss("post/ugc_imgs_style.css");
        }

        @Override
        String divClass() {
            return "txtmid";
        }

        @Override
        void buildBodyScriptHtml() {
            addArticleBridgeJs();
            addJs("post/ugc_detail_imgs.js");
            addArticleJs();
        }

    };

    // 回帖详情 h5
    HtmlUtil POST_REPLY = new HtmlMaker() {

        @Override
        void buildHeadHtml() {
            addCss("post/post_reply_detail.css");
        }

        @Override
        String divClass() {
            return "txtmid";
        }

        @Override
        void buildBodyScriptHtml() {
            addArticleBridgeJs();
            addArticleJs();
        }

    };

}
