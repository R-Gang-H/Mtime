package com.kotlin.android.card.monopoly.adapter.deal

import android.text.method.LinkMovementMethod
import android.util.Range
import android.view.View
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ItemGameBiddingBinding
import com.kotlin.android.card.monopoly.ext.setTextBySuitType
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.ItemData
import com.kotlin.android.app.data.entity.monopoly.Record
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toLinkNotUnderLine
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.ktx.ext.span.toTextSizeSpan
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @desc 竞价类型item(9竞价成功,10竞价失败)
 * @author zhangjian
 * @date 2020/9/23 15:21
 */
class BiddingBinder(
    val data: Record,
    onBtnAction: (item: ItemData, binder: MultiTypeBinder<*>?) -> Unit
) : MultiTypeBinder<ItemGameBiddingBinding>() {

    var mOnBtnAction: ((item: ItemData, binder: MultiTypeBinder<*>?) -> Unit)? = onBtnAction

    override fun layoutId(): Int = R.layout.item_game_bidding

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is BiddingBinder && other.data == data
    }

    override fun onBindViewHolder(binding: ItemGameBiddingBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        val card_name = DealRecordHelper.getCardName(data.cards, data.toolCards, data.suits)
        //点击跳转主页
        binding.tvMessage.movementMethod = LinkMovementMethod.getInstance()
        binding.flCoverContainer.visible()
        binding.llIconMessage.gone()
        when (data.recordType) {
            //10.竞拍失败
            Constants.GAME_RECORD_BIDDING_FAIL -> {
                //标题
                binding.tvState.text = getString(R.string.record_item_bidding_title_fail)
                //附言
                val cardStyleName =
                    card_name.toSpan().toColor(color = getColor(R.color.color_ff5a36))
                val mText = "您对".toSpan().append(cardStyleName)
                    .append(getString(R.string.record_item_bidding_fail))
                binding.tvMessage.text = mText
                //按钮
                binding.tvHandle.visible()
                binding.tvHandle.text = getString(R.string.record_item_bidding_again)
                //按钮样式
                ShapeExt.setShapeCorner2ColorWithColor(
                    binding.tvHandle,
                    getColor(R.color.color_12c7e9),
                    45
                )
//                //展示金币
//                binding.llIconMessage.visible()
//                binding.tvIconMessage.text = data.gold.toString()
                //按钮的点击事件
                setBtnOnClick(Constants.GAME_RECORD_BIDDING_FAIL.toInt(), position)
            }
            //9.竞拍成功
            Constants.GAME_RECORD_BIDDING_SUCCESS -> {
                //标题
                binding.tvState.text = getString(R.string.record_item_bidding_title_success)
                //附言
                val cardStyleName =
                    card_name.toSpan().toColor(color = getColor(R.color.color_ff5a36))
                val mText = "您对".toSpan().append(cardStyleName)
                    .append(getString(R.string.record_item_bidding_success))
                binding.tvMessage.text = mText
                //按钮
                binding.tvHandle.visible()
                binding.tvHandle.text = setTextBySuitType(DealRecordHelper.getImageCardType(data))
                //按钮样式
                ShapeExt.setShapeCorner2ColorWithColor(
                    binding.tvHandle,
                    getColor(R.color.color_feb12a),
                    45
                )
                //展示金币
                binding.llIconMessage.visible()
                binding.tvIconMessage.text = data.gold.toString()
                //按钮的点击事件
                setBtnOnClick(Constants.GAME_RECORD_BIDDING_SUCCESS.toInt(), position)

            }
            //11.拍卖成功
            Constants.GAME_RECORD_AUCTION_SUCCESS -> {
                //标题
                val title_fore = getString(R.string.record_item_auction_title_success_1).toSpan()
                val title_mid = card_name.toSpan().toColor(color = getColor(R.color.color_ff5a36))
                val title_back = getString(R.string.record_item_auction_title_success_2).toSpan()
                //标题
                val mTitleStr = title_fore.append(title_mid).append(title_back)
                binding.tvState.text = mTitleStr
                //附言
                val ragne = Range(0, data.senderInfo?.nickName?.length)
                val userName = data.senderInfo?.nickName.toSpan()
                    .toColor(color = getColor(R.color.color_12c7e9)).toLinkNotUnderLine(ragne) { _, _ ->
                    mOnBtnAction?.invoke(
                        ItemData(
                            actionType = Constants.TYPE_USE_TOOLS,
                            userInfo = data.senderInfo
                        ), null
                    )
                }
                val price = String.format("以%sG买走", data.gold.toString())
                val extraInfo = String.format(
                    "\n扣除拍卖费%sG,实得%sG,已经存入您的账户中.",
                    data.auctionFee.toString(),
                    data.auctionGold.toString()
                ).toSpan().toTextSizeSpan(12).toColor(color = getColor(R.color.color_8798af))

                val msg = "您拍卖的".toSpan().append(title_mid).append("已被".toSpan())
                    .append(userName).append(price.toSpan()).append(extraInfo)
                binding.tvMessage.text = msg
                //按钮
                binding.tvHandle.visibility = View.GONE
            }

            //12.拍卖失败
            Constants.GAME_RECORD_AUCTION_FAIL -> {
                //标题
                binding.tvState.text = getString(R.string.record_item_auction_title_fail)
                //附言
                val name = card_name.toSpan().toColor(color = getColor(R.color.color_ff5a36)) ?: ""
                val mText = getString(R.string.record_item_first).toSpan().append(name)
                    .append(getString(R.string.record_item_auction_fail))
                binding.tvMessage.text = mText
                //按钮
                binding.tvHandle.visible()
                binding.tvHandle.text = setTextBySuitType(DealRecordHelper.getImageCardType(data))
                //按钮样式
                ShapeExt.setShapeCorner2ColorWithColor(
                    binding.tvHandle,
                    getColor(R.color.color_feb12a),
                    45
                )
                //按钮点击事件
                setBtnOnClick(Constants.GAME_RECORD_AUCTION_FAIL.toInt(), position)
            }
            //0.赠送卡片
            Constants.GAME_RECORD_GIFT_CARD -> {
                //标题
                binding.tvState.text = getString(R.string.str_gift_card_title)
                //附言
                binding.tvMessage.text = data.message
                //隐藏按钮
                binding.tvHandle.gone()
            }
            //17.赠送金币
            Constants.GAME_RECORD_GIFT_GOLD -> {
                //标题
                binding.tvState.text = String.format(
                    getString(R.string.str_gift_gold_title),
                    (data.gold ?: 0L).toInt()
                )
                //附言
                binding.tvMessage.text = data.message
                //隐藏按钮
                binding.tvHandle.gone()
                //隐藏卡片
                binding.flCoverContainer.gone()
            }
            //18.赠送道具卡
            Constants.GAME_RECORD_GIFT_PROP -> {
                //标题
                binding.tvState.text = getString(R.string.str_gift_prop_title)
                //附言
                binding.tvMessage.text = data.message
                //隐藏按钮
                binding.tvHandle.gone()
            }


        }
        //拍卖图片
        when(DealRecordHelper.getImageCardType(data)){
            Constants.TYPE_SUIT->{
                binding.cardImageView.visibility = View.GONE
                binding.suitImageView.visibility = View.VISIBLE
                binding.suitImageView.data = Suit(suitCover = DealRecordHelper.getImageCover(data))
            }
            else->{
                binding.cardImageView.visibility = View.VISIBLE
                binding.suitImageView.visibility = View.GONE
                binding.cardImageView.card = Card(cardCover = DealRecordHelper.getImageCover(data))
            }
        }

        //背景圆角
        ShapeExt.setShapeCorner2ColorWithColor(
            binding.llContainer,
            getColor(R.color.color_f2f3f6),
            4.dp
        )
        //时间
        binding.tvTime.text = DealRecordHelper.setTime(data.enterTime)
        //删除
        binding.rlClose.onClick {
            val item = ItemData()
            item.actionType = Constants.TYPE_DELETE
            item.position = position
            item.recordDetail = data.recordDetailId
            mOnBtnAction?.invoke(item, null)
        }

    }

    private fun setBtnOnClick(type: Int, position: Int) {
        //按钮点击事件
        binding?.tvHandle?.onClick {
            val item = ItemData()
            item.actionType = type
            item.position = position
            item.recordDetail = data.recordDetailId
            mOnBtnAction?.invoke(item, null)
        }
    }

}