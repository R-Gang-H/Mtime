package com.kotlin.android.home.ui.toplist.constant

/**
 * @author vivian.wei
 * @date 2020/7/21
 * @desc 榜单常量
 */
class TopListConstant {

    companion object {

        /**榜单类型*/
        const val TOPLIST_TYPE_MOVIE: Long = 1
        const val TOPLIST_TYPE_TV: Long = 2
        const val TOPLIST_TYPE_PERSON: Long = 3
        const val TOPLIST_TYPE_GAME: Long = 1001

        /**首页_榜单Fragment bundle key*/
        const val KEY_TOPLIST_TYPE = "toplist_type"
        const val KEY_TOPLIST_ID = "toplist_id"

        /**首页_榜单Fragment 分类榜单显示数*/
        const val MOVIE_TOPLIST_CATEGORY_SHOWCOUNT = 7
        const val PERSON_TOPLIST_CATEGORY_SHOWCOUNT = 4
        /**首页_榜单Fragment 影片年度榜单显示数*/
        const val MOVIE_TOPLIST_YEAR_SHOWCOUNT = 4
    }

}