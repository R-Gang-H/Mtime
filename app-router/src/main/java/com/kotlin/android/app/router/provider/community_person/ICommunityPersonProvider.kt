package com.kotlin.android.app.router.provider.community_person

import android.app.Activity
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * create by wei.wang on 2020/9/14
 * description: 个人主页
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_COMMUNITY)
interface ICommunityPersonProvider : IBaseProvider {


    /**
     * 跳转到个人主页
     */
    fun startPerson(userId: Long, type: Long? = 0)


    /**
     *跳转到我的好友页
     */
    fun startMyFriend(
        userId: Long,
        type: Long,
        isPublish: Boolean? = false,
        activity: Activity? = null
    )


    /**
     * 跳转到个人收藏页面
     */
    fun startPersonCollection(userId: Long = 0L, type: Long? = 0)


    /**
     * 跳转到时光轴页面
     */
    fun startTimeLinePage()

}