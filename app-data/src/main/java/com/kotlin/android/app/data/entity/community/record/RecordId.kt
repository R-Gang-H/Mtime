package com.kotlin.android.app.data.entity.community.record

import com.kotlin.android.app.data.ProguardRule

/**
 * 社区内容api - 未发布内容-获取我的新记录ID(/record_id.api)
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */
data class RecordId(
        var recId: Long = 0
) : ProguardRule