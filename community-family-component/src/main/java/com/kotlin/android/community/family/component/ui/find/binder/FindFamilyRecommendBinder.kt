package com.kotlin.android.community.family.component.ui.find.binder

import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.app.data.entity.family.FindFamilyRecommend
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ItemRecommendFindFamilyBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * @author zhangjian
 * @date 2022/3/18 17:14
 */
class FindFamilyRecommendBinder(
    val data: FindFamilyRecommend,
    var mamagerGroup: ((type: Long, id: Long, binder: MultiTypeBinder<*>) -> Unit)?
) :
    MultiTypeBinder<ItemRecommendFindFamilyBinding>() {
    override fun layoutId(): Int = R.layout.item_recommend_find_family

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FindFamilyRecommendBinder
    }

    override fun onBindViewHolder(binding: ItemRecommendFindFamilyBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.tvTitle.text = data.name
        val mAdapter = createMultiTypeAdapter(
            binding.rv,
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        )
        val list = ArrayList<FindFamilyRecommendItemBinder>()
        data.rcmdGroupList?.forEach {
            val binder = FindFamilyRecommendItemBinder(it, mamagerGroup)
            list.add(binder)
        }
        mAdapter.notifyAdapterAdded(list)
    }

}