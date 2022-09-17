package com.kotlin.android.bind

/**
 *
 * Created on 2021/7/7.
 *
 * @author o.s
 */
class DelegateNotFoundException(clazz: Class<*>) : RuntimeException("未注册：${clazz.name}")