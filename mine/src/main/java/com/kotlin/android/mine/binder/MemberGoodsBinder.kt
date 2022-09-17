package com.kotlin.android.mine.binder

import android.util.Range
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.GoodsViewBean
import com.kotlin.android.mine.databinding.ItemMemberGoodsBinding
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/12/23
 * description:兑换商品
 */
class MemberGoodsBinder(var bean:GoodsViewBean): MultiTypeBinder<ItemMemberGoodsBinding>() {
    override fun layoutId(): Int  = R.layout.item_member_goods

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean  = other is MemberGoodsBinder
            && other.bean.id == bean.id && other.bean.isHiddenDes == bean.isHiddenDes && other.bean.mNeedNum == bean.mNeedNum

    override fun onBindViewHolder(binding: ItemMemberGoodsBinding, position: Int) {
        super.onBindViewHolder(binding, position)
//        设置M豆
        var mNeedNumContent = if (bean.mNeedNum >= 9999999) "9999999" else bean.mNeedNum.toString()
        var mNumberStr =getString(R.string.mine_member_m_bean_format).format(mNeedNumContent)
        val range = Range(0, mNeedNumContent.length)
        mNumberStr.toSpan().toBold(range)?.toColor(range,color =  getColor(R.color.color_20a0da))?.apply {
            binding.mNeedNumTv?.text = this
        }

        ShapeExt.setShapeColorAndCorner(binding.exchangeTv,R.color.color_20a0da,12)
    }
}