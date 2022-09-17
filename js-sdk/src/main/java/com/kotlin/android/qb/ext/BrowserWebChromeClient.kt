package com.kotlin.android.qb.ext

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Message
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.qb.webview.X5WebView
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebView

/**
 * create by lushan on 2021/11/11
 * des:
 **/
class BrowserWebChromeClient(
    var context: Activity,
    var mWebView: X5WebView? = null,
    var progressBar: ProgressBar? = null,
    var listener: ((ValueCallback<Array<Uri>>?) -> Unit)? = null
) : WebChromeClientExt() {
    companion object{
        const val REQUEST_SELECT_FILE = 200
    }
    private var x5CustomViewCallback: IX5WebChromeClient.CustomViewCallback? = null
    private var fullScreenContainer: FrameLayout? = null
    override fun onShowCustomView(p0: View?, p1: IX5WebChromeClient.CustomViewCallback?) {
        super.onShowCustomView(p0, p1)
        if (fullScreenContainer != null) {
            p1?.onCustomViewHidden()
            return
        }
        fullScreenContainer = FrameLayout(context).apply {
            setBackgroundColor(Color.BLACK)
        }
        x5CustomViewCallback = p1
        fullScreenContainer?.addView(p0)
        val frameLayout = context.window?.decorView as? FrameLayout
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        frameLayout?.addView(fullScreenContainer)

    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        fullScreenContainer ?: return
        val decorView = context.window?.decorView as? FrameLayout
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        fullScreenContainer?.removeAllViews()
        decorView?.removeView(fullScreenContainer)
        fullScreenContainer = null
        x5CustomViewCallback?.onCustomViewHidden()
        x5CustomViewCallback = null

    }

    override fun onReceivedTitle(p0: WebView?, p1: String?) {
        super.onReceivedTitle(p0, p1)
//            titleBar.setTitle(p1.orEmpty())
    }

    override fun onProgressChanged(p0: WebView?, p1: Int) {
        super.onProgressChanged(p0, p1)
        if (progressBar?.visibility == View.GONE) {
            showProgressView()
        }
        progressBar?.progress = p1
        /**
         * 进度到100，表示完全加载成功
         */
        if (p1 == 100) {
            dismissProgressView()
        }
    }


    /**
     * For Android  >= 5.0，低于5.0的不考虑了
     */
    override fun onShowFileChooser(
        p0: WebView?,
        p1: ValueCallback<Array<Uri>>?,
        p2: FileChooserParams?
    ): Boolean {
        listener?.invoke(p1)
        openFileChooseProcess()
        return true
    }

    /**
     * 避免因target 为blank导致电机无反应
     */
    override fun onCreateWindow(p0: WebView?, p1: Boolean, p2: Boolean, p3: Message?): Boolean {
        val newX5WebView = X5WebView(context, null).apply {
            webViewClient = object : WebViewClientExt() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    mWebView?.loadUrl(url)
                    return true
                }
            }
        }

        val transport = p3?.obj as? WebView.WebViewTransport
        transport?.webView = newX5WebView
        p3?.sendToTarget()
        return true
    }


    /**
     * 打开设备相册
     */
    private fun openFileChooseProcess() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }.also {
            context.startActivityForResult(
                Intent.createChooser(it, "File Chooser"),
                REQUEST_SELECT_FILE
            )
        }
    }

    private fun showProgressView() {
        AlphaAnimation(0f, 1.0f).apply {
            duration = 200
        }.also {
            progressBar?.apply {
                startAnimation(it)
                visible()
            }
        }
    }

    private fun dismissProgressView() {
        AlphaAnimation(1.0f, 0f).apply {
            duration = 200
            setAnimationListener(object : AnimationExt() {
                override fun onAnimationEnd(animation: Animation?) {
                    super.onAnimationEnd(animation)
                    progressBar?.gone()
                }
            })
        }.also {
            progressBar?.apply {
                startAnimation(it)
                visible()
            }
        }

    }


}