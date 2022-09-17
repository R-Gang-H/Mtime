package com.kotlin.tablet.adapter

import com.kotlin.android.app.data.entity.filmlist.MovieFormalVideo
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemFilmDetailsItemLayoutBinding

class FilmDetailsItemBinder(val movieFormalVideos: MovieFormalVideo) :
    MultiTypeBinder<ItemFilmDetailsItemLayoutBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_film_details_item_layout
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FilmDetailsItemBinder
    }

    override fun onBindViewHolder(binding: ItemFilmDetailsItemLayoutBinding, position: Int) {
        binding.bean = movieFormalVideos
        binding.mIcon.onClick {
            getProvider(IJsSDKProvider::class.java)?.startH5Activity(
                BrowserEntity(
                    title = "",
                    url = movieFormalVideos.h5Url.toString()
                )
            )
        }
    }
}