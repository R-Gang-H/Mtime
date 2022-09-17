package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/10/13
 * description: UserController - 查询认证影评人是否符合条件(/user/auth/mtime/permission.api)

 */
data class CheckAuthPermission(var success: Boolean = false,//是否成功
                               var error: String? = "",//提示信息
                               var permission: Boolean = false//是否有权限
) : ProguardRule