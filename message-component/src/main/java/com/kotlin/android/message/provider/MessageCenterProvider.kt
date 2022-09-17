package com.kotlin.android.message.provider

import android.app.Activity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.app.router.provider.message_center.UnReadMessageObserver
import com.kotlin.android.ktx.ext.core.put
import com.kotlin.android.message.MessageConstant
import com.kotlin.android.message.tools.UnreadMessageHelper
import com.kotlin.android.router.RouterManager
import com.kotlin.chat_component.MtimeUnReadMessageObserve

/**
 * Created by zhaoninglongfei on 2022/3/14
 *
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_MESSAGE_CENTER)
class MessageCenterProvider : IMessageCenterProvider {

    override fun startMessageCenterActivity(activity: Activity) {
        RouterManager.instance.navigation(
            path = RouterActivityPath.MessageCenter.PAGE_MESSAGE_CENTER,
            context = activity
        )
    }

    override fun startFansActivity(activity: Activity) {
        RouterManager.instance.navigation(
            path = RouterActivityPath.MessageCenter.PAGE_FANS,
            context = activity
        )
    }

    override fun startMovieNotifyActivity(activity: Activity) {
        RouterManager.instance.navigation(
            path = RouterActivityPath.MessageCenter.PAGE_MOVIE_REMIND,
            context = activity
        )
    }

    override fun startCommentActivity(activity: Activity) {
        RouterManager.instance.navigation(
            path = RouterActivityPath.MessageCenter.PAGE_COMMENT,
            context = activity
        )
    }

    override fun startPraiseActivity(activity: Activity) {
        RouterManager.instance.navigation(
            path = RouterActivityPath.MessageCenter.PAGE_PRAISE,
            context = activity
        )
    }

    override fun startPrivateChatActivity(activity: Activity) {
        RouterManager.instance.navigation(
            path = RouterActivityPath.MessageCenter.PAGE_PRIVATE_CHAT,
            context = activity
        )
    }

    override fun startBlackListActivity(activity: Activity) {
        RouterManager.instance.navigation(
            path = RouterActivityPath.MessageCenter.PAGE_BLACK_LIST,
            context = activity
        )
    }

    override fun startChatActivity(
        activity: Activity,
        mtimeId: Long?,
        isOfficial: Boolean?,
        title: String?,
        conversationId: String?,
        otherMtimeId: Long?,
        otherNickName: String?,
        otherHeadPic: String?,
        otherAuthType: Long?,
        otherAuthRole: String?
    ) {
        val bundle = Bundle()
        bundle.put(MessageConstant.KEY_CHAT_CONVERSATION_ID, conversationId)
        bundle.put(MessageConstant.KEY_CHAT_MTIME_ID, mtimeId)
        bundle.put(MessageConstant.KEY_CHAT_IS_OFFICIAL, isOfficial)
        bundle.put(MessageConstant.KEY_CHAT_TITLE, title)
        bundle.put(MessageConstant.KEY_CHAT_OTHER_MTIME_ID, otherMtimeId)
        bundle.put(MessageConstant.KEY_CHAT_OTHER_NICK_NAME, otherNickName)
        bundle.put(MessageConstant.KEY_CHAT_OTHER_HEAD, otherHeadPic)
        bundle.put(MessageConstant.KEY_CHAT_OTHER_AUTH_TYPE, otherAuthType)
        bundle.put(MessageConstant.KEY_CHAT_OTHER_AUTH_ROLE, otherAuthRole)
        RouterManager.instance.navigation(
            path = RouterActivityPath.MessageCenter.PAGE_CHAT,
            bundle = bundle,
            context = activity
        )
    }

    override fun addUnreadMessageCountObserver(observer: UnReadMessageObserver) {
        UnreadMessageHelper.getUnreadMessageCount(observer)
        MtimeUnReadMessageObserve.addUnReadMessageObserver(observer)
    }

    override fun removeUnreadMessageCountObserver(observer: UnReadMessageObserver) {
        MtimeUnReadMessageObserve.removeUnReadMessageObserver(observer)
    }
}