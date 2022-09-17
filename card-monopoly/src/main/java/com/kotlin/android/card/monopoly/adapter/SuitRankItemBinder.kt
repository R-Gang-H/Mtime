package com.kotlin.android.card.monopoly.adapter

import androidx.core.content.ContextCompat
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.ItemSuitRankBinding
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @desc 排行榜
 * @author zhangjian
 * @date 2020/9/11 10:44
 */
class SuitRankItemBinder(val data: UserInfo, jump: ((userId: Long) -> Unit)) : MultiTypeBinder<ItemSuitRankBinding>() {

    var mJump: ((userId: Long) -> Unit)? = jump

    override fun layoutId(): Int = R.layout.item_suit_rank

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SuitRankItemBinder && other.data == data
    }

    override fun onBindViewHolder(binding: ItemSuitRankBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.civRank.setUserInfo(data)
        ShapeExt.setShapeCorner2ColorWithColor(binding.container,
                ContextCompat.getColor(binding.container.context, R.color.color_f2f3f6),
                12, false)
        binding.tvCount.text = String.format(getString(R.string.have_suit_count), data.suitCount)
        //跳转
        binding.container.onClick {
            mJump?.invoke(data.userId)
        }

    }

}