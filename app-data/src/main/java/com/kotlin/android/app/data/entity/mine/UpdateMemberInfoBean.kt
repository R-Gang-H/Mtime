package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

data class UpdateMemberInfoBean(
    var bizCode: Int, // 0-成功
    var bizMsg: String,
    var birthday: String, // 生日 2008-08-08
    var locationRelation: String // 居住地层级关系  国家id-省id-城市id
) : ProguardRule