package com.kotlin.android.community.family.component.ui.details.adapter

import com.kotlin.android.community.family.component.databinding.ItemFamilyDetailMemberBinding
import com.kotlin.android.community.family.component.ui.details.bean.FamilyDetailMember
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.widget.adapter.bindingadapter.BaseBindingAdapter

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/29
 */
class FamilyDetailMemberAdapter: BaseBindingAdapter<FamilyDetailMember, ItemFamilyDetailMemberBinding>() {
    override fun onBinding(binding: ItemFamilyDetailMemberBinding?, item: FamilyDetailMember, position: Int) {
        binding?.root?.setOnClickListener {
            //item点击
            getProvider(ICommunityPersonProvider::class.java)
                    ?.startPerson(item.id)
        }
    }
}