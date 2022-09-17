package com.kotlin.android.router.annotation.path

import com.alibaba.android.arouter.utils.ClassUtils
import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.router.KEY_DOLLAR
import com.kotlin.android.router.annotation.LoginPath
import com.kotlin.android.router.annotation.RouteGroup
import com.kotlin.android.router.annotation.RoutePath

/**
 * [LoginPath] 登录注解标识信息解析管理类：
 *
 * Created on 2021/4/21.
 *
 * @author o.s
 */
class LoginPathAnnotationManager private constructor() {

    companion object {
        val instance by lazy { LoginPathAnnotationManager() }
    }

    private val paths = HashSet<String>()

//    fun inject(any: Any) {
//        "RouterPathAnnotationManager inject".e()
//        any::class.java.declaredFields.forEach {
//            it.isAccessible = true
//            if (it.isAnnotationPresent(LoginPath::class.java)) {
//                if (it.type == String::class.java) {
//                    (it.get(any) as? String)?.apply {
//                        "RouterPathAnnotationManager LoginPath :: $this".e()
//                        paths.add(this)
//                    }
//                }
//            }
//        }
//    }

    /**
     * 判断路由路径 path 是否需要登录
     */
    fun isLoginPath(path: String?): Boolean {
        return paths.contains(path)
    }

    /**
     * 初始化 [LoginPath] 注解标识的需要登录的 path 路由路径集合
     */
    fun initLoginPaths(hostPath: String) {
        if (paths.isEmpty()) {
            ensureInject(hostPath)
        }
    }

    /**
     * 确保 [RoutePath] 注解类注入
     */
    private fun ensureInject(hostPath: String) {
        /**
         * packageName为app-router中path的路径
         * 通过反射拿到相关的类名
         */
        val fileNameByPackageNames =
            ClassUtils.getFileNameByPackageName(
                CoreApp.instance.applicationContext,
                hostPath
            )
        fileNameByPackageNames.forEach {
            if (!it.contains(KEY_DOLLAR)) {
                inject(Class.forName(it))
            }
        }
    }

    /**
     * 注入 [RoutePath] 注解类
     */
    fun inject(clazz: Class<*>) {
        clazz.declaredClasses.forEach { subClass ->
            if (subClass.isAnnotationPresent(RouteGroup::class.java)) {
                subClass.declaredFields.forEach { field ->
                    field.isAccessible = true
                    if (field.isAnnotationPresent(LoginPath::class.java)) {
                        if (field.type == String::class.java) {
                            (field.get(subClass) as? String)?.apply {
//                                "RouterPathAnnotationManager LoginPath :: $this".i()
                                paths.add(this)
                            }
                        }
                    }
                }
            }
        }
    }

    fun clear() {
        paths.clear()
    }
}