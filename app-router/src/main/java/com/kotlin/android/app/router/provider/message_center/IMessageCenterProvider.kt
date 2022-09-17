package com.kotlin.android.app.router.provider.message_center

import android.app.Activity
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * Created by zhaoninglongfei on 2022/3/14
 * 新版消息中心provider
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_MESSAGE_CENTER)
interface IMessageCenterProvider : IBaseProvider {

    //跳转到10.0新版消息页面
    fun startMessageCenterActivity(activity: Activity)

    //打开粉丝列表
    fun startFansActivity(activity: Activity)

    //打开观影通知
    fun startMovieNotifyActivity(activity: Activity)

    //打开评论回复页
    fun startCommentActivity(activity: Activity)

    //打开点赞页
    fun startPraiseActivity(activity: Activity)

    //打开发起私聊页
    fun startPrivateChatActivity(activity: Activity)

    //打开黑名单页
    fun startBlackListActivity(activity: Activity)

    //打开私聊页
    fun startChatActivity(
        activity: Activity,
        mtimeId: Long?,
        isOfficial: Boolean?,
        title: String?,
        conversationId: String?,
        otherMtimeId: Long? = null,
        otherNickName:String? = null,
        otherHeadPic : String? = null,
        otherAuthType : Long? = null,
        otherAuthRole : String? =null
    )

    /**
     * 添加未读消息数的监听
     * 适用于消息中心外部消息数的展示  刷新title消息数
     */
    fun addUnreadMessageCountObserver(observer: UnReadMessageObserver)

    /**
     * 移除消息未读数的监听
     */
    fun removeUnreadMessageCountObserver(observer: UnReadMessageObserver)
}