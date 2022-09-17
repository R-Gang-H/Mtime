package com.kotlin.android.js.sdk.callback

import com.kotlin.android.js.sdk.entity.*

/**
 * 创建者: zl
 * 创建时间: 2020/12/9 1:40 下午
 * 描述:
 */
interface JsCallback {

    interface AppLinkCallback {
        fun appLink(data: JsEntity.AppLinkEntity.DataBean?)
    }

    interface HandleGoBackCallback {
        fun handleGoBack(data: JsEntity.HandleGoBackEntity.DataBean?)
    }

    interface ImageBrowserCallback {
        fun imageBrowser(data: JsEntity.ImageBrowserEntity.DataBean?)
    }

    interface ShareCallback {
        fun share(data: JsEntity.ShareEntity.DataBean?)
    }

    interface VideoPlayerCallback {
        fun videoPlay(data: JsEntity.VideoPlayerEntity.DataBean?)
    }

}