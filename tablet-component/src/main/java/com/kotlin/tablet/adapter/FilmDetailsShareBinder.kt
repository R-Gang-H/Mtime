package com.kotlin.tablet.adapter

import com.kotlin.android.app.data.entity.filmlist.ShareMovy
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemFilmDetailsShareLayoutBinding

class FilmDetailsShareBinder(val shareMovies: List<ShareMovy>) :
    MultiTypeBinder<ItemFilmDetailsShareLayoutBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_film_details_share_layout
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FilmDetailsShareBinder
    }

    override fun onBindViewHolder(binding: ItemFilmDetailsShareLayoutBinding, position: Int) {
        binding.bean = shareMovies[position]
        binding.ivImageUrl.setBackground(
            cornerRadius = 12.dpF
        )
        if (shareMovies[position].titleCn == "" || shareMovies[position].titleCn == null) {
            binding.tvTitle.text = shareMovies[position].titleEn
        } else {
            binding.tvTitle.text = shareMovies[position].titleCn
        }
    }
}