package com.kotlin.android.home.ui.toplist.adapter

import android.graphics.Typeface
import com.kotlin.android.app.data.entity.toplist.TopListItem
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemToplistSelectMovieBinding
import com.kotlin.android.home.ui.toplist.constant.TopListConstant
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author vivian.wei
 * @date 2020/10/2
 * @desc 类描述
 */
class TopListSelectMovieItemBinder (val bean: TopListItem) : MultiTypeBinder<ItemToplistSelectMovieBinding>() {

    val ticketProvider = getProvider(ITicketProvider::class.java)
    val mainProvider = getProvider(IMainProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_toplist_select_movie
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TopListSelectMovieItemBinder && other.bean.itemId == bean.itemId
    }

    override fun onBindViewHolder(binding: ItemToplistSelectMovieBinding, position: Int) {
        var hasScore = false
        var score: String ?= ""
        var scoreTip: String ?= ""
        if(bean.itemType == TopListConstant.TOPLIST_TYPE_PERSON) {
            // 影人
            bean.personInfo?.let {
                hasScore = it.hasScore()
                score = it.score
            }
            binding.mItemToplistSelectMovieScoreTipTv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            scoreTip = getString(R.string.home_toplist_select_like_tip)
        } else {
            // 电影/电视剧
            bean.movieInfo?.let {
                hasScore = it.hasScore()
                score = it.showScore
            }
            binding.mItemToplistSelectMovieScoreTipTv.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            scoreTip = getString(R.string.home_toplist_select_score_tip)
        }
        binding.mItemToplistSelectMovieScoreTipTv.text =
                if(hasScore) scoreTip else getString(R.string.home_toplist_select_no_score_tip)
        // 评分
        binding.mItemToplistSelectMovieScoreTv.text = score ?: ""
    }
}