package com.kotlin.android.mine.binder

import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.CardItemViewBean
import com.kotlin.android.mine.bean.CouponItemViewBean
import com.kotlin.android.mine.databinding.ItemGiftCardBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 礼品卡binder
 */
class GiftCardBinder(val bean: CardItemViewBean) : MultiTypeBinder<ItemGiftCardBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_gift_card
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is GiftCardBinder && other.bean != bean
    }
}