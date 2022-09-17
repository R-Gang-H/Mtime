package com.kotlin.android.js.sdk

import android.content.Context
import android.webkit.JavascriptInterface
import com.kotlin.android.js.sdk.callback.JsCallback
import com.kotlin.android.js.sdk.entity.BaseEntity
import com.kotlin.android.js.sdk.entity.JsEntity
import com.kotlin.android.ktx.ext.core.getNetworkType
import com.kotlin.android.ktx.ext.core.isInternetAvailable
import com.kotlin.android.ktx.ext.handleJson
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.isLogin

/**
 * 创建者: zl
 * 创建时间: 2020/5/18 10:57 AM
 * 描述: JSSDK主轴
 */
class SDKWarp(context: Context) {
    var mContext = context
    var appLinkCallback: JsCallback.AppLinkCallback? = null
    var imageBrowserCallback: JsCallback.ImageBrowserCallback? = null
    var shareCallback: JsCallback.ShareCallback? = null
    var videoPlayerCallback: JsCallback.VideoPlayerCallback? = null
    var handleGoBackCallback: JsCallback.HandleGoBackCallback? = null

    /**
     *  实名认证功能，h5控制app返回上一页
     */
    @JavascriptInterface
    fun handleGoBack(jsonStr: String?) {
//        Log.e("zl", "handleGoBack " + jsonStr)
        jsonStr?.apply {
            val entity = handleJson(jsonStr, JsEntity.HandleGoBackEntity::class.java)
            handleGoBackCallback?.handleGoBack(entity?.data)
        }
    }

    @JavascriptInterface
    fun openAppLinkClient(jsonStr: String?) {
//        Log.e("zl", "openAppLinkClient  " + jsonStr)
        jsonStr?.apply {
            val entity = handleJson(jsonStr, JsEntity.AppLinkEntity::class.java)
            appLinkCallback?.appLink(entity?.data)
        }
    }

    @JavascriptInterface
    fun showShare(jsonStr: String?) {
//        Log.e("zl", "showShare  " + jsonStr)
        jsonStr?.apply {
            val entity = handleJson(jsonStr, JsEntity.ShareEntity::class.java)
            shareCallback?.share(entity?.data)
        }
    }

    @JavascriptInterface
    fun showVideoPlayer(jsonStr: String?) {
//        Log.e("zl", "showVideoPlayer " + jsonStr)
        jsonStr?.apply {
            val entity = handleJson(jsonStr, JsEntity.VideoPlayerEntity::class.java)
            videoPlayerCallback?.videoPlay(entity?.data)
        }
    }

    @JavascriptInterface
    fun openImageBrowser(jsonStr: String?) {
//        Log.e("zl", "openImageBrowser " + jsonStr)
        jsonStr?.apply {
            val entity = handleJson(jsonStr, JsEntity.ImageBrowserEntity::class.java)
            imageBrowserCallback?.imageBrowser(entity?.data)
        }
    }

    /**
     * H5 登录信息
     * @return
     */
    @JavascriptInterface
    fun whetherLogin(jsonStr: String?): String {
        val model = JsEntity.LoginStateEntity(
                userId = UserManager.instance.userId.toString(),
                loginToken = UserManager.instance.token,
                action = if (isLogin()) "login" else "logout")
        return handleJson(BaseEntity(data = model))
    }

    /**
     * 设置平台类型
     * @param jsonStr
     * @return
     */
    @JavascriptInterface
    fun platform(jsonStr: String?): String {
        val model = JsEntity.PlatformEntity()
        return handleJson(BaseEntity(data = model))
    }

    @JavascriptInterface
    fun getNativeNetStatus(jsonStr: String?): String {
        var nativeNetStatus = 0
        try {
            if (mContext.isInternetAvailable()) {
                nativeNetStatus = mContext.getNetworkType()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        val model = JsEntity.NetStatusEntity(nativeNetStatus = nativeNetStatus.toString())
        return handleJson(BaseEntity(data = model))
    }

}