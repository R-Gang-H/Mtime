package com.kotlin.android.live.component.viewbean

import android.text.Html
import android.text.Spanned
import com.kotlin.android.core.CoreApp
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.live.component.R

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/3/15
 *
 * 直播聊天信息UI实体
 */
data class LiveChatMessageBean(
        var userName: String,
        var clientType: Long,
        var roomNum: String,
        var message: String,
        var headIcon: String,
        var userId: Long,
        var messageId: String,
        var chatType: Long //1：普通消息 2：欢迎消息
) : ProguardRule {

    fun getMsg(): Spanned {
        return if (chatType == 1L) {
            Html.fromHtml(CoreApp.instance.getString(R.string.live_component_chat_msg, userName, message), Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(CoreApp.instance.getString(R.string.live_component_chat_join, userName, message), Html.FROM_HTML_MODE_LEGACY)
        }
    }
}
