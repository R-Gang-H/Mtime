package com.kotlin.android.home.ui.findmovie.adapter

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.app.data.entity.filmlist.PageRcmd
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemFmFilmListContainLayoutBinding
import com.kotlin.android.home.databinding.ItemFmFilmListLayoutBinding
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * 甄选片单contain
 */
class FilmListBinder(private var datas: List<PageRcmd>) :
    MultiTypeBinder<ItemFmFilmListContainLayoutBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_fm_film_list_contain_layout
    }

    override fun onBindViewHolder(binding: ItemFmFilmListContainLayoutBinding, position: Int) {
        binding?.rvFilmList.apply {
            var binders = arrayListOf<FilmListItemBinder>()
            datas?.forEach {
                binders.add(FilmListItemBinder(it))
            }
            setMovieAdapter(binders, binding)
        }
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FilmListBinder
    }

    private fun setMovieAdapter(
        list: List<FilmListItemBinder>,
        binding: ItemFmFilmListContainLayoutBinding
    ) {
        createMultiTypeAdapter(
            binding.rvFilmList,
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        ).run {
            mOnClickListener?.let {
                setOnClickListener(it)
            }
            notifyAdapterDataSetChanged(list)
        }
    }

    /**
     * 甄选榜单子item
     */
    class FilmListItemBinder(val bean: PageRcmd) : MultiTypeBinder<ItemFmFilmListLayoutBinding>() {
        override fun layoutId(): Int {
            return R.layout.item_fm_film_list_layout
        }

        override fun onBindViewHolder(binding: ItemFmFilmListLayoutBinding, position: Int) {
            binding.mCardView.onClick {
                val bundle = Bundle()
                bean.filmListId?.let {
                    bundle.putLong(
                        "filmListId",
                        bean.filmListId!!
                    )
                }
                getProvider(ITabletProvider::class.java)?.startFilmListDetailsActivity(
                    filmListId = bean.filmListId
                )
            }
            binding.tvFilmListLook.text = getString(R.string.home_total, formatCount(bean.numMovie?:0))
            binding.tvFilmCollection.text = getString(R.string.home_film_collect_tip, formatCount(bean.numFavorites?:0))

        }

        override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
            return other is FilmListItemBinder
        }

    }
}