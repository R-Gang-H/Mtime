package com.kotlin.android.card.monopoly.adapter

import android.view.View
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ItemMyPropBinding
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.PropCard
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @desc 我的道具卡列表
 * @author zhangjian
 * @date 2020/9/22 13:58
 */
class MyPropItemBinder(val data: PropCard, val user: UserInfo) : MultiTypeBinder<ItemMyPropBinding>() {

    var usePropsListener: IUseProps? = null

    val mProvider = getProvider(ICardMonopolyProvider::class.java)

    override fun layoutId(): Int = R.layout.item_my_prop

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is MyPropItemBinder && other.data == data
    }

    override fun onBindViewHolder(binding: ItemMyPropBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //封面图片
        binding.cardImageView.card = Card(cardCover = data.toolCover)
        //剩余的数量
        binding.tvCount.text = String.format(getString(R.string.str_my_prop_remain), data.remainCount)
        //购买样式
        ShapeExt.setShapeCorner2ColorWithColor(binding.tvUse, getColor(R.color.color_feb12a), 45)
        //使用道具
        if(data.toolId == Constants.TOOlS_CARD_DEMON){
            binding.tvUse.visibility = View.GONE
        }else{
            binding.tvUse.visibility = View.VISIBLE
        }
        binding.tvUse.onClick {
            usePropsListener?.usePropCard(data)
        }
    }

    interface IUseProps {
        fun usePropCard(data: PropCard)
    }

}