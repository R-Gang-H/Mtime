package com.kotlin.android.search.newcomponent.ui.result.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.ktx.ext.convertToHtml
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultCinemaBinding
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultCinemaFeatureBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant
import com.kotlin.android.search.newcomponent.ui.result.bean.CinemaItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

class SearchResultCinemaItemBinder(val keyword: String, val bean : CinemaItem) : MultiTypeBinder<ItemSearchResultCinemaBinding>() {

    companion object {
        const val FEATURE_MAX_SIZE = 4
        const val FEATURE_SPLIT = ","
    }

    val mTicketProvider: ITicketProvider? = getProvider(ITicketProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_search_result_cinema
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultCinemaItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemSearchResultCinemaBinding, position: Int) {
        // 搜索关键词标红
        binding.mItemSearchResultCinemaNameTv.convertToHtml(bean.name, SearchResultConstant.MATCH_KEYWORD_COLOR, keyword)
        // 特色设施
        binding.mItemSearchResultCinemaFeatureRv.apply {
            isVisible = bean.featureInfos.isNotEmpty()
            if(isVisible) {
                createMultiTypeAdapter(this,
                    LinearLayoutManager(context, RecyclerView.HORIZONTAL, false))
                    .notifyAdapterAdded(getBinderList(bean.featureInfos))
            }
        }
        // 距离
        binding.mItemSearchResultCinemaDistanceTv.apply {
            text = getDistance(bean.distance)
            isVisible = text.isNotEmpty()
        }
    }

    /**
     * 特色设施
     */
    private fun getBinderList(features: String): List<MultiTypeBinder<*>> {
        val binderList = mutableListOf<SearchResultCinemaFeatureItemBinder>()
        var list = features.split(FEATURE_SPLIT)
        list.subList(0, list.size.coerceAtMost(FEATURE_MAX_SIZE))
            .map {
            binderList.add(SearchResultCinemaFeatureItemBinder(it))
        }
        return binderList
    }

    /**
     * 距离
     */
    private fun getDistance(distanceKm: Double): String {
        var distance = distanceKm * 1000
        return when {
            distance < 1 -> {
                ""
            }
            distance < 500 -> {
                "<500m"
            }
            distance < 1000 -> {
                String.format("%dm", distance.toInt())
            }
            distance <= 20000 -> {
                String.format("%.1fkm", (distance / 1000).toFloat())
            }
            else -> {
                ">20km"
            }
        }
    }

}

class SearchResultCinemaFeatureItemBinder(val bean : String) : MultiTypeBinder<ItemSearchResultCinemaFeatureBinding>() {

    companion object {
        const val CORNER = 3
    }

    override fun layoutId(): Int {
        return R.layout.item_search_result_cinema_feature
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultCinemaFeatureItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemSearchResultCinemaFeatureBinding, position: Int) {
        ShapeExt.setShapeColorAndCorner(binding.mItemSearchResultCinemaFeatureTv,
            R.color.color_edf0f3,
            CORNER)
    }
}