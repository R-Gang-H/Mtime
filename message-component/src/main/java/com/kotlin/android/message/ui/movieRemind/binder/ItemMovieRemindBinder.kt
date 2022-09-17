package com.kotlin.android.message.ui.movieRemind.binder

import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageItemMovieRemindBinding
import com.kotlin.android.message.tools.MessageCenterJumper
import com.kotlin.android.message.ui.movieRemind.viewBean.MovieRemindViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by zhaoninglongfei on 2022/3/23
 *
 */
class ItemMovieRemindBinder(val bean: MovieRemindViewBean) :
    MultiTypeBinder<MessageItemMovieRemindBinding>() {

    override fun layoutId(): Int = R.layout.message_item_movie_remind

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ItemMovieRemindBinder
    }

    fun jumpToMovieDetail(movieId: Long) {
        MessageCenterJumper.jumpToMovieDetail(binding?.root?.context, movieId)
    }

    fun jumpToBuyTicket() {
        MessageCenterJumper.jumpToBuyTicket(binding?.root?.context, bean.movieId.toString())
    }
}