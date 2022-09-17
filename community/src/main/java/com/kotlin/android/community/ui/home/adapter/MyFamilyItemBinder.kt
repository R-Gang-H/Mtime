package com.kotlin.android.community.ui.home.adapter

import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ItemCommunityMyFamilyBinding
import com.kotlin.android.community.ui.home.bean.MyFamilyItem
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/17
 *
 * 我的家族ItemBinder
 */
class MyFamilyItemBinder(val myFamilyItem: MyFamilyItem) : MultiTypeBinder<ItemCommunityMyFamilyBinding>() {

    val mFamilyProvider = getProvider(ICommunityFamilyProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_community_my_family
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is MyFamilyItemBinder && other.myFamilyItem.id == myFamilyItem.id
    }
}