package com.kotlin.android.community.card.component.item.adapter

import android.view.View
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.community.card.component.R
import com.kotlin.android.community.card.component.databinding.ItemCommunityCardLongReviewBinding
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem
import com.kotlin.android.router.ext.getProvider


/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/23
 *
 * 长影评无图样式
 */
class CommunityCardLongReviewNoPicBinder(item: CommunityCardItem):
        CommunityCardBaseBinder<ItemCommunityCardLongReviewBinding>(item){

    override fun layoutId(): Int {
        return R.layout.item_community_card_long_review
    }

    override fun onBindViewHolder(binding: ItemCommunityCardLongReviewBinding, position: Int) {
        binding.mCommunityCardCommonBottom.data = this
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mCommunityCardMovieNameTv -> {
                item.movieId?.let {
                    getProvider(ITicketProvider::class.java) {
                        startMovieDetailsActivity(it)
                    }
                }
            }
            else -> {
                super.onClick(view)
            }
        }
    }
}