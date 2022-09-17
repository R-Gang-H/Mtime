package com.kotlin.android.router.annotation

/**
 * 路由路径属性注解：标记需要登录才能访问的路由路径 path。
 * 路由框架内部处理登录跳转及登录成功后续跳转。
 *
 * Created on 2021/4/21.
 *
 * @author o.s
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)//, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginPath
