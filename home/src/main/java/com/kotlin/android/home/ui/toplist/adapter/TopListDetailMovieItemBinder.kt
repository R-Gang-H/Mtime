package com.kotlin.android.home.ui.toplist.adapter

import android.graphics.drawable.GradientDrawable
import com.kotlin.android.app.data.entity.toplist.TopListItem
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemToplistDetailMovieBinding
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getString

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author vivian.wei
 * @date 2020/8/21
 * @desc 电影榜单详情页_影片列表
 */
class TopListDetailMovieItemBinder(val bean: TopListItem,
                                   onClickWantSee: (bean: TopListItem, binder: MultiTypeBinder<*>) -> Unit)
    : MultiTypeBinder<ItemToplistDetailMovieBinding>() {

    companion object {
        const val DES_MAX_LINE = 50
        const val DES_MIN_LINE = 2
    }

    val ticketProvider = getProvider(ITicketProvider::class.java)
    private var mOnClickWantSee = onClickWantSee

    override fun layoutId(): Int {
        return R.layout.item_toplist_detail_movie
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TopListDetailMovieItemBinder && other.bean.itemId == bean.itemId
    }

    override fun onBindViewHolder(binding: ItemToplistDetailMovieBinding, position: Int) {
        // 排名
        binding.mItemToplistDetailMovieRankTv.let {
            var rankTv = it
            bean.rank?.let {
                when (it) {
                    1L -> {
                        rankTv.setBackgroundResource(R.drawable.ic_toplist_movie_first)
                    }
                    2L -> {
                        rankTv.setBackgroundResource(R.drawable.ic_toplist_movie_second)
                    }
                    3L -> {
                        rankTv.setBackgroundResource(R.drawable.ic_toplist_movie_third)
                    }
                    else -> {
                        rankTv.setBackgroundResource(R.drawable.ic_toplist_movie_rank_other)
                        if (it > 99) {
                            rankTv.textSize = 6f
                        } else if(it > 9) {
                            rankTv.textSize = 8f
                        } else {
                            rankTv.textSize = 10f
                        }
                    }
                }
            }
        }
        // 描述
        ShapeExt.setShapeColorAndCorner(binding.mItemToplistDetailMovieDesTv,
                R.color.color_f2f3f6,
                5)
        binding.mItemToplistDetailMovieDesTv.onClick {
            bean.expanded = !bean.expanded!!
            binding.mItemToplistDetailMovieDesTv.maxLines =
                    if(bean.expanded!!) DES_MAX_LINE else DES_MIN_LINE
        }
        // 想看
        binding.mItemToplistDetailMovieWantSeeBtn.let {
            ShapeExt.setGradientColorWithColor(it, GradientDrawable.Orientation.BL_TR,
                    it.getColor(R.color.color_20a0da), it.getColor(R.color.color_1bafe0), 27)
            it.text = if(bean.movieInfo?.currentUserWantSee == true)
                getString(R.string.home_wanted_see_btn_text) else
                getString(R.string.home_want_see_btn_text)
            bean.movieInfo?.let { movie ->
                it.onClick {
                    // 点击关注按钮
                    mOnClickWantSee.invoke(bean, this)
                }
            }
        }

    }
}