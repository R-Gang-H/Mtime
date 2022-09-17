package com.kotlin.android.search.newcomponent.ui.result.adapter

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.view.isVisible
import com.kotlin.android.ktx.ext.convertToHtml
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultMovieBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant
import com.kotlin.android.search.newcomponent.ui.result.bean.MovieItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 搜索结果_影片Item
 * @isResult 搜索结果页传true，其他页不用传，UI有区别
 */
class SearchResultMovieItemBinder(val keyword: String, val bean : MovieItem, val isResult: Boolean? = false) :
    MultiTypeBinder<ItemSearchResultMovieBinding>() {

    companion object {
        const val TICKET_BUTTON_CORNER = 27 // 购票按钮corner
        val NAME_EN_ALL_MARGIN = 45.dp   // 英文名所在行所有margin总和 dp
        val IMAGE_WIDTH = 65.dp          // 图片宽 dp
    }

    val mTicketProvider: ITicketProvider? = getProvider(ITicketProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_search_result_movie
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultMovieItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemSearchResultMovieBinding, position: Int){
        // 中文名（搜索关键词标红）
        var name = if(bean.name.isEmpty()) bean.nameEn else bean.name
        binding.mItemSearchResultMovieNameCnTv.convertToHtml(name, SearchResultConstant.MATCH_KEYWORD_COLOR, keyword)
        // 年代
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        var yearWidth = 0
        binding.mItemSearchResultMovieYearTv.apply {
            if (bean.year.isEmpty()) {
                text = ""
            } else {
                text = this.getString(R.string.search_result_movie_year, bean.year)
                this.measure(spec, spec)
                yearWidth = this.measuredWidth
            }
        }
        // 英文名
        var nameEn = if(bean.name.isEmpty()) "" else bean.nameEn
        binding.mItemSearchResultMovieNameEnTv.apply {
            if(nameEn.isEmpty()) {
                text = ""
            } else {
                // 最大宽度
                maxWidth = screenWidth - (NAME_EN_ALL_MARGIN + IMAGE_WIDTH) - yearWidth
                // 搜索关键词标红
                convertToHtml(nameEn, SearchResultConstant.MATCH_KEYWORD_COLOR, keyword)
            }
        }
        // 购票按钮
        binding.mItemSearchResultMovieTicketTv?.let {
            when (bean.saleType) {
                MovieItem.SALE_TYPE_PRE_SELL -> {
                    it.text = getString(R.string.search_result_movie_presell)
                    // 背景色
                    ShapeExt.setGradientColorWithColor(it,
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        getColor(R.color.color_afd956),
                        getColor(R.color.color_c0dc4d),
                        TICKET_BUTTON_CORNER)
                    it.isVisible = true
                }
                MovieItem.SALE_TYPE_TICKET -> {
                    it.text = getString(R.string.search_result_movie_ticket)
                    // 背景色
                    ShapeExt.setGradientColorWithColor(it,
                        GradientDrawable.Orientation.BL_TR,
                        getColor(R.color.color_20a0da),
                        getColor(R.color.color_1bafe0),
                        TICKET_BUTTON_CORNER)
                    it.isVisible = true
                }
                else -> {
                    it.isVisible = false
                    it.text = ""
                }
            }
        }
    }

}