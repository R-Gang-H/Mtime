package com.kotlin.android.router.interceptor.callback

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.KEY_LOGIN_NAVIGATION_PATH
import com.kotlin.android.router.provider.IUserProvider

/**
 * 导航中断回调：需要登录
 *
 * Created on 2021/4/20.
 *
 * @author o.s
 */
open class LoginNavigationCallback(
        private val context: Context? = null,
        private val requestCode: Int? = null
) : NavigationCallback {
    /**
     * Callback when find the destination.
     *
     * @param postcard meta
     */
    override fun onFound(postcard: Postcard?) {

    }

    /**
     * Callback after lose your way.
     *
     * @param postcard meta
     */
    override fun onLost(postcard: Postcard?) {
    }

    /**
     * Callback after navigation.
     *
     * @param postcard meta
     */
    override fun onArrival(postcard: Postcard?) {
    }

    /**
     * Callback on interrupt.
     *
     * @param postcard meta
     */
    override fun onInterrupt(postcard: Postcard?) {
        val bundle = postcard?.extras?.apply {
            putString(KEY_LOGIN_NAVIGATION_PATH, postcard.path)
        }

        Looper.getMainLooper()?.apply {
            Handler(this).post {
                RouterManager.instance.getProvider(IUserProvider::class.java)
                        ?.startLoginPage(context, bundle, requestCode)
            }
        }
    }
}