package com.kotlin.android.search.newcomponent.ui.result

/**
 * 搜索结果页常量
 */
class SearchResultConstant {

    companion object {
        // 搜索类型
        const val KEY_UNION_SEARCH_TYPE = "key_union_search_type"
        // 搜索关键词
        const val SP_UNION_SEARCH_KEYWORD = "sp_union_search_keyword"
        //匹配的搜索关键词高亮颜色
        const val MATCH_KEYWORD_COLOR = "#FF5A36"
        // 搜索排序 0默认 1时间顺序 2时间倒序
        const val SEARCH_SORT_COMPREHENSIVE = 0L   // 综合排序
        const val SEARCH_SORT_TIME_DESC = 2L       // 时间倒序
    }

}