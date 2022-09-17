package com.kotlin.android.search.newcomponent.ui.result.adapter

import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultAllTypeCountBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class SearchResultAllTypeCountBinder(val searchType: Long, private val totalCount: Long) :
    MultiTypeBinder<ItemSearchResultAllTypeCountBinding>() {

    override fun layoutId(): Int {
        return R.layout.item_search_result_all_type_count
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultAllTypeCountBinder && other.searchType == searchType
    }

    override fun onBindViewHolder(binding: ItemSearchResultAllTypeCountBinding, position: Int){
        binding.mItemSearchResultAllTypeTotalCountTv.apply {
            text = when(searchType) {
                SEARCH_MOVIE -> this.getString(R.string.search_type_movie_total_count, totalCount)
                SEARCH_CINEMA -> this.getString(R.string.search_type_cinema_total_count, totalCount)
                SEARCH_PERSON -> this.getString(R.string.search_type_person_total_count, totalCount)
                SEARCH_ARTICLE -> this.getString(R.string.search_type_article_total_count, totalCount)
                SEARCH_USER -> this.getString(R.string.search_type_user_total_count, totalCount)
                SEARCH_POST -> this.getString(R.string.search_type_post_total_count, totalCount)
                SEARCH_FILM_COMMENT -> this.getString(R.string.search_type_film_comment_total_count, totalCount)
                SEARCH_FAMILY -> this.getString(R.string.search_type_family_total_count, totalCount)
                SEARCH_LOG -> this.getString(R.string.search_type_log_total_count, totalCount)
                SEARCH_FILM_LIST -> getString(R.string.search_type_film_list_total_count, totalCount)
                SEARCH_VIDEO -> getString(R.string.search_type_video_total_count, totalCount)
                SEARCH_AUDIO -> getString(R.string.search_type_audio_total_count, totalCount)
                else -> ""
            }
        }
    }
}