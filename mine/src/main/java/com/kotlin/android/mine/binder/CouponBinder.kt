package com.kotlin.android.mine.binder

import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.CouponItemViewBean
import com.kotlin.android.mine.databinding.ItemCouponBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 购票优惠券binder
 */
class CouponBinder(val bean: CouponItemViewBean) : MultiTypeBinder<ItemCouponBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_coupon
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CouponBinder && other.bean != bean
    }
}