package com.kotlin.android.ugc.detail.component.binder

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.util.TypedValue
import android.view.View
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.LinkMovieViewBean
import com.kotlin.android.ugc.detail.component.binderadapter.MOVIE_STATE_HSA_LIKE
import com.kotlin.android.ugc.detail.component.binderadapter.MOVIE_STATE_LIKE
import com.kotlin.android.ugc.detail.component.binderadapter.MOVIE_STATE_PRESCALE
import com.kotlin.android.ugc.detail.component.binderadapter.MOVIE_STATE_SALE
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcLinkMovieBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import org.jetbrains.anko.Bold
import java.time.format.TextStyle

/**
 * create by lushan on 2022/3/14
 * des:关联影片
 **/
class UgcLinkMovieBinder(var bean: LinkMovieViewBean) : MultiTypeBinder<ItemUgcLinkMovieBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_ugc_link_movie
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is UgcLinkMovieBinder && other.bean == bean
    }

    fun updateWannaStatus(){
        bean.movieStatus =if (bean.isWanna()){
            MOVIE_STATE_HSA_LIKE
        }else{
            MOVIE_STATE_LIKE
        }
        notifyAdapterSelfChanged()
    }
    override fun onBindViewHolder(binding: ItemUgcLinkMovieBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        if (bean.score.isNotEmpty()) {
            val score =
                getString(R.string.ugc_detail_movie_score_format).format(bean.score)
            SpannableStringBuilder(score).apply {
                var indexOf = score.indexOf(bean.score)
                setSpan(
                    ForegroundColorSpan(getColor(R.color.color_20a0da)),
                    indexOf,
                    indexOf + bean.score.length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    indexOf,
                    indexOf + bean.score.length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }.also {
                binding.movieScoreTv.text = it
            }
        } else {
            binding.movieScoreTv.text = ""
        }

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.movieBtnFl -> {
                if (bean.movieStatus == MOVIE_STATE_SALE || bean.movieStatus == MOVIE_STATE_PRESCALE) {
                    getProvider(ITicketProvider::class.java) {
                        startMovieShowtimeActivity(bean.movieId)
                    }
                } else {
                    super.onClick(view)
                }
            }
            R.id.movieContentCl->{
                getProvider(ITicketProvider::class.java) {
                    startMovieDetailsActivity(bean.movieId)
                }
            }
            else -> {
                super.onClick(view)
            }
        }
    }
}