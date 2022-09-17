package com.kotlin.android.router.interceptor.callback

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.alibaba.android.arouter.facade.Postcard

/**
 * create by lushan
 * des: 避免部分手机从启动页到主activity不能正常跳转，需要在跳转完成后再销毁activity
 *
 * 注意：app冷启动时，建议使用 Android API 直接启动入口页和主页，避免使用路由（路由初始化耗时，导致app启动变慢）。
 **/
class CloseCallback(
    private val context: Context? = null,
    private val requestCode: Int = -1,
    private val needCloseCurrentActivity: Boolean = false
) : LoginNavigationCallback(context, requestCode) {

    /**
     * Callback after navigation.
     *
     * @param postcard meta
     */
    override fun onArrival(postcard: Postcard?) {
        super.onArrival(postcard)
        if (needCloseCurrentActivity) {
            Looper.getMainLooper()?.apply {
                Handler(this).post {
                    (context as? Activity)?.finish()
                }
            }
        }
    }

}