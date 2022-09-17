package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule

class ThirdAccountDataBean(
    var bindName: String = "",
    var bindState: String = "",
    var bindStatus: Int, // 绑定为0,解绑为1
    var platformId: Int // 1 新浪微博、2 qq、4微信、6苹果apple
) : ProguardRule