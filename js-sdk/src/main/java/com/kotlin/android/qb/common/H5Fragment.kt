package com.kotlin.android.qb.common

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.FrameLayout
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.AndroidBug5497Workaround
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.js.sdk.callback.JsCallback
import com.kotlin.android.js.sdk.databinding.FragH5Binding
import com.kotlin.android.js.sdk.entity.JsEntity
import com.kotlin.android.ktx.ext.KEY_DATA
import com.kotlin.android.ktx.ext.core.put
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.qb.JsManager
import com.kotlin.android.qb.base.BrowserViewModel
import com.kotlin.android.qb.ext.BrowserWebChromeClient
import com.kotlin.android.qb.ext.BrowserWebChromeClient.Companion.REQUEST_SELECT_FILE
import com.kotlin.android.qb.ext.WebViewClientExt
import com.kotlin.android.qb.ext.saveBitmapFromWebView
import com.kotlin.android.qb.webview.X5WebView
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.user.isLogin
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.CookieSyncManager
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/**
 * create by lushan on 2022/3/30
 * des:
 **/
class H5Fragment: BaseVMFragment<BrowserViewModel, FragH5Binding>() {
    protected var mWebView: X5WebView? = null
    protected var mEntity: BrowserEntity? = null
    private var mFilePathCallback: android.webkit.ValueCallback<Array<Uri>>? = null
    companion object{
        fun newInstance(entity: BrowserEntity): H5Fragment {
            val h5Fragment = H5Fragment()
            h5Fragment.arguments = Bundle().apply {
                put(KEY_DATA, entity)
            }
            return h5Fragment
        }
    }
    override fun initView() {
        mEntity = arguments?.getSerializable(KEY_DATA) as? BrowserEntity
        JsManager.initQbSdk(mActivity)
        initWebView()
        fillCookie(mEntity?.url.orEmpty())
        initEvent()
    }

    private fun initWebView() {
        mWebView = X5WebView(mActivity, null)
        mBinding?.webView?.apply {
            addView(
                mWebView, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }
        mWebView?.webViewClient = WebViewClientExt()
        mWebView?.webChromeClient = BrowserWebChromeClient(mActivity,mWebView,mBinding?.progressBar){
            mFilePathCallback = it
        }

        if (isStartEditProperty()) {
            AndroidBug5497Workaround.assistActivity(mActivity)
        }
        CookieSyncManager.createInstance(mActivity)
        CookieSyncManager.getInstance().sync()
    }


    private fun initEvent() {

        mWebView?.setOnLongClickListener {
            mWebView?.saveBitmapFromWebView(mActivity)
            false
        }

        mWebView?.sdkWarp?.handleGoBackCallback = object : JsCallback.HandleGoBackCallback {
            override fun handleGoBack(data: JsEntity.HandleGoBackEntity.DataBean?) {
                if (data?.isCloseWindow == true) {
                    mActivity.finish()
                }
            }
        }
    }


    private fun fillCookie(url: String?) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(mWebView, true)
        val cookie = cookieManager.getCookie(url)
        if (TextUtils.isEmpty(cookie).not()) {
            val split = cookie.split(";")
            split.forEach {
                val httpUrl = "https://api-m.mtime.cn/AccountDetail.api".toHttpUrlOrNull()
                if (it.contains("_mi_").not()) {
                    if (httpUrl != null && !TextUtils.isEmpty(it) && null != Cookie.parse(
                            httpUrl,
                            it
                        )
                    ) {
                        cookieManager.setCookie(url, it)
                    }
                }
            }
            cookieManager?.removeAllCookie()
            if (isLogin()) {
                val cookieByName =
                    com.kotlin.android.retrofit.cookie.CookieManager.instance.getCookieByName("_mi_")
                cookieManager.setCookie(
                    if (TextUtils.isEmpty(cookieByName?.domain)) url else cookieByName?.domain,
                    cookieByName.toString()
                )
            }

        } else {
            if (isLogin()) {
                val cookieByName =
                    com.kotlin.android.retrofit.cookie.CookieManager.instance.getCookieByName("_mi_")
                cookieManager.setCookie(
                    if (TextUtils.isEmpty(cookieByName?.domain)) url else cookieByName?.domain,
                    cookieByName.toString()
                )
            }
        }

        cookieManager.flush()
    }
    /**
     * 有些H5页面底部有EditText控件，设置true，当键盘弹出时，EditText会自动上移，反之会被键盘遮盖
     */
    protected open fun isStartEditProperty(): Boolean {
        return false
    }

    override fun startObserve() {
        LiveEventBus
            .get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java)
            .observe(this) {
                if (it.isLogin) {
                    mWebView?.loadUrl(mEntity?.url)
                    fillCookie(mEntity?.url)
                }
            }
    }

    override fun initData() {
        mWebView?.loadUrl(mEntity?.url)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SELECT_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                mFilePathCallback = if (data != null) {
                    mFilePathCallback?.apply {
                        try {
                            val dataUris = Array<Uri>(2) { Uri.parse(data.dataString) }
                            onReceiveValue(dataUris)
                        } catch (e: java.lang.Exception) {
                            "$e".e()
                        }
                    }
                    null
                } else {
                    mFilePathCallback?.apply {
                        onReceiveValue(null)
                    }
                    null
                }
            } else {
                /**
                 * https://www.jianshu.com/p/2a12e5fbb748
                 */
                mFilePathCallback?.apply {
                    onReceiveValue(null)
                }
                mFilePathCallback = null
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

     override fun onResume() {
        super.onResume()
        show()

        /**
         * 取消之后要通知WebView不要再等待返回结果，设置为空就等于重置了状态,避免只能选择一次图片的问题
         */
        mFilePathCallback?.onReceiveValue(null)
         mFilePathCallback = null

    }

     override fun onPause() {
        super.onPause()
        hide()
    }

    override fun onDestroy() {
        mWebView?.destroy()
        super.onDestroy()
    }

    fun backward(): Boolean {
        if (mWebView?.canGoBack() == true) {
            mWebView?.goBack()
            return true
        }
        return false
    }

    override fun hide() {
        super.hide()
        mWebView?.pauseTimers()
        mWebView?.onPause()
    }

    override fun show() {
        super.show()
        mWebView?.onResume()
        val settings = mWebView?.settings
        settings?.builtInZoomControls = true
        mWebView?.resumeTimers()
    }

}