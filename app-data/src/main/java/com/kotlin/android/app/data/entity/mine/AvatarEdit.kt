package com.kotlin.android.app.data.entity.mine

/**
 * 更新头像
 *
 * Created on 2020/12/25.
 *
 * @author o.s
 */
data class AvatarEdit(
        val success: Boolean, // 成功与否
        val error: String, // 错误提示
        val headPic: String // 头像
)
