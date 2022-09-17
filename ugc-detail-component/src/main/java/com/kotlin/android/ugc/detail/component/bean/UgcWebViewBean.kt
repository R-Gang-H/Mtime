package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.ProguardRule

/**
 * Created by lushan on 2020/8/6
 * webView内容
 */
data class UgcWebViewBean(var content: String = "",//接口返回html内容
                          var webType: Int = 0,//webView类型
                          var reMovie: CommunityContent.RoMovie? = null//相关影片

) : ProguardRule {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UgcWebViewBean

        if (content != other.content) return false
        if (webType != other.webType) return false
        if (reMovie != other.reMovie) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.hashCode()
        result = 31 * result + webType
        result = 31 * result + (reMovie?.hashCode() ?: 0)
        return result
    }
}