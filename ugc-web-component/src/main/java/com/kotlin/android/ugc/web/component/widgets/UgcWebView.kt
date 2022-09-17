package com.kotlin.android.ugc.web.widgets

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.kotlin.android.api.config.AppConfig
import com.kotlin.android.api.header.getToken
import com.kotlin.android.core.ext.versionName
import com.kotlin.android.core.statusbar.StatusBarUtils
import com.kotlin.android.js.sdk.callback.JsCallback
import com.kotlin.android.js.sdk.entity.JsEntity
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ugc.web.component.html.ARTICLE
import com.kotlin.android.ugc.web.component.html.POST_COMMENT
import com.kotlin.android.ugc.web.component.widgets.NoBlankScrollWebView
import com.tencent.smtt.export.external.interfaces.*
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import java.net.URL

/**
 * Created by lushan on 2020/8/6
 * UGC、文章详情webView
 */
open class UgcWebView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var x5WebView: NoBlankScrollWebView? = NoBlankScrollWebView(context)
    private var mUrl: String = ""
    private var localRect = Rect()

    //    视频回调
    private var videoListener: (( JsEntity.VideoPlayerEntity.DataBean?) -> Unit)? = null
    private var loadFinishListener: ((Any?) -> Unit)? = null

    init {
        removeAllViews()
        val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.TOP
        addView(x5WebView, layoutParams)
        x5WebView?.apply {
            initJsSdk()
            initWebView()
        }

    }


    fun getWebView(): WebView? = x5WebView

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destory() {
        removeView(x5WebView)
        x5WebView?.apply {
            stopLoading()
            settings?.javaScriptEnabled = false
            clearHistory()
            clearView()
            this.removeAllViews()
            this.destroy()
        }
        x5WebView = null

    }

    fun notScroll() {
        x5WebView?.apply {
            settings?.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
//            settings?.loadWithOverviewMode = true
//            settings?.useWideViewPort = true
//            settings?.builtInZoomControls = true
            isVerticalScrollBarEnabled = false
            setVerticalScrollbarOverlay(false)
            setHorizontalScrollbarOverlay(false)
            isHorizontalScrollBarEnabled = false


        }

    }

    fun setData(content: String, ugcType: Int, videoListener: (( JsEntity.VideoPlayerEntity.DataBean?) -> Unit)? = null,
                loadFinishListener: ((Any?) -> Unit)? = null,isPostComment:Boolean = false) {
        this.videoListener = videoListener
        this.loadFinishListener = loadFinishListener
        if (TextUtils.isEmpty(content).not()) {
            x5WebView?.apply {
                visible()
//                initWebView()
            }.also {
                val webContent = getWebContent(content,isPostComment)
//                LogUtils.e("正则替换","替换前："+webContent)
////                去掉html中的style
//                val relContent = webContent.replace(Regex("style=\".*?\""),"")
//                LogUtils.e("正则替换","替换后："+relContent)
                it?.loadDataWithBaseURL("file:///android_asset/",webContent , "text/html", "UTF-8", null)
            }
        } else {
            x5WebView?.gone()
        }
    }



    private fun WebView.initWebView() {
        settings.let {
            it.javaScriptEnabled = true
            it.javaScriptCanOpenWindowsAutomatically = true
            it.allowFileAccess = true
            it.defaultTextEncodingName = "UTF-8"
            it.setSupportZoom(false)
            it.setSupportMultipleWindows(true)
            it.builtInZoomControls = false
            it.displayZoomControls = false
            it.setSupportMultipleWindows(true)
            it.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            it.cacheMode = WebSettings.LOAD_DEFAULT
            it.databaseEnabled = true
            it.domStorageEnabled = true
            it.pluginState = WebSettings.PluginState.ON_DEMAND
            it.setAppCacheMaxSize(Long.MAX_VALUE)
//            it.textZoom = 100
            it.useWideViewPort = false
            it.loadWithOverviewMode = false
//            it.loadWithOverviewMode = true
//            it.setGeolocationEnabled(true)
        }
        val sb = "${settings.userAgentString} Mtime_Android_Showtime${getCommonUAParams(0)}"
        settings.userAgentString = sb
//        this.requestFocus()
//        scrollBarStyle = 0


        webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
                p3?.confirm()
                return true
            }

            override fun onJsConfirm(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
                p3?.confirm()
                return true
            }

            override fun onJsBeforeUnload(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
                return true
            }

            override fun onJsPrompt(p0: WebView?, p1: String?, p2: String?, p3: String?, p4: JsPromptResult?): Boolean {
                return true
            }
        }
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(p0: WebView?, p1: String?) {
                super.onPageFinished(p0, p1)
//                p0?.loadUrl("javascript:topicCallback.resize(document.body.scrollHeight)")
//                "onPageFinished==webView加载结束".e()
                if (TextUtils.isEmpty(mUrl)) {
                    mUrl = p1.orEmpty()
                }
                loadFinishListener?.invoke(null)
            }

            override fun shouldInterceptRequest(p0: WebView?, p1: WebResourceRequest?): WebResourceResponse? {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (null != p0 && null != p1 && null != p1.url) {
                        val resultUrl = p1.url.toString()
                        resultUrl.d()
                        return super.shouldInterceptRequest(p0, p1)
//                        if (isVisit(resultUrl)) {
//                            return super.shouldInterceptRequest(p0, p1)
//                        }
                    }
                }
                return null
            }

            override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
                p1?.proceed()
            }

            override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    return isVisit(p1.orEmpty()).not()
//                }
                return true
            }
        }
    }

    private fun isVisit(url: String): Boolean {
//        todo 是否判断白名单
        if (TextUtils.isEmpty(url)) {
            return false
        }
        try {
            val urls = URL(url)
            if (TextUtils.isEmpty(urls.host)) {
                return false
            }

//            todo 添加白名单判断
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            if (url.startsWith("goto") || url.startsWith("open") || url.startsWith("close")) {
                return true
            }
        }

        return false

    }

    private fun getWebContent(content: String,isPostComment:Boolean): String {
        val type = if (isPostComment) POST_COMMENT else ARTICLE
        return type.getHtml(content)
    }

    private fun getCommonUAParams(height: Int): String {
        val sb = StringBuilder()
        sb.append("/channel_").append(AppConfig.instance.channel).append("/").append(versionName)
                .append("(WebView Width ").append(StatusBarUtils.screenWidthPx)
                .append(" Height ").append(StatusBarUtils.screenHeightPx - height).append(")")
                .append(" (Device ").append(Build.MODEL).append(")")
                .append(" (Token ").append(getToken()).append(")")
                .append(" (UDID ").append(getToken()).append(")")
                .append(" (Brand ").append(Build.BRAND).append(")")
        return sb.toString()
    }

    private fun initJsSdk() {
        x5WebView?.sdkWarp?.videoPlayerCallback = object : JsCallback.VideoPlayerCallback{
            override fun videoPlay(data: JsEntity.VideoPlayerEntity.DataBean?) {
                videoListener?.invoke(data)
            }
        }

    }
}