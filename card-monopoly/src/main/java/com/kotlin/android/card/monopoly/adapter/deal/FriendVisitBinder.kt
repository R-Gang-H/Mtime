package com.kotlin.android.card.monopoly.adapter.deal

import android.view.View
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ItemGameFriendBinding
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.app.data.entity.monopoly.ItemData
import com.kotlin.android.app.data.entity.monopoly.Record
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @desc 交易记录中好友的展示
 * @author zhangjian
 * @date 2020/9/24 17:00
 */
class FriendVisitBinder(val data: Record,
                        onBtnAction: ((item: ItemData, binder: MultiTypeBinder<*>?) -> Unit),
                        jump: ((userId: Long) -> Unit)) : MultiTypeBinder<ItemGameFriendBinding>() {

    var mOnBtnAction: ((item: ItemData, binder: MultiTypeBinder<*>?) -> Unit) = onBtnAction

    var mJump: ((userId: Long) -> Unit)? = jump

    override fun layoutId(): Int = R.layout.item_game_friend

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FriendVisitBinder && other.data == data
    }

    override fun onBindViewHolder(binding: ItemGameFriendBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        var title = ""
        when (data.recordType) {
            Constants.GAME_RECORD_DEL_FRIEND -> {
                title = getString(R.string.record_item_friend_delete_success)
                binding.llAction.visibility = View.GONE
            }
            Constants.GAME_RECORD_ADD_FRIEND_SUCCESS -> {
                title = getString(R.string.record_item_friend_add_success)
                binding.llAction.visibility = View.GONE

            }
            Constants.GAME_RECORD_ADD_FRIEND -> {
                title = getString(R.string.record_item_friend_add)
                if (data.recordStatus == 1L) {
                    binding.llAction.visibility = View.GONE
                } else {
                    binding.llAction.visibility = View.VISIBLE
                }
            }
            Constants.GAME_RECORD_REFUSE_ADD -> {
                title = getString(R.string.record_item_friend_refuse)
                binding.llAction.visibility = View.GONE

            }
        }
        //背景圆角
        ShapeExt.setShapeCorner2ColorWithColor(binding.llContainer, getColor(R.color.color_f2f3f6), 4.dp)
        //头像
        binding.civAvatar.setUserInfo(data.senderInfo)
        binding.civAvatar.onClick {
            mJump?.invoke(data.senderInfo?.userId ?: 0L)
        }
        //标题
        val name = data.senderInfo?.nickName?.toSpan().toColor(color = getColor(R.color.color_12c7e9))
        binding.tvMsg.text = name?.append(title.toSpan())
        //附言
        binding.tvLeaveMsg.text = data.message ?: ""
        //时间
        binding.tvTime.text = DealRecordHelper.setTime(data.enterTime)
        //同意按钮
        ShapeExt.setShapeCorner2ColorWithColor(binding.tvAgree, getColor(R.color.color_12c7e9), 45)
        //拒绝按钮
        ShapeExt.setShapeCorner2ColorWithColor(binding.tvRefuse, getColor(R.color.color_c9cedc), 45)

        binding.tvAgree.onClick {
            mOnBtnAction.invoke(ItemData(position = position, recordDetail = data.recordDetailId, actionType = Constants.TYPE_AGREE_FRIEND), this)
        }

        binding.tvRefuse.onClick {
            mOnBtnAction.invoke(ItemData(position = position, recordDetail = data.recordDetailId,
                    actionType = Constants.TYPE_REFUSE_FRIEND, userName = data.senderInfo?.nickName
                    ?: ""), null)
        }
        //删除
        binding.rlClose.onClick {
            mOnBtnAction.invoke(ItemData(recordDetail = data.recordDetailId, position = position, actionType = Constants.TYPE_DELETE), this)
        }
    }
}
