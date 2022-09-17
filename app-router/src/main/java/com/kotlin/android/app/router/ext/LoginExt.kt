package com.kotlin.android.router.ext

import com.kotlin.android.router.provider.IUserProvider

/**
 * 导航路由器，登录态扩展
 *
 * Created on 2020/8/14.
 *
 * @author o.s
 */

/**
 * 判断此用户ID是否为登录的帐号ID
 */
fun isSelf(userId: Long) = getProvider(IUserProvider::class.java)?.isSelf(userId) ?: false

/**
 * 路由调用 user 模块的登录态
 */
fun isLogin(): Boolean {
    return getProvider(IUserProvider::class.java)?.isLogin() ?: false
}

/**
 * 如果已经登录，进行传入的方法处理。
 * 如果没有登录，进入登录界面。
 * 只是做了个判断，并没有处理登录后的逻辑，如有重定向需求请参考 [LoginNavigationCallbackImpl]，通常用在点击收藏、想看等等需要依赖登录状态的事件
 */
fun afterLogin(method: () -> Unit) {
    if (isLogin()) {
        method()
    } else {
        getProvider(IUserProvider::class.java)?.startLoginPage()
    }
}