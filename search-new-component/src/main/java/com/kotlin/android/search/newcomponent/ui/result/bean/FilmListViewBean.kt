package com.kotlin.android.search.newcomponent.ui.result.bean

import android.graphics.Rect
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.annotation.SEARCH_ALL
import com.kotlin.android.app.data.entity.search.FilmList
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.search.newcomponent.ui.result.adapter.SearchResultFilmListItemBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/29
 * 描述: 搜索结果_片单ViewBean
 */
data class FilmListViewBean(
        var id: Long = 0L,           // 片单Id
        var title: String = "",      // 名称
        var cover: String = "",      // 封面图
        var authorImg: String = "",  // 发布人头像
        var authorId: Long = 0L,     // 发布人id
        var authorName: String = "", // 发布人昵称
        var films: String = "",      // 影片列表
        var collectNum: Long = 0L,   // 收藏数
        var authTag: Long = 0L,      // 认证标识
): ProguardRule {

    companion object {

        private val mFirstMarginTopAll = 8.dp
        private val mFirstMarginTopType = 15.dp
        private val mRootPaddingLeft = 15.dp
        private val mRootPaddingRight = 15.dp
        private val mRootPaddingTopAll = 7.dp
        private val mRootPaddingTopType = 15.dp
        private val mRootPaddingBottomAll = 8.dp
        private val mRootPaddingBottomType = 15.dp

        /**
         * 转换ViewBean
         */
        private fun objectToViewBean(bean: FilmList): FilmListViewBean {
            return FilmListViewBean(
                    id = bean.id.orZero(),
                    title = bean.title.orEmpty(),
                    cover = bean.cover.orEmpty(),
                    authorImg = bean.authorImg.orEmpty(),
                    authorId = bean.authorId.orZero(),
                    authorName = bean.authorName.orEmpty(),
                    films = bean.films.orEmpty(),
                    collectNum = bean.collectNum.orZero(),
                    authTag = bean.authTag.orZero(),
            )
        }

        /**
         * 片单Binders
         */
        fun build(searchType: Long, keyword: String, beans: List<FilmList>) : MutableList<MultiTypeBinder<*>> {
            val binderList = mutableListOf<MultiTypeBinder<*>>()
            beans.mapIndexed { index, it ->
                val viewBean = objectToViewBean(it)
                binderList.add(
                        SearchResultFilmListItemBinder(
                                keyword = keyword,
                                viewBean = viewBean,
                                isFirstItem = index == 0,
                                firstMarginTop = if(searchType == SEARCH_ALL) mFirstMarginTopAll else mFirstMarginTopType,
                                rootPadding = Rect(
                                        mRootPaddingLeft,
                                        if(searchType == SEARCH_ALL) mRootPaddingTopAll else mRootPaddingTopType,
                                        mRootPaddingRight,
                                        if(searchType == SEARCH_ALL) mRootPaddingBottomAll else mRootPaddingBottomType
                                ),
                        )
                )
            }
            return binderList
        }

    }
}
