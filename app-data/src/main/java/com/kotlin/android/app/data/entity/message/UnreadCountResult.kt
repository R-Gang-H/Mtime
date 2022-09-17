package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/3/18
 * 未读消息数目, 消息同步也是此消息体
 */
data class UnreadCountResult(
    var errorCode: Long = 0L,//错误码
    var errorMsg: String? = "",//错误信息

    var commentReply: Long = 0L,//【用户动态消息】评论回复未读数
    var praise: Long = 0L,//【用户动态消息】点赞未读数
    var userFollow: Long = 0L,//【用户动态消息】新增关注数
    var movieRelease: Long = 0L,//【用户动态消息】观影通知数目
    var movieName: String? = "",//第一个未读的观影通知电影名称 如果movieRelease=0，则此为空字符串
) : ProguardRule
