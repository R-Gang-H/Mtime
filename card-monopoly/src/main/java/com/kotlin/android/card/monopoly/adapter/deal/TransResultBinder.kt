package com.kotlin.android.card.monopoly.adapter.deal

import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.util.Range
import android.view.View
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ItemGameTransResultBinding
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
 * @desc 交易成功失败结果展示
 * @author zhangjian
 * @date 2020/12/7 14:50
 */
class TransResultBinder(val data: Record, onBtnAction: (item: ItemData, binder: MultiTypeBinder<*>?) -> Unit) : MultiTypeBinder<ItemGameTransResultBinding>() {

    var mOnBtnAction: ((item: ItemData, binder: MultiTypeBinder<*>?) -> Unit)? = onBtnAction

    override fun layoutId(): Int = R.layout.item_game_trans_result

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TransResultBinder && other.data == data
    }

    override fun onBindViewHolder(binding: ItemGameTransResultBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        val card_name = if (data.cards?.size ?: 0 > 1) {
            data.cards?.get(1)?.cardName
        } else {
            "普通卡"
        }
        binding.tvMessage.movementMethod = LinkMovementMethod.getInstance()
        val range = Range(0, data.senderInfo?.nickName?.length)
        val userName = data.senderInfo?.nickName.toSpan().toColor(color = getColor(R.color.color_12c7e9)).toLinkNotUnderLine(range) { _, _ ->
            mOnBtnAction?.invoke(ItemData(actionType = Constants.TYPE_USE_TOOLS, userInfo = data.senderInfo), null)
        }
        binding.tvHandle.visibility = View.GONE
        when (data.recordType) {
            //4.交易成功
            Constants.GAME_RECORD_TRANS_SUCCESS -> {
                //标题
                binding.tvState.text = "交易成功"
                //附言
                val cardStyleName = card_name.toSpan().toColor(color = getColor(R.color.color_ff5a36))
                val mText = userName?.append("同意了你的")?.append(cardStyleName)?.append("卡片的交易")
                if (data.message?.isNotEmpty() == true) {
                    mText?.append("\n附送信息:${data.message}")
                }
                binding.tvMessage.text = mText
            }
            //5.交易失败
            Constants.GAME_RECORD_TRANS_FAIL -> {
                //标题
                binding.tvState.text = "交易失败"
                //附言
                val cardStyleName = card_name.toSpan().toColor(color = getColor(R.color.color_ff5a36))
                val mText = userName?.append("对你的")?.append(cardStyleName)?.append("的卡片交易失败")
                        ?: SpannableStringBuilder()
                if (data.message?.isNotEmpty() == true) {
                    mText.append("\n附送信息:${data.message}")
                }
                binding.tvMessage.text = mText

            }
        }

        //交易展示金币数
        val totalGold = (data.gold ?: 0) + data.addPrice
        binding.tvIconMessage.text = "$totalGold"
        //交易图片
        binding.cardImageView.card = Card(cardCover = DealRecordHelper.getSecondImageCover(data))
        //背景圆角
        ShapeExt.setShapeCorner2ColorWithColor(binding.llContainer, getColor(R.color.color_f2f3f6), 4.dp)
        //时间
        binding.tvTime.text = DealRecordHelper.setTime(data.enterTime)
        //按钮点击事件
        binding.tvHandle.onClick {
            val item = ItemData()
            item.actionType = -1
            item.position = position
            item.recordDetail = data.recordDetailId
            mOnBtnAction?.invoke(item, null)
        }
        //删除
        binding.rlClose.onClick {
            val item = ItemData()
            item.actionType = Constants.TYPE_DELETE
            item.position = position
            item.recordDetail = data.recordDetailId
            mOnBtnAction?.invoke(item, null)
        }

    }

}