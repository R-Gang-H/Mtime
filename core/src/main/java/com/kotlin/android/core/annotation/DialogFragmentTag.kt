package com.kotlin.android.core.annotation

/**
 * DialogFragment TAG 标识
 *
 * Created on 2022/4/15.
 *
 * @author o.s
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DialogFragmentTag(val tag: String)
