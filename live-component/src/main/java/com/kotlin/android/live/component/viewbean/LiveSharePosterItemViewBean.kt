package com.kotlin.android.live.component.viewbean

import android.graphics.drawable.Drawable
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.live.component.R
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString

/**
 * create by vivian.wei on 2021/10/24
 * description: 直播生成海报分享平台
 */
data class LiveSharePosterItemViewBean(
    var platformId: Long = 0L // 分享平台
) : ProguardRule {

    companion object {
        const val PLATFORM_LOCAL = 1L  // 保存本地
        const val PLATFORM_WECHAT = 2L // 微信
        const val PLATFORM_FRIEND = 3L // 朋友圈
        const val PLATFORM_SINA = 4L   // 微博
        const val PLATFORM_QQ = 5L     // QQ
    }

    fun getPlatformName(): String {
        return when (platformId) {
            PLATFORM_LOCAL -> {  //本地保存
                getString(R.string.live_component_save_local)
            }
            PLATFORM_WECHAT -> { // 微信分享
                getString(R.string.we_chat)
            }
            PLATFORM_FRIEND -> { // 朋友圈分享
                getString(R.string.we_chat_timeline)
            }
            PLATFORM_SINA -> { // 新浪分享
                getString(R.string.wei_bo)
            }
            PLATFORM_QQ -> {   // QQ分享
                getString(R.string.qq)
            }
            else -> getString(R.string.qq)
        }
    }

    fun getPlatformDrawable(): Drawable? {
        return when (platformId) {
            PLATFORM_LOCAL -> {//本地保存
                getDrawable(R.mipmap.ic_down_local)
            }
            PLATFORM_WECHAT -> {//微信分享
                getDrawable(R.mipmap.ic_wechat)
            }
            PLATFORM_FRIEND -> {//朋友圈分享
                getDrawable(R.mipmap.ic_wechat_timeline)
            }
            PLATFORM_SINA -> {//新浪分享
                getDrawable(R.mipmap.ic_sina)
            }
            PLATFORM_QQ -> {//QQ分享
                getDrawable(R.mipmap.ic_qq)
            }
            else -> getDrawable(R.mipmap.ic_down_local)

        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LiveSharePosterItemViewBean

        if (platformId != other.platformId) return false

        return true
    }

    override fun hashCode(): Int {
        return platformId.hashCode()
    }


}