package com.kotlin.android.ktx.ext.validator

import java.util.regex.Pattern

const val REGEX_MOBILE = "^1[0-9]{10}\$"

const val REGEX_PASSWORD = "^(?![0-9]+\$)(?![a-zA-Z]+\$)[0-9A-Za-z]{6,16}\$"

/**
 * 手机号校验
 * 校验规则：手机号码为11位，且以1开头
 */
fun isMobile(mobile:String):Boolean{
    return Pattern.matches(REGEX_MOBILE, mobile)
}

/**
 * 密码校验
 * 校验规则：大于6位 数字+字母
 */
fun isPassword(password:String):Boolean{
    return Pattern.matches(REGEX_PASSWORD, password)
}
/**
 * 邮箱校验
 * 校验规则：包含“@”
 */
fun isEmail(email:String):Boolean{
    return email.contains("@")
}