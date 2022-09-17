package com.kotlin.android.review.component.item.ui.movie.constant

/**
 * @author vivian.wei
 * @date 2021/1/4
 * @desc 影片长/短影评列表页常量
 */
class MovieReviewConstant {

    companion object {
        // 页面参数
        const val KEY_MOVIE_ID = "movie_id"
        const val KEY_MOVIE_TITLE = "movie_title"
        const val KEY_ORDER_TYPE = "order_type"

        // 最大评分
        const val RATING_MAX = 10L

        /**
         * 长影评
         */
        // 每页记录数
        const val REVIEW_PAGE_SIZE = 10L
        // 排序类型：1最热 2最新
        const val REVIEW_ORDER_TYPE_HOT = 1L
        const val REVIEW_ORDER_TYPE_NEW = 2L
        // 内容最大行数
        const val REVIEW_CONTENT_MAX_LINE = 3

        /**
         * 短影评
         */
        // 每页记录数
        const val SHORT_COMMENT_PAGE_SIZE = 20L
        // 排序类型：1最热 2最新
        const val SHORT_COMMENT_ORDER_TYPE_HOT = 1L
        const val SHORT_COMMENT_ORDER_TYPE_NEW = 2L
        // 内容最大行数
        const val SHORT_COMMENT_CONTENT_MAX_LINE = 5

        // 用户tab背景参数
        const val USER_TAB_CORNER = 2       // dp
        const val USER_TAB_STROKE_WIDTH = 2 // px
    }

}