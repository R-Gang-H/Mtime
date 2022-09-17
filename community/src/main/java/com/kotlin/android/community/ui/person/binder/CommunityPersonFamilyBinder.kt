package com.kotlin.android.community.ui.person.binder

import android.view.View
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ItemCommunityPersonFamilyBinding
import com.kotlin.android.community.ui.person.bean.PersonFamilyViewBean
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.ShapeExt

import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by Wangwei on 2020/9/25
 * description:社区个人主页中家族信息卡片
 */
class CommunityPersonFamilyBinder(var bean: PersonFamilyViewBean) : MultiTypeBinder<ItemCommunityPersonFamilyBinding>() {
    override fun layoutId(): Int = R.layout.item_community_person_family

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommunityPersonFamilyBinder && other.bean.status != bean.status
    }

    override fun onBindViewHolder(binding: ItemCommunityPersonFamilyBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        ShapeExt.setShapeCorner2Color(binding.familyRootView,R.color.color_ffffff,4.dp)

//        binding.familyRootView.familyHeadIv.
//        binding.familyItemView.joinFL?.onClick {//加入家族
//            super.onClick(it)
//        }
    }

    override fun onClick(view: View) {
        if(view.id == R.id.familyRootView){//跳转到家族详情
            val provider: ICommunityFamilyProvider? = getProvider(ICommunityFamilyProvider::class.java)
            provider?.startFamilyDetail(bean.groupId)
        }else{
            super.onClick(view)
        }
    }
}