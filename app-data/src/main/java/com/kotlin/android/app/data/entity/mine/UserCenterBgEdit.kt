package com.kotlin.android.app.data.entity.mine

/**
 * 更新用户中心bg
 *
 * Created on 2022/4/23.
 *
 * @author ww
 */
data class UserCenterBgEdit(
        val success: Boolean, // 成功与否
        val error: String, // 错误提示
        val backgroundAppPic: String // url
)
