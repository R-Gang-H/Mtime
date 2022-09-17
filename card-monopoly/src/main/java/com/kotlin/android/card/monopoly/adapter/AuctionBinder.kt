package com.kotlin.android.card.monopoly.adapter

import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.ItemAuctionAucBinding
import com.kotlin.android.card.monopoly.ext.getCountDownTime
import com.kotlin.android.card.monopoly.ext.setTextBySuitType
import com.kotlin.android.app.data.entity.monopoly.Auction
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.CardImageDetailBean
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlin.math.ceil

/**
 * @desc 拍卖行购买的item
 * @author zhangjian
 * @date 2020/9/15 10:25
 */
class AuctionBinder(
    val data: Auction,
    onRetrieveCard: ((auctionId: Long, type: Long) -> Unit),
    jump: ((userId: Long) -> Unit),
    mCardJump: ((image: View, cardDetails: CardImageDetailBean) -> Unit)
) : MultiTypeBinder<ItemAuctionAucBinding>() {

    var mOnRetrieveCard: ((auctionId: Long, type: Long) -> Unit)? = onRetrieveCard

    var mJump: ((userId: Long) -> Unit)? = jump

    var timer: CountDownTimer? = null

    var mCardJump: ((image: View, cardDetails: CardImageDetailBean) -> Unit)? = mCardJump

    override fun layoutId(): Int = R.layout.item_auction_auc

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is AuctionBinder && other.data == data
    }

    override fun onUnBindViewHolder(binding: ItemAuctionAucBinding?) {
        super.onUnBindViewHolder(binding)
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    override fun onBindViewHolder(binding: ItemAuctionAucBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //背景
        ShapeExt.setShapeCorner2ColorWithColor(
            binding.itemContainer,
            ContextCompat.getColor(binding.itemContainer.context, R.color.color_f2f3f6),
            12,
            false
        )
        if (data.bidUserNickName.isNullOrEmpty()) {
            //买家信息
            binding.tvName.text = "无人竞价"
        } else {
            //买家信息
            binding.tvName.text = String.format(
                binding.tvName.context.getString(R.string.buyer_name),
                data.bidUserNickName
            )
            binding.tvName.onClick {
                mJump?.invoke(data.bidUserId)
            }
        }

        binding.cardImageView.onClick {
            val location = IntArray(2)
            it.getLocationOnScreen(location)
            val cardDetailsInfo = CardImageDetailBean()
            cardDetailsInfo.card = listOf(data.auctionCard ?: Card())
            cardDetailsInfo.pointX = location[0] + it.measuredWidth / 2f
            cardDetailsInfo.pointX = location[1] + it.measuredHeight / 2f
            mCardJump?.invoke(binding.cardImageView, cardDetailsInfo)
        }
        //封面(卡片和封面的比例不一致,通过type判断)
        if(data.auctionCard?.type == 3L){
            setImageViewState(false)
            binding.suitImageView.data = Suit(suitCover = data.auctionCard?.cardCover?:"")
            binding.suitImageView.onClick {
                data.auctionCard?.apply {
                    val cardDetailsInfo = CardImageDetailBean()
                    cardDetailsInfo.card = listOf(this)
                    mCardJump?.invoke(binding.suitImageView, cardDetailsInfo)
                }
            }
        }else{
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

        //名称
        val nameDes =
            binding.desNameView.setNameDataBean(data.auctionCard?.type, data.auctionCard?.cardName)
        binding.desNameView.setContentData(nameDes)

        //起拍价
        val startDes =
            binding.desNameView.setStartDataBean(data.bidPrice, data.startPrice, data.bidUserId)
        binding.desStartView.setContentData(startDes)

        //一口价
        val fixDes = binding.desNameView.setFixDataBean(data.fixPrice)
        binding.desFixView.setContentData(fixDes)

        //倒计时
        val mTime = data.auctionEndTime - (TimeExt.getNowMills() / 1000)
        timer = binding.tvTime.getCountDownTime(mTime, binding.tvTime)
        if (mTime > 0L) {
            timer?.start()
        } else {
            timer?.cancel()
        }

        //一口价按钮展示(0拍卖进行中，1一口价成交待领取，2流拍待取回，3一口价/竞拍成功已领取，4流拍已取回，6取消拍卖已取回)
        if (data.bidUserNickName.isNullOrEmpty()) {
            if (mTime > 0) {
                ShapeExt.setShapeCorner2Color(binding.btnCancel, R.color.color_12c7e9, 45)
                binding.btnCancel.visibility = View.VISIBLE
                binding.llPickCard.visibility = View.GONE
                binding.tvTime.visibility = View.VISIBLE
            } else {
                binding.tvTime.visibility = View.GONE
                binding.btnCancel.visibility = View.GONE
                binding.llPickCard.visibility = View.VISIBLE
                //展示多少小时后销毁
                showRestTime(binding.tipsView)
                ShapeExt.setShapeCorner2Color(binding.btnPickCard, R.color.color_feb12a, 45)
                //展示按钮的文案
                binding.btnPickCard.text = setTextBySuitType(data.auctionCard?.type)
            }
            //取消拍卖
            binding.btnCancel.onClick {
                mOnRetrieveCard?.invoke(data.auctionId, data.auctionStatus)
            }
            //流拍取回卡片
            binding.btnPickCard.onClick {
                mOnRetrieveCard?.invoke(data.auctionId, data.auctionStatus)
            }

        } else {
            binding.btnCancel.visibility = View.GONE
        }


    }

    private fun setImageViewState(cardImageVisible:Boolean){
        if(cardImageVisible){
            binding?.cardImageView?.visibility = View.VISIBLE
            binding?.suitImageView?.visibility = View.GONE
        }else{
            binding?.cardImageView?.visibility = View.GONE
            binding?.suitImageView?.visibility = View.VISIBLE
        }
    }

    /**
     * 展示剩余销毁时间
     */
    private fun showRestTime(tv: TextView) {
        val resetHour = data.destroyTime - (TimeExt.getNowMills() / 1000)
        val showHour = ceil(resetHour / 3600f).toInt()
        if (showHour > 0) {
            tv.text = String.format(getString(R.string.auction_receive_card_limit), showHour)
        }
    }
}