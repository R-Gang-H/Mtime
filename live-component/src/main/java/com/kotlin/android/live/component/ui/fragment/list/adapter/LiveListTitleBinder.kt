package com.kotlin.android.live.component.ui.fragment.list.adapter

import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.databinding.ItemLiveListBinding
import com.kotlin.android.live.component.databinding.ItemLiveListTitleBinding
import com.kotlin.android.live.component.viewbean.LiveListInfoBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/3/4
 */
class LiveListTitleBinder(private val iconResId: Int, private val titleResId: Int)
    : MultiTypeBinder<ItemLiveListTitleBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_live_list_title
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is LiveListTitleBinder
    }

    override fun onBindViewHolder(binding: ItemLiveListTitleBinding, position: Int) {
        binding.mLiveListTitleTv.setText(titleResId)
        binding.mLiveListTitleBgIv.setImageResource(iconResId)
    }
}