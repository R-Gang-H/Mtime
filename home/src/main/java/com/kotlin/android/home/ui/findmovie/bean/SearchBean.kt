package com.kotlin.android.home.ui.findmovie.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.search.ConditionResult
import com.kotlin.android.comment.component.bean.ReplyViewBean
import com.kotlin.android.home.ui.findmovie.view.ItemFilmFilterConditionBinder

/**
 * 搜索条件bean
 */
data class SearchBean(
    var type: Int = 0,//0类型 ,1地区 ,2排序 3年代
    var keyword: String? = "",
    var genreTypes: Long = 0L,//电影类型	 默认0全部，多条以逗号分隔
    var year: String = "-1:-1",//认-1:-1全部，范围筛选 2020:2020
    var area: Long = 0L,//默认0全部，多条以逗号分隔
    var sortType: Long = 0L,//默认0， 时光评分0 音乐1  画面2  导演3  故事4  表演5  近期热度6  上映时间7
) : ProguardRule {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchBean

        if (year != other.year) return false
        if (keyword != other.keyword) return false
        if (genreTypes != other.genreTypes) return false
        if (area != other.area) return false
        if (sortType != other.sortType) return false
        return true
    }

    fun update(
        keyword: String?,
        genreTypes: Long,
        year: String,
        area: Long,
        sortType: Long
    ) {
        this.keyword = keyword
        this.genreTypes = genreTypes
        this.year = year
        this.area = area
        this.sortType = sortType
    }

    companion object {
        fun convertFilterConditionBinder(
            type: Int,
            conditionResult: ConditionResult
        ): List<ItemFilmFilterConditionBinder>? {
            return when (type) {
                0 -> {
                    conditionResult?.genres?.map {
                        ItemFilmFilterConditionBinder(it)
                    }
                }
                1 -> {
                    conditionResult?.genres?.map {
                        ItemFilmFilterConditionBinder(it)
                    }
                }

                2 -> {
                    conditionResult?.genres?.map {
                        ItemFilmFilterConditionBinder(it)
                    }
                }

                3 -> {
                    conditionResult?.genres?.map {
                        ItemFilmFilterConditionBinder(it)
                    }
                }
                else -> {
                    conditionResult?.genres?.map {
                        ItemFilmFilterConditionBinder(it)
                    }
                }
            }
        }
    }
}
