package com.kotlin.android.search.newcomponent.ui.result.adapter

import android.graphics.Rect
import android.view.View
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.ktx.ext.convertToHtml
import com.kotlin.android.ktx.ext.core.marginTop
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.mtime.ktx.setUserAuthType
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultFilmListBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant
import com.kotlin.android.search.newcomponent.ui.result.bean.FilmListViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/29
 * 描述: 搜索结果_片单ItemBinder
 */
class SearchResultFilmListItemBinder(
    val keyword: String,
    val viewBean: FilmListViewBean,
    private val isFirstItem: Boolean? = false,
    val firstMarginTop: Int? = 0,                                // 第一个item marginTop 单位:px
    val rootPadding: Rect = Rect(0, 0, 0, 0)  // item padding 单位:px
) : MultiTypeBinder<ItemSearchResultFilmListBinding>() {

    private val mProvider = getProvider(ITabletProvider::class.java)

    override fun layoutId(): Int = R.layout.item_search_result_film_list

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultFilmListItemBinder && other.viewBean == viewBean
    }

    override fun onBindViewHolder(binding: ItemSearchResultFilmListBinding, position: Int) {
        binding.apply {
            // position在全部搜索中是整个列表的位置，不是模块列表的位置，所以无法用position判断第1个
            firstMarginTop?.let {
                root.marginTop = if (isFirstItem.orFalse()) it else 0
            }
            rootPadding.apply {
                root.setPadding(left, top, right, bottom)
            }
            // 搜索关键词标红
            filmListTitleTv.convertToHtml(
                title = viewBean.title,
                color = SearchResultConstant.MATCH_KEYWORD_COLOR,
                keyword = keyword
            )
            // 收藏数
            filmListCollectCountTv.text =
                formatCount(viewBean.collectNum, true)
                    .plus(getString(R.string.search_result_film_list_collect_count))
            // 用户认证类型
            filmListAuthorAuthTypeIv.setUserAuthType(viewBean.authTag)
        }
    }

    override fun onClick(view: View) {
        // 跳转片单详情页
        mProvider?.startFilmListDetailsActivity(filmListId = viewBean.id)
    }
}