package com.kotlin.android.search.newcomponent.ui.result.adapter

import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultAllTypeTitleBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class SearchResultAllTypeTitleBinder(val searchType: Long) : MultiTypeBinder<ItemSearchResultAllTypeTitleBinding>() {

    override fun layoutId(): Int {
        return R.layout.item_search_result_all_type_title
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultAllTypeTitleBinder && other.searchType == searchType
    }

    override fun onBindViewHolder(binding: ItemSearchResultAllTypeTitleBinding, position: Int){
        binding.mItemSearchResultAllTypeTitleTv.text = when(searchType) {
            SEARCH_MOVIE -> getString(R.string.search_type_movie)
            SEARCH_CINEMA -> getString(R.string.search_type_cinema)
            SEARCH_PERSON -> getString(R.string.search_type_person)
            SEARCH_ARTICLE -> getString(R.string.search_type_article)
            SEARCH_USER -> getString(R.string.search_type_user)
            SEARCH_POST -> getString(R.string.search_type_post)
            SEARCH_FILM_COMMENT -> getString(R.string.search_type_film_comment)
            SEARCH_FAMILY -> getString(R.string.search_type_family)
            SEARCH_LOG -> getString(R.string.search_type_log)
            SEARCH_FILM_LIST -> getString(R.string.search_type_film_list)
            SEARCH_VIDEO -> getString(R.string.search_type_video)
            SEARCH_AUDIO -> getString(R.string.search_type_audio)
            else -> ""
        }
    }
}