package com.kotlin.android.community.ui.home.adapter

import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ItemCommunityJoinFamilyBinding

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/17
 *
 * 我的家族ItemBinder
 */
class JoinFamilyItemBinder : MultiTypeBinder<ItemCommunityJoinFamilyBinding>() {

    val mFamilyProvider = getProvider(ICommunityFamilyProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_community_join_family
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is JoinFamilyItemBinder
    }
}