package com.kotlin.android.router.bus.annotation

/**
 * 事件对象类注解，指定事件的key
 *
 * Created on 2021/4/23.
 *
 * @author o.s
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LiveEventKey(val key: String)
