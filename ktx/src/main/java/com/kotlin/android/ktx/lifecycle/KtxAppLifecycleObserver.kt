package com.kotlin.android.ktx.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.kotlin.android.ktx.bean.AppState
import com.kotlin.android.ktx.ext.appState
import com.kotlin.android.ktx.ext.log.v

/**
 *
 * Created on 2020/6/2.
 *
 * @author o.s
 */
class KtxAppLifecycleObserver : LifecycleObserver {

    /**
     * app启动，只调用一次
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onAppLaunch() {
        "app启动".v()
    }

    /**
     * app进入前台调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForeground() {
        "app进入前台".v()
        appState = AppState.FOREGROUND
    }

    /**
     * app进入后台调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackground() {
        "app进入后台".v()
        appState = AppState.BACKGROUND
    }

    /**
     * 不会调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onAppDestroy() {
        "app没运行".v()
        appState = AppState.NOT_RUNNING
    }
}