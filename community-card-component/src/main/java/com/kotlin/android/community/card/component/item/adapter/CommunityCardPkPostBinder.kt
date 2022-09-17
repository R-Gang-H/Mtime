package com.kotlin.android.community.card.component.item.adapter

import com.kotlin.android.community.card.component.R
import com.kotlin.android.community.card.component.databinding.ItemCommunityCardPkPostBinding
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem


/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/23
 *
 * PK类型帖子
 */
class CommunityCardPkPostBinder(item: CommunityCardItem):
        CommunityCardBaseBinder<ItemCommunityCardPkPostBinding>(item) {
    override fun layoutId(): Int {
        return R.layout.item_community_card_pk_post
    }
}