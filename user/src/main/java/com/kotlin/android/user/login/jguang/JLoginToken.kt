package com.kotlin.android.user.login.jguang

/**
 * 极光一键登录TOKEN
 *
 * Created on 2021/4/9.
 *
 * @author o.s
 */
data class JLoginToken(
            val code: Int,
            var content: String? = null, // token有效期为1分钟
            var operator: String? = null
    ) {
        val isOk: Boolean
            get() = code == 6000 || code == 2000 // [2000]getToken(), [6000]authLogin()

        val token: String?
            get() {
                return if (isOk) {
                    content
                } else {
                    null
                }
            }

        val ope: String?
            get() {
                return when (operator) {
                    "CM" -> {
                        "中国移动"
                    }
                    "CU" -> {
                        "中国联通"
                    }
                    "CT" -> {
                        "中国电信"
                    }
                    else -> {
                        null
                    }
                }
            }
    }