package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 最新的签名档列表（/signatureList.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class SignatureList(
        var signatures: List<Signature>? = null // 签名档列表
) : ProguardRule