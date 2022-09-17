package com.kotlin.android.app.data.entity.common

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2022/4/18
 * des:视频发布
 **/
data class PublishResult(
    val contentId: Long = 0L,//内容id
    val recId: Long = 0L,//记录id
    val bizCode: Long = 0L,//业务返回码 0:成功 1010101, "非法的保存参数" 1010102, "内容不存在" 1010103, "发表者非法" 1010104, "记录ID非法" 1010105, "不可编辑" 1010106, "敏感词：{0}" 1010107, "家族非法" 1010108, "家族权限错误" 1010109, "禁止发表文章"
    val bizMsg: String? = ""//业务返回消息
) : ProguardRule {
    fun isSuccess() = bizCode == 0L
}