package com.kotlin.android.message.ui.center.viewBean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.message.UnreadCountResult


/**
 * Created by zhaoninglongfei on 2022/3/7
 *
 */
data class MessageCenterHeaderViewBean(
    val commentCount: Int? = null,
    val likeCount: Int? = null
) : ProguardRule {
    companion object {
        fun build(result: UnreadCountResult): MessageCenterHeaderViewBean {
            return MessageCenterHeaderViewBean(
                commentCount = if (result.commentReply == 0L) null else result.commentReply.toInt(),
                likeCount = if (result.praise == 0L) null else result.praise.toInt()
            )
        }
    }
}
