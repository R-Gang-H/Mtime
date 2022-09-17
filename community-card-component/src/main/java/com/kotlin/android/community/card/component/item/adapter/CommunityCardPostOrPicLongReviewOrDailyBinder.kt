package com.kotlin.android.community.card.component.item.adapter

import android.view.View
import androidx.core.view.isVisible
import com.kotlin.android.community.card.component.R
import com.kotlin.android.community.card.component.databinding.ItemCommunityCardPostLongreviewDailyBinding
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.ktx.ext.core.setCompoundDrawablesAndPadding
import com.kotlin.android.ktx.ext.dimension.dp


/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/23
 *
 * 普通帖子/带图长影评/日志
 */
class CommunityCardPostOrPicLongReviewOrDailyBinder(item: CommunityCardItem):
        CommunityCardBaseBinder<ItemCommunityCardPostLongreviewDailyBinding>(item) {
    private val mTicketProvider by lazy { getProvider(ITicketProvider::class.java) }
    private val mFamilyProvider by lazy { getProvider(ICommunityFamilyProvider::class.java) }

    override fun layoutId(): Int {
        return R.layout.item_community_card_post_longreview_daily
    }

    override fun onBindViewHolder(binding: ItemCommunityCardPostLongreviewDailyBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.mCommunityCardCommonBottom.data = this
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mCommunityCardMovieNameTv -> {
                item.movieId?.let {
                    mTicketProvider?.startMovieDetailsActivity(it)
                }
            }
            R.id.mCommunityCardFamilyNameTv -> {
                item.familyId?.let {
                    mFamilyProvider?.startFamilyDetail(it)
                }
            }
            else -> {
                super.onClick(view)
            }
        }
    }
}