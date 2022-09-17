package com.kotlin.android.card.monopoly.adapter.deal

import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.util.Range
import android.view.View
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ItemGamePropsBinding
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.card.monopoly.widget.dialog.view.DealCardView
import com.kotlin.android.card.monopoly.widget.dialog.view.DealCardView.Companion.EXCHANGE_COIN
import com.kotlin.android.app.data.entity.monopoly.*
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toLinkNotUnderLine
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @desc  道具的item相关布局
 * @author zhangjian
 * @date 2020/9/24 15:27
 */
class PropsBinder(val data: Record, onBtnAction: ((item: ItemData, binder: MultiTypeBinder<*>?) -> Unit)) : MultiTypeBinder<ItemGamePropsBinding>() {

    var mOnBtnAction: ((item: ItemData, binder: MultiTypeBinder<*>?) -> Unit)? = onBtnAction

    override fun layoutId(): Int = R.layout.item_game_props

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is PropsBinder && other.data == data
    }

    override fun onBindViewHolder(binding: ItemGamePropsBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //背景圆角
        ShapeExt.setShapeCorner2ColorWithColor(binding.llContainer, getColor(R.color.color_f2f3f6), 4.dp)
        //添加点击username跳转
        binding.tvMsg.movementMethod = LinkMovementMethod.getInstance()
        //自己
        if (data.senderInfo?.userId == UserManager.instance.userId) {
            binding.civAvatar.visibility = View.GONE
            binding.llInfoContainer.visibility = View.GONE
            binding.tvTime.visibility = View.VISIBLE
            binding.tvPlaceHolder.visibility = View.GONE
            binding.tvTime.text = DealRecordHelper.setTime(data.enterTime)
            //判断有没有目标道具卡(使用道具卡后产生的道具卡---例如复制卡)
            val targetPropCard = if (data.toolCards?.size ?: 0 > 1) {
                data.toolCards?.get(1)
            } else {
                PropCard()
            }
            val cardName = data.toolCards?.firstOrNull()?.toolName?.toSpan().toColor(color = getColor(R.color.color_feb12a))
            val title = "你使用了".toSpan().append(cardName)
                    .append(getPropInfo(data.toolCards?.firstOrNull(), targetPropCard, data.gold,data.suits?.firstOrNull()))
            binding.tvMsg.text = title
        } else {
            //头像
            binding.civAvatar.setUserInfo(data.senderInfo)
            //点击头像跳转
            binding.civAvatar.onClick {
                mOnBtnAction?.invoke(ItemData(actionType = Constants.TYPE_USE_TOOLS, userInfo = data.senderInfo), null)
            }
            //标题
            val name = data.senderInfo?.nickName?.toSpan().toColor(color = getColor(R.color.color_12c7e9))
                    .toLinkNotUnderLine(Range(0, data.senderInfo?.nickName?.length)) { _, _ ->
                        mOnBtnAction?.invoke(ItemData(actionType = Constants.TYPE_USE_TOOLS, userInfo = data.senderInfo), null)
                    }
            val toolsName = data.toolCards?.firstOrNull()?.toolName?.toSpan().toColor(color = getColor(R.color.color_feb12a))
            val title = name?.append("对你使用了".toSpan())?.append(toolsName)
            binding.civAvatar.visibility = View.VISIBLE
            binding.tvTime.visibility = View.VISIBLE
            binding.tvPlaceHolder.visibility = View.VISIBLE
            //时间
            binding.tvTime.text = DealRecordHelper.setTime(data.enterTime)
            when {
                data.toolCards?.firstOrNull()?.toolId ?: 0L == Constants.TOOlS_CARD_HACKER -> {
                    binding.llInfoContainer.visibility = View.GONE
                    when (data.toolCardResult) {
                        2L->{
                            val msg = title?.append("，被反弹卡回击。".toSpan())
                            binding.tvMsg.text = msg
                        }
                        else->{
                            binding.tvMsg.text = title
                        }
                    }
                    //黑客卡不展示
    //                val info = "规定时间内，你的所有卡片将变成黑客。".toSpan()
    //                binding.tvMsg.text = title?.append(info)
                    //时间
                    binding.tvTime.text = DealRecordHelper.setTime(data.enterTime)
    //                binding.llInfoContainer.visibility = View.VISIBLE
    //                binding.dealCardView.data = DealCardView.Data(
    //                        srcCard = Card(cardCover = data.toolCards?.firstOrNull()?.toolCover),
    //                        desCard = data.cards?.firstOrNull()
    //                )
    //                //按钮
    //                ShapeExt.setShapeCorner2ColorWithColor(binding.tvHandle, getColor(R.color.color_12c7e9), 45)
    //                binding.tvHandle.onClick {
    //                    mOnBtnAction?.invoke(ItemData(actionType = Constants.TYPE_USE_TOOLS, userInfo = data.senderInfo), null)
    //                }
                }
                data.toolCards?.firstOrNull()?.toolId ?: 0L == Constants.TOOlS_CARD_SLAVE -> {
                    //奴隶卡
                    binding.llInfoContainer.visibility = View.GONE
                    when (data.toolCardResult) {
                        2L->{
                            val msg = title?.append("，被反弹卡回击。".toSpan())
                            binding.tvMsg.text = msg
                            //时间
                            binding.tvTime.text = DealRecordHelper.setTime(data.enterTime)
                        }
                        else->{
                            binding.tvMsg.text = title
                            //时间
                            binding.tvTime.text = DealRecordHelper.setTime(data.enterTime)
                        }
                    }
                }
                data.toolCards?.firstOrNull()?.toolId ?: 0L == Constants.TOOlS_CARD_ROB ||
                        data.toolCards?.firstOrNull()?.toolId ?: 0L == Constants.TOOlS_CARD_ROB_LIMIT->{
                    //打劫卡信息展示
                    when (data.toolCardResult) {
                        2L -> {
                            binding.llInfoContainer.visibility = View.GONE
                            val msg = title?.append("，被防盗卡抵消。".toSpan())
                            binding.tvMsg.text = msg
                        }
                        3L -> {
                            binding.llInfoContainer.visibility = View.GONE
                            val msg = title?.append("，被你成功闪避开。".toSpan())
                            binding.tvMsg.text = msg
                        }
                        else -> {
                            showCardInfo(binding,title)
                        }
                    }
                }
                else -> {
                    showCardInfo(binding,title)
                }
            }
        }

        //删除
        binding.rlClose.onClick {
            mOnBtnAction?.invoke(ItemData(recordDetail = data.recordDetailId, position = position, actionType = Constants.TYPE_DELETE), null)
        }

    }

    fun showCardInfo(binding: ItemGamePropsBinding,title:SpannableStringBuilder?){
        binding.llInfoContainer.visibility = View.VISIBLE
        binding.civAvatar.visibility = View.VISIBLE
        binding.tvTime.visibility = View.VISIBLE
        binding.tvPlaceHolder.visibility = View.VISIBLE
        //时间
        binding.tvTime.text = DealRecordHelper.setTime(data.enterTime)
        //其余卡信息展示
        if (data.cards.isNullOrEmpty()) {
            //卡片为空,盗取的是金币
            binding.dealCardView.data = DealCardView.Data(
                    exchangeType = EXCHANGE_COIN,
                    srcCard = Card(cardCover = data.toolCards?.firstOrNull()?.toolCover),
                    coin = data.gold ?: 0L
            )
            //文案消息
            val coinCount = data.gold.toString().toSpan().toColor(color = getColor(R.color.color_feb12a))
            val msg = title?.append("，从你身上获得金币".toSpan())?.append(coinCount)
            binding.tvMsg.text = msg
        } else if (data.cards?.isNotEmpty() == true) {
            //卡片不为空,盗取的是卡片
            binding.dealCardView.data = DealCardView.Data(
                    srcCard = Card(cardCover = data.toolCards?.firstOrNull()?.toolCover),
                    desCard = data.cards?.firstOrNull()
            )
            //文案消息
            val cardName = data.cards?.firstOrNull()?.cardName?.toSpan().toColor(color = getColor(R.color.color_feb12a))
            val msg = title?.append("，从你口袋打劫了一张".toSpan())?.append(cardName)
            binding.tvMsg.text = msg
        }
        //按钮
        ShapeExt.setShapeCorner2ColorWithColor(binding.tvHandle, getColor(R.color.color_12c7e9), 45)
        binding.tvHandle.onClick {
            mOnBtnAction?.invoke(ItemData(actionType = Constants.TYPE_USE_TOOLS, userInfo = data.senderInfo), null)
        }
    }


    /**
     * 道具卡使用拼接文案
     * @param prop 道具卡
     * @param targetPropCard 使用道具卡后获得的道具卡
     * @param gold 金币数量
     */
    fun getPropInfo(prop: PropCard?, targetPropCard: PropCard?, gold: Long?,suit:Suit?): SpannableStringBuilder {
        var info = SpannableStringBuilder()
        when (prop?.toolId) {
            Constants.TOOlS_CARD_MAMMON -> {
                info = "，限定时间内合成套装获得双倍金币奖励。".toSpan()
            }
            Constants.TOOlS_CARD_HIDE -> {
                info = "，在1小时内，口袋和屋顶不显示卡片。".toSpan()
            }
            Constants.TOOlS_CARD_POCKET -> {
                info = "，限定时间内获得固定口袋一个。".toSpan()
            }
            Constants.TOOLS_CARD_DISMANTLE -> {
                info = "，您选择的".toSpan().append(setTextColor(suit?.suitName)).append("套装被拆到我的口袋里。")
            }
            Constants.TOOlS_CARD_SCAMP -> {
                info = "，限定时间内浏览其他人卡片页将窃取一定金币。".toSpan()
            }
            Constants.TOOlS_CARD_GUARD -> {
                info = "，可以抵抗打劫卡一次。".toSpan()
            }
            Constants.TOOlS_CARD_COPY -> {
                info = "，再次获得一张".toSpan().append(setTextColor(targetPropCard?.toolName)).append("。".toSpan())
            }
            Constants.TOOlS_CARD_BOUNCE -> {
                info = "，可以反弹部分属性道具卡效果一次。".toSpan()
            }
            Constants.TOOlS_CARD_CURE -> {
                info = "，解除了一个负面效果。".toSpan()
            }
            Constants.TOOlS_CARD_LUCK -> {
                info = "，获得了".toSpan().append(setTextColor(gold.toString())).append(" G 。".toSpan())
            }

        }
        return info
    }
    //设置某个属性的颜色
    private fun setTextColor(str: String?): SpannableStringBuilder {
        return if (str?.isEmpty() == true) {
            SpannableStringBuilder("")
        } else {
            str.toSpan().toColor(color = getColor(R.color.color_feb12a))!!
        }
    }
}