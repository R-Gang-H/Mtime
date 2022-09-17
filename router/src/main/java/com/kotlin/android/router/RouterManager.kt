package com.kotlin.android.router

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.annotation.path.LoginPathAnnotationManager
import com.kotlin.android.router.interceptor.callback.CloseCallback

/**
 * Router 路由管理器：
 * [init] Application中初始化。
 * [destroy] Application中适当位置销毁。
 * [debug] 开启debug模式
 * [inject] 注入对象：如Activity、Fragment 等。
 * [navigation] 导航路由方法。
 * [getProvider] 获取 [IProvider] 自定义实现类对象。
 * [getFragment] 获取 [android.app.Fragment] 自定义实现类对象。
 *
 * Created on 2021/4/20.
 *
 * @author o.s
 */
class RouterManager private constructor() {

    companion object {
        val instance by lazy { RouterManager() }
    }

    /**
     * 在 [Application] 中初始化 [ARouter]
     */
    fun init(application: Application, isDebug: Boolean,
             hostPath: String = "com.wandafilm.app.router.path") {
        debug(isDebug)
        ARouter.init(application)
        LoginPathAnnotationManager.instance.initLoginPaths(hostPath)
    }

    /**
     * 设置debug模式
     */
    private fun debug(isDebug: Boolean = true) {
        if (isDebug) {
            ARouter.openLog()
            ARouter.openDebug()
        }
    }

    /**
     * 注入对象：[Activity] [Fragment] 等
     */
    fun inject(obj: Any) {
        ARouter.getInstance().inject(obj)
    }

    /**
     * 在 [Application] 中适当的情况下销毁 [ARouter]
     */
    fun destroy() {
        ARouter.getInstance().destroy()
    }


    /**
     * 路由到 [path] 指定目标页面，并通过 [bundle] 传递参数。
     *
     * 路由的场景：
     * 1，普通方式路由：无回调，无返回结果。
     *  [path] 必须，
     *  [bundle] 可选，
     *  [context] 可选。null时使用 [Application]。
     *
     * 2，[Activity.startActivityForResult] 方式路由：返回结果。
     *  [path] 必须，
     *  [bundle] 可选，
     *  [requestCode] 必须，且>=0，
     *  [context] 必须，且为[Activity]。否则按照普通方式路由。
     *
     *  内部根据 [@LoginPath] 注解的 path 自动添加登录拦截回调处理，及后续页面跳转逻辑。
    //     * 3，[NavigationCallback] 导航回调方式路由：
    //     *  [path] 必须，
    //     *  [bundle] 可选，
    //     *  [callback] 必须，
    //     *  [context] 可选。null时使用 [Application]。
    //     *  通过指定拦截器，对响应的行为拦截回调，进行预设处理。
    //     *  如设置登录拦截器，对路由到需要登录的页面进行登录状态的判断，未登录的执行拦截操作，回调给 [callback] 进行后续处理。
    //     *
    //     * 4，组合2，3场景。
     *
     * 5，带有启动模式的路由跳转
     * [context] 必须,
     *  singleTop启动模式路由跳转
     *  1.如果在AndroidManifest中只设置android:launchMode="singleTop" ,路由中不设置flag和标准模式一样
     *  2.不在AndroidManifest中设置android:launchMode="singleTop",只在路由中设置flag,此时跳转到目标页面不走onNewIntent重新创建了
     *  3.在androidManifest中设置android:launchMode="singleTop",并在路由中设置flag，此时会走onNewIntent
     *  @param needCloseCurrentActivity 是否需要在跳转的时候关闭当前activity，如果需要，必须传递context,此处需要解决的问题是，finish当前activity的时候可能会出现
     *  路由路径初始化的反射过程未完毕当前activity销毁，造成部分手机上无法跳转activity
     */
    fun navigation(
        path: String,
        bundle: Bundle? = null,
        context: Context? = null,
        requestCode: Int = -1,
        @Postcard.FlagInt flags: Int = 0,
        needCloseCurrentActivity: Boolean = false,
        activityOptionsCompat: ActivityOptionsCompat? = null
    ) {
        val postcard = ARouter.getInstance().build(path).with(bundle)
        activityOptionsCompat?.let {
            ARouter.getInstance().build(path).with(bundle).withOptionsCompat(it)
        }
        if (flags > 0 && context != null) {
            postcard.withFlags(flags)
        }
        //application中获取登录的path
        val callback = CloseCallback(context, requestCode, needCloseCurrentActivity)
        if (requestCode >= 0 && context is Activity) {
            postcard.navigation(context, requestCode, callback)
        } else {
            postcard.navigation(context, callback)
        }
    }

    fun <T : IProvider> getProvider(clazz: Class<T>): T? {
        return clazz.getAnnotation(RouteProvider::class.java)?.run {
            ARouter.getInstance().build(path).navigation() as? T
        }
    }

    /**
     * 通过 [path] 获取 [android.app.Fragment] 自定义子类实例。
     * 如果 [path] 不存在或对应标注的资源不是 [android.app.Fragment] 子类，将获得 null
     */
    fun <T> getFragment(path: String?): T? {
        return ARouter.getInstance().build(path).navigation() as? T
    }

}