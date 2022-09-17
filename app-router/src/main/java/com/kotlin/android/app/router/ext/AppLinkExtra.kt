package com.kotlin.android.app.router.ext

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.ktx.ext.toLongOrDefault

/**
 * 创建者: zl
 * 创建时间: 2020/12/9 10:39 上午
 * 描述:{"handleType":"jumpPage","pageType":"login"}
 */
data class AppLinkExtra(
        var pageType: String? = "",
        var handleType: String? = "",
        var url: String? = "",
        var isOpenByBrowser: String? = "",
        var isHorizontalScreen: String? = "",
        private var movieId: String? = null,
        private var contentId: String? = null,
        private var type: String? = null,
        var movieName: String? = "",
        private var starId: String? = null,
        private var cinemaId: String? = null,
        var date: String? = "",
        private var showTimeId: String? = null,
        private var groupID: String? = null,
        private var albumId: String? = null,
        private var userId: String? = null,
        private var videoId: String? = null,
        var videoSource: String? = "",
        private var listID: String? = null,
        var uuid: String? = "",
        var liveId:String? = "",
        private var filmListId: String? = ""
) : ProguardRule {
    fun getMovieId(): Long = movieId.toLongOrDefault(0)
    fun getContentId(): Long = contentId.toLongOrDefault(0)
    fun getType(): Long = type.toLongOrDefault(0)
    fun getStarId(): Long = starId.toLongOrDefault(0)
    fun getCinemaId(): Long = cinemaId.toLongOrDefault(0)
    fun getShowTimeId(): Long = showTimeId.toLongOrDefault(0)
    fun getGroupID(): Long = groupID.toLongOrDefault(0)
    fun getAlbumId(): Long = albumId.toLongOrDefault(0)
    fun getUserId(): Long = userId.toLongOrDefault(0)
    fun getVideoId(): Long = videoId.toLongOrDefault(0)
    fun getListID(): Long = listID.toLongOrDefault(0)
    fun getLiveId():Long = liveId.toLongOrDefault(0)
    fun getFilmListId(): Long = filmListId.toLongOrDefault(0)
}