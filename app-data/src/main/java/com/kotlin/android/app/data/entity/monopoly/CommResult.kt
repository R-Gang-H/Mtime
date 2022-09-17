package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 交易加价（/tradeRaisePrice.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的信息详情Id，-2信息接收人不是我，-3我已经处理过此信息，-4金币数<=0
 *
 * 卡片大富翁api - 一口价/竞价成功领取卡片（/bidSuccessPickCard.api）:
 *  bizCode: 业务返回码： 0 对不起，操作失败请刷新后再试试! 1 成功 2 不好意思,商品已被系统回收! 3 对不起,商品已被竞价不能取消!
 *      4 对不起,你的竞价无效，请重新竞价! 5 对不起,你的包位已满请先清理包位! 6 对不起,你的金币不足!
 *
 * 卡片大富翁api - 从许愿树取回卡片（/pickCardFromWish.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1没有已实现的愿望，-2我的固定口袋没有空位
 *
 * 卡片大富翁api - 帮TA实现愿望（/wishComeTrue.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1好友没有已发布的愿望，-2我的固定口袋没有好友许愿的卡片

 * 卡片大富翁api - 添加或更换许愿（/addOrChangeWish.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的卡片Id，-2愿望内容为空，-3愿望内容包含一级敏感词，-4当前用户有已实现的愿望
 *
 * 卡片大富翁api - 使用道具卡（/useToolCard.api）:
 *  业务返回码：1成功，0失败
 *
 * 卡片大富翁api - 删除卡友（/deleteFriend.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1已经不是好友关系
 *
 * 卡片大富翁api - 发起交易（/startTrade.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的好友Id，-2不能跟自己交易，-3我的卡片不存在，-4好友的卡片不存在，-5金币数小于0，-6我的金币数不足，-7好友已命中黑客卡，-8我已命中黑客卡
 *
 * 卡片大富翁api - 发起竞价（/bid.api）:
 *  bizCode: 业务返回码： 0 对不起，操作失败请刷新后再试试! 1 成功 2 不好意思,商品已被系统回收! 3 对不起,商品已被竞价不能取消! 4 对不起,你的竞价无效，请重新竞价! 5 对不起,你的包位已满请先清理包位! 6 对不起,你的金币不足!
 *
 * 卡片大富翁api - 取消拍卖（/cancelAuction.api）:
 *  bizCode: 业务返回码 0 对不起，操作失败请刷新后再试试 1 成功 2 不好意思,商品已被系统回收! 3 对不起,商品已被竞价不能取消! 4 对不起,你的竞价无效，请重新竞价! 5 对不起,你的包位已满请先清理包位! 6 对不起,你的金币不足!
 *
 * 卡片大富翁api - 同意交易（/agreeTrade.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的信息详情Id，-2信息接收人不是我，-3我已经处理过此信息，-4我的卡片不存在，-5好友的卡片不存在，-6我的金币数不足
 *
 * 卡片大富翁api - 同意添加卡友（/agreeFriend.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的信息详情Id，-2信息接收人不是我，-3我已经处理过此信息，-4已经是好友关系
 *
 * 卡片大富翁api - 帮TA实现愿望（/wishComeTrue.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1好友没有已发布的愿望，-2我的固定口袋没有好友许愿的卡片
 *
 * 卡片大富翁api - 开通保险箱空位（/activeStrongBoxPosition.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的保险箱空位，-2当前空位已经开通，-3空位卡不足
 *
 * 卡片大富翁api - 批量删除游戏信息（/deleteGameRecords.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的信息详情Id
 *
 * 卡片大富翁api - 拒绝交易（/refuseTrade.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的信息详情Id，-2信息接收人不是我，-3我已经处理过此信息
 *
 * 卡片大富翁api - 拒绝添加卡友（/refuseFriend.api）
 *  bizCode: 业务返回码：1成功，0失败，-1无效的信息详情Id，-2信息接收人不是我，-3我已经处理过此信息
 *
 * 卡片大富翁api - 更新签名档（/updateSignature.api）
 *  bizCode: 业务返回码：1成功，0失败，-1签名档包含一级敏感词，-2剩余的金币数小于10个
 *
 * 卡片大富翁api - 流拍取回卡片（/auctionFailedPickCard.api）:
 *  bizCode: 业务返回码 0 对不起，操作失败请刷新后再试试 1 成功 2 不好意思,商品已被系统回收! 3 对不起,商品已被竞价不能取消! 4 对不起,你的竞价无效，请重新竞价! 5 对不起,你的包位已满请先清理包位! 6 对不起,你的金币不足!
 *
 * 卡片大富翁api - 添加卡友（/addFriend.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的好友Id，-2已经是好友关系
 *
 * 卡片大富翁api - 添加或更换许愿（/addOrChangeWish.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的卡片Id，-2愿望内容为空，-3愿望内容包含一级敏感词，-4当前用户有已实现的愿望
 *
 * 卡片大富翁api - 添加拍卖（/addAuction.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的卡片/道具卡Id，-2当前用户没有该卡片/道具卡，-3当前用户发布的拍卖数大于10个，-4无效的底价，-5当前用户的金币不够支付保管费，-6当前用户已命中黑客卡
 *
 * 卡片大富翁api - 解锁保险箱（/unlockStrongBox.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的保险箱空位，-2当前空位未开通，-3当前空位的保险箱已满，-4保险箱卡不足
 *
 * 卡片大富翁api - 购买道具卡（/toolCardList.api）:
 *  bizCode: 业务返回码：1成功，0失败，-1无效的道具卡Id，-2无效的宝箱Id，-3库存不足
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class CommResult(
        var bizCode: Long = 0,
        var bizMsg: String? = null, // 错误提示信息
        var bizMessage: String? = null // 错误提示信息
) : ProguardRule

