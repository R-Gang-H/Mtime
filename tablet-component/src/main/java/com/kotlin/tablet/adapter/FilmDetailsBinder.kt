package com.kotlin.tablet.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.utils.TextUtils
import com.kotlin.android.app.data.entity.filmlist.MovieFormalVideo
import com.kotlin.android.app.data.entity.filmlist.Movy
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemFilmDetailsLayoutBinding

class FilmDetailsBinder(
    val movies: Movy,
    val context: Context,
    val filmListType: Long?
) :
    MultiTypeBinder<ItemFilmDetailsLayoutBinding>() {
    private lateinit var mAdapter: MultiTypeAdapter
    override fun layoutId(): Int {
        return R.layout.item_film_details_layout
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FilmDetailsBinder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(binding: ItemFilmDetailsLayoutBinding, position: Int) {
        binding.bean = movies
        binding.binder = this
        binding.mConstraintLayout.onClick {
            //跳转影片详情
            movies.movieId?.let { it1 ->
                getProvider(ITicketProvider::class.java)?.startMovieDetailsActivity(
                    it1
                )
            }
        }
        binding.mRelative.setBackground(
            colorRes = R.color.color_f2f3f6,
            cornerRadius = 9.dpF
        )
        if (!TextUtils.isEmpty(movies.date)) {
            if (filmListType == 2L) {
                binding.tvDate.visible()
                binding.tvDate.text = "-" + movies.date + "更新"
            } else {
                binding.tvDate.gone()
            }
        } else {
            binding.tvDate.gone()
        }
        if (movies.titleCn.isNullOrEmpty() && !TextUtils.isEmpty(movies.year)) {
            binding.tvTitle.text = movies.titleEn + "(${movies.year})"
        } else if (movies.titleCn.isNullOrEmpty() && movies.year.isNullOrEmpty()) {
            binding.tvTitle.text = movies.titleEn
        } else if (!TextUtils.isEmpty(movies.titleCn) && !TextUtils.isEmpty(movies.year)) {
            binding.tvTitle.text = movies.titleCn + "(${movies.year})"
        } else if (!TextUtils.isEmpty(movies.titleCn) && movies.year.isNullOrEmpty()) {
            binding.tvTitle.text = movies.titleCn
        }
        if (movies.filmTypes.isNullOrEmpty() && movies.mainActors.isNullOrEmpty()) {
            binding.tvFilmTypes.gone()
        } else if (movies.filmTypes.isNullOrEmpty() && !TextUtils.isEmpty(movies.mainActors)) {
            binding.tvFilmTypes.visible()
            binding.tvFilmTypes.text = movies.mainActors
        } else if (movies.mainActors.isNullOrEmpty() && !TextUtils.isEmpty(movies.filmTypes)) {
            binding.tvFilmTypes.visible()
            binding.tvFilmTypes.text = movies.filmTypes
        } else {
            binding.tvFilmTypes.visible()
            binding.tvFilmTypes.text = movies.filmTypes + "/" + movies.mainActors
        }
        if (movies.rating == "0.0") {
            binding.tvRating.gone()
            binding.tvRatingValue.gone()
        } else {
            binding.tvRating.visible()
            binding.tvRatingValue.visible()
        }
        if (isLogin()) {
            if (movies.readTag) {
                binding.tvReadTag.visible()
            } else {
                binding.tvReadTag.gone()
            }
        } else {
            binding.tvReadTag.gone()
        }
        mAdapter = createMultiTypeAdapter(
            binding.mRecycleView,
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        )
        getItemBinder(movies.movieFormalVideos).let { mAdapter.notifyAdapterAdded(it) }
    }

    private fun getItemBinder(movieFormalVideos: List<MovieFormalVideo>?): List<MultiTypeBinder<*>> {
        val listBinder = mutableListOf<MultiTypeBinder<*>>()
        movieFormalVideos?.forEach {
            listBinder.add(FilmDetailsItemBinder(it))
        }
        return listBinder
    }
}