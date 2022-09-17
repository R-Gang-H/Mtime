package com.kotlin.android.app.data.entity.upload

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2022/4/13
 * des:发起音频视频上传
 **/
data class ApplyUpload(
    val bizCode: Long = 0L,//业务代码0为成功
    val bizMsg: String? = "",//业务信息
    val videoId: Long = 0L,//视频ID
    val token: String? = ""//一次性token，腾讯云客户端上传
) : ProguardRule