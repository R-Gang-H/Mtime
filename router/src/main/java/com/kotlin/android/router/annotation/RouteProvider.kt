package com.kotlin.android.router.annotation

/**
 *
 * Created on 2021/4/21.
 *
 * @author o.s
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RouteProvider(val path: String)
