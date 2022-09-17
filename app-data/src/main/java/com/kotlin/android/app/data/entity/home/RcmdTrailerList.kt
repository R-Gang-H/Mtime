package com.kotlin.android.app.data.entity.home

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhousuqiang on 2020-08-24
 * 预告片列表
 */
data class RcmdTrailerList(var items: List<RcmdTrailer>?) : ProguardRule {
    companion object {
        const val TYPE_TODAY = 1L //1当前日期推荐
        const val TYPE_HISTORY = 2L //2历史推荐
    }

    data class RcmdTrailer(
            var videoId: Long?,
            var useDate: String?,
            var bigTitle: String?,
            var smallTitle: String?,
            var appBigImageUrl: String?,
            var appSmallImageUrl: String?
    ) : ProguardRule 
}