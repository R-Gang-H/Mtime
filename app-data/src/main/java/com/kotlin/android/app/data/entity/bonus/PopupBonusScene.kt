package com.kotlin.android.app.data.entity.bonus

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/12/28
 * description:是否弹出彩蛋
 */
data class PopupBonusScene(var success: Boolean = false,
                           var code: Long = 0L,
                           var msg: String? = "") : ProguardRule

const val ACTION_SHARE_SUCCESS = 1L//用户当日第一次分享成功
const val ACTION_PUBLISH_REVIEW = 2L//用户当日第一成功发布长短影评
const val ACTION_PUBLISH_POST = 3L//用户当日第一次成功发布帖子
const val ACTION_JOIN_FAMILY = 4L//用户当日第一次加入新家族
const val ACTION_CREATE_FAMILY = 5L//用户当日对此成功创建家族
const val ACTION_COMPOSED_ARMOR = 6L//用户当日第一次成功合成卡片大富翁套装
const val ACTION_PAY_SUCCESS = 7L//用户当日第一次购票且支付成功

