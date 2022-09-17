package com.kotlin.android.qb.webview

import android.content.Context
import android.util.AttributeSet
import com.kotlin.android.app.router.ext.parseAppLink
import com.kotlin.android.js.sdk.BuildConfig
import com.kotlin.android.js.sdk.SDKWarp
import com.kotlin.android.js.sdk.callback.JsCallback
import com.kotlin.android.js.sdk.entity.JsEntity

import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ext.showShareDialog
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import org.jetbrains.anko.runOnUiThread


/**
 * 创建者: zl
 * 创建时间: 2020/8/3 11:10 AM
 * 描述:
 */
open class X5WebView : WebView {
    val sdkWarp: SDKWarp = SDKWarp(context)

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        setWebAttribute()
        //测试环境打开调试功能
        if (BuildConfig.DEBUG) {
            android.webkit.WebView.setWebContentsDebuggingEnabled(true)
        } else {
            android.webkit.WebView.setWebContentsDebuggingEnabled(false)
        }
        this.webViewClient = client
        this.view.isClickable = true
        initJsSdk()
    }

    private fun initJsSdk() {
        this.addJavascriptInterface(sdkWarp, "mtime")
        object : JsCallback.AppLinkCallback {
            override fun appLink(data: JsEntity.AppLinkEntity.DataBean?) {
                data?.applinkData?.apply {
                    parseAppLink(context, this)
                }
            }
        }.also { sdkWarp.appLinkCallback = it }
        sdkWarp.imageBrowserCallback = object : JsCallback.ImageBrowserCallback {
            override fun imageBrowser(data: JsEntity.ImageBrowserEntity.DataBean?) {
                val provider = getProvider(IMainProvider::class.java)
                provider?.startPhotoDetailActivity(data?.photoImageUrls
                        ?: arrayListOf(), data?.currentImageIndex ?: 0)
            }
        }

        sdkWarp.shareCallback = object : JsCallback.ShareCallback {
            override fun share(data: JsEntity.ShareEntity.DataBean?) {
                val shareEntity = ShareEntity(
                        title = data?.title, summary = data?.summary, targetUrl = data?.url
                ).apply {
                    imageUrl = data?.pic
                }
                context.runOnUiThread {
                    showShareDialog(
                            shareEntity = shareEntity
                    )
                }
            }
        }

        /**
         * 子类去实现
         */
//        sdkWarp.videoPlayerCallback = object : JsCallback.VideoPlayerCallback {
//            override fun videoPlay(entity: VideoPlayerEntity) {
//                entity.e()
//            }
//        }
    }

    /**
     * 设置WebSetting属性
     */
    private fun setWebAttribute() {
        val webSetting = this.settings
        webSetting.apply {
            /**js支持相关*/
            javaScriptEnabled = true //是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
            javaScriptCanOpenWindowsAutomatically = true //设置js可以直接打开窗口，如window.open()，默认为false

            /**设置页面布局,自动根据手机分辨率缩放*/
            useWideViewPort = true
            loadWithOverviewMode = true

            /**页面缩放支持*/
            setSupportZoom(true) //仅支持双击缩放，不支持触摸缩放（android4.0以上）
            builtInZoomControls = false //是否显示缩放按钮，默认false

            /**设置WebView中加载页面字体变焦百分比，默认100，整型数。*/
            textZoom = 100

            /**是否允许WebView度超出以概览的方式载入页面，默认false。
             * 即缩小内容以适应屏幕宽度。该项设置在内容宽度超出WebView控件的宽度时生效.
             * 例如当getUseWideViewPort() 返回true时*/
            loadWithOverviewMode = true
            useWideViewPort = true

            /**设置WebView是否支持多窗口。默认false;如果设置为true，主程序要实现onCreateWindow(WebView, boolean, boolean, Message)。*/
            setSupportMultipleWindows(false)

            setAppCacheEnabled(true)       //是否使用缓存
            /**设置应用缓存文件的路径。为了让应用缓存API可用，此方法必须传入一个应用可写的路径。该方法只会执行一次，重复调用会被忽略*/
//            setAppCachePath(getDir("app_cache", 0).path)

            /**Https网页要加载http图片*/
            android.webkit.WebView.enableSlowWholeDocumentDraw()
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

            /**DOM存储API是否可用，默认false*/
            domStorageEnabled = true
            /**
             * H5那边说要用到这个字段
             */
            setUserAgent("MTimeApiHeader")

            //webview 中视频自动播放
            settings.mediaPlaybackRequiresUserGesture = false
        }
    }

    /**
     * X5左上角标识
     */
//    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
//        val ret = super.drawChild(canvas, child, drawingTime)
//        canvas.save()
//        val paint = Paint()
//        paint.color = 0x7fff0000
//        paint.textSize = 24f
//        paint.isAntiAlias = true
//        if (x5WebViewExtension != null) {
//            canvas.drawText(this.context.packageName + "-pid:"
//                    + Process.myPid(), 10f, 50f, paint)
//            canvas.drawText(
//                    "X5  Core:" + QbSdk.getTbsVersion(this.context), 10f, 100f, paint)
//        } else {
//            canvas.drawText(this.context.packageName + "-pid:"
//                    + Process.myPid(), 10f, 50f, paint)
//            canvas.drawText("Sys Core", 10f, 100f, paint)
//        }
//        canvas.drawText(Build.MANUFACTURER, 10f, 150f, paint)
//        canvas.drawText(Build.MODEL, 10f, 200f, paint)
//        canvas.restore()
//        return ret
//    }

    private val client: WebViewClient = object : WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

}