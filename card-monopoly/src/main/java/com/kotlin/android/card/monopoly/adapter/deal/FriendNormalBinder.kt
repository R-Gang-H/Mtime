package com.kotlin.android.card.monopoly.adapter.deal

import android.text.method.LinkMovementMethod
import android.util.Range
import android.view.View
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ItemGameFriendNormalBinding
import com.kotlin.android.card.monopoly.ext.setUserInfo
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
 * @desc 交易记录中拒接交易信息正常展示页面
 * @author zhangjian
 * @date 2020/9/24 17:00
 */
class FriendNormalBinder(val data: Record, onBtnAction: ((item: ItemData, binder: MultiTypeBinder<*>?) -> Unit)) : MultiTypeBinder<ItemGameFriendNormalBinding>() {

    var mOnBtnAction: ((item: ItemData, binder: MultiTypeBinder<*>?) -> Unit)? = onBtnAction

    override fun layoutId(): Int = R.layout.item_game_friend_normal

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FriendNormalBinder && other.data == data
    }

    override fun onBindViewHolder(binding: ItemGameFriendNormalBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //添加点击username跳转
        binding.tvTitle.movementMethod = LinkMovementMethod.getInstance()
        when (data.recordType) {
            //拒绝交易
            Constants.GAME_RECORD_REFUSE_TRANS -> {
                //标题
                val name = data.senderInfo?.nickName?.toSpan().toColor(color = getColor(R.color.color_12c7e9))
                        .toLinkNotUnderLine(Range(0, data.senderInfo?.nickName?.length)) { _, _ ->
                            mOnBtnAction?.invoke(ItemData(actionType = Constants.TYPE_USE_TOOLS, userInfo = data.senderInfo), null)
                        }
                val mText = data.cards?.firstOrNull()?.cardName?.toSpan().toColor(color = getColor(R.color.color_ff5a36))
                val title = name?.append("谢绝了你的")?.append(mText)?.append("卡的交易")
                binding.tvTitle.text = title
            }
            //拒绝议价
            Constants.GAME_RECORD_REFUSE_DISS -> {
                //标题
                val name = data.senderInfo?.nickName?.toSpan().toColor(color = getColor(R.color.color_12c7e9))
                        .toLinkNotUnderLine(Range(0, data.senderInfo?.nickName?.length)) { _, _ ->
                            mOnBtnAction?.invoke(ItemData(actionType = Constants.TYPE_USE_TOOLS, userInfo = data.senderInfo), null)
                        }
                val mText = data.cards?.firstOrNull()?.cardName?.toSpan().toColor(color = getColor(R.color.color_ff5a36))
                val title = name?.append("谢绝了你的")?.append(mText)?.append("卡的交易")
                binding.tvTitle.text = title
            }
        }
        //附言
        if(data.message.isNullOrEmpty()){
            binding.tvMessage.visibility = View.GONE
        }else{
            binding.tvMessage.visibility = View.VISIBLE
            binding.tvMessage.text = data.message ?: ""
        }
        //金币展示
        //交易展示金币数
        val totalGold = (data.gold ?: 0) + data.addPrice
        binding.tvIconMessage.text = "$totalGold"
        //背景圆角
        ShapeExt.setShapeCorner2ColorWithColor(binding.llContainer, getColor(R.color.color_f2f3f6), 4.dp)
        //头像
        binding.civAvatar.setUserInfo(data.senderInfo)
        //时间
        binding.tvTime.text = DealRecordHelper.setTime(data.enterTime)
        //加载图片
        binding.cardImageView.card = Card(cardCover = DealRecordHelper.getImageCover(data))
        //删除
        binding.rlClose.onClick {
            mOnBtnAction?.invoke(ItemData(recordDetail = data.recordDetailId, position = position, actionType = Constants.TYPE_DELETE), null)
        }

    }
}
