package com.kotlin.android.router.interceptor

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.annotation.path.LoginPathAnnotationManager
import com.kotlin.android.router.annotation.path.LoginPathConfigurableManager
import com.kotlin.android.router.provider.IUserProvider

/**
 * 路由登录状态拦截器：
 * 对路由进行登录状态拦截，如果目标页面需要登录，在未登录的情况进行路由就会被拦截下来，进行回调。在回调业务中执行
 *
 * Created on 2021/4/20.
 *
 * @author o.s
 */
@Interceptor(priority = 6, name = "login")
class LoginInterceptor : IInterceptor {

    /**
     * Do your init work in this method, it well be call when processor has been load.
     *
     * @param context ctx
     */
    override fun init(context: Context?) {
    }

    /**
     * The operation of this interceptor.
     *
     * @param postcard meta
     * @param callback cb
     */
    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
        val path = postcard?.path
        val isLogin = RouterManager.instance.getProvider(IUserProvider::class.java)?.isLogin() ?: false
        if (isLogin) {
            "LoginInterceptor 【已登录】 path=$path".i()
            callback?.onContinue(postcard)
        } else {
            when {
                LoginPathAnnotationManager.instance.isLoginPath(path) -> {
                    "LoginInterceptor 【需要登录】 path=$path".i()
                    // 需要登录
                    callback?.onInterrupt(null)
                }
                LoginPathConfigurableManager.instance.isPathNeedLogin(path.orEmpty()) -> { // 只关心动态登录path
                    // 需要登录
                    callback?.onInterrupt(null)
                    // 关闭path动态登录状态，如果未登录，下一次发起时会自动重置动态业务登录状态（避免下次调用出问题），只关心动态登录path
                    LoginPathConfigurableManager.instance.setPathNeedLogin(path = path.orEmpty(), needLogin = false)
                }
                else -> {
                    "LoginInterceptor 【不需要登录】 path=$path".i()
                    callback?.onContinue(postcard)
                }
            }
        }
    }
}