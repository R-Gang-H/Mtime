package com.kotlin.android.card.monopoly.adapter

import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.ItemAuctionBuyBinding
import com.kotlin.android.card.monopoly.ext.getCountDownTime
import com.kotlin.android.card.monopoly.ext.showCardMonopolyCommDialog
import com.kotlin.android.card.monopoly.widget.dialog.CommDialog
import com.kotlin.android.app.data.entity.monopoly.Bid
import com.kotlin.android.app.data.entity.monopoly.CardImageDetailBean
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlin.math.ceil

/**
 * @desc 拍卖行购买的item
 * @author zhangjian
 * @date 2020/9/15 10:25
 */
class AuctionBuyBinder(
    val data: Bid,
    val activity: FragmentActivity?,
    onBuyClick: ((type: Int, price: Long, auctionId: Long, index: Int) -> Unit),
    jump: ((userId: Long) -> Unit),
    mCardJump: ((image: View, cardDetails: CardImageDetailBean) -> Unit)
) : MultiTypeBinder<ItemAuctionBuyBinding>() {

    var mOnBuyClick: ((type: Int, price: Long, auctionId: Long, index: Int) -> Unit)? = onBuyClick
    var mJump: ((userId: Long) -> Unit)? = jump
    var mCardJump: ((image: View, cardDetails: CardImageDetailBean) -> Unit)? = mCardJump
    var timer: CountDownTimer? = null

    override fun layoutId(): Int = R.layout.item_auction_buy

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is AuctionBuyBinder && other.data == data
    }

    override fun onUnBindViewHolder(binding: ItemAuctionBuyBinding?) {
        super.onUnBindViewHolder(binding)
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    override fun onBindViewHolder(binding: ItemAuctionBuyBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //背景
        ShapeExt.setShapeCorner2ColorWithColor(
            binding.itemContainer,
            ContextCompat.getColor(binding.itemContainer.context, R.color.color_f2f3f6),
            12,
            false
        )
        //卖家信息
        binding.tvName.text = String.format(
            binding.tvName.context.getString(R.string.saler_name),
            data.vendorInfo.nickName
        )
        //点击昵称跳转主页
        binding.tvName.onClick {
            mJump?.invoke(data.vendorInfo.userId)
        }

        //倒计时
        val mTime = data.auctionEndTime - (TimeExt.getNowMills() / 1000)
        timer = binding.tvTime.getCountDownTime(mTime, binding.tvTime)
        if (mTime > 0L) {
            timer?.start()
        } else {
            timer?.cancel()
            binding.tvTime.setText(R.string.str_finish)
        }

        //名称
        val nameDes =
            binding.desNameView.setNameDataBean(data.auctionCard.type, data.auctionCard.cardName)
        binding.desNameView.setContentData(nameDes)

        //起拍价
        val startDes =
            binding.desNameView.setStartDataBean(data.bidPrice, data.startPrice, data.bidUserId)
        binding.desStartView.setContentData(startDes)

        //一口价
        val fixDes = binding.desNameView.setFixDataBean(data.fixPrice)
        binding.desFixView.setContentData(fixDes)
        //封面
        if (data.auctionCard.type == 3L) {
            setImageViewState(false)
            binding.suitImageView.data = Suit(suitCover = data.auctionCard?.cardCover ?: "")
            binding.suitImageView.onClick {
                data.auctionCard?.apply {
                    val cardDetailsInfo = CardImageDetailBean()
                    cardDetailsInfo.card = listOf(this)
                    mCardJump?.invoke(binding.suitImageView, cardDetailsInfo)
                }
            }
        } else {
            setImageViewState(true)
            binding.cardImageView.card = data.auctionCard
            binding.cardImageView.onClick {
                data.auctionCard?.apply {
                    val cardDetailsInfo = CardImageDetailBean()
                    cardDetailsInfo.card = listOf(this)
                    mCardJump?.invoke(binding.suitImageView, cardDetailsInfo)
                }
            }

        }

        //竞价按钮展示
        if (data.bidUserId == UserManager.instance.userId) {
            //自己竞价
            ShapeExt.setShapeCorner2Color(binding.btnStartPrice, R.color.color_8898c2, 45)
            binding.btnStartPrice.isEnabled = false
        } else {
            binding.btnStartPrice.isEnabled = true
            ShapeExt.setShapeCorner2Color(binding.btnStartPrice, R.color.color_feb12a, 45)
        }

        //展示价格
        val mBidPrice: Long = if (data.bidPrice == 0L) {
            data.startPrice
        } else {
            data.bidPrice
        }
        //点击展示弹框
        binding.btnStartPrice.onClick {
            val price = ceil(mBidPrice * 1.05).toLong() + 1
            val label2 = "*请输入大于 ".toSpan()
                .append(
                    "$price".toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                ).append(
                    " 的整数"
                )
            if (price >= data.fixPrice) {
                mOnBuyClick?.invoke(2, data.fixPrice, data.auctionId, position)
            } else {
                activity?.showCardMonopolyCommDialog(
                    CommDialog.Style.BIDDING, CommDialog.Data(
                        label1 = "出价",
                        label2 = label2,
                        biddingPrice = price

                    )
                ) {
                    it?.biddingPrice?.apply {
                        mOnBuyClick?.invoke(1, this, data.auctionId, position)
                    }
                }
            }
        }
        //一口价按钮展示
        ShapeExt.setShapeCorner2Color(binding.btnFixPrice, R.color.color_12c7e9, 45)
        binding.btnFixPrice.onClick {
            if (data.fixPrice >= 5000L) {
                val message = "是否需要".toSpan()
                    .append(
                        data.fixPrice.toString().toSpan().toBold()
                            .toColor(color = getColor(R.color.color_12c7e9))
                    )
                    .append("金币购买此卡片？")
                activity?.showCardMonopolyCommDialog(
                    style = CommDialog.Style.SUBMIT_BUY,
                    data = CommDialog.Data(message = message),
                ) {
                    mOnBuyClick?.invoke(2, data.fixPrice, data.auctionId, position)
                }
            } else {
                mOnBuyClick?.invoke(2, data.fixPrice, data.auctionId, position)
            }
        }
    }

    private fun setImageViewState(cardImageVisible: Boolean) {
        if (cardImageVisible) {
            binding?.cardImageView?.visibility = View.VISIBLE
            binding?.suitImageView?.visibility = View.GONE
        } else {
            binding?.cardImageView?.visibility = View.GONE
            binding?.suitImageView?.visibility = View.VISIBLE
        }
    }

}