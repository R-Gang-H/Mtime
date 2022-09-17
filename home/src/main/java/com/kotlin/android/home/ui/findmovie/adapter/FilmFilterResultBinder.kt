package com.kotlin.android.home.ui.findmovie.adapter

import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemFilmFilterResultBinding
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: wangwei
 * 创建时间: 2022/4/13
 * 描述:找电影筛选后结果item
 **/
class FilmFilterResultBinder(val bean: Movie) :
    MultiTypeBinder<ItemFilmFilterResultBinding>() {

    override fun layoutId(): Int = R.layout.item_film_filter_result

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean =
        other is FilmFilterResultBinder && other.bean == bean

    override fun onBindViewHolder(binding: ItemFilmFilterResultBinding, position: Int) {
        binding.CLLayout?.onClick {
            getProvider(ITicketProvider::class.java)?.startMovieDetailsActivity(bean.id ?: 0)
        }

        var content = bean.movieType
        var actors = ""
        bean.actors?.forEach {
            actors += " / $it"
        }
        if (content?.isNotEmpty() == true) {
            content += actors
            binding?.mFilmTypeTv.visible()
        } else if (actors.isNotEmpty()) {
            content = actors.subSequence(3, actors.length).toString()//空格 / 空格  0 1 2
            binding?.mFilmTypeTv.visible()
        } else {
            binding?.mFilmTypeTv.gone()
        }
        binding?.mFilmTypeTv.text = content


        if (bean.rating == 0.0 || bean.rating == null) {
            binding?.mFilmScoreTv.text = getString(R.string.home_movie_result_details_no_score)
            binding?.mFilmScoreTextTv.gone()
        } else {
            binding?.mFilmScoreTv.text = bean.rating.toString()
            binding?.mFilmScoreTextTv.visible()
        }

    }
}