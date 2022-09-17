package com.kotlin.android.live.component.ui.adapter

import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.databinding.ItemLiveSharePosterBinding
import com.kotlin.android.live.component.viewbean.LiveSharePosterItemViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 直播生成海报分享平台Item
 */
class LiveSharePosterItemBinder(var bean: LiveSharePosterItemViewBean) :
    MultiTypeBinder<ItemLiveSharePosterBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_live_share_poster
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is LiveSharePosterItemBinder && other.bean != bean
    }

}