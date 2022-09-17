package com.kotlin.android.card.monopoly.adapter.deal

import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.util.Range
import android.view.View
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ItemGameTransBinding
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.card.monopoly.widget.dialog.view.DealCardView
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.ItemData
import com.kotlin.android.app.data.entity.monopoly.Record
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toLinkNotUnderLine
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @desc 交易布局
 * @author zhangjian
 * @date 2020/9/23 16:50
 */
class TransBinder(val data: Record,
                  onBtnAction: ((item: ItemData, binder: MultiTypeBinder<*>?) -> Unit),
                  jump: ((userId: Long) -> Unit)) : MultiTypeBinder<ItemGameTransBinding>() {

    var mOnBtnAction: ((item: ItemData, binder: MultiTypeBinder<*>?) -> Unit) = onBtnAction

    var mJump: ((userId: Long) -> Unit)? = jump

    override fun layoutId(): Int = R.layout.item_game_trans

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TransBinder && other.data == data
    }

    override fun onBindViewHolder(binding: ItemGameTransBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //添加点击username跳转
        binding.tvMsg.movementMethod = LinkMovementMethod.getInstance()
        when (data.recordType) {
            //发起交易
            Constants.GAME_RECORD_INIT_TRANS -> {
                //标题
                val name = data.senderInfo?.nickName?.toSpan().toColor(color = getColor(R.color.color_12c7e9))
                        .toLinkNotUnderLine(Range(0, data.senderInfo?.nickName?.length)) { _, _ ->
                            mOnBtnAction?.invoke(ItemData(actionType = Constants.TYPE_USE_TOOLS, userInfo = data.senderInfo), null)
                        }
                var cardName = SpannableStringBuilder()
                if (data.cards?.size ?: 0 > 1) {
                    cardName = data.cards?.get(1)?.cardName?.toSpan().toColor(color = getColor(R.color.color_ff5a36))
                            ?: SpannableStringBuilder()
                }
                val title = name?.append("想和你交易".toSpan())?.append(cardName)?.append("卡片")

                binding.tvMsg.text = title
            }

            //议价
            Constants.GAME_RECORD_DISS_PRICE -> {
                //标题
                val name = data.senderInfo?.nickName?.toSpan().toColor(color = getColor(R.color.color_12c7e9))
                        .toLinkNotUnderLine(Range(0, data.senderInfo?.nickName?.length)) { _, _ ->
                            mOnBtnAction?.invoke(ItemData(actionType = Constants.TYPE_USE_TOOLS, userInfo = data.senderInfo), null)
                        }
                val gold = data.addPrice.toString().toSpan().toColor(color = getColor(R.color.color_12c7e9))
                val title = name?.append("希望提高此卡片的交易价格,你需要增加".toSpan())?.append(gold)?.append("个金币")
                binding.tvMsg.text = title
                binding.tvAddPrice.visibility = View.VISIBLE
            }
        }
        //背景圆角
        ShapeExt.setShapeCorner2ColorWithColor(binding.llContainer, getColor(R.color.color_f2f3f6), 4.dp)
        //头像
        binding.civAvatar.setUserInfo(data.senderInfo)
        //点击头像跳转主页
        binding.civAvatar.onClick {
            mJump?.invoke(data.senderInfo?.userId ?: 0L)
        }
        //附言
        val msgInfo = StringBuilder()
        if (data.message?.isNotEmpty() == true) {
            msgInfo.append(String.format("附送信息:%s", data.message ?: ""))
        }
        //附送消息展示
        if (msgInfo.toString().isEmpty()) {
            binding.tvMessage.visibility = View.GONE
        } else {
            binding.tvMessage.visibility = View.VISIBLE
            binding.tvMessage.text = msgInfo.toString()
        }
        //附送金币数量
        val totalPrice = (data.gold ?: 0L) + data.addPrice
        binding.tvIconMessage.text = "$totalPrice"
        //时间
        binding.tvTime.text = DealRecordHelper.setTime(data.enterTime)

        data.cards?.apply {
            if (size > 1) {
                binding.dealCardView.data = DealCardView.Data(
                        srcCard = firstOrNull(),
                        desCard = get(1)
                )
            }
        }

        //按钮
        ShapeExt.setShapeCorner2ColorWithColor(binding.tvAgree, getColor(R.color.color_12c7e9), 45)
        ShapeExt.setShapeCorner2ColorWithColor(binding.tvAddPrice, getColor(R.color.color_feb12a), 45)
        ShapeExt.setShapeCorner2ColorWithColor(binding.tvRefuse, getColor(R.color.color_12c7e9), 45)
        //议价不展示加价
        if (data.recordType == Constants.GAME_RECORD_DISS_PRICE) {
            binding.tvAddPrice.visibility = View.INVISIBLE
        } else {
            binding.tvAddPrice.visibility = View.VISIBLE
        }

        binding.tvAgree.onClick {
            mOnBtnAction.invoke(ItemData(position = position, recordDetail = data.recordDetailId,
                    userName = data.senderInfo?.nickName,
                    price = data.gold?.plus(data.addPrice),
                    recordStatus = data.recordStatus,
                    actionType = Constants.TYPE_AGREE_TRADE), null)
        }
        binding.tvAddPrice.onClick {
            val itemData = ItemData()
            itemData.srcCard = Card(cardId = data.cards?.firstOrNull()?.cardId ?: 0)
            if (data.cards?.size ?: 0 > 1) {
                itemData.desCard = Card(data.cards?.get(1)?.cardId)
            }
            itemData.position = position
            itemData.recordStatus = data.recordStatus
            itemData.userInfo = data.senderInfo
            itemData.recordDetail = data.recordDetailId
            itemData.actionType = Constants.TYPE_ADD_TRADE_PRICE
            mOnBtnAction.invoke(itemData, null)
        }
        binding.tvRefuse.onClick {
            mOnBtnAction.invoke(ItemData(position = position,
                    recordStatus = data.recordStatus,
                    userName = data.senderInfo?.nickName,
                    desCard = data.cards?.firstOrNull(),
                    recordDetail = data.recordDetailId, actionType = Constants.TYPE_REFUSE_TRADE), null)
        }

        if (data.recordStatus == 1L) {
            binding.cslBtnContainer.visibility = View.GONE
        } else {
            binding.cslBtnContainer.visibility = View.VISIBLE
        }
        //删除
        binding.rlClose.onClick {
            mOnBtnAction.invoke(ItemData(recordDetail = data.recordDetailId, position = position, actionType = Constants.TYPE_DELETE), null)
        }


    }
}