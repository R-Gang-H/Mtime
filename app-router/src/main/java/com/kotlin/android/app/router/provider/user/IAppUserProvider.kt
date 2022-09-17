package com.kotlin.android.app.router.provider.user

import android.app.Activity
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * create by lushan on 2020/8/28
 * description: 旧的用户相关
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_APP_USER)
interface IAppUserProvider : IBaseProvider {

    /**
     * 跳转到个人资料页
     */
    fun startProfileActivity(activity: Activity)

    /**
     *  跳转到设置页面
     */
    fun startSettingActivity()

    /**
     * 跳转到关于我们
     */
    fun startAboutActivity()

    /**
     * 跳转到任务中心
     */
    fun startTaskActivity()
//    //    抽奖页面
//    fun startDrawALotteryActivity(bundle: Bundle, activity: Activity? = null,
//                                  callback: NavigationCallback? = null)
}