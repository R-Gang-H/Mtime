package com.kotlin.android.community.ui.person.binder

import android.view.View
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ItemCommunityPersonAttendBinding
import com.kotlin.android.community.ui.person.bean.MyFriendViewBean
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by Wangwei on 2020/9/25
 * description:社区个人主页中卡片
 * 关注和粉丝列表binder
 */
class CommunityPersonFriendBinder(var bean: MyFriendViewBean) :
    MultiTypeBinder<ItemCommunityPersonAttendBinding>() {
    override fun layoutId(): Int = R.layout.item_community_person_attend

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommunityPersonFriendBinder && other.bean.userId != bean.userId
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_icon, R.id.tv_name -> {
                val provider: ICommunityPersonProvider? =
                    getProvider(ICommunityPersonProvider::class.java)
                provider?.startPerson(bean.userId)
            }
            else -> super.onClick(view)
        }

    }
}