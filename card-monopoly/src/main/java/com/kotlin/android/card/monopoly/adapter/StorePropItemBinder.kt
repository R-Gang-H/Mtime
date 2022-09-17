package com.kotlin.android.card.monopoly.adapter

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.ItemStorePropBinding
import com.kotlin.android.card.monopoly.ext.showCardMonopolyCommDialog
import com.kotlin.android.card.monopoly.widget.dialog.CommDialog
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.PropCard
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @desc 道具卡展示item
 * @author zhangjian
 * @date 2020/9/19 16:56
 */
class StorePropItemBinder(
    val data: PropCard,
    val activity: FragmentActivity?,
    onBuyPropCard: ((bean: PropCard) -> Unit)
) : MultiTypeBinder<ItemStorePropBinding>() {

    private var mOnBuyPropCard = onBuyPropCard

    override fun layoutId(): Int = R.layout.item_store_prop

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is StorePropItemBinder && other.data == data
    }

    override fun onBindViewHolder(binding: ItemStorePropBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //封面图片
        binding.cardImageView.card = Card(cardCover = data.toolCover)
        //今日售出的数量
        binding.tvCount.text =
            String.format(getString(R.string.str_store_prop), data.todaySaleCount)
        //金币价格
        binding.tvPrice.text = data.price.toString()
        //购买样式
        ShapeExt.setShapeCorner2ColorWithColor(binding.tvBuy, getColor(R.color.color_feb12a), 45)
        //设置购买样式
        if(data.buy == false){
            binding.tvBuy.visibility = View.GONE
        }else{
            binding.tvBuy.visibility = View.VISIBLE
        }
        //购买点击事件
        binding.tvBuy.onClick {
            activity?.showCardMonopolyCommDialog(
                CommDialog.Style.BUY_COUNT, CommDialog.Data(
                    biddingPrice = data.price
                )
            ) {
                it?.buyCount?.apply {
                    data.buyNum = this
                    mOnBuyPropCard.invoke(data)
                }
            }
        }

    }

}