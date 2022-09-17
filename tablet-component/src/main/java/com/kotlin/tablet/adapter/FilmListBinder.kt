package com.kotlin.tablet.adapter

import android.view.ViewGroup
import com.kotlin.android.app.data.entity.filmlist.PageRcmd
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemFilmListLayoutBinding

class FilmListBinder(val pageRcmd: PageRcmd) : MultiTypeBinder<ItemFilmListLayoutBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_film_list_layout
    }

    override fun onBindViewHolder(binding: ItemFilmListLayoutBinding, position: Int) {
        val provider = getProvider(ITabletProvider::class.java)
        binding.bean = pageRcmd
        binding.mCardView.onClick {
            pageRcmd.filmListId?.let {
                provider?.startFilmListDetailsActivity(filmListId = it)
            }
        }
        if (pageRcmd.numFavorites != null) {
            if (pageRcmd.numFavorites!! > 9999) {
                binding.tvFilmCollection.text =
                    getString(
                        R.string.tablet_main_num_favorites,
                        formatCount(pageRcmd.numFavorites!!)
                    )
            } else {
                binding.tvFilmCollection.text =
                    getString(R.string.tablet_main_num_favorites, pageRcmd.numFavorites.toString())
            }
        }
        if (pageRcmd.numRead != null && pageRcmd.numMovie != null) {
            if (pageRcmd.numMovie!! > 9999) {
                binding.tvNumMovie.text = formatCount(pageRcmd.numMovie!!)
            } else {
                binding.tvNumMovie.text = pageRcmd.numMovie.toString()
            }
            if (pageRcmd.numRead!! > 9999) {
                binding.tvRead.text = formatCount(pageRcmd.numRead!!)
            } else {
                binding.tvRead.text = pageRcmd.numRead.toString()
            }
        }

    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FilmListBinder
    }


}