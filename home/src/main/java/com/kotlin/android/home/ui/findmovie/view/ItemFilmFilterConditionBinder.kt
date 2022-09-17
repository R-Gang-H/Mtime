package com.kotlin.android.home.ui.findmovie.view

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.app.data.entity.search.ConditionResult
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemFilmFilterConditionLayoutBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * 电影条件筛选 过滤binder
 */
class ItemFilmFilterConditionBinder(var bean: ConditionResult.Content) :
    MultiTypeBinder<ItemFilmFilterConditionLayoutBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_film_filter_condition_layout
    }

    override fun onBindViewHolder(binding: ItemFilmFilterConditionLayoutBinding, position: Int) {
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ItemFilmFilterConditionBinder
    }

    override fun onClick(view: View) {
        super.onClick(view)
    }
}