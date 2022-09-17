package com.kotlin.android.router.ext

import com.alibaba.android.arouter.facade.template.IProvider
import com.kotlin.android.router.RouterManager

/**
 * Provider扩展类
 */

/**
 * 通过 [IProvider] 路由子接口的 [clazz] 获取注解中的path [com.kotlin.android.router.annotation.RouteProvider.path] 。
 * 如果不存在或对应标注的资源不是 [IProvider] 子类，将获得 null
 *
 * 注意：[IProvider] 路由子接口需添加注解 [com.kotlin.android.router.annotation.RouteProvider]，并指定path值
 */
fun <T : IProvider> getProvider(clazz: Class<T>): T? {
    return RouterManager.instance.getProvider(clazz)
}

fun <T : IProvider> getProvider(clazz: Class<T>, jump: T.() -> Unit) {
    RouterManager.instance.getProvider(clazz)?.let(jump)
}
