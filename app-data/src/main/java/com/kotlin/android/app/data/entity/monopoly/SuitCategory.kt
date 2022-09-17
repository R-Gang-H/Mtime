package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 套装分类列表（用于套装列表上面的分类标签展示）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class SuitCategory(
        var id: Long = 0, // 分类Id：1简装版套装，2精装版套装，3终极版套装，4限量版套装
        var shortName: String? = null // 分类简称：简，精，终，限
) : ProguardRule