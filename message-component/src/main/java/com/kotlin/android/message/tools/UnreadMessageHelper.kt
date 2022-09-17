package com.kotlin.android.message.tools

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.data.entity.message.UnreadCountResult
import com.kotlin.android.app.router.provider.message_center.UnReadMessageObserver
import com.kotlin.android.message.repository.MessageRepository
import com.kotlin.android.user.isLogin
import com.kotlin.chat_component.HuanxinConversationManager
import com.kotlin.chat_component.MtimeUnReadMessageObserve
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by zhaoninglongfei on 2022/4/24
 * 获取时光业务未读消息数 + 环信未读消息数
 */
object UnreadMessageHelper {

    private var repo: MessageRepository? = null

    fun getUnreadMessageCount(observer: UnReadMessageObserver) {
        if (!isLogin()) {
            observer.onNotifyMessageCount(0L)
            return
        }
        if (repo == null) {
            repo = MessageRepository()
        }

        CoroutineScope(Dispatchers.Main).launch {
            val result: ApiResult<UnreadCountResult>? = withContext(Dispatchers.IO) {
                repo?.loadUnreadCount()
            }

            when (result) {
                is ApiResult.Success -> {
                    if (result.data.errorCode == 0L) {
                        MtimeUnReadMessageObserve.notice(
                            cmdMessageCount = result.data.praise
                                    + result.data.commentReply
                                    + result.data.userFollow
                                    + result.data.movieRelease,
                            huanxinMessageCount = HuanxinConversationManager.getUnreadMessageCount()
                        )
                    } else {
                        observer.onNotifyMessageCount(
                            HuanxinConversationManager.getUnreadMessageCount().toLong()
                        )
                    }
                }
                is ApiResult.Error -> observer.onNotifyMessageCount(
                    HuanxinConversationManager.getUnreadMessageCount().toLong()
                )
                else -> observer.onNotifyMessageCount(
                    HuanxinConversationManager.getUnreadMessageCount().toLong()
                )
            }
        }
    }
}