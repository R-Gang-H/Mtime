package com.kotlin.tablet.adapter

import android.os.Bundle
import android.view.ViewGroup
import com.kotlin.android.app.data.entity.search.FilmList
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.KEY_FILM_LIST_ID
import com.kotlin.tablet.databinding.ItemSearchFilmListLayoutBinding
import com.kotlin.tablet.R

class FilmSearchListBinder(val film: FilmList) :
    MultiTypeBinder<ItemSearchFilmListLayoutBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_search_film_list_layout
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FilmSearchListBinder
    }

    override fun onBindViewHolder(binding: ItemSearchFilmListLayoutBinding, position: Int) {
        val provider = getProvider(ITabletProvider::class.java)
        binding.bean = film
        binding.mCardView.onClick {
            film.id?.let {
                provider?.startFilmListDetailsActivity(filmListId = it)
            }
        }
        if (film.collectNum != null) {
            if (film.collectNum!! > 9999) {
                binding.tvFilmCollection.text =
                    getString(
                        R.string.tablet_main_num_favorites,
                        formatCount(film.collectNum!!)
                    )
            } else {
                binding.tvFilmCollection.text =
                    getString(R.string.tablet_main_num_favorites, film.collectNum.toString())
            }
        }
        if (film.watchedNum != null && film.movieNum != null) {
            if (film.movieNum!! > 9999) {
                binding.tvNumMovie.text = formatCount(film.movieNum!!)
            } else {
                binding.tvNumMovie.text = film.movieNum.toString()
            }
            if (film.watchedNum!! > 9999) {
                binding.tvRead.text = formatCount(film.watchedNum!!)
            } else {
                binding.tvRead.text = film.watchedNum.toString()
            }
        }
    }
}