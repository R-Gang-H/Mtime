package com.kotlin.android.applink

/**
 * 创建者: zl
 * 创建时间: 2020/12/7 3:36 下午
 * 描述:暂时没用到
 */
enum class AppLinkFrom(val value: Int = 0) {
    /**
     * app 内部
     */
    Internal(0),

    /**
     * Scheme_H5
     */
    Scheme_H5(1),

    /**
     * Push
     */
    Push(2),

    /**
     * Internal_H5
     */
    Internal_H5(2)
}