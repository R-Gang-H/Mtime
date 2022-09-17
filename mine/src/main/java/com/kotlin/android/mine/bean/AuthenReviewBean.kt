package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/8
 * description:身份认证-长影评
 */
data class AuthenReviewBean(var reviewId: Long = 0L,//影评id
                            var content: String = ""//影评内容
) : ProguardRule