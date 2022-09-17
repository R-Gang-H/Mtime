package com.kotlin.android.app.data.entity.community.record

import com.kotlin.android.app.data.ProguardRule

/**
 * 图片集合 图集传入，富文本内图集不要传
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */
data class Image(
        var imageId: String? = null, // 图片ID 必填
        var imageUrl: String? = null, // 图片URL
        var imageFormat: String? = null, // 图片格式 例：gif,jpg,png
        var imageDesc: String? = null, // 图片描述
        var isGif: Boolean = false // 是否gif图
) : ProguardRule