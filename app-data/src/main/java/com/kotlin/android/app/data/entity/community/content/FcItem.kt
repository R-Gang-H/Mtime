package com.kotlin.android.app.data.entity.community.content

import com.kotlin.android.app.data.ProguardRule

/**
 *
 * Created on 2022/5/17.
 *
 * @author o.s
 */
data class FcItem(
    var id: Long = 0,           // 电影ID         // 影人ID      // 家族ID
    var name: String? = null,   // 电影中文名                    // 家族名称
    var nameCn: String? = null, //               // 影人中文名
    var nameEn: String? = null, // 电影英文名     // 影人英文名
    var url: String? = null,    // 电影url
): ProguardRule
