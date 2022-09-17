package com.kotlin.android.mine.bean

import android.graphics.drawable.Drawable
import com.kotlin.android.app.data.ProguardRule

class CreatorViewBean : ProguardRule {

    /**
     * 我的内容
     */
    data class MyContentBean(
        val conIcon: Drawable?,//内容图标
        val conName: String? = "",//内容名称
    ) : ProguardRule

}
