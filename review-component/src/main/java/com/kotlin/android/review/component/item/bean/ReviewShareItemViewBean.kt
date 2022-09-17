package com.kotlin.android.review.component.item.bean

import android.graphics.drawable.Drawable
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.review.component.R

/**
 * create by lushan on 2020/11/4
 * description: 应评分享分享平台
 */
data class ReviewShareItemViewBean(var platformId: Long = 0L,//分享平台
                                   var isOpen: Boolean = false//是否已经打开，匿名专用
) : ProguardRule {
    companion object {
        const val REVIEW_SHARE_PLATFORM_SWITCH = 1L//匿名开关
        const val REVIEW_SHARE_PLATFORM_LOCAL = 2L//本地保存
        const val REVIEW_SHARE_PLATFORM_WECHAT = 3L//微信
        const val REVIEW_SHARE_PLATFORM_FIREND = 4L//朋友圈
        const val REVIEW_SHARE_PLATFORM_SINA = 5L//微博
        const val REVIEW_SHARE_PLATFORM_QQ = 6L//QQ
    }

    fun getPlatformName(): String {
        return when (platformId) {
            REVIEW_SHARE_PLATFORM_SWITCH -> {//匿名开关
                getString(R.string.film_review_share_anonymity)
            }
            REVIEW_SHARE_PLATFORM_LOCAL -> {//本地保存
                getString(R.string.film_review_share_save_local)
            }
            REVIEW_SHARE_PLATFORM_WECHAT -> {//微信分享
                getString(R.string.we_chat)
            }
            REVIEW_SHARE_PLATFORM_FIREND -> {//朋友圈分享
                getString(R.string.we_chat_timeline)
            }
            REVIEW_SHARE_PLATFORM_SINA -> {//新浪分享
                getString(R.string.wei_bo)
            }
            REVIEW_SHARE_PLATFORM_QQ -> {//QQ分享
                getString(R.string.qq)
            }
            else -> getString(R.string.qq)

        }
    }

    fun getPlatformDrawable(): Drawable? {
        return when (platformId) {
            REVIEW_SHARE_PLATFORM_SWITCH -> {//匿名开关
                if (isOpen) {
                    getDrawable(R.mipmap.ic_state_on)
                } else {
                    getDrawable(R.mipmap.ic_state_off)
                }
            }
            REVIEW_SHARE_PLATFORM_LOCAL -> {//本地保存
                getDrawable(R.mipmap.ic_down_local)
            }
            REVIEW_SHARE_PLATFORM_WECHAT -> {//微信分享
                getDrawable(R.mipmap.ic_wechat)
            }
            REVIEW_SHARE_PLATFORM_FIREND -> {//朋友圈分享
                getDrawable(R.mipmap.ic_wechat_timeline)
            }
            REVIEW_SHARE_PLATFORM_SINA -> {//新浪分享
                getDrawable(R.mipmap.ic_sina)
            }
            REVIEW_SHARE_PLATFORM_QQ -> {//QQ分享
                getDrawable(R.mipmap.ic_qq)
            }
            else -> getDrawable(R.mipmap.ic_down_local)

        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReviewShareItemViewBean

        if (platformId != other.platformId) return false
        if (isOpen != other.isOpen) return false

        return true
    }

    override fun hashCode(): Int {
        var result = platformId.hashCode()
        result = 31 * result + isOpen.hashCode()
        return result
    }


}