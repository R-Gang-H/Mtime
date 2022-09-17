package com.kotlin.android.ugc.detail.component

import androidx.annotation.StringRes
import com.kotlin.android.app.data.constant.CommConstant.CONTENT_STATE_RELEASED
import com.kotlin.android.mtime.ktx.ext.showToast

/**
 * create by lushan on 2020/11/10
 * description:
 */

/**
 * 如果没有审核通过，统一弹出正在审核中，文章、帖子、日志、影评
 */
fun Boolean.isPublished(block: () -> Unit) {
    if (this) {
        block()
    } else {
        showToast(R.string.ugc_detail_common_is_checking)
    }
}

fun Long.isPublished(block: (() -> Unit)? = null): Boolean {
    val canPublish = this == CONTENT_STATE_RELEASED || this == -1L
    if (canPublish) {
        block?.invoke()
    } else {
        showToast(R.string.ugc_detail_common_is_checking)
    }
    return canPublish
}

/**
 * 内容个是否可以分享
 */
fun Long.contentCanShare(@StringRes id: Int, block: (() -> Unit)? = null): Boolean {
    val canShare = this >= CONTENT_STATE_RELEASED || this == -1L
    if (canShare) {
        block?.invoke()
    } else {
        showToast(id)
    }
    return canShare
}

/**
 * 内容是否可以编辑
 */
fun Long.contentCanEdit(): Boolean {
    return this >= CONTENT_STATE_RELEASED
}
