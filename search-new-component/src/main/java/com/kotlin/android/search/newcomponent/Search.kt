package com.kotlin.android.search.newcomponent

/**
 * 搜索组件
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */

object Search {

    const val SEARCH_REQUEST_CODE = 10067       // 发布组件-关联搜索requestCode
    const val SEARCH_MOVIE_RESULT_CODE = 10067  // 影片
    const val SEARCH_PERSON_RESULT_CODE = 10068 // 影人

    const val KEY_SEARCH_DATA = "data"
    const val KEY_SEARCH_DATA_MOVIE = "data_movie"
    const val KEY_SEARCH_DATA_CINEMA = "data_cinema"
    const val KEY_SEARCH_DATA_PERSON = "data_person"
    const val KEY_SEARCH_DATA_GOODS = "data_goods"
    const val KEY_SEARCH_DATA_ARTICLE = "data_article"
    const val KEY_SEARCH_DATA_GROUP = "data-group"

    const val SP_KEY_SEARCH_HISTORY = "sp_key_search_history"

    const val SEARCH_PAGE_SATE_HISTORY = 0
    const val SEARCH_PAGE_SATE_HINT = 1
    const val SEARCH_PAGE_SATE_RESULT = 2

    // 发布组件-关联电影/影人搜索-bundle key
    const val PUBLISH_SEARCH_BUNDLE_KEY_TYPE = "publish_search_bundle_key_type"
    const val PUBLISH_SEARCH_BUNDLE_KEY_FROM = "publish_search_bundle_key_from"

    // 发布组件-关联电影/影人搜索-页面来源
    const val PUBLISH_SEARCH_FROM_PUBLISH = 1L       // 来自发布组件(选中的影片/影人信息回传给发布页)
    const val PUBLISH_SEARCH_FROM_FIND_MOVIE = 2L    // 来自找电影(点击item跳转详情页)

}
