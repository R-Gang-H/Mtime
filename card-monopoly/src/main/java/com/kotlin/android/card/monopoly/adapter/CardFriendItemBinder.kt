package com.kotlin.android.card.monopoly.adapter

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ItemCardFriendBinding
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.card.monopoly.widget.nickname.NickNameView
import com.kotlin.android.app.data.entity.monopoly.Friend
import com.kotlin.android.app.data.entity.monopoly.Robot
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getActivity
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.ShapeExt

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @desc 我的卡友列表的item
 * @author zhangjian
 * @date 2020/9/3 16:37
 */
class CardFriendItemBinder(
    val cardBean: Friend,
    val jumpType: Int,
    val getFriendData: (friendData: Friend) -> Unit,
    val deleteFriend: (friendId: Long, position: Int) -> Unit?
) : MultiTypeBinder<ItemCardFriendBinding>() {

    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }

    override fun layoutId(): Int = R.layout.item_card_friend

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CardFriendItemBinder && other.cardBean == cardBean
    }

    override fun onBindViewHolder(binding: ItemCardFriendBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //设置头像
//        binding.civAvatar.setUserInfo(cardBean.avatarUrl)
        //设置进度
        binding.cardProgerss.setFillCount(
            cardBean.openPocketRemainCount.toInt(),
            cardBean.openPocketCount.toInt()
        )
        //开放口袋数量
        val count = cardBean.openPocketCount - cardBean.openPocketRemainCount
        binding.tvSeatProgress.text =
            "${cardBean.openPocketRemainCount}/${cardBean.openPocketCount}"

        //设置在线
        if (cardBean.isRobot) {
            binding.tvName.state = NickNameView.State.UNKNOWN
        } else {
            binding.tvName.isOnline = cardBean.isOnline
        }
        //设置金币
        binding.llIcon.visibility = if (cardBean.isRobot) View.GONE else View.VISIBLE
        ShapeExt.setShapeCorner2Color(binding.llSuit, R.color.color_e3e5ed, 45, true)
        //设置套装
        binding.llSuit.visibility = if (cardBean.isRobot) View.GONE else View.VISIBLE
        ShapeExt.setShapeCorner2Color(binding.llIcon, R.color.color_e3e5ed, 45, true)
        //机器人的话
        if (cardBean.isRobot) {
            //设置头像
            binding.civAvatar.loadImage(
                data = R.drawable.ic_robot_avatar,
                defaultImgRes = R.drawable.ic_robot_avatar
            )
            //name 居中展示
            val param = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            param.topToTop = R.id.ctlLayout
            param.bottomToBottom = R.id.ctlLayout
            param.leftToRight = R.id.civAvatar
            param.marginStart = 10.dp
            binding.tvName.layoutParams = param
            //进度居中
            val param1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                30.dp
            )
            param1.topToTop = R.id.ctlLayout
            param1.bottomToBottom = R.id.ctlLayout
            param1.rightToRight = R.id.ctlLayout
            binding.llSeat.layoutParams = param1
        } else {
            //设置头像
            binding.civAvatar.setUserInfo(cardBean.avatarUrl)
            //name 居中展示
            val param = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            param.topToTop = R.id.ctlLayout
            param.leftToRight = R.id.civAvatar
            param.marginStart = 10.dp
            param.topMargin = 17.dp
            binding.tvName.layoutParams = param
            //进度居中
            val param1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                30.dp
            )
            param1.topToTop = R.id.ctlLayout
            param1.rightToRight = R.id.ctlLayout
            param1.topMargin = 17.dp
            binding.llSeat.layoutParams = param1
        }

        //点击事件跳转
        binding.ctlLayout.onClick {
            when (jumpType) {
                Constants.CARD_FRIEND_CARD_MAIN -> {
                    binding.ctlLayout.getActivity()?.apply {
                        if (cardBean.isRobot) {
                            mProvider?.startCardRobotActivity(
                                Robot(
                                    robotId = cardBean.userId,
                                    robotName = cardBean.nickName,
                                    openPocketCount = cardBean.openPocketCount,
                                    openPocketRemainCount = cardBean.openPocketRemainCount
                                )
                            )
                        } else {
                            mProvider?.startCardMainActivity(
                                context = this,
                                userId = cardBean.userId
                            )
                        }
                    }
                }
                Constants.CARD_FRIEND_CARD_DETAIL -> {
                }
                Constants.CARD_FRIEND_CARD_DISCARD,
                Constants.CARD_FRIEND_TOOLS_SLAVE,
                Constants.CARD_FRIEND_TOOLS_ROB,
                Constants.CARD_FRIEND_TOOLS_ROB_LIMIT,
                Constants.CARD_FRIEND_TOOLS_HACK-> {
                    getFriendData.invoke(cardBean)
                }
            }
        }
        binding.ctlLayout.setOnLongClickListener {
            if (!cardBean.isRobot) {
                deleteFriend.invoke(cardBean.userId,position)
                true
            } else {
                false
            }

        }

    }

}