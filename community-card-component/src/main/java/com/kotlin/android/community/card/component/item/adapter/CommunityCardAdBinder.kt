package com.kotlin.android.community.card.component.item.adapter

import android.view.View
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.community.card.component.R
import com.kotlin.android.community.card.component.databinding.ItemCommunityCardAdBinding
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem
import com.kotlin.android.router.ext.getProvider


/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/23
 *
 * PK类型帖子
 */
class CommunityCardAdBinder(item: CommunityCardItem):
        CommunityCardBaseBinder<ItemCommunityCardAdBinding>(item) {
    override fun layoutId(): Int {
        return R.layout.item_community_card_ad
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mCommunityCardRoot -> {
                item.applinkData?.let {
                    getProvider(IMainProvider::class.java) {
                        startForApplink(it)
                    }
                }
            }
            else -> {
                super.onClick(view)
            }
        }
    }
}