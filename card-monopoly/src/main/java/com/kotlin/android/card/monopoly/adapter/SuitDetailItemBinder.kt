package com.kotlin.android.card.monopoly.adapter

import android.view.View
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.ItemSuitDetailBinding
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.app.data.entity.monopoly.CardImageDetailBean
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.time.TimeExt.millis2String
import com.kotlin.android.mtime.ktx.ext.ShapeExt

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @desc 套装详情展示item
 * @author zhangjian
 * @date 2020/9/10 09:58
 */
class SuitDetailItemBinder(
    val suit: Suit, jump: ((userId: Long) -> Unit),
    mCardJump: ((cardDetails: CardImageDetailBean) -> Unit),
    val mSuitJump: ((suitId: Long,name:String) -> Unit)
) : MultiTypeBinder<ItemSuitDetailBinding>() {

    var mJump: ((userId: Long) -> Unit)? = jump
    var mCardJump: ((cardDetails: CardImageDetailBean) -> Unit)? = mCardJump

    override fun layoutId(): Int = R.layout.item_suit_detail

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SuitDetailItemBinder && other.suit == suit
    }

    override fun onBindViewHolder(binding: ItemSuitDetailBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //封面
        binding.suitImageView.apply {
            data = suit
        }
        //标识
        if (suit.earliestMixUser == null) {
            binding.earlyUserInfo.visibility = View.GONE
        } else {
            binding.earlyUserInfo.visibility = View.VISIBLE
        }
        //合成者头像
        binding.ivAvatar.setUserInfo(suit.earliestMixUser?.avatarUrl)

        //标识
        ShapeExt.setShapeCorner2Color2Stroke(
            binding.rlFlagContain,
            R.color.color_ffffff,
            45,
            R.color.color_c9cedc,
            2,
            true
        )
        //卡片展示(始终是5个)
        binding.suitCardView.apply {
            data = suit.cardList
            action = {
                val cardDetails = CardImageDetailBean()
                cardDetails.card = suit.cardList
                mCardJump?.invoke(cardDetails)
            }
        }
        //时间
        binding.tvTime.text = String.format(
            "Mtime · %s发行 · 共%d张卡", millis2String(
                (suit.issueTime
                    ?: 0) * 1000, "yyyy年MM月dd日"
            ), suit.cardList?.size
        )

        //查看套装会员
        binding.tvSeeSuitMember.visibility = View.VISIBLE
        binding.tvSeeSuitMember.onClick {
            val mProvider =
                getProvider(ICardMonopolyProvider::class.java)
            mProvider?.startSuitRank(suit.suitId, suit.type == 2L)
        }

        //是否已收集
        if (suit.hasMixed) {
            binding.ivCollectionFlag.visibility = View.VISIBLE
        } else {
            binding.ivCollectionFlag.visibility = View.GONE
        }
        //套装评论跳转
        ShapeExt.setShapeCorner2Color2Stroke(
            binding.tvComment,
            R.color.color_12c7e9,
            45,
            R.color.color_12c7e9,
            1,
            false
        )
        binding.tvComment.onClick {
            mSuitJump.invoke(suit.suitId,suit.suitName?:"")
        }

        //头像跳转
        binding.ivAvatar.onClick {
            mJump?.invoke(suit.earliestMixUser?.userId ?: 0L)
        }
        //名称跳转
        binding.tvUserName.onClick {
            mJump?.invoke(suit.earliestMixUser?.userId ?: 0L)
        }
    }

}