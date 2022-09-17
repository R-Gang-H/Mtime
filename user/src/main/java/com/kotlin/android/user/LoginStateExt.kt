package com.kotlin.android.user

import com.kotlin.android.user.login.gotoLoginPage

/**
 * 创建者: zl
 * 创建时间: 2020/6/8 10:29 AM
 * 描述:
 */
/**
顶层函数，判断是否登录
 */
fun isLogin(): Boolean {
    return UserManager.instance.isLogin
}

/**
 * 如果已经登录，进行传入的方法处理
 * 如果没有登录，进入登录界面
 * 只是做了个判断，并没有处理登录后的逻辑，如有重定向需求请参考LoginNavigationCallbackImpl，通常用在点击收藏、想看等等需要依赖登录状态的事件
 */
fun afterLogin(method: () -> Unit) {
    if (isLogin()) {
        method()
    } else {
        gotoLoginPage()
    }
}