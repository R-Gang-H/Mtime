package com.kotlin.android.home.ui.toplist.adapter

import com.kotlin.android.app.data.entity.toplist.GameRankUser
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemToplistGameDetailBinding
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDrawable

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlin.math.abs

/**
 * @author vivian.wei
 * @date 2020/8/21
 * @desc 类描述
 */
class TopListGameDetailItemBinder(val bean: GameRankUser) : MultiTypeBinder<ItemToplistGameDetailBinding>() {

    private val mCommunityPersonProvider =
            getProvider(ICommunityPersonProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_toplist_game_detail
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TopListGameDetailItemBinder && other.bean.userInfo == bean.userInfo
    }

    override fun onBindViewHolder(binding: ItemToplistGameDetailBinding, position: Int) {
        bean.orderNumber?.let {
            if(it < 100) {
                binding.mToplistGameDetailRankTv.textSize = 21f
            } else {
                binding.mToplistGameDetailRankTv.textSize = 13f
            }
        }
        if(bean.rankFluctuation == null || bean.rankFluctuation == 0L) {
            binding.mToplistGameDetailRankTrendIv.setImageDrawable(null)
            binding.mToplistGameDetailRankTrendTv.text = ""
        } else {
            bean.rankFluctuation?.let {
                binding.mToplistGameDetailRankTrendIv.setImageDrawable(
                        if (it > 0) getDrawable(R.drawable.ic_toplist_game_rank_up)
                        else getDrawable(R.drawable.ic_toplist_game_rank_down))
                binding.mToplistGameDetailRankTrendTv.setTextColor(
                        if (it > 0) getColor(R.color.color_ff5a36) else getColor(R.color.color_91d959))
                binding.mToplistGameDetailRankTrendTv.text = abs(it).toString()
            }
        }

        bean.userInfo?.let {
            it.userId?.let { userId ->
                binding.root.onClick {
                    mCommunityPersonProvider?.startPerson(userId, 0L)
                }
            }
        }

    }
}