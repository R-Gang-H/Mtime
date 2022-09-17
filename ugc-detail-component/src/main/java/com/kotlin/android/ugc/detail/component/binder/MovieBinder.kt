package com.kotlin.android.ugc.detail.component.binder

import android.view.View

import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.MovieViewBean
import com.kotlin.android.ugc.detail.component.binderadapter.MOVIE_STATE_HSA_LIKE
import com.kotlin.android.ugc.detail.component.binderadapter.MOVIE_STATE_LIKE
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcDetailMovieBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by lushan on 2020/8/5
 * UGC-影片卡片
 */
open class MovieBinder(val movieBean: MovieViewBean) : MultiTypeBinder<ItemUgcDetailMovieBinding>() {
    private val provider = getProvider(ITicketProvider::class.java)
    override fun layoutId(): Int = R.layout.item_ugc_detail_movie

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is MovieBinder && other.movieBean != movieBean
    }

    override fun onClick(view: View) {
        when (view) {
            binding?.movieRootCl -> {//点击影片卡片，跳转到影片详情页面
                provider?.startMovieDetailsActivity(movieId = movieBean.movieId)
            }
            binding?.movieBtnFl -> {//点击想看按钮

                when (movieBean.movieStatus) {
                    MOVIE_STATE_LIKE -> {//想看
                        super.onClick(view)
                    }
                    MOVIE_STATE_HSA_LIKE -> {//已想看
                        super.onClick(view)
                    }
                    else -> {//跳转到排期页
                        val ticketProvider: ITicketProvider? = getProvider(ITicketProvider::class.java)
                        ticketProvider?.startMovieShowtimeActivity(movieBean.movieId)
                    }
                }

            }
            else -> {
                super.onClick(view)
            }
        }
    }


}