package com.kotlin.android.app.data.entity.home

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhousuqiang on 2020-08-20
 *
 * 首页-正在热映和即将上映数据实体
 */
data class HomeShowingComingMovies(
    var hotPlayMovies: List<Movie>?,
    var mobilemoviecoming: Mobilemoviecoming?
) : ProguardRule {

    data class Mobilemoviecoming(var moviecomings: List<Movie>?) : ProguardRule

    data class Movie(
        var movieId: Long,
        var title: String? = "",
        var imgUrl: String? = "",
        var score: String? = "",
        var is3D: Boolean = false,
        var isIMAX: Boolean = false,
        var isIMAX3D: Boolean = false,
        var isDMAX: Boolean = false,
        var wantedCount: Long? = 0,
        var releaseDateStr: String? = "",
        var btnShow: Long = 2 //1:预售 2:购票 3:想看 4:已想看
    ) : ProguardRule {

        /**
         * 影片tag
         *
         * IMAX 3D, IMAX, 中国巨幕, 3D
         */
        fun getTag(): String {
            val sb = StringBuilder()
            if (isIMAX3D) {
                if (sb.isNotEmpty()) {
                    sb.append(",")
                }
                sb.append("IMAX 3D")
            }
            if (isIMAX) {
                if (sb.isNotEmpty()) {
                    sb.append(",")
                }
                sb.append("IMAX")
            }
            if (isDMAX) {
                if (sb.isNotEmpty()) {
                    sb.append(",")
                }
                sb.append("中国巨幕")
            }
            if (is3D) {
                if (sb.isNotEmpty()) {
                    sb.append(",")
                }
                sb.append("3D")
            }
            return sb.toString()
        }
    }
}