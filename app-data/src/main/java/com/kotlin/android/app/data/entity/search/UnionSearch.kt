package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * MtimeSearchController
 * http://apidocs-bd.wd-dev.com/mtime-search-front-api/index.html#api-MtimeSearchController-unionSearch
 *
 * MtimeSearchController - unionSearch
 * GET ("/search/search/unionSearch")
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
data class UnionSearch(
        val fullMatchType: Long? = null, // 最佳匹配
        val articles: List<Article>? = null, // 文章列表
        val articlesCount: Long? = null, // 文章条数
        val cinemaCount: Long? = null, // 影院条数
        val cinemas: List<Cinema>? = null, // 影院列表
        val locationCinemas: List<Cinema>? = null, // 本地影院列表
        val locationCinemasCount: Long? = null, // 本地影院条数
        val movies: List<Movie>? = null, // 电影列表
        val moviesCount: Long? = null, // 电影条数
        val persons: List<Person>, // 影人列表
        val personsCount: Long? = null, // 影人条数
        // 二期新增
        val userInfos: List<User>? = null,              // 用户列表
        val userInfosCount: Long? = 0L,                 // 用户总数
        val families: List<Family>? = null,             // 家族列表
        val familiesCount: Long? = 0L,                  // 家族总数
        val filmComments: List<FilmComment>? = null,    // 影评列表
        val filmCommentsCount: Long? = 0L,              // 影评总数
        val posts : List<Post>? = null,                 // 帖子列表
        val postsCount : Long? = 0L,                    // 帖子总数
        val logs : List<Log>? = null,                   // 日志列表
        val logsCount : Long? = 0L,                     // 日志总数
        // 2.0新增
        var filmListCount: Long? = null,                // 片单总数
        var filmList: List<FilmList>? = null,           // 片单列表
        var videoCount: Long? = null,                   // 视频总数
        var videoList: List<Video>? = null,             // 视频列表
        var audioCount: Long? = null,                   // 播客总数
        var audioList: List<Audio>? = null,             // 播客列表
) : ProguardRule, Serializable